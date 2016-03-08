package view;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.UUID;

/**
 * Created by iscod on 2016/3/8.
 */
public class AudioManager {
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    public boolean isPrepared;

    private static AudioManager mInstance;

    private AudioManager(String dir) {
        mDir = dir;
    }


    /**
     * 回调准备完毕
     */
    public interface AudioStateListener {
        void wellPreared();
    }

    public AudioStateListener mListener;

    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

    public static AudioManager getInstance(String dir) {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioManager(dir);
                }
            }
        }
        return mInstance;
    }

    public void prepareAudio() {
        try {
            //开始准备
            isPrepared = false;
            //创建一个文件夹
            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //根据文件名和文件夹创建一个文件
            String fileName = generateFileName();
            File file = new File(dir, fileName);
            //获取文件绝对路径，后面删除用。
            mCurrentFilePath = file.getAbsolutePath();

            mMediaRecorder = new MediaRecorder();
            //设置输出文件
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            //设置MediaRecorder的音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            //设置音频编码为AMR_NB
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            //准备结束
            isPrepared = true;

            if (mListener != null) {
                mListener.wellPreared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 随机生成一个文件名称
     *
     * @return
     */
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceLevel(int MaxLevel) {
        if (isPrepared) {
            try {//捕获异常 不能让程序因为获取不到值而崩溃
                //mMediaRecorder.getMaxAmplitude()值的范围 1~32767
                return MaxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {
            }
        }
        return 1;
    }

    public void release() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    public void cancle() {
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
        release();
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

}
