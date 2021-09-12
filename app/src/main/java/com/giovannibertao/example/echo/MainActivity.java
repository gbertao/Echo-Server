package com.giovannibertao.example.echo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.lang.Thread.State;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Button btnGET;
    private TextView tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMsg = (TextView) findViewById(R.id.tvMessage);
        tvMsg.setText("11111111");
        btnGET = (Button) findViewById(R.id.btnSendGet);
        btnGET.setOnClickListener(v -> {
            AtomicReference<String> msg = new AtomicReference<>("");
            Thread t = new Thread(() -> msg.set(getReq()));

            new Thread(() -> {
                String base = "Downloading";
                String var[] = {".", " .", "  ."};
                int i = 0;
                while (t.getState() != State.TERMINATED) {
                    int finalI = i;
                    runOnUiThread(() -> tvMsg.setText(base + var[finalI]));
                    i = (i + 1) % 3;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(() -> tvMsg.setText(msg.get()));
            }).start();
            t.start();
        });
    }

    private String getReq() {
        OkHttpClient client = new OkHttpClient();
        Request get = new Request.Builder()
                .url(Config.URL)
                .build();
        try {
            Response response = client.newCall(get).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}