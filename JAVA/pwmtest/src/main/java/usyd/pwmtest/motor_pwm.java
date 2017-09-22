package usyd.pwmtest;


import com.pi4j.io.gpio.*;
import com.pi4j.util.CommandArgumentParser;

public class motor_pwm implements Runnable{
    public static long[] esc= new long[4];
    public static double[] timer_test= new double[4];

    public void run() {
        
       /* 
        Pin[] pin = new Pin[4]; 
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
        final GpioPinDigitalOutput[] output = new GpioPinDigitalOutput[4]; 
            for (int i = 0;i<4;i++)   {  
                output[i]= gpio.provisionDigitalOutputPin(Initial_start.pin[i], "My Output", PinState.HIGH);  
            }   */
            
            ///variations 
            
            long time_next = System.nanoTime()/1000;
            
            int[] mark= new int[4];
            // initial start
           /* long initial_time_now;
            long initial_time_next;
            int loop_num = 100;
            initial_time_now = System.nanoTime()/1000;
            initial_time_next = initial_time_now;
            for (int j = 0; j<= loop_num; j++) {
                while(initial_time_now - initial_time_next >= 20000) {
                    for (int i = 0;i<4;i++)   {  
                        Initial_start.output[i].setState(PinState.HIGH); 
                        timer[i] = time_now + 2400;
                    }   
                    long initial_loop_timer = initial_time_now;
                    while (initial_loop_timer - initial_time_now <= 2400 ) {
                         initial_loop_timer = System.nanoTime()/1000;
                         for (int i = 0;i<4;i++) {
                             
                                 if (timer[i]<initial_loop_timer) {
                                     Initial_start.output[i].setState(PinState.LOW);
                                     
                                 }
                            
                         }
                    }
                }
            }
            /////////initial high position end
            App.throttle = 1300;
            /////set low initial position
            initial_time_now = System.nanoTime()/1000;
            initial_time_next = initial_time_now;
            for (int j = 0; j<= (loop_num/2); j++) {
                while(initial_time_now - initial_time_next >= 20000) {
                    for (int i = 0;i<4;i++)   {  
                        output[i].setState(PinState.HIGH); 
                        timer[i] = time_now + 1300;
                    }   
                    long initial_loop_timer = initial_time_now;
                    while (initial_loop_timer - initial_time_now <= 2400 ) {
                         initial_loop_timer = System.nanoTime()/1000;
                         for (int i = 0;i<4;i++) {
                             
                                 if (timer[i]<initial_loop_timer) {
                                     output[i].setState(PinState.LOW);
                                     
                                 }
                            
                         }
                    }
                }
            }
            //////set end 
            */
            ////motor start loop
             
             while(true) {
                 long time_now = System.nanoTime()/1000;
                 
                 double[] timer = new double[4];
                 
                 while(time_now > time_next ) {
                     
                      timer[0] = App.throttle - pid_controller.pid_output_pitch +pid_controller.pid_output_roll - pid_controller.pid_output_yaw;
                      timer[1] = App.throttle + pid_controller.pid_output_pitch +pid_controller.pid_output_roll + pid_controller.pid_output_yaw;
                      timer[2] = App.throttle + pid_controller.pid_output_pitch -pid_controller.pid_output_roll - pid_controller.pid_output_yaw;
                      timer[3] = App.throttle - pid_controller.pid_output_pitch -pid_controller.pid_output_roll + pid_controller.pid_output_yaw;
                     time_next =time_now+20000;
                     
                     for (int i = 0;i<4;i++)   {  
                         Initial_start.output[i].setState(PinState.HIGH); 
                         mark[i]=0;
                         if (timer[i] >=  2400) timer[i] =  2400;
                         if (timer[i] <=  1200) timer[i] =  1200;
                         timer_test[i]=timer[i];
                         timer[i] += time_now;
                     }   
                      
                    long loop_timer = time_now;
                    while (loop_timer - time_now <= 2500 ) {
                         loop_timer = System.nanoTime()/1000;
                         for (int i = 0;i<4;i++) {
                             if (mark[i] == 0) {
                                 if (timer[i]<loop_timer) {
                                     Initial_start.output[i].setState(PinState.LOW);
                                     mark[i]=1;
                                     //esc[i]=loop_timer- time_now;
                                 }
                             }
                         }
                         
                         
                     }
                     
                 }
             }
        }



    
}