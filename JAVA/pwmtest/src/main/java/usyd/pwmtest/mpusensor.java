package usyd.pwmtest;

import java.io.IOException;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import de.buschbaum.java.pathfinder.device.mpu6050.Mpu6050Controller;

public class mpusensor implements Runnable {
    Mpu6050Controller mpucontroller= new Mpu6050Controller();
    public void run(){
        System.out.println("Theard mpu start");
        try {
            Mpu6050Controller.initialize();
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (InterruptedException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (UnsupportedBusNumberException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
       
        
        
       
        Mpu6050Controller.lastUpdateTime=System.currentTimeMillis();
        while(true) {
            
            
                
                    try {
                        
                        Mpu6050Controller.updateValues();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.out.println("Sensor reading error");
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.out.println("interrupted error");
                    }
                }
       
                /*if(cyc>=5) {
                    cyc=0;
                    System.out.println("mpu data update 2 times");
                    System.out.println("mpu data pitch  " + Mpu6050Controller.filteredAngleX);
                    System.out.println("mpu data roll  " + Mpu6050Controller.filteredAngleY);
                    System.out.println("mpu data yaw  " + Mpu6050Controller.filteredAngleZ);
                   
                }*/
        
    }
}
