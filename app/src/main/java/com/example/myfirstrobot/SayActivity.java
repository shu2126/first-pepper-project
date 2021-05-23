package com.example.myfirstrobot;

import android.util.Log;

import com.aldebaran.qi.Consumer;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.Qi;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.BodyLanguageOption;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.object.locale.Language;
import com.aldebaran.qi.sdk.object.locale.Locale;
import com.aldebaran.qi.sdk.object.locale.Region;

import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;
import static com.aldebaran.qi.sdk.object.locale.Language.*;

public class SayActivity {

    private com.aldebaran.qi.sdk.object.locale.Locale qiLocale;

    public Say testHello(QiContext qiContext){

////        동기식
//        Say say = SayBuilder.with(qiContext) //Create the builder with the context (context 사용 빌더 만듦)
//                .withText("Hello Suji!!") //Set the text to say(뭐라고 말할 것인지 set)
//                .build(); //Build the say action
//        say.run(); //실행
//        return say;


//        비동기식
        Phrase phrase = new Phrase("Hello");
        com.aldebaran.qi.sdk.object.locale.Locale locale = new Locale(Language.KOREAN,Region.REPUBLIC_OF_KOREA);

        Future<Say> say = SayBuilder.with(qiContext)
                .withPhrase(phrase)
                .withBodyLanguageOption(BodyLanguageOption.NEUTRAL)
                .buildAsync();

        Say sayAsync = null;
        try {
            sayAsync = say.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return sayAsync;
    }

    private static com.aldebaran.qi.sdk.object.locale.Locale getQiLocale(java.util.Locale locale) {
        com.aldebaran.qi.sdk.object.locale.Locale qiLocale;
        String strLocale = locale.toString();
       if (strLocale.contains("ko")) {
            qiLocale = new com.aldebaran.qi.sdk.object.locale.Locale(KOREAN, Region.REPUBLIC_OF_KOREA);
        } else {
            qiLocale = new com.aldebaran.qi.sdk.object.locale.Locale(ENGLISH, Region.UNITED_STATES);
        }
        return qiLocale;
    }
}
