package com.giovannibertao.example.echo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.Thread.State;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Button btnGET, btnPOST;
    private TextView tvMsg, tvPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMsg = (TextView) findViewById(R.id.tvMessage);
        tvMsg.setText("11111111");

        tvPost = (TextView) findViewById(R.id.tvPost);
        tvPost.setText("2222222");

        // GET
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

        // POST
        btnPOST = (Button) findViewById(R.id.btnPost);
        btnPOST.setOnClickListener(v -> {
            String input = ((EditText) findViewById(R.id.etName)).getText().toString();
            input="{\"name\":\""+input+"\"}";
            AtomicReference<String> msg = new AtomicReference<>("");
            String finalInput = input;
            Thread t = new Thread(() -> msg.set(postReq(finalInput)));

            new Thread(() -> {
                String base = "Downloading";
                String var[] = {".", " .", "  ."};
                int i = 0;
                while (t.getState() != State.TERMINATED) {
                    int finalI = i;
                    runOnUiThread(() -> tvPost.setText(base + var[finalI]));
                    i = (i + 1) % 3;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(() -> tvPost.setText(msg.get()));
            }).start();
            t.start();
        });
    }

    private String getReq() {
        OkHttpClient client = new OkHttpClient();
        Request get = new Request.Builder()
                .url(Config.getURL)
                .build();
        try {
            Response response = client.newCall(get).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String postReq(String json) {
        final MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);
        Request post = new Request.Builder()
                .url(Config.postURL)
                .post(body)
                .build();
        try {
            Response response = client.newCall(post).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}