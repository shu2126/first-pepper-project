package com.example.myfirstrobot;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Chatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatView extends MainActivity{


    public int count = 0;

    private Button start;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

//        //사용자에게 음성을 요구하고 음성 인식기를 통해 전송하는 활동 시작
//        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        //음성 인식을위한 음성 인식기의 의도에 사용되는 여분의 키
//        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
//        //음성을 번역할 언어 설정
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        start = (Button)findViewById(R.id.sttStart_chat);
        dataList = new ArrayList<>();
        dataAdapter = new DataAdapter(this,dataList);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);

        recyclerView.setAdapter(dataAdapter);

        start.setOnClickListener(view ->{
//            insertData();
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);// 새 SpeechRecognizer를 만드는 팩토리 메서드
            mRecognizer.setRecognitionListener(listener);// 모든 콜백을 수신하는 리스너를 설정
            mRecognizer.startListening(intent);// 듣기 시작

            manager();
//            LinearLayoutManager manager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
//            manager.scrollToPosition(dataAdapter.getItemCount()-1);
//            recyclerView.setLayoutManager(manager);
//            recyclerView.setAdapter(new DataAdapter(this,dataList));
        });

    }

//    private void insertData(){
//
//        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);// 새 SpeechRecognizer를 만드는 팩토리 메서드
//        mRecognizer.setRecognitionListener(listener);// 모든 콜백을 수신하는 리스너를 설정
//        mRecognizer.startListening(intent);// 듣기 시작
//
//        dataItem = new DataItem(content,viewType);
////        dataItem.setContent(content);
////        dataItem.setViewType(viewType);
//        dataList.add(dataItem);
//
//    }

}
