package mawashi.alex.speechrecognition;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, OnUtteranceCompletedListener{
    private TextToSpeech tts;
    protected static final int RESULT_SPEECH = 1;
    public boolean finish_speak = true;
    boolean supported;
    public int level_conversation = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tts = new TextToSpeech(this, this);
        //if(lingua.equals("ITALIANO"))
            tts.setLanguage(Locale.ITALY);
        //else
        //    tts.setLanguage(Locale.US);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS) {
            int result;
            result = tts.setLanguage(Locale.ITALY);
            /*if(lingua.equals("ITALIANO"))
                result = tts.setLanguage(Locale.ITALY);
            else
                result = tts.setLanguage(Locale.US);*/
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
                supported = false;
            } else {
                supported = true;
            }
            tts.setOnUtteranceCompletedListener(this);
        } else {
            Log.e("TTS", "Initilization Failed!");
            supported = false;
        }
        Log.d("ON-INIT COMPLETATO","ON INIT COMPLETATO");
    }		//END OF ON_INIT




    @Override
    public void onUtteranceCompleted(String utteranceId) {
        finish_speak = true;
        Log.d("A SPICCIATO DI PARLA'", "A SPICCIATO DI PARLA'");
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        Shut();
    }


    public void Shut(){
        if (tts != null) {  // controllare se serve anche isSpeaking
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        finish_speak = true;
        tts.stop();
    }

    public void VocalMode(View v){
        level_conversation = 0;
        Log.d("Bottone premuto", "Bottone vocale premuto");
        interact("Cosa vuoi che io faccia per te?");
    }

    //*******************INTERACT*****************
    public void interact(String instr){
        Log.d("Sto per parlare", "Sto per parlare");
        try{
            Speak(instr);
            Log.e("CIAO", "is speaking");
            Listen();
        }catch(Exception e){Log.e("ECCEZIONE AUDIO", "ECCEZIONE AUDIO: " + e.toString()); }
    }


    //*********************SPEAK*********************
    public void Speak(String text) {
        while(finish_speak==false){
            //DOING NOTHING
        }
        Log.d("parlo","inizio a parlare");
        HashMap<String, String> myHashAlarm = new HashMap<String, String>();
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_SYSTEM));  //AudioManager.STREAM_ALARM
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOME MESSAGE");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        finish_speak=false;
    }
    //*******************LISTEN**********************
    public void Listen() {
        while(finish_speak==false){
            //DOING NOTHING
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        intent.putExtra(RecognizerIntent.ACTION_RECOGNIZE_SPEECH,  Locale.ITALY);//"en-US");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,  Locale.ITALY);//"en-US");

        /*    if(lingua.equals("ITALIANO")){
                intent.putExtra(RecognizerIntent.ACTION_RECOGNIZE_SPEECH,  Locale.ITALY);//"en-US");
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,  Locale.ITALY);//"en-US");

            }else if(lingua.equals("ENGLISH")){
                intent.putExtra(RecognizerIntent.ACTION_RECOGNIZE_SPEECH,  Locale.US);//"en-US");
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,  Locale.US);//"en-US");
            }*/
        try {
            Log.e("CIAO", "is listening");
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getApplicationContext(),"Opps! Your device doesn't support Speech to Text", Toast.LENGTH_SHORT);
            t.show();
        }
        // I RISULTATI VENGONO GESTITI DA ON_ACTIVITY_RESULT()
    }





    ///////////////////////ON ACTIVITY RESULT///////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(level_conversation==0){
                        //RICONOSCERE LE PAROLE CHIAVE "FRIGO", "TAVOLO", "ACQUA", "MEDICINA"
                        if(text.get(0).contains("frigo")){
                            level_conversation = 1;
                            interact("Vuoi l'acqua dal frigo, confermi?");
                        }else if(text.get(0).contains("tavolo")){
                            level_conversation = 1;
                            interact("Vuoi la medicina dal tavolo, confermi?");
                        }else{
                            level_conversation = 0;
                            interact("Non ho capito cosa desideri. Potresti ripetere?");
                            //NON HO CAPITO
                        }

                    }else if(level_conversation==1){
                        //RICONOSCERE IL SI O IL NO ALLA DOMANDA DI CONFERMA
                        if(text.get(0).contains("si")||text.get(0).contains("sì")||text.get(0).contains("certo")||text.get(0).contains("certamente")||text.get(0).contains("sicuramente")||text.get(0).contains("confermo")||text.get(0).contains("assolutamente")){
                            Speak("Perfetto, vado e torno, aspettami qui");
                        }else if(text.get(0).contains("no")||text.get(0).contains("non")||text.get(0).contains("rinnego")) {
                            Speak("Vedo che hai cambiato idea. Sei un po' volubile");
                        }else if(text.get(0).contains("Marco")||text.get(0).contains("marco")){
                            Speak("Ok, la porto a Marco, il George Clunei del cetma");

                        }else{
                            level_conversation = 1;
                            //NON HO CAPITO
                            interact("Non ho capito cosa desideri. Potresti ripetere?");
                        }
                    }
                }

            }
        }
    }


    ///////////////////////END ON ACTIVITY RESULT///////////////////////////////////////////////////

}
