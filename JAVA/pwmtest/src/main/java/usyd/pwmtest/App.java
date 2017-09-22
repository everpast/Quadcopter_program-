package usyd.pwmtest;



import de.buschbaum.java.pathfinder.device.mpu6050.Mpu6050Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
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
import com.pi4j.platform.PlatformAlreadyAssignedException;

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
    private static String readtext;
    private static boolean read_flag;
    private static Serial serial;
    private volatile static boolean motor_flag= true;
    static Thread motor_start;
    static int exit_flag;
    //static Initial_start motor1;
    public static void main(String[] args) throws InterruptedException, PlatformAlreadyAssignedException, IOException, UnsupportedBusNumberException {
      
        System.out.println("==>>>>>>>P =1 limit = 50 ");
        //start serial connect
        serial = SerialFactory.createInstance();

        serial.addListener(new SerialDataEventListener() {
         
            public void dataReceived(SerialDataEvent event) {
///////////no response when received message//////
                
                try {
                  
                    System.out.println("[ASCII DATA] " + event.getAsciiString());
                  
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            sendtoserial(1000,1000,1000,1000);
            System.out.println("==>>>>>>>motor reset to zero");

        }
        catch(IOException ex) {
            System.out.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            return;
        } 
        
        // start wifi connect
        int port = 1423;
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("server waiting to connect");
            Socket socket = server.accept();
            System.out.println("client connected");
            in = socket.getInputStream();
            isr = new InputStreamReader(in);
            br = new BufferedReader(isr);
            out = socket.getOutputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sendtomobile("server start");
            
     
        
            try {
                out.write(("server: connect"+"\n").getBytes("utf-8"));
                System.out.println("send success");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("send fail");
            }
            
          ////////////////////wifi reading Thread//////////////////////////
            new Thread (new Runnable() {
                

                public void run() {
                  while(true) {  
                    try {
                        readtext= br.readLine();
                        read_flag= true;
                        System.out.println("server received :" +  readtext);
                        sendmessage(); //////////////response to received message
                        
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                  }
                }
            }).start();
            
        // start mpusensor Thread////////////////////////////////////////////
        mpusensor mpu6050 = new mpusensor();
		Thread thread_mpu = new Thread(mpu6050);
		thread_mpu.start();
		
        // start motor_pwm Thread
       /* motor1= new Initial_start();
        Thread thread_motor1 = new Thread(motor1);
        thread_motor1.start();*/
	    
//////////////////////////////////////////////////////
///////keyboard control/////////////////////
/*   
        
        
		final Console console = new Console();
		console.title("<-- The Pi4J Project -->", "SoftPWM Example (Software-driven PWM Emulation)");
		console.promptForExit();
        console.println(" press \"all\" to start");
        console.println(" type number from 1000 to 2500 to test motors");
        console.println(" press \"exit\" to exit");

        while(true) {
            String mode_set = System.console().readLine();
            int motor_test = 1000;
           try { motor_test= Integer.parseInt(mode_set);}
           catch(NumberFormatException e) {
            if(mode_set.equals("all")) {
                //////stop initial motor Thread 
                motor1.exit = false;
                ////start pid controller 
                 pid_controller pid = new pid_controller();
                 Thread thread_pid = new Thread(pid);
                 thread_pid.start();
               /////start motor   
                 motor_pwm motor= new motor_pwm();
                 Thread thread_motor = new Thread(motor);
                 thread_motor.start();
                 
                  while(true) {
                     console.println(" ... putvalue for  PWM pin : "  + "all pin" +" from 1300 to 2400");
                     console.println(" press\" timer\" to print timer from motor 1 to 4");
                     int pwm_value_int=1000;
                     String pwm_value_string = System.console().readLine();
                     ///////show timer of 4 motors
                     if (pwm_value_string.equals("timer")){
                             console.println("sensor initial roll pitch yaw angle : " + Mpu6050Controller.initial_acc_angleX +"  "+Mpu6050Controller.initial_acc_angleY);
                             long time_now = System.currentTimeMillis();
                             long time_next = System.currentTimeMillis();
                             int cyc=0;
                             
                             while(true) {
                                 time_now = System.currentTimeMillis();
                                 
                                     while(time_now - time_next >= 1000) {  
                                         time_next = time_now;
                                         cyc++;
                                         if(cyc==1)console.println("timer from motor 1 to 4 are " + (motor_pwm.timer_test[0] ) + " " + (motor_pwm.timer_test[1]) +" " + (motor_pwm.timer_test[2]) + " " +(motor_pwm.timer_test[3]) );
                                         if(cyc==2) {
                                             console.println("sensor roll pitch yaw angle : "+ " " +Mpu6050Controller.filteredAngleX +" "+ Mpu6050Controller.filteredAngleY +" "+ Mpu6050Controller.filteredAngleZ);
                                             cyc=0;
                                         }
                                     }
                                     
                                 }
                     }
                     //////
                     while (pwm_value_string == null) {
                         console.println("reenter a value");
                         pwm_value_string=System.console().readLine();
                     }
                     
                    try { pwm_value_int= Integer.parseInt(pwm_value_string);
                    } catch(NumberFormatException e1) {
                        
                        console.println("enter error number, throttle set to 1000");   
                        throttle=1000;
                    }
                    
                     while(pwm_value_int == 0 |pwm_value_int > 2500 | pwm_value_int < 1000) {
                         console.println("reenter a value from 1300 to 2400");
                         pwm_value_string=System.console().readLine();
                         pwm_value_int= Integer.parseInt(pwm_value_string);
                     
                     }
                     throttle = pwm_value_int;
                     
                     console.println("motor timer is " + motor_pwm.esc[1]);
                 }
                 
             }
                     
                             
                 
                 if(mode_set.equals("exit")) {
                     return;
                 }
                 
                 if(mode_set.equals("mpu")) {
                     console.println("sensor initial roll pitch yaw angle : " + Mpu6050Controller.initial_acc_angleX +"  "+Mpu6050Controller.initial_acc_angleY);
                     long time_now1 = System.currentTimeMillis();
                     long time_next1 = time_now1;
                     
                     while(true) {
                         time_now1 = System.currentTimeMillis();
                        
                         
                         
                             while(time_now1 >= time_next1 ) {  
                                 time_next1 = time_now1 +   1000;
                                 console.println("sensor angle speed : "+ Mpu6050Controller.gyroAngularSpeedX +" "+ Mpu6050Controller.gyroAngularSpeedY +" "+Mpu6050Controller.gyroAngularSpeedZ);
                                 console.println("sensor roll pitch yaw angle : "+ " " +Mpu6050Controller.filteredAngleX +" "+ Mpu6050Controller.filteredAngleY +" "+ Mpu6050Controller.filteredAngleZ);
                             }
                     }
                 }
                 else {
                     console.println("wrong enter");
                     console.println(" press \"all\" to set together");
                     console.println(" press \"sep\" to set seperately");
                     console.println(" press \"exit\" to exit");
                     mode_set = System.console().readLine();
                 }
           }
        
       
        //gpio.shutdown();
        throttle =motor_test;
        console.println(" type number from 1000 to 2500 to test motors");
        }*/
    }
    protected static boolean read_once=true;
    static boolean sensor_flag = false;
    static void sendmessage() {
        new Thread(new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {
               
               
                  
                           switch(readtext) {
                           case "sensor" :
                              sensor_flag= !sensor_flag;
                              while(sensor_flag) {
                               ////////////////send sensor information
                               try {
                                   out.write(("s1"+Mpu6050Controller.filteredAngleX+"\n").getBytes("utf-8"));
                                   try {
                                       Thread.sleep(10);
                                   } catch (InterruptedException e) {
                                       // TODO Auto-generated catch block
                                       e.printStackTrace();
                                        }
                                   out.write(("s2"+Mpu6050Controller.filteredAngleY+"\n").getBytes("utf-8"));
                                   try {
                                       Thread.sleep(10);
                                   } catch (InterruptedException e) {
                                       // TODO Auto-generated catch block
                                       e.printStackTrace();
                                   }
                                   out.write(("s3"+Mpu6050Controller.filteredAngleZ+"\n").getBytes("utf-8"));
                                   try {
                                       Thread.sleep(500);
                                   } catch (InterruptedException e) {
                                       // TODO Auto-generated catch block
                                       e.printStackTrace();
                                   }
                               } catch (IOException e) {
                                   // TODO Auto-generated catch block
                                   e.printStackTrace();
                               }
                               ///////////send motor information
                               
                               try {
                                   out.write(("t1"+pid_controller.timer[0]+"\n").getBytes("utf-8"));
                                   try {
                                       Thread.sleep(10);
                                   } catch (InterruptedException e) {
                                       // TODO Auto-generated catch block
                                       e.printStackTrace();
                                   }
                                   out.write(("t2"+pid_controller.timer[1]+"\n").getBytes("utf-8"));
                                   try {
                                       Thread.sleep(10);
                                   } catch (InterruptedException e) {
                                       // TODO Auto-generated catch block
                                       e.printStackTrace();
                                   }
                                   out.write(("t3"+pid_controller.timer[2]+"\n").getBytes("utf-8"));
                                   try {
                                       Thread.sleep(10);
                                   } catch (InterruptedException e) {
                                       // TODO Auto-generated catch block
                                       e.printStackTrace();
                                   }
                                   out.write(("t4"+pid_controller.timer[3]+"\n").getBytes("utf-8"));
                                   try {
                                       Thread.sleep(500);
                                   } catch (InterruptedException e) {
                                       // TODO Auto-generated catch block
                                       e.printStackTrace();
                                   }
                               } catch (IOException e) {
                                   // TODO Auto-generated catch block
                                   e.printStackTrace();
                               }
                               }
                               break; 
                          /// case "motor":
                
                   
                         //////  break;
               
               case "start":
                            if (read_once==true) {
                       read_once=false; //////stop initial motor Thread 
                 
                   ////start pid controller 
                    pid_controller pid = new pid_controller();
                    Thread thread_pid = new Thread(pid);
                    thread_pid.start();
                    motor_start= new Thread (new Runnable() {
                          public void run() {
                          while(true) {  
                            sendtoserial(pid_controller.timer[0],pid_controller.timer[1],pid_controller.timer[2],pid_controller.timer[3]);
                            
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                sendtoserial(1000,1000,1000,1000);
                                System.out.println("motor control interrupted");
                                break;
                                
                            }
                          }
                        }
                    });
                    motor_start.start();;
                            }
                   break;
               case "exit":
                   
                            
                            motor_start.interrupt();
                            
                            sendtoserial(1000,1000,1000,1000);
                            exit_flag++;
                            if (exit_flag ==2)System.exit(0);
                   /*new Thread (new Runnable() {
                       public void run() {
                       while(motor_flag==false) {  
                         sendtoserial((int)1000,(int)1000,(int)1000,(int)1000);
                         
                         try {
                             Thread.sleep(250);
                         } catch (InterruptedException e) {
                             // TODO Auto-generated catch block
                             e.printStackTrace();
                         }
                       }
                     }
                 }).start();*/
                    break; 
                          
                   
            }
                          
                       if(readtext.substring(0,2).equals("th")) {
                           throttle = Integer.parseInt(readtext.substring(2,readtext.length()));
                        //   System.out.println("set throttle to " + throttle);
                           try {
                               out.write(("received" + throttle+"\n").getBytes("utf-8"));
                           } catch (IOException e) {
                               // TODO Auto-generated catch block
                               e.printStackTrace();
                           }
                           
                       } 
                       
                              
                              
               
               
            }
        }).start();
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
  
   private static void sendtomobile(String message) {
   try {
    out.write((message+"\n").getBytes("utf-8"));
} catch (UnsupportedEncodingException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
} catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
   }
}


   
