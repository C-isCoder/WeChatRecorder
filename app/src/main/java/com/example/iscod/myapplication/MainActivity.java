package com.example.iscod.myapplication;

import android.graphics.drawable.AnimationDrawable;
import android.media.*;
import android.media.projection.MediaProjectionManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import adapter.RecorderAdapter;
import model.RecorderData;
import view.AudioRecordButton;
import view.MediaManager;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private ArrayAdapter<RecorderData> mAdapter;
    private List<RecorderData> mDatas = new ArrayList<>();
    private AudioRecordButton mAudioRecordButton;

    private View mAnimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.id_listview);
        mAudioRecordButton = (AudioRecordButton) findViewById(R.id.id_record_button);
        mAudioRecordButton.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                RecorderData recorder = new RecorderData(seconds, filePath);
                mDatas.add(recorder);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(mDatas.size() - 1);

            }
        });
        mAdapter = new RecorderAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAnimView != null) {
                    mAnimView.setBackgroundResource(R.mipmap.adj);
                    mAnimView = null;
                }
                //播放动画
                mAnimView = view.findViewById(R.id.id_recorder_anim);
                mAnimView.setBackgroundResource(R.drawable.pay_anim);
                AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
                anim.start();
                //播放音频
                MediaManager.playSound(mDatas.get(position).filePath, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAnimView.setBackgroundResource(R.mipmap.adj);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        MediaManager.release();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        MediaManager.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        MediaManager.pause();
        super.onPause();
    }
}
