package com.kang.receiverv1;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edIP, edPort;
    private Button btStart, btSensor,btMotor;
    public static TextView txReceive;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static Handler mMainHandler;
    private Handler handler2 = new Handler(Looper.getMainLooper());
    public static InputStream in;
    public static OutputStream out;
    public BufferedReader intext;
    public BufferedInputStream inbuff;
    public  Sockeybuild client = new Sockeybuild();
    public boolean runflag;
    public  static String hostIP, receivedtext, receivedtext_before;
    public static int port;
    public static Socket socket= null;
    static InputStreamReader isr ;
    static BufferedReader br ;
    static String response;
    // 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private static ExecutorService mThreadPool;
    private static String  TAG= "message text";

    public static Handler getmMainHandler() {
        return mMainHandler;
    }

    public static void setmMainHandler(Handler mMainHandler) {
        MainActivity.mMainHandler = mMainHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btSensor = (Button) this.findViewById(R.id.button);
        btSensor.setOnClickListener(this);
        btMotor =(Button) this.findViewById(R.id.button2);
        btMotor.setOnClickListener(this);
        btStart = (Button) this.findViewById(R.id.button4);
        btStart.setOnClickListener(this);
        edIP = (EditText) this.findViewById(R.id.editText);
        edPort =(EditText) this.findViewById(R.id.editText2);
        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();

        txReceive = (TextView) this.findViewById(R.id.tx_receive);
        txReceive.setOnClickListener(this);


        dealmessage(txReceive);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button:
                Tosensor();
                break;
            case R.id.button2:
                Tomotor();
                break;
            case R.id.button4:
                Log.d(TAG, "onClick: try connect server button");
                ToStart();
                break;
            case R.id.tx_receive:
                clear();
                break;
        }

    }
    void ToStart() {

                hostIP = edIP.getText().toString();
                port = Integer.parseInt(edPort.getText().toString());
                Log.d(TAG, "ToStart:  try connect server");

                buildsocket();
    }

    void Tosensor(){
        // 利用线程池直接开启一个线程 & 执行该线程
        sendstr("sensor");
    }




    void Tomotor(){
        //Intent motorswitch = new Intent(this, motor.class);
        sendstr("motor");
        //startActivity(motorswitch);
    }
    void clear(){

    }
    public static void readsocket(){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    // 步骤1：创建输入流对象InputStream
                    in = socket.getInputStream();

                    // 步骤2：创建输入流读取器对象 并传入输入流对象
                    // 该对象作用：获取服务器返回的数据
                    isr = new InputStreamReader(in);
                    br = new BufferedReader(isr);

                    // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
                    while(true){
                        response = br.readLine();
                        Log.d(TAG, "run: Read sth");
                        // 步骤4:通知主线程,将接收的消息显示到界面
                        Message msg = Message.obtain();
                        msg.what = 0;
                        mMainHandler.sendMessage(msg);}

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public static void buildsocket(){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {

                    // 创建Socket对象 & 指定服务端的IP 及 端口号
                    socket = new Socket(hostIP, port);

                    // 判断客户端和服务器是否连接成功
                    Log.d(TAG, "run:"+ (socket.isConnected()));
                    readsocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public static void sendstr(final String text){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    // 步骤1：从Socket 获得输出流对象OutputStream
                    // 该对象作用：发送数据
                    out = socket.getOutputStream();

                    // 步骤2：写入需要发送的数据到输出流对象中
                    Log.d(TAG, "run: send " + text + "to server");
                    out.write((text + "\n").getBytes("utf-8"));
                    Log.d(TAG, "run: send " + text + "to server success");
                    // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞

                    // 步骤3：发送数据到服务端
                    out.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public static void dealmessage(final TextView textview){
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        textview.append(response);
                        break;
                }
            }
        };
    }
    }

