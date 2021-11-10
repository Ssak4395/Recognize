package com.example.recognize;

import android.app.Activity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Locale;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private TextView rtv;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        rtv = findViewById(R.id.result);

        String result = getIntent().getExtras().getString("result");
        String type = getIntent().getExtras().getString("type");
        if (result != null) {
            StringBuilder sb  = new StringBuilder();
            Map data = (Map) JSON.parse(result);
            Object words_result = data.get("words_result");
            switch (type) {
                case "universal":
                    getJsonArrayString(sb, (JSONArray) words_result);
                    break;
                case "card":
                    getJSONObjectString(sb, (JSONObject) words_result);
                    break;
                case "certificate":
                    for (Map.Entry<String, Object> entry : ((JSONObject) words_result).entrySet()) {
                        sb.append(entry.getKey() + ":" + ((JSONObject) entry.getValue()).get("words")).append("\n");
                    }
                    break;
                case "handwrite":
                    getJsonArrayString(sb, (JSONArray) words_result);
                    break;
                case "netImage":
                    getJsonArrayString(sb, (JSONArray) words_result);
                    break;
                default:
                    break;
            }
            //Display of identification results
            rtv.setText(sb.toString());
            speech(sb.toString());
        }

    }

    private void speech(String toString) {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
//                int result = tts.setLanguage(Locale.E);
//                if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
//                        && result != TextToSpeech.LANG_AVAILABLE){
//                    Toast.makeText(ResultActivity.this, "TTS暂时不支持这种语音的朗读！",
//                            Toast.LENGTH_SHORT).show();
//                }
                tts.speak(toString,TextToSpeech.QUEUE_ADD,null);
            }
        });
    }

    private void getJSONObjectString(StringBuilder sb, JSONObject words_result) {
        for (Map.Entry<String, Object> entry : words_result.entrySet()) {
            sb.append(entry.getKey() + ":" + entry.getValue()).append("\n");
        }
    }

    private void getJsonArrayString(StringBuilder sb, JSONArray words_result) {
        for (Object o : words_result) {
            sb.append(((Map) o).get("words")).append("\n");
        }
    }
}
