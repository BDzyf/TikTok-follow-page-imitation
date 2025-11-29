package com.demo.follow.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.demo.follow.api.ApiService;
import com.demo.follow.api.BaseResponse;
import com.demo.follow.api.CountResponse;
import com.demo.follow.api.FollowPageResponse;
import com.demo.follow.api.UpdateStatusRequest;
import com.demo.follow.api.UpdateSpecialRequest;
import com.demo.follow.api.UpdateRemarkRequest;
import com.demo.follow.db.AppDatabase;
import com.demo.follow.db.FollowDao;
import com.demo.follow.db.FollowUser;
import com.demo.follow.util.AppExecutors;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 关注数据仓库
 * 统一管理本地数据库与远程API的数据交互，实现线程分离避免主线程阻塞
 */
public class FollowRepository {

    private final FollowDao dao;
    private final AppExecutors executors;
    private final ApiService apiService;
    private final Context appContext;

    /**
     * 分页状态管理：当前页码
     */
    private final AtomicInteger currentPage = new AtomicInteger(0);

    /**
     * 分页状态管理：是否为最后一页
     */
    private volatile boolean isLastPage = false;

    /**
     * 分页状态管理：是否正在加载中
     */
    private volatile boolean isLoading = false;

    /**
     * 服务端关注数量缓存
     */
    private final MutableLiveData<Integer> serverFollowCount = new MutableLiveData<>();

    /**
     * 防重复加载标志
     */
    private volatile boolean isLoadingCount = false;

    /**
     * 构造函数
     * 初始化数据库访问对象、线程执行器和Retrofit网络服务
     * @param context 应用上下文
     */
    public FollowRepository(Context context) {
        this.appContext = context.getApplicationContext();
        dao = AppDatabase.get(appContext).followDao();
        executors = AppExecutors.getInstance();

        String baseUrl = "http://10.0.2.2:8080/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    // ==================== 数据查询方法 ====================

    /**
     * 获取所有关注用户列表
     * @return 实时更新的用户列表LiveData
     */
    public LiveData<List<FollowUser>> getAll() {
        return dao.getAllFollow();
    }

    /**
     * 获取有效关注用户数量
     * @return 实时更新的数量LiveData
     */
    public LiveData<Integer> getCount() {
        return dao.getFollowCount();
    }

    /**
     * 根据抖音号获取用户（实时）
     * @param douyinId 抖音号
     * @return 用户对象的LiveData
     */
    public LiveData<FollowUser> getUserByDouyinIdLive(String douyinId) {
        return dao.getUserByDouyinIdLive(douyinId);
    }

    /**
     * 获取服务端关注数量
     * @return 服务端数量的LiveData
     */
    public LiveData<Integer> getServerFollowCount() {
        return serverFollowCount;
    }

    // ==================== 分页控制 ====================

    /**
     * 判断是否正在加载
     * @return 是否加载中
     */
    public boolean isLoading() {
        return isLoading;
    }

    /**
     * 判断是否为最后一页
     * @return 是否最后一页
     */
    public boolean isLastPage() {
        return isLastPage;
    }

    /**
     * 重置分页状态
     */
    public void resetPaging() {
        currentPage.set(0);
        isLastPage = false;
    }

    // ==================== 数据加载回调接口 ====================

    /**
     * 分页加载回调
     */
    public interface LoadCallback {
        void onLoaded(List<FollowUser> users, boolean isLast);
        void onError(Exception e);
    }

    /**
     * 刷新数据回调
     */
    public interface RefreshCallback {
        void onComplete();
        void onError(Exception e);
    }

    /**
     * 操作回调（关注、取消关注、设置备注等）
     */
    public interface OperationCallback {
        void onSuccess();
        void onError(String message);
    }

    // ==================== 核心数据操作方法 ====================

    /**
     * 从服务端同步分页数据
     * 网络请求在独立线程执行，数据库操作在diskIO线程执行
     * @param callback 加载结果回调
     */
    public void syncFromServer(LoadCallback callback) {
        if (isLoading) return;
        isLoading = true;

        executors.networkIO().execute(() -> {
            try {
                Call<FollowPageResponse> call = apiService.getFollows(currentPage.get(), 5);
                Response<FollowPageResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    FollowPageResponse pageResponse = response.body();
                    if (!pageResponse.isSuccess()) {
                        throw new Exception("业务错误: " + pageResponse.getMessage());
                    }

                    final List<FollowUser> users = pageResponse.getContent();

                    executors.diskIO().execute(() -> {
                        for (FollowUser user : users) {
                            user.synced = true;
                        }
                        dao.insertAll(users);

                        isLastPage = pageResponse.isLast();
                        if (!isLastPage) {
                            currentPage.incrementAndGet();
                        }

                        executors.mainThread().execute(() -> {
                            if (callback != null) callback.onLoaded(users, isLastPage);
                        });
                    });
                } else {
                    throw new Exception("HTTP响应失败: " + response.message());
                }
            } catch (Exception e) {
                executors.mainThread().execute(() -> {
                    if (callback != null) callback.onError(e);
                });
            } finally {
                isLoading = false;
            }
        });
    }

    /**
     * 刷新本地数据：删除已取消关注的用户
     * @param callback 刷新结果回调
     */
    public void refreshData(RefreshCallback callback) {
        execute(() -> {
            try {
                dao.deleteUnfollowedUsers();
                if (callback != null) {
                    executors.mainThread().execute(() -> callback.onComplete());
                }
            } catch (Exception e) {
                if (callback != null) {
                    executors.mainThread().execute(() -> callback.onError(e));
                }
            }
        });
    }

    /**
     * 从服务端加载关注数量
     * 网络请求在独立线程执行，避免阻塞主线程
     */
    public void loadFollowCountFromServer() {
        if (isLoadingCount) return;
        isLoadingCount = true;

        executors.networkIO().execute(() -> {
            try {
                Call<CountResponse> call = apiService.getFollowCount(1);
                Response<CountResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    CountResponse countResponse = response.body();
                    if (countResponse.isSuccess()) {
                        int count = countResponse.getCount();
                        executors.mainThread().execute(() -> serverFollowCount.setValue(count));
                    }
                }
            } catch (Exception e) {
                // 静默失败，不影响主流程
            } finally {
                isLoadingCount = false;
            }
        });
    }

    // ==================== 关注操作（先调服务端，再改本地） ====================

    /**
     * 切换关注状态
     * 先请求服务端更新，成功后再更新本地数据库
     * @param douyinId 抖音号
     * @param callback 操作结果回调
     */
    public void toggleFollow(String douyinId, OperationCallback callback) {
        executors.networkIO().execute(() -> {
            try {
                FollowUser user = dao.getUserByDouyinId(douyinId);
                if (user == null) {
                    mainThreadError(callback, "用户不存在");
                    return;
                }

                boolean isCurrentlyFollowing = user.status == 1;
                long uid = user.uid;
                int newStatus = isCurrentlyFollowing ? 0 : 1;
                Long followTime = isCurrentlyFollowing ? null : System.currentTimeMillis();

                Call<BaseResponse> call = apiService.updateStatus(uid,
                        new UpdateStatusRequest(newStatus, followTime));
                Response<BaseResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse baseResponse = response.body();
                    if (baseResponse.isSuccess()) {
                        executors.diskIO().execute(() -> {
                            if (newStatus == 0) {
                                dao.unfollow(uid);
                            } else {
                                user.status = 1;
                                user.followTime = followTime;
                                dao.insert(user);
                            }
                            executors.mainThread().execute(() -> {
                                loadFollowCountFromServer();
                                callback.onSuccess();
                            });
                        });
                    } else {
                        throw new Exception("业务错误: " + baseResponse.getMessage());
                    }
                } else {
                    throw new Exception("HTTP响应失败: " + response.message());
                }
            } catch (Exception e) {
                mainThreadError(callback, "网络错误");
            }
        });
    }

    /**
     * 切换特别关注状态
     * 先请求服务端更新，成功后再更新本地数据库
     * @param douyinId 抖音号
     * @param callback 操作结果回调
     */
    public void toggleSpecial(String douyinId, OperationCallback callback) {
        executors.networkIO().execute(() -> {
            try {
                FollowUser user = dao.getUserByDouyinId(douyinId);
                if (user == null) {
                    mainThreadError(callback, "用户不存在");
                    return;
                }

                long uid = user.uid;
                boolean newSpecialState = !user.isSpecial;

                Call<BaseResponse> call = apiService.updateSpecial(uid,
                        new UpdateSpecialRequest(newSpecialState));
                Response<BaseResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse baseResponse = response.body();
                    if (baseResponse.isSuccess()) {
                        executors.diskIO().execute(() -> {
                            dao.setSpecial(uid, newSpecialState);
                            executors.mainThread().execute(() -> callback.onSuccess());
                        });
                    } else {
                        throw new Exception("业务错误: " + baseResponse.getMessage());
                    }
                } else {
                    throw new Exception("HTTP响应失败: " + response.message());
                }
            } catch (Exception e) {
                mainThreadError(callback, "网络错误");
            }
        });
    }

    /**
     * 更新用户备注
     * 先请求服务端更新，成功后再更新本地数据库
     * @param douyinId 抖音号
     * @param remark 新备注内容
     * @param callback 操作结果回调
     */
    public void updateRemark(String douyinId, String remark, OperationCallback callback) {
        executors.networkIO().execute(() -> {
            try {
                FollowUser user = dao.getUserByDouyinId(douyinId);
                if (user == null) {
                    mainThreadError(callback, "用户不存在");
                    return;
                }

                long uid = user.uid;

                Call<BaseResponse> call = apiService.updateRemark(uid,
                        new UpdateRemarkRequest(remark));
                Response<BaseResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse baseResponse = response.body();
                    if (baseResponse.isSuccess()) {
                        executors.diskIO().execute(() -> {
                            dao.setRemark(uid, remark);
                            executors.mainThread().execute(() -> callback.onSuccess());
                        });
                    } else {
                        throw new Exception("业务错误: " + baseResponse.getMessage());
                    }
                } else {
                    throw new Exception("HTTP响应失败: " + response.message());
                }
            } catch (Exception e) {
                mainThreadError(callback, "网络错误");
            }
        });
    }

    // ==================== 线程辅助方法 ====================

    /**
     * 在磁盘IO线程执行操作
     */
    private void execute(Runnable action) {
        executors.diskIO().execute(action);
    }

    /**
     * 在主线程执行错误回调
     * @param callback 操作回调
     * @param msg 错误消息
     */
    private void mainThreadError(OperationCallback callback, String msg) {
        executors.mainThread().execute(() -> callback.onError(msg));
    }
}