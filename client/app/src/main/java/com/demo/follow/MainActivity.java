package com.demo.follow;

import android.os.Bundle;
import android.util.Log;
import android.view.Choreographer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.demo.follow.ui.RelationshipsFragment;

/**
 * 应用主 Activity
 * 作为应用的入口点，负责加载主界面 Fragment
 */
public class MainActivity extends AppCompatActivity {

    private FPSCounter fpsCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new RelationshipsFragment())
                .commit();

        // 初始化 FPS 计数器
        fpsCounter = new FPSCounter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 启动 FPS 监控
        Choreographer.getInstance().postFrameCallback(fpsCounter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 停止 FPS 监控以节省资源
        Choreographer.getInstance().removeFrameCallback(fpsCounter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // ✅ 请求更高的渲染优先级，减少被调度器延迟，确保VSync准时交付
            getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    /**
     * FPS 计数器，用于监控应用帧率
     * 每秒输出一次日志到 Logcat，标签为 "FPSCounter"
     */
    private static class FPSCounter implements Choreographer.FrameCallback {
        private static final String TAG = "FPSCounter";
        private long lastTime = System.nanoTime();
        private int frames = 0;

        @Override
        public void doFrame(long frameTimeNanos) {
            frames++;
            long currentTime = System.nanoTime();

            // 每秒输出一次 FPS
            if (currentTime - lastTime >= 1_000_000_000) {
                double fps = (double) frames * 1_000_000_000 / (currentTime - lastTime);
                Log.d(TAG, String.format("FPS: %.2f", fps));
                frames = 0;
                lastTime = currentTime;
            }

            // 注册下一帧回调，继续监控
            Choreographer.getInstance().postFrameCallback(this);
        }
    }
}