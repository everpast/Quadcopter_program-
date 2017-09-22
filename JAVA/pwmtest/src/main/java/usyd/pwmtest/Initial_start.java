package usyd.pwmtest;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.util.CommandArgumentParser;

public class Initial_start implements Runnable {
    public volatile boolean exit=true;
    public Pin[] pin = new Pin[4]; 
    public final static GpioPinDigitalOutput[] output = new GpioPinDigitalOutput[4];
    public void run() {
        
        final GpioController gpio = GpioFactory.getInstance();
        String args = null;
       pin[0] = CommandArgumentParser.getPin(
                RaspiPin.class,    // pin provider class to obtain pin instance from
                RaspiPin.GPIO_04,  // default pin if no pin argument found
                args);             // argument array to search in
        pin[1] = CommandArgumentParser.getPin(
                RaspiPin.class,    // pin provider class to obtain pin instance from
                RaspiPin.GPIO_05,  // default pin if no pin argument found
                args); 
        pin[2] = CommandArgumentParser.getPin(
                RaspiPin.class,    // pin provider class to obtain pin instance from
                RaspiPin.GPIO_06,  // default pin if no pin argument found
                args); 
        pin[3] = CommandArgumentParser.getPin(
                RaspiPin.class,    // pin provider class to obtain pin instance from
                RaspiPin.GPIO_27,  // default pin if no pin argument found
                args); 
         
            for (int i = 0;i<4;i++)   {  
                output[i]= gpio.provisionDigitalOutputPin(pin[i], "My Output", PinState.HIGH);  
            }   
            
            long time_now = System.nanoTime()/1000;
            long time_next = System.nanoTime()/1000;
            double[] timer = new double[4];
            int[] mark= new int[4];
            
            while(exit) {
                time_now = System.nanoTime()/1000;
                
                while(time_now > time_next ) {
                    
                     timer[0] = App.throttle; 
                     timer[1] = App.throttle;
                     timer[2] = App.throttle;
                     timer[3] = App.throttle;
                    time_next =time_now+20000;
                    
                    for (int i = 0;i<4;i++)   {  
                        output[i].setState(PinState.HIGH); 
                        mark[i]=0;
                        if (timer[i] >=  2500) timer[i] =  2500;
                        if (timer[i] <=  1000) timer[i] =  1000;
                        timer[i] += time_now;
                    }   
                     
                   long loop_timer = time_now;
                   while (loop_timer - time_now <= 2500 ) {
                        loop_timer = System.nanoTime()/1000;
                        for (int i = 0;i<4;i++) {
                            if (mark[i] == 0) {
                                if (timer[i]<loop_timer) {
                                    output[i].setState(PinState.LOW);
                                    mark[i]=1;
                                    
                                }
                            }
                        }
                        
                        
                    }
                    
                }
            }
            System.out.println("initial start stoped.");
       }
    }


