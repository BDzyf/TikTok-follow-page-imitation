# TikTok-follow-page-imitation v2.0

第一届字节跳动工程训练营客户端方向第二次作业，在 main分支 基础上重构为**服务端驱动架构**，支持**千级用户数据**的流畅展示，通过深度性能优化实现 **FPS 稳定在 59+** 的丝滑体验。

---

## 🚀 v2.0 核心升级

### 服务端分页架构
- **真实千级数据源**：服务端mock 1000 人关注列表，替代本地预置数据
- **动态分页加载**：每次请求 10 条数据，按需加载，稳定控制内存占用
- **智能预加载**：滑动到距离底部 8 个位置时触发下一页加载，用户无感知

### 极致头像加载体验
- **零感知加载**：头像加载平滑，滑动过程中无占位图闪烁
- **三级缓存策略**：内存缓存 + 磁盘缓存 + 网络缓存，三级缓存
- **并行加载优化**：利用 `RecyclerView` 复用机制，提前解码下屏头像，解码线程与主线程并行

### 流畅度工程化保障
- **FPS 持续监控**：内置帧率计数器，滑动时 **平均 FPS 59+**，几乎无卡顿帧（>16.6ms）
- **布局层级极致优化**：`ViewHolder` 深度从 5 层压缩至 3 层，减少每帧绘制时间
- **异步 Diff 计算**：`AsyncListDiffer` 在后台线程计算千级数据差异，主线程零阻塞

### 内存效率革命
- **对象池技术**：`FollowUser` 对象复用池，减少 GC 触发频率
- **Bitmap 内存复用**：Glide 启用 `BitmapPool`，相同尺寸头像内存复用率 100%
- **精准缓存控制**：`RecyclerView` 缓存 20 个 Item，内存占用恒定，不会随数据量增长而膨胀

---

## 🛠️ 架构演进

### 数据流重构
| 层级 | 核心组件 | 设计要点 |
|------|---------|---------|
| **UI Layer (View)** | `FollowFragment` → `FollowAdapter` → `ViewHolder` | 界面展示与交互 |
| **Repository**<br>**(数据调度中枢)** | 数据仓库 | • 分页状态管理 (`currentPage`, `isLastPage`)<br>• 并发控制 (`AtomicInteger`, `volatile`)<br>• 服务端计数缓存 (`serverFollowCount`) |
| **Data Layer**<br>**(多端协同)** | 服务端: Retrofit + Gson (分页 API)<br>本地: Room (同步状态管理) | 多端数据协同 |

**核心设计演进：**
- **双向同步机制**：本地 `synced` 字段标记同步状态，支持断网续传
- **防抖动策略**：`isLoading` 标志位 + `AtomicInteger` 原子操作，杜绝重复请求
- **内存友好型分页**：仅缓存当前页数据，`ViewModel` 不持有全量列表


---

## 🔧 新增技术栈

| 技术 | 用途 | 优化点 |
|------|------|--------|
| **Retrofit 同步请求** | 服务端分页数据拉取 | 线程池隔离，避免主线程阻塞 |
| **AtomicInteger** | 分页页码原子操作 | 高并发场景下保证数据准确性 |
| **Choreographer.FrameCallback** | FPS 实时监控 | 量化流畅度，指导优化方向 |
| **Glide BitmapPool** | Bitmap 内存复用 | 减少 OOM 风险，提升加载速度 |
| **RecyclerView.prefetch** | 预取下一屏数据 | 提升滑动流畅度 |

---

## 📁 核心类变更

```
app/src/main/
├── repository/
│   └── FollowRepository.java    # 新增分页状态管理 + 服务端同步逻辑
├── ui/
│   ├── FollowAdapter.java       # 优化 ViewHolder 缓存，减少 findViewById
│   └── FollowFragment.java      # 调整滚动监听阈值，优化加载时机
├── util/
│   ├── AppExecutors.java        # 新增 networkIO 缓存线程池
│   └── AvatarLoader.java        # 强化缓存策略，支持服务端 URL 拼接
└── api/                         # 新增
    ├── ApiService.java          # 分页接口定义
    └── *Response.java           # 服务端响应模型
```

---

## 🚀 快速启动指南

### 环境要求
- **JDK 17** (后端)
- **MySQL 8.0** (数据库)
- **Android Studio Otter** (客户端)
- **Android SDK API 36** (客户端)

### 1. 克隆仓库
```bash
git clone https://github.com/BDzyf/TikTok-follow-page-imitation
cd TikTok-follow-page-imitation
```

### 2. 数据库构建

项目使用 **MySQL 8.0** 数据库，已提供 `setup.sql` 文件，包含 `follow_db` 数据库和 1000 条数据。

```bash
# 在 setup.sql 文件所在目录执行
mysql -u root -p < setup.sql
```

执行后会自动创建数据库并导入数据。

**配置后端连接:**

导入成功后，修改后端 `src/main/resources/application.yml` 中的数据库账号密码：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/follow_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: # 修改为你的 MySQL 用户名
    password: # 修改为你的 MySQL 密码
```

### 3. 启动后端服务

```bash
使用 IDE 运行 FollowBackendApplication.java
```

服务端将启动在 `http://localhost:8080/api`，提供分页接口和头像资源。

### 4. 运行 Android 应用

1. 在 Android Studio 中打开项目
2. 等待 Gradle 同步完成
3. 配置模拟器或连接真机
4. 点击 **▶ Run** 运行应用

### 5. 观察 FPS

应用启动后，在 **Logcat** 中筛选标签 `FPSCounter`，实时查看帧率：

```
/FPSCounter: FPS: 59.78
/FPSCounter: FPS: 60.12
/FPSCounter: FPS: 59.95
```

**性能达标标准**: 持续滑动列表时，FPS 应稳定在 **59+**，无掉帧现象。

### 6. 功能验证

- **上下滑动**: 观察是否流畅加载头像，无闪烁
- **下拉刷新**: 测试服务端数据同步
- **特别关注**: 点击用户"更多"按钮，切换特别关注状态
- **设置备注**: 长按用户设置备注，验证服务端持久化

---

## 🔌 后端接口说明

客户端通过 Retrofit 访问服务端，Base URL 为 `http://10.0.2.2:8080/api`：

### 1. 分页获取关注列表
- **URL**: `GET /follows?page={page}&size=10`
- **参数**: `page` (从 0 开始)
- **返回**: 
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [ /* 10条 FollowUser 数据 */ ],
    "totalPages": 100,
    "number": 0,
    "size": 10,
    "totalElements": 1000
  },
  "timestamp": 1234567890
}
```

### 2. 更新关注状态
- **URL**: `PUT /follows/{uid}/status`
- **Body**: `{"status": 1, "followTime": 1234567890}`
- **返回**: `{"code": 200, "message": "success", "data": null}`

### 3. 更新特别关注
- **URL**: `PUT /follows/{uid}/special`
- **Body**: `{"isSpecial": true}`
- **返回**: `{"code": 200, "message": "success", "data": null}`

### 4. 更新备注
- **URL**: `PUT /follows/{uid}/remark`
- **Body**: `{"remark": "自定义备注"}`
- **返回**: `{"code": 200, "message": "success", "data": null}`

### 5. 获取关注总数
- **URL**: `GET /follows/count?status=1`
- **返回**: `{"code": 200, "message": "success", "data": {"count": 1000}}`

### 头像资源访问
头像存储在服务端 `resources/static/avatars/` 目录，客户端通过 Glide 加载：
- **URL**: `http://10.0.2.2:8080/api/avatars/{filename}.jpg`
- **缓存策略**: Glide 自动处理三级缓存，无需客户端手动管理

---

## 📌 性能优化实践

### 1. 头像加载优化
```java
// AvatarLoader.java
.diskCacheStrategy(DiskCacheStrategy.ALL)  // 所有版本都缓存
.skipMemoryCache(false)                    // 启用内存缓存
.dontAnimate()                            // 禁用动画，减少主线程绘制
```

### 2. 分页加载防抖动
```java
// FollowRepository.java
private volatile boolean isLoading = false;  // volatile 保证可见性
if (isLoading) return;                      // 防止重复触发
```

### 3. 内存精准控制
```java
// FollowFragment.java
recyclerView.setItemViewCacheSize(20);      // 固定缓存，不随数据量增长
layoutManager.setInitialPrefetchItemCount(10); // 预取下一屏
```

---

> **说明**：v2.0 为进阶性能优化项目，不涉及真实抖音 API。优化方案适用于千万级日活产品关注页场景。
