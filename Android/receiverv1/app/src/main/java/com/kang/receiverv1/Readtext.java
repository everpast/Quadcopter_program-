package com.kang.receiverv1;

import java.io.IOException;

/**
 * Created by kang on 7/08/2017.
 */

public class Readtext implements Runnable {
    public static boolean Readflag;
    public static String receivedtext;
    @Override
    public void run() {
        while ( true){
            try {
                receivedtext = Sockeybuild.br.readLine();
                MainActivity.txReceive.append(receivedtext);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
