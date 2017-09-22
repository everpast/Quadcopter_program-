package com.kang.receiverv1;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;

public class motor extends AppCompatActivity implements View.OnClickListener {

    private Button motorstartbt,motorstopbt;
    private TextView motorreceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motor);
        motorstartbt =(Button) this.findViewById(R.id.button2);
        motorstartbt.setOnClickListener(this);
        motorstopbt = (Button) this.findViewById(R.id.button4);
        motorstopbt.setOnClickListener(this);
        Handler motorHandler;
        ExecutorService motorThreadPool;

        motorHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        motorreceived.append(response);
                        break;
                }
            }
        };


    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.motorstartbt:
                MainActivity.
                break;
            case R.id.motorstopbt:

                break;
        }
    }



}
