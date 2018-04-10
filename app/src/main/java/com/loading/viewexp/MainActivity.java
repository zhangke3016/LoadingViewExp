package com.loading.viewexp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.loading.viewexp.view.LoadingBallView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        final LoadingBallView loadingview = findViewById(R.id.loadingview);
//        loadingview.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                loadingview.start();
//            }
//        },3000);
    }
}
