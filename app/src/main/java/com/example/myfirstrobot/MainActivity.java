package com.example.myfirstrobot;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.aldebaran.qi.sdk.object.human.Human;
import com.aldebaran.qi.sdk.object.humanawareness.EngageHuman;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;

import java.util.ArrayList;
import java.util.HashMap;

import static android.os.SystemClock.sleep;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {
    private TextView text, pepper_textView, human_textView;
    private Button sayButton, sttStartButton, targetView;

    private Chat chat;
    private Chatbot myChatBot;

    //STT : 말하는거 텍스트로 옮김 -> 텍스트로 옮겨진 것을 읽고 답할 수 있게 테스트 예정
    final int PERMISSION = 1;
    protected Intent intent;
    protected SpeechRecognizer mRecognizer;

    //TTS : 텍스트를 음성으로
    protected TextToSpeech tts;

    public ArrayList<DataItem> dataList;
    public DataItem dataItem;
    public DataAdapter dataAdapter;
    public RecyclerView recyclerView;
    public LinearLayoutManager manager;

    //    //pepper 애니메이션
    private MediaPlayer mediaPlayer = null;
    private Animate dog, elephant, hello, question, dance, hand, hand2, miss, confused,self,shy,dance2, music, music2;
    public String content;
    public int viewType;

//   사람인식
//    private HumanAwareness humanAwareness = null;
//    private Human engagedHuman;
//    private Human nextHuman;
//    final int STATE_INITIALIZING = 0;
//    final int STATE_ALONE = 1;
//    final int STATE_ENGAGING = 2;
//    private int state = STATE_INITIALIZING;
    private QiContext qi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sayButton = (Button) findViewById(R.id.sayButton);
        sttStartButton = (Button) findViewById(R.id.sttStart);
        text = (TextView) findViewById(R.id.sttText);
        targetView = (Button) findViewById(R.id.button);

        pepper_textView = (TextView)findViewById(R.id.pepper_msg);
        human_textView = (TextView) findViewById(R.id.human_msg);
        manager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);

        //RobotLifecycleCallbacks Register(콜백등록)
        QiSDK.register(this, this);

        if ( Build.VERSION.SDK_INT >= 23 ){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }

        //사용자에게 음성을 요구하고 음성 인식기를 통해 전송하는 활동 시작
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //음성 인식을위한 음성 인식기의 의도에 사용되는 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        //음성을 번역할 언어 설정
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");


        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status != TextToSpeech.ERROR){
                    //음성 톤
//                    tts.setPitch(1.4f);
                    //읽는 속도
                    tts.setSpeechRate(0.7f); //1.0f -> 1배속
//                    언어
                    tts.setLanguage(java.util.Locale.KOREAN);

                }
            }
        });

    }

    //음성 듣기
    protected RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            // 사용자가 말하기 시작할 준비가되면 호출
            toast("음성 인식 준비 완료");
        }

        @Override
        public void onBeginningOfSpeech() {
            // 사용자가 말하기 시작했을 때 호출
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // 입력받는 소리의 크기를 알려줍니다.
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // 사용자가 말을 시작하고 인식이 된 단어를 buffer에 담음
        }

        @Override
        public void onEndOfSpeech() {
            // 사용자가 말하기를 중지하면 호출
        }

        @Override
        public void onError(int error) {
            // 네트워크 또는 인식 오류가 발생했을 때 호출

            String message="";
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류";
                    break;
            }
            toast("에러 발생 : "+message);

        }

        @Override  // 인식 결과가 준비되면 호출
        public void onResults(Bundle results) {

            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for(int i=0; i< matches.size();i++){
                text.setText(matches.get(i));
            }

            String imSay = text.getText().toString();
            String name ="";
            String nameText = "";

//            content = imSay;
//            viewType = Position.ViewType.RIGHT_CONTENT;
            human_textView.setText(imSay);

            dataItem = new DataItem(imSay,Position.ViewType.RIGHT_CONTENT);
            dataList.add(dataItem);
            dataAdapter.notifyDataSetChanged();
            manager();

            if(imSay.contains("안녕") || imSay.contains("반가워")){
                hello.async().run();
                if(imSay.contains("나는")) {
                    if(imSay.contains("야"))name = imSay.substring(imSay.indexOf("는")+2, imSay.indexOf("야"));
                    else if(imSay.contains("이야"))name = imSay.substring(imSay.indexOf("는")+2, imSay.indexOf("이"));
                    nameText = "안녕 "+ name + ", 멋진 이름이에요! 만나서 반가워요.";
                    googleTts(nameText);
                }
                else if(imSay.contains("이름은")){
                    if(imSay.contains("야"))name = imSay.substring(imSay.indexOf("은")+2, imSay.indexOf("야"));
                    else if(imSay.contains("이야"))name = imSay.substring(imSay.indexOf("은")+2, imSay.indexOf("이"));
                    nameText = "안녕 "+ name + ", 너무 멋진 이름이에요!";
                    googleTts(nameText);
                }
                else if(imSay.contains(("하세요")))googleTts("안녕하세요? 만나서 반갑습니다!");
                else if(imSay.contains(("반가워")))googleTts("저도 반가워요.");
                else googleTts("안녕 반가워, 난 페퍼라고 해.");
            }
            else if(imSay.contains("고마워") || imSay.contains("감사")){
                shy.async().run();
                googleTts("칭찬해주셔서 감사합니다. 부끄럽네요.");
            }
            else if(imSay.contains("이름")){
                hand.async().run();
                if(imSay.contains("이름이") || imSay.contains("뭐야"))googleTts("제 이름은 페퍼예요!");
                else if(imSay.contains("이름은")){
                    if(imSay.contains("야"))name = imSay.substring(imSay.indexOf("은")+2, imSay.indexOf("야"));
                    else if(imSay.contains("이야"))name = imSay.substring(imSay.indexOf("은")+2, imSay.indexOf("이"));
                    nameText = "안녕 "+ name + ", 너무 멋진 이름이에요!";
                    googleTts(nameText);
                }
            }
            else if(imSay.contains("페퍼")){
                hand.async().run();
                if(imSay.contains(("야")))googleTts("네 저 여기 있어요.");
                else if(imSay.contains("이놈") || imSay.contains("이 놈"))googleTts("저를 페퍼라고 불러주세요.");
                else googleTts("저를 페퍼라고 불러주세요.");
            }
            else if(imSay.contains("좋아하니") || imSay.contains("좋아해")){
                hand2.async().run();
                if(imSay.contains("음식")) googleTts("저는 전기를 좋아하며, 전기를 먹습니다. 하하하하하!");
                else googleTts("저는 여러분을 좋아해요!");
            }
            else if(imSay.contains("기분이") && imSay.contains("어때")){
                question.async().run();
                googleTts("저는 지금 기분이 아주 좋아요!");
            }
            else if(imSay.contains("사랑해")){
                shy.async().run();
                googleTts("저도 많이 사랑해요!");
            }
            else if(imSay.contains("선물") || imSay.contains("주려고") || imSay.contains("준비")){
                self.async().run();
                googleTts("어머나 너무 감사해요.");
            }
            else if(imSay.contains("할수있어") || imSay.contains("할 수 있어") || imSay.contains("할 수 있니") || imSay.contains("할수있니")){
                question.async().run();
                googleTts("저는 춤, 노래, 강아지와 코끼리를 흉내 낼 수 있어요.");
            }
            else if(imSay.contains("잘하는게") || imSay.contains("잘 하는게") || imSay.contains("특기") || imSay.contains("장기")){
                question.async().run();
                googleTts("저는 춤, 노래, 강아지와 코끼리를 흉내 낼 수 있어요.");
            }
            else if(imSay.contains("웃어")){
                question.async().run();
                googleTts("하하하하하");
            }
            else if(imSay.contains("몇살") || imSay.contains("몇 살") ||imSay.contains("나이")){
                self.async().run();
                googleTts("저는 태어난지 얼마되지 않았어요!");

           } else if(imSay.contains("남자") || imSay.contains("여자") ||imSay.contains("성별")){
                hand.async().run();
                googleTts("로봇은 성별이 없어요.");
            }
            else if(imSay.contains("직업")){
                hand2.async().run();
                googleTts("저는, 어디서든 찾아오신 손님을 안내해 드리는 안내 로봇이에요.");
            }
            else if(imSay.contains("보고싶")){
                hand2.async().run();
                googleTts("저도 많이 보고싶었어요.");
            }
            else if(imSay.contains("무슨 일")|| imSay.contains("무슨일")){
                hand2.async().run();
                googleTts("저는, 사람들이 하기 싫은 일들을 대신해주는 인공지능 로봇이에요.");
            }
            else if(imSay.contains("하는 일")|| imSay.contains("하는일")){
                self.async().run();
                googleTts("저는, 사람들을 도우며! 함께 일하는! 안내 로봇이에요!");
            }
            else if(imSay.contains("뭐하는")|| imSay.contains("뭐 하는")){
                self.async().run();
                googleTts("저는, 사람을 닮은 휴머노이드 안내 로봇이에요.");
            }
            else if(imSay.contains("가족")){
                hand2.async().run();
                googleTts("오늘부터 여러분 모두가 저의 가족입니다.");
            }
            else if(imSay.contains("친구")){
                confused.async().run();
                if(imSay.contains("우리")){
                    if(imSay.contains("하자"))googleTts("그래 좋아! 친구!");
                    else if(imSay.contains("할까") || imSay.contains("할 까"))googleTts("좋아요! 우리 친구해요!");
                    else googleTts("좋아요! 친구해요!");
                }
                else{
                    googleTts("좋아요! 친구!");
                }
            }
            else if(imSay.contains("바보")){
                shy.async().run();
                googleTts("제가 아직 공부가 부족해서 많이 서투릅니다, 이해를 부탁드려요!");
            }
            else if(imSay.contains("똑똑")){
                question.async().run();
                googleTts("똑똑해지려고! 매일매일 공부하고 있어요!");
            }
            else if(imSay.contains("코끼리")){
                googleTts("코끼리를 흉내내 볼게요.");
                sleep(3000);
                elephant.async().run();
            }
            else if(imSay.contains("불러")){
                if(imSay.contains("동요"))googleTts("동요를 불러 볼게요.");
                else googleTts("노래를 불러 볼게요.");
                sleep(3000);

                if(imSay.contains("동요"))music.async().run();
                else music2.async().run();
            }
            else if(imSay.contains("강아지") || imSay.contains("개")){
                googleTts("강아지를 흉내내 볼게요.");
                sleep(3000);
                dog.async().run();
            }
            else if(imSay.contains("춤")){
                googleTts("제 춤실력 한번 봐보실래요?");
                sleep(3000);
                if(imSay.contains("다른") || imSay.contains("따른"))dance2.async().run();
                else dance.async().run();
            }
            else if(imSay.contains("날씨") && imSay.contains("어때")){
                confused.async().run();
                googleTts("나가보지 못해서 잘 모르겠어요.");
            }
            else if(imSay.contains("몇 살이니") || imSay.contains("몇 살이야") || imSay.contains("몇살이니") || imSay.contains("몇살이야")){
                question.async().run();
                googleTts("저도 제 나이를 모르겠어요.");
            }
            else{
                miss.async().run();
                googleTts("죄송해요 제대로 듣지 못했어요.");
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            // 부분 인식 결과를 사용할 수 있을 때 호출
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // 향후 이벤트를 추가하기 위해 예약
            toast("이벤트 추가 부분");
        }
    };

    private void googleTts(String str){

//        content = str;
//        viewType = Position.ViewType.LEFT_CONTENT;
        dataItem = new DataItem(str,Position.ViewType.LEFT_CONTENT);
        dataList.add(dataItem);
        dataAdapter.notifyDataSetChanged();
        pepper_textView.setText(str);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ttsGreater(str);
        }else{
            ttsUnder(str);
        }

    }


    //타겟 버전 미만
    @SuppressWarnings("deprecation")
    private void ttsUnder(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    //타겟 버전 이상
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    protected void onDestroy(){

        //RobotLifecycleCallbacks Unregister(콜백등록취소)
        QiSDK.unregister(this,this);
        super.onDestroy();

        tts.stop();
        tts.shutdown();
    }

    //로봇의 포커스 얻었을때 호출됨 -> 페퍼 동작 실행
    @Override
    public void onRobotFocusGained(QiContext qiContext){
        qi = qiContext;
        //The robot focus is gained (로봇초점)
        dog = action(qiContext,R.raw.dog_sound,R.raw.dog_a001);
        elephant = action(qiContext,R.raw.elephant_sound,R.raw.elephant_a001);
        hello = action(qiContext,0,R.raw.hello_a009);
        question = action(qiContext,0,R.raw.question_both_hand_a002);
        dance = action(qiContext,R.raw.dance_sound,R.raw.dance_b005);
        hand = action(qiContext,0,R.raw.enumeration_both_hand_a003);
        hand2 = action(qiContext,0,R.raw.spread_both_hands_b001);
        miss = action(qiContext,0,R.raw.thinking_a002);
        confused = action(qiContext,0,R.raw.confused_a001);
        self = action(qiContext,0,R.raw.show_self_a001);
        shy = action(qiContext,0,R.raw.shy_b002);
        dance2 = action(qiContext,R.raw.dance_sound2,R.raw.dance_b001);

        music = action(qiContext,R.raw.song,R.raw.spread_both_hands_so_a003);
        music2 = action(qiContext,R.raw.frozen2_sound,R.raw.spread_both_hands_so_a003);

//        question.async().run();
//        googleTts("버튼을 눌러주세요. 그렇지 않으면 들을 수가 없어요. 감사합니다.");
        //사람인식
        //        stop = action(qiContext,0,0);

//        humanAwareness = qiContext.getHumanAwareness();
//        humanAwareness.addOnRecommendedHumanToEngageChangedListener(this::onRecommendedHuman);
//        onRecommendedHuman(humanAwareness.getRecommendedHumanToEngage());

//        question.async().run();


        //버튼 누르면 인사
        buttonClickListener(qiContext);

    }
    //사람인식
////사람인식
//    public void onRecommendedHuman(Human human) {
//        if (human != null) {
//            setState(STATE_ENGAGING);
//            engagedHuman = human;
//            Log.i("TAG", "참여 시작");
//
//            toast("참여 시작");
//            engagedHuman = null;
//            setState(STATE_ALONE);
//            onRecommendedHuman(nextHuman);
//        }else{
//                nextHuman = human; // Who do I talk to if this guy leaves?
//            }
//        }
//
////로봇 상태
//    public void setState(int newState) {
//
//        // Set state
//        state = newState;
//        String stateName = "UNKNOWN " + newState;
//        switch(state) {
//            case STATE_ALONE:
//                stateName = "Alone";
//                break;
//            case STATE_ENGAGING:
//                stateName = "Engaging";
//                break;
//            case STATE_INITIALIZING:
//                stateName = "Initializing";
//                break;
//        }
//
//        runOnUiThread(() -> {
////            Button button = findViewById(R.id.sayhello);
////            button.setText(finalStateName);
//        });
//
//    }

//    private void recognizer(){
//        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);// 새 SpeechRecognizer를 만드는 팩토리 메서드
//        mRecognizer.setRecognitionListener(listener);// 모든 콜백을 수신하는 리스너를 설정
//        mRecognizer.startListening(intent);// 듣기 시작
//    }

    public void manager(){
        manager.scrollToPosition(dataAdapter.getItemCount()-1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(new DataAdapter(this,dataList));
    }

    private Animate action(QiContext qiContext, int sound, int mimic){
        Animation animation = AnimationBuilder.with(qiContext)
                .withResources(mimic)
                .build();
        Animate animate = AnimateBuilder.with(qiContext)
                .withAnimation(animation)
                .build();

        if(sound != 0){
            animate.addOnStartedListener(new Animate.OnStartedListener() {
                @Override
                public void onStarted() {
                    mediaPlayer = MediaPlayer.create(qiContext,sound);
                    mediaPlayer.start();
                }
            });
        }
        return animate;
    }

    //로봇의 초점을 잃음 -> 페퍼 작업 실행 불가
    @Override
    public void onRobotFocusLost(){
        //The robot focus is Lost (초점 사라짐)
        if(chat != null){
            chat.removeAllOnStartedListeners();
        }
    }

    @Override
    public void onRobotFocusRefused(String reason){
        //The robot focus is refused (초점거부)
    }

    public void buttonClickListener(QiContext qiContext){
        sayButton.setOnClickListener(view -> {
            SayActivity sayActivity = new SayActivity();

           Say say = sayActivity.testHello(qiContext);
           say.async().run();
        });

        sttStartButton.setOnClickListener(view -> {
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);// 새 SpeechRecognizer를 만드는 팩토리 메서드
            mRecognizer.setRecognitionListener(listener);// 모든 콜백을 수신하는 리스너를 설정
            mRecognizer.startListening(intent);// 듣기 시작
        });

        targetView.setOnClickListener(view->{
            Intent intent = new Intent(getApplicationContext(), ChatView.class);
            startActivity(intent);
        });

    }


    private void toast(String str){
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }


}
