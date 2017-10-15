package cn.ezandroid.ezfilter.video;

import android.media.MediaMetadataRetriever;
import android.net.Uri;

import java.io.IOException;

import cn.ezandroid.ezfilter.EZFilter;
import cn.ezandroid.ezfilter.core.FBORender;
import cn.ezandroid.ezfilter.core.FilterRender;
import cn.ezandroid.ezfilter.environment.IFitView;
import cn.ezandroid.ezfilter.extra.IAdjustable;
import cn.ezandroid.ezfilter.video.offscreen.OffscreenVideo;
import cn.ezandroid.ezfilter.video.player.IMediaPlayer;

/**
 * 视频处理构造器
 */
public class VideoBuilder extends EZFilter.Builder {

    private Uri mVideo;
    private boolean mVideoLoop;
    private float mVideoVolume;
    private IMediaPlayer.OnPreparedListener mPreparedListener;
    private IMediaPlayer.OnCompletionListener mCompletionListener;

    public VideoBuilder(Uri uri) {
        mVideo = uri;
    }

    public VideoBuilder setLoop(boolean loop) {
        mVideoLoop = loop;
        return this;
    }

    public VideoBuilder setVolume(float volume) {
        mVideoVolume = volume;
        return this;
    }

    public VideoBuilder setPreparedListener(IMediaPlayer.OnPreparedListener listener) {
        mPreparedListener = listener;
        return this;
    }

    public VideoBuilder setCompletionListener(IMediaPlayer.OnCompletionListener listener) {
        mCompletionListener = listener;
        return this;
    }

    public void output(String output) {
        // 离屏渲染
        OffscreenVideo offscreenVideo = new OffscreenVideo(mVideo.getPath());
        try {
            for (FilterRender filterRender : mFilterRenders) {
                offscreenVideo.addFilterRender(filterRender);
            }
            offscreenVideo.save(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void output(String output, int width, int height) {
        // 离屏渲染
        OffscreenVideo offscreenVideo = new OffscreenVideo(mVideo.getPath());
        try {
            for (FilterRender filterRender : mFilterRenders) {
                offscreenVideo.addFilterRender(filterRender);
            }
            offscreenVideo.save(output, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected FBORender getStartPointRender(IFitView view) {
        VideoInput videoInput = new VideoInput(view.getContext(), view, mVideo);
        videoInput.setLoop(mVideoLoop);
        videoInput.setVolume(mVideoVolume, mVideoVolume);
        videoInput.setOnPreparedListener(mPreparedListener);
        videoInput.setOnCompletionListener(mCompletionListener);
        videoInput.start();
        return videoInput;
    }

    @Override
    protected float getAspectRatio(IFitView view) {
        MediaMetadataRetriever metadata = new MediaMetadataRetriever();
        try {
            metadata.setDataSource(view.getContext(), mVideo);
            String width = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            return Integer.parseInt(width) * 1.0f / Integer.parseInt(height);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            metadata.release();
        }
    }

    @Override
    public VideoBuilder addFilter(FilterRender filterRender) {
        return (VideoBuilder) super.addFilter(filterRender);
    }

    @Override
    public <T extends FilterRender & IAdjustable> VideoBuilder addFilter(T filterRender, float progress) {
        return (VideoBuilder) super.addFilter(filterRender, progress);
    }

    @Override
    public VideoBuilder enableRecord(String outputPath, boolean recordVideo, boolean recordAudio) {
        return (VideoBuilder) super.enableRecord(outputPath, recordVideo, recordAudio);
    }
}
