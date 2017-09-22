package usyd.Balance_control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.DataBits;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.StopBits;
import com.pi4j.util.Console;

import de.buschbaum.java.pathfinder.device.mpu6050.Mpu6050Controller;
import usyd.Balance_control.mpusensor;
/**
 * Hello world!
 *
 */
public class App 
{
    public static int throttle=1000;
    public static int wifimode;
    public static InputStream in;
    public static OutputStream out;
    public static InputStreamReader isr ;
    public static BufferedReader br ;
    private static Serial serial;
    public static void main( String[] args )
    {
        //start serial connect
        serial = SerialFactory.createInstance();

        serial.addListener(new SerialDataEventListener() {
         
            public void dataReceived(SerialDataEvent event) {
///////////no response when received message//////
                
              /*  try {
                  
                    System.out.println("[ASCII DATA] " + event.getAsciiString());
                  
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        });

        try {
         
            SerialConfig config = new SerialConfig();

            config.device("/dev/ttyACM0")
                  .baud(Baud._38400)
                  .dataBits(DataBits._8)
                  .parity(Parity.NONE)
                  .stopBits(StopBits._1)
                  .flowControl(FlowControl.NONE);

            serial.open(config);
            System.out.println("==>>>>>>>serial start");

        }
        catch(IOException ex) {
            System.out.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            return;
        } 
        // start mpusensor Thread////////////////////////////////////////////
        mpusensor mpu6050 = new mpusensor();
        Thread thread_mpu = new Thread(mpu6050);
        thread_mpu.start();   
        ///////////////keyboard control
        ///////keyboard control/////////////////////
           
                
                
                final Console console = new Console();
                console.title("<-- The Pi4J Project -->", "SoftPWM Example (Software-driven PWM Emulation)");
                console.promptForExit();
                console.println(" press \"all\" to run together");
                console.println(" press \"mt1\" to run motor 1");
                console.println(" press \"mt2\" to run motor 1");
                console.println(" press \"mt3\" to run motor 1");
                console.println(" press \"mt4\" to run motor 1");
                console.println(" press \"exit\" to exit");

                while(true) {
                    String mode_set = System.console().readLine();
                    if(mode_set.equals("all")) {
                        sendtoserial(1100,1100,1100,1100);
                        showvibration();
                    } 
                  
                    if(mode_set.equals("mt1")) {
                            sendtoserial(1100,1000,1000,1000);
                            showvibration();
                    }       
                    if(mode_set.equals("mt2")) {
                        sendtoserial(1000,1100,1000,1000);
                        showvibration();
                    }  
                    if(mode_set.equals("mt3")) {
                        sendtoserial(1000,1000,1100,1000);
                        showvibration();
                    }  
                    if(mode_set.equals("mt4")) {
                        sendtoserial(1000,1000,1000,1100);
                        showvibration();
                    }  
                    if(mode_set.equals("exit")) {
                            sendtoserial(1000,1000,1000,1000);
                            
                    }
                    
                    }
    }
    private static void sendtoserial(int t1,int t2, int t3 , int t4) {
        try {
         serial.writeln(t1+","+t2+","+t3+","+t4);
     } catch (IllegalStateException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
     } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
     }
    } 
    private static double calvibration() {
        double vibration=Math.pow(Mpu6050Controller.filteredAngleX, 2)+Math.pow(Mpu6050Controller.filteredAngleY, 2);
        return vibration;
    }
    private static void showvibration() {
        new Thread(new Runnable() {
            public void run() {
            double error = 0;
            for (int i = 0 ; i<100; i++) { 
            double sysvibration=calvibration();
            System.out.println("total vibration is " + sysvibration);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
             error += sysvibration;
            }
            error /=100;
            System.out.println("////////////////////");
            System.out.println("average vibration is " + error);
            System.out.println("////////////////////");
            sendtoserial(1000,1000,1000,1000);
            }
            }).start();;
        
    }
   
}
