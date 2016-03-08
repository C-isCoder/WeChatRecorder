package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.example.iscod.myapplication.R;

/**
 * Created by iscod on 2016/3/7.
 */
public class AudioRecordButton extends Button {
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

    public AudioRecordButton(Context context) {
        //重写构造方法，一个参数的构造方法默认（super改成this）调用，两个参数的构造方法。
        this(context, null);
        scale = context.getResources().getDisplayMetrics().density;
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        //TODO 测试
        isRecording = true;
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
                    if (mCurState == STATE_RECORDING) {
                        //释放录音资源
                        //回调方法传值到Activity
                    } else if (mCurState == STATE_WANT_TO_CANCLE) {
                        //cancle

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

                    }
                    break;
                }
                case STATE_WANT_TO_CANCLE: {
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.str_record_want_cancl);
                    break;
                }
            }
        }
    }

}
