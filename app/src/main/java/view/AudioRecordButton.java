package view;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.iscod.myapplication.R;

/**
 * Created by iscod on 2016/3/7.
 */
public class AudioRecordButton extends Button implements AudioManager.AudioStateListener {
    private static float scale;
    //初始化 将50pd转换成像素px
    private static final int DISTANCE_Y_CANCLE = (int) (50 * scale + 0.5f);
    //private static final int DISTANCE_Y_CANCLE = 50;
    //设置butto的三个状态常量
    private static final int STATE_NORMAL = 1;//正常
    private static final int STATE_RECORDING = 2;//录音
    private static final int STATE_WANT_TO_CANCLE = 3;//取消

    private int mCurState = STATE_NORMAL;
    //已经开始录音
    private boolean isRecording = false;

    private AudioManager mAudioManager;

    private DialogManager mDialogManager;

    private float mTime;
    //是否触发longclick
    private boolean mReady;

    public AudioRecordButton(Context context) {
        //重写构造方法，一个参数的构造方法默认（super改成this）调用，两个参数的构造方法。
        this(context, null);
        scale = context.getResources().getDisplayMetrics().density;
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(context);
        String dir = Environment.getExternalStorageDirectory() + "/wechat_recorder_audio";
        //单例初始化
        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(this);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    /**
     * 录音完成后的回调
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }

    private AudioFinishRecorderListener mListener;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        mListener = listener;
    }

    /**
     * 获取音量大小的Runnable
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;//录音记时
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private static final int MSG_AUDIO_PREPARED = 0x110;
    private static final int MSG_VOICE_CHANGED = 0x111;
    private static final int MSG_DIALOG_DIMISS = 0x112;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED: {
                    //TODO 显示Dialog 显示是在Audio end prepared以后
                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                }
                case MSG_VOICE_CHANGED: {
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                }
                case MSG_DIALOG_DIMISS: {
                    mDialogManager.dimissDialog();
                    break;
                }

            }
        }
    };

    @Override
    public void wellPreared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (isRecording)
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    changeState(STATE_RECORDING);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    //根据x，y的坐标判断是否想要取消。
                    if (wantToCancle(x, y)) {
                        changeState(STATE_WANT_TO_CANCLE);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if (!mReady) {
                        reset();
                        return super.onTouchEvent(event);
                    }
                    if (!isRecording || mTime < 0.6f) {
                        mDialogManager.tooShort();
                        mAudioManager.cancle();
                        //延时1.3秒发送 message 主要为了让Dialog显示1.3秒再消失。
                        mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
                    } else if (mCurState == STATE_RECORDING) {//正常录制结束
                        mDialogManager.dimissDialog();
                        //释放录音资源
                        mAudioManager.release();

                        //回调方法传值到Activity
                        if (mListener != null) {
                            mListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                        }

                    } else if (mCurState == STATE_WANT_TO_CANCLE) {
                        //cancle
                        mDialogManager.dimissDialog();
                        mAudioManager.cancle();
                    }
                    reset();
                }
            }
        return super.onTouchEvent(event);
    }

    /**
     * 恢复状态及标志位
     */
    private void reset() {
        mTime = 0;
        mReady = false;
        isRecording = false;
        changeState(STATE_NORMAL);
    }

    private boolean wantToCancle(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }
        if (y < -DISTANCE_Y_CANCLE || y > getHeight() + DISTANCE_Y_CANCLE) {
            return true;
        }
        return false;
    }

    private void changeState(int state) {
        if (mCurState != state) {
            mCurState = state;
            switch (state) {
                case STATE_NORMAL: {
                    setBackgroundResource(R.drawable.btn_record_normal);
                    setText(R.string.str_record_normal);
                    break;
                }
                case STATE_RECORDING: {
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.str_record_recording);
                    if (isRecording) {
                        mDialogManager.recording();
                    }
                    break;
                }
                case STATE_WANT_TO_CANCLE: {
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.str_record_want_cancl);
                    mDialogManager.wantToCancle();
                    break;
                }
            }
        }
    }


}
