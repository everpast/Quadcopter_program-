package com.kang.receiverv1;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by kang on 4/08/2017.
 */

public class Sockeybuild implements Runnable{
    private String ipaddress;
    private int port;
    private Handler handler= new Handler(Looper.getMainLooper());
    private Socket socket= null;
    public static InputStream in;
    public static OutputStream out;
    public static BufferedReader br;
    private String TAG= "message";
    public void connect(String IP, int port){
        this.ipaddress = IP;
        this.port = port;

    }

    public void run(){

       // MainActivity.txReceive.append("try connecting/n");
        try {
            ipaddress=MainActivity.hostIP;
            port=MainActivity.port;
            socket = new Socket(ipaddress, port);
            Log.d(TAG, "socket build");
            //in = socket.getInputStream();
            //out = socket.getOutputStream();
            //br = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            //Readtext.Readflag= true;
            //Readtext Threadtext = new Readtext();

            //new Thread(Threadtext).start();
            Log.d(TAG, "ToStart: start reading");



        } catch (IOException e) {
            e.printStackTrace();
            MainActivity.txReceive.append("connect fail/n");
        }
    }
}
