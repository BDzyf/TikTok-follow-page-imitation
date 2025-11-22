package com.demo.follow.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.app.AlertDialog;
import com.demo.follow.R;
import com.demo.follow.db.FollowUser;
import com.demo.follow.repository.FollowRepository;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * 用户操作底部弹窗
 * 提供特别关注、设置备注、取消关注等功能
 */
public class UserActionBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_DOUYIN_ID = "douyinId";
    private static final String DIALOG_TAG = "user_action";
    private static final String DEFAULT_REMARK_HINT = "设置备注";
    private static final String DEFAULT_REMARK_INPUT_HINT = "请输入备注";
    private static final String EMPTY_STRING = "";

    /**
     * 抖音号
     */
    private String douyinId;

    /**
     * 数据管理
     */
    private FollowRepository repository;

    /**
     * 当前用户缓存
     */
    private FollowUser currentUser;

    /**
     * 显示底部弹窗
     * @param fragmentManager Fragment 管理器
     * @param douyinId 抖音号
     */
    public static void showForUser(@NonNull androidx.fragment.app.FragmentManager fragmentManager, String douyinId) {
        UserActionBottomSheet fragment = new UserActionBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_DOUYIN_ID, douyinId);
        fragment.setArguments(args);
        fragment.show(fragmentManager, DIALOG_TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            douyinId = getArguments().getString(ARG_DOUYIN_ID);
        }
        repository = new FollowRepository(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sheet_user_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupObserver();
        setupListeners(view);
    }

    /**
     * 设置用户数据观察者
     */
    private void setupObserver() {
        TextView tvNameOnly = requireView().findViewById(R.id.tv_name_only);
        TextView tvUser = requireView().findViewById(R.id.tv_user);
        SwitchCompat swSpecial = requireView().findViewById(R.id.sw_special);

        repository.getUserByDouyinIdLive(douyinId).observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                dismiss();
                return;
            }
            currentUser = user;

            String displayName = getDisplayName(user);

            // 第一行：显示名字（备注或昵称）
            tvNameOnly.setText(displayName);

            // 第二行：显示详细信息（抖音号 + 昵称）
            tvUser.setText("名字：" + user.nick + " | 抖音号：" + user.douyinId);

            swSpecial.setChecked(user.isSpecial);
        });
    }

    /**
     * 设置点击事件监听器
     */
    private void setupListeners(@NonNull View view) {
        // 特别关注开关
        view.findViewById(R.id.sw_special).setOnClickListener(v -> {
            repository.toggleSpecial(douyinId);
        });

        // 设置备注
        view.findViewById(R.id.tv_remark).setOnClickListener(v -> {
            showRemarkDialog();
        });

        // 取消关注
        view.findViewById(R.id.tv_unfollow).setOnClickListener(v -> {
            repository.toggleFollow(douyinId);
            dismiss();
        });
    }

    /**
     * 显示备注设置对话框
     */
    private void showRemarkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_remark, null);
        builder.setView(dialogView);

        EditText editText = dialogView.findViewById(R.id.et_remark);

        // 设置当前备注值
        if (currentUser != null) {
            editText.setText(currentUser.remark);
        }

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            String newRemark = editText.getText().toString().trim();
            if (DEFAULT_REMARK_HINT.equals(newRemark) || DEFAULT_REMARK_INPUT_HINT.equals(newRemark)) {
                newRemark = EMPTY_STRING;
            }
            repository.updateRemark(douyinId, newRemark);
            dialog.dismiss();
        });

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    /**
     * 获取显示名称（优先使用备注）
     */
    private String getDisplayName(FollowUser user) {
        if (user.remark != null && !user.remark.isEmpty()
                && !DEFAULT_REMARK_HINT.equals(user.remark)
                && !DEFAULT_REMARK_INPUT_HINT.equals(user.remark)) {
            return user.remark;
        }
        return user.nick;
    }
}