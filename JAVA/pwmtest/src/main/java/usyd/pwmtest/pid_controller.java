package usyd.pwmtest;

import de.buschbaum.java.pathfinder.device.mpu6050.Mpu6050Controller;

public class pid_controller implements Runnable{
    
    public static double roll_offset=0;
    public static double pitch_offset=0;
    public static double yaw_offset=0;
    public static double pid_output_roll=0;
    public static double pid_output_pitch=0;
    public static double pid_output_yaw=0;
    public static int[] timer= {1000,1000,1000,1000}, aftercontrol= {0,0,0,0};
    
    public void run() {
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//PID gain and limit settings
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
double pid_p_gain_roll = 2;               //Gain setting for the roll P-controller
double pid_i_gain_roll = 0.04;              //Gain setting for the roll I-controller
double pid_d_gain_roll = 0;              //Gain setting for the roll D-controller
double pid_max_roll = 400;                    //Maximum output of the PID-controller (+/-)

double pid_p_gain_pitch = pid_p_gain_roll;  //Gain setting for the pitch P-controller.
double pid_i_gain_pitch = pid_i_gain_roll;  //Gain setting for the pitch I-controller.
double pid_d_gain_pitch = pid_d_gain_roll;  //Gain setting for the pitch D-controller.
double pid_max_pitch = pid_max_roll;          //Maximum output of the PID-controller (+/-)

double pid_p_gain_yaw = 4;                //Gain setting for the pitch P-controller. //4.0
double pid_i_gain_yaw = 0.02;               //Gain setting for the pitch I-controller. //0.02
double pid_d_gain_yaw = 0.0;                //Gain setting for the pitch D-controller.
double pid_max_yaw = 400;                     //Maximum output of the PID-controller (+/-)
int[] controlvalue= {0,0,0,0};

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //initial value   
    double pid_last_roll_d_error = 0;
    double pid_last_pitch_d_error = 0;
    double pid_last_yaw_d_error = 0;
   
        while(true) {
                //roll calculation
                double pid_error_temp_roll = Mpu6050Controller.initial_acc_angleX - Mpu6050Controller.filteredAngleX -roll_offset;
                double pid_i_mem_roll = pid_i_gain_roll * pid_error_temp_roll;
    
                if(pid_i_mem_roll > pid_max_roll)pid_i_mem_roll = pid_max_roll;
                else if(pid_i_mem_roll < pid_max_roll * -1)pid_i_mem_roll = pid_max_roll * -1;

    
                pid_output_roll = pid_p_gain_roll * pid_error_temp_roll + pid_i_mem_roll + pid_d_gain_roll * (pid_error_temp_roll - pid_last_roll_d_error);
                if(pid_output_roll > pid_max_roll)pid_output_roll = pid_max_roll;
                else if(pid_output_roll < pid_max_roll * -1)pid_output_roll = pid_max_roll * -1;

                pid_last_roll_d_error = pid_error_temp_roll;
    
                //Pitch calculations
                double pid_error_temp = Mpu6050Controller.initial_acc_angleY - Mpu6050Controller.filteredAngleY - pitch_offset;
                double pid_i_mem_pitch = pid_i_gain_pitch * pid_error_temp;
                if(pid_i_mem_pitch > pid_max_pitch)pid_i_mem_pitch = pid_max_pitch;
                else if(pid_i_mem_pitch < pid_max_pitch * -1)pid_i_mem_pitch = pid_max_pitch * -1;

    
                pid_output_pitch = pid_p_gain_pitch * pid_error_temp + pid_i_mem_pitch + pid_d_gain_pitch * (pid_error_temp - pid_last_pitch_d_error);
                if(pid_output_pitch > pid_max_pitch)pid_output_pitch = pid_max_pitch;
                else if(pid_output_pitch < pid_max_pitch * -1)pid_output_pitch = pid_max_pitch * -1;

                pid_last_pitch_d_error = pid_error_temp;

                //Yaw calculations
                pid_error_temp = Mpu6050Controller.filteredAngleZ - yaw_offset;
                double pid_i_mem_yaw = pid_i_gain_yaw * pid_error_temp;
                if(pid_i_mem_yaw > pid_max_yaw)pid_i_mem_yaw = pid_max_yaw;
                else if(pid_i_mem_yaw < pid_max_yaw * -1)pid_i_mem_yaw = pid_max_yaw * -1;

    
                pid_output_yaw = pid_p_gain_yaw * pid_error_temp + pid_i_mem_yaw + pid_d_gain_yaw * (pid_error_temp - pid_last_yaw_d_error);
                if(pid_output_yaw > pid_max_yaw)pid_output_yaw = pid_max_yaw;
                else if(pid_output_yaw < pid_max_yaw * -1)pid_output_yaw = pid_max_yaw * -1;

                pid_last_yaw_d_error = pid_error_temp;  
                
                ////////////////////limit control value////////////////////
                //////control value between -200 and 200///////////////////
                controlvalue[0]=(int) (-pid_output_roll - pid_output_pitch ); //+ pid_output_yaw
                controlvalue[1]=(int) (+pid_output_roll - pid_output_pitch );//- pid_output_yaw
                controlvalue[2]=(int) (+pid_output_roll + pid_output_pitch );//+ pid_output_yaw
                controlvalue[3]=(int) (-pid_output_roll + pid_output_pitch );//- pid_output_yaw
                for(int i =0 ; i<4; i++) {
                    if (controlvalue[i]>400)controlvalue[i]=400;
                    if (controlvalue[i]<-400)controlvalue[i]=-400;
                    aftercontrol[i]= App.throttle + controlvalue[i];
                    if (aftercontrol[i]<1050) aftercontrol[i]=1050;
                    if (aftercontrol[i]>2000) aftercontrol[i]=2000;
                    timer[i]=aftercontrol[i];
                }
                ///////////////pid control method////////////
                /*timer[0] = (int)(App.throttle - pid_controller.pid_output_pitch +pid_controller.pid_output_roll - pid_controller.pid_output_yaw);
                timer[1] = (int)(App.throttle + pid_controller.pid_output_pitch +pid_controller.pid_output_roll + pid_controller.pid_output_yaw);
                timer[2] = (int)(App.throttle + pid_controller.pid_output_pitch -pid_controller.pid_output_roll - pid_controller.pid_output_yaw);
                timer[3] = (int)(App.throttle - pid_controller.pid_output_pitch -pid_controller.pid_output_roll + pid_controller.pid_output_yaw);
                */
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }
}
