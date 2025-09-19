package net.sourceforge.opencamera.camera;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Minimal camera API contract for app <-> camera impl.
 * 目标：提供最小、异步且线程约定明确的接口，便于逐步替换现有调用。
 */
public interface CameraModuleApi {

    /**
     * 回调接收预览帧（文档：回调在后台线程调用，若需更新 UI，请切换到主线程）。
     */
    interface PreviewCallback {
        void onPreviewFrame(byte[] nv21, int width, int height, long timestamp);
    }

    interface CaptureCallback {
        void onCaptureStarted();
        void onCaptureCompleted(boolean success, Uri savedUri);
        void onCaptureError(Exception e);
    }

    interface VideoCallback {
        void onVideoStarted(Uri outputUri);
        void onVideoStopped(Uri outputUri);
        void onVideoError(Exception e);
    }

    // 初始化模块（在 Application 或 Activity.onCreate 时调用）
    void initialize(Context context);

    // 打开/关闭相机（同步返回操作是否发起成功；状态变更通过回调/事件通知）
    boolean openCamera(int cameraId);
    void closeCamera();

    // 预览 Surface 设置（camera 将在此 surface输出预览）
    void setPreviewSurface(Surface surface);

    void setPreviewCallback(PreviewCallback cb);
    void removePreviewCallback();

    // 异步拍照 API：返回一个 CompletableFuture，完成时给出保存的 Uri（或异常）
    CompletableFuture<Uri> takePictureAsync(CaptureCallback cb);

    // 录像控制
    CompletableFuture<Uri> startRecordingAsync(String filenameHint, VideoCallback cb);
    CompletableFuture<Void> stopRecordingAsync();

    CameraCapabilities getCapabilities();

    // 释放资源（永久）
    void release();

    class CameraCapabilities {
        public final boolean supportsCamera2;
        public final boolean supportsRaw;
        public final List<String> supportedISOs;
        public CameraCapabilities(boolean supportsCamera2, boolean supportsRaw, List<String> supportedISOs) {
            this.supportsCamera2 = supportsCamera2;
            this.supportsRaw = supportsRaw;
            this.supportedISOs = supportedISOs;
        }
    }
}