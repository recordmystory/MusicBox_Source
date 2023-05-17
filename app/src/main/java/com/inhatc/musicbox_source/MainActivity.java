package com.inhatc.musicbox_source;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Intent objIntent;
    private SpeechRecognizer mRecognizer;
    private ImageButton btnImgRecording;
    private TextView txtSTT, txtTitle;
    private ImageView objImageView, objCTRLImageView;
    private MediaPlayer objMP;
    private boolean bRecording = false;
    private boolean bVoiceControl = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnImgRecording = (ImageButton) findViewById(R.id.btnImageRecording);
        txtSTT = (TextView) findViewById(R.id.txtShowText);
        objImageView = (ImageView) findViewById(R.id.imageView);
        objCTRLImageView = (ImageView) findViewById(R.id.imageView_Control);
        objCTRLImageView.setVisibility(View.INVISIBLE);
        CheckPermission();

        objIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        objIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        objIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        btnImgRecording.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        txtSTT.setText("[ Music Box ]");
        switch (view.getId()) {
            case R.id.btnImageRecording:
                if (!bRecording) {
                    Start_Record();
                    Toast.makeText(getApplicationContext(), "Set Voice Control...", Toast.LENGTH_SHORT).show();
                } else {
                    Stop_Record();
                }
                break;
            default:
                break;
        }
    }

    RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {
            String strMessage;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    strMessage = "Audio error...";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    strMessage = "Client error...";
                    return;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    strMessage = "Permission error...";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    strMessage = "Network error...";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    strMessage = "Network Timeout...";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    strMessage = "Not found error...";
                    if (bRecording) Start_Record();
                    return;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    strMessage = "Recognizer busy...";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    strMessage = "Server error...";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    strMessage = "Speech timeout...";
                    break;
                default:
                    strMessage = "Unknown error...";
                    break;
            }
            Toast.makeText(getApplicationContext(), "eMessage: " + strMessage, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> arrayList_Result;
            arrayList_Result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            String[] strResult = new String[arrayList_Result.size()];
            arrayList_Result.toArray(strResult);

            if (!bVoiceControl) {
                mMusic_Box(strResult[0]);
            } else {
                mVoice_Control(strResult[0]);
            }

            mRecognizer.startListening(objIntent);
        }

        @Override
        public void onPartialResults(Bundle Bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    public void mMusic_Box(String strSongTitle) {
        switch (strSongTitle) {
            case "토끼":
                txtSTT.setText("[ Hare ]");
                objImageView.setImageResource(R.drawable.img_hare);
                objMP = MediaPlayer.create(MainActivity.this, R.raw.hare);
                bVoiceControl = true;
                break;
            case "코끼리":
                txtSTT.setText("[ Elephant ]");
                objImageView.setImageResource(R.drawable.img_elephant);
                objMP = MediaPlayer.create(MainActivity.this, R.raw.elephant);
                bVoiceControl = true;
                break;
            case "사랑할수록":
                txtSTT.setText("[ 사랑할수록 ]");
                objImageView.setImageResource(R.drawable.img_love);
                objMP = MediaPlayer.create(MainActivity.this, R.raw.loving);
                bVoiceControl = true;
                break;
            default:
                break;
        }
        if (bVoiceControl) {
            objCTRLImageView.setVisibility(View.VISIBLE);
            objCTRLImageView.setImageResource(R.drawable.img_play);
        }
    }

    public void mVoice_Control(String strComand) {
        switch (strComand) {
            case "플레이":
                objCTRLImageView.setImageResource(R.drawable.img_play);
                objMP.start();
                break;
            case "포즈":
                objCTRLImageView.setImageResource(R.drawable.img_pause);
                if (objMP.isPlaying()) objMP.pause();
                break;
            case "스톱":
                objCTRLImageView.setImageResource(R.drawable.img_stop);
                if (objMP.isPlaying()) objMP.stop();
                break;
            case "종료":
                txtSTT.setText("[ Music Box ]");
                objCTRLImageView.setImageResource(R.drawable.img_musicbox);
                objCTRLImageView.setVisibility(View.INVISIBLE);
                objMP.release();
                bVoiceControl = false;
                Stop_Record();
                break;
            default:
                break;
        }
    }

    public void Start_Record() {
        bRecording = true;
        btnImgRecording.setImageResource(R.drawable.img_microphone);
        txtTitle.setText("[ Set Voice Control ]");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(recognitionListener);
        mRecognizer.startListening(objIntent);
    }

    public void Stop_Record() {
        bRecording = false;
        btnImgRecording.setImageResource(R.drawable.img_microphone);
        txtTitle.setText("Click the MIC and request a song...");
        mRecognizer.stopListening();
        Toast.makeText(getApplicationContext(), "Reset Voice Control...", Toast.LENGTH_SHORT).show();
    }

    public void CheckPermission() {
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET, android.Manifest.permission.RECORD_AUDIO}, 1);
        }
    }
}