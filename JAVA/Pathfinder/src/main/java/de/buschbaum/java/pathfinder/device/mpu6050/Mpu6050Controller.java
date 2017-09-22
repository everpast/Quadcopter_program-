/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.buschbaum.java.pathfinder.device.mpu6050;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import de.buschbaum.java.pathfinder.Helper;
import java.io.IOException;

/**
 *
 * @author uli
 */
public class Mpu6050Controller {
    public static double gyroAngularSpeedX;
    public static double gyroAngularSpeedY;
    public static double gyroAngularSpeedZ;
    private static I2CBus bus = null;
    private static I2CDevice mpu6050 = null;
    private static double gyroAngularSpeedOffsetX;
    private static double gyroAngularSpeedOffsetY;
    private static double gyroAngularSpeedOffsetZ;
    public static final double RADIAN_TO_DEGREE = 180. / Math.PI;
    public static  double filteredAngleX=0;
    public static  double filteredAngleY=0;
    public static  double filteredAngleZ=0;
    private static double accelLSBSensitivity = 16384;
    private final static double offsetaccelangleX=-12.45;     /////////////////if indicator <0    decrease 
    private final static double offsetaccelangleY=5.77;       /////////////////if indicator >0    increase
    private static double ACCEL_Z_ANGLE= 0;

    private static double gyroLSBSensitivity= 131;

    public static double initial_acc_angleX;

    public static double initial_acc_angleY;
    public static long lastUpdateTime=0;
    
    public static void initialize() throws IOException, InterruptedException, UnsupportedBusNumberException {
        initializeI2C();
        configureMpu6050();
    }

    private static void initializeI2C() throws IOException, UnsupportedBusNumberException {
        System.out.println("Creating I2C bus");
        bus = I2CFactory.getInstance(I2CBus.BUS_1);
        System.out.println("Creating I2C device");
        mpu6050 = bus.getDevice(0x68);
    }

    private static void configureMpu6050() throws IOException, InterruptedException {

        //1 Waking the device up
        writeConfigRegisterAndValidate(
                "Waking up device",
                "Wake-up config succcessfully written: ",
                Mpu6050Registers.MPU6050_RA_PWR_MGMT_1,
                Mpu6050RegisterValues.MPU6050_RA_PWR_MGMT_1);

        //2 Configure sample rate
        writeConfigRegisterAndValidate(
                "Configuring sample rate",
                "Sample rate succcessfully written: ",
                Mpu6050Registers.MPU6050_RA_SMPLRT_DIV,
                Mpu6050RegisterValues.MPU6050_RA_SMPLRT_DIV);

        //3 Setting global config
        writeConfigRegisterAndValidate(
                "Setting global config (digital low pass filter)",
                "Global config succcessfully written: ",
                Mpu6050Registers.MPU6050_RA_CONFIG,
                Mpu6050RegisterValues.MPU6050_RA_CONFIG);

        //4 Configure Gyroscope
        writeConfigRegisterAndValidate(
                "Configuring gyroscope",
                "Gyroscope config successfully written: ",
                Mpu6050Registers.MPU6050_RA_GYRO_CONFIG,
                Mpu6050RegisterValues.MPU6050_RA_GYRO_CONFIG);

        //5 Configure Accelerometer
        writeConfigRegisterAndValidate(
                "Configuring accelerometer",
                "Accelerometer config successfully written: ",
                Mpu6050Registers.MPU6050_RA_ACCEL_CONFIG,
                Mpu6050RegisterValues.MPU6050_RA_ACCEL_CONFIG);

        //6 Configure interrupts
        writeConfigRegisterAndValidate(
                "Configuring interrupts",
                "Interrupt config successfully written: ",
                Mpu6050Registers.MPU6050_RA_INT_ENABLE,
                Mpu6050RegisterValues.MPU6050_RA_INT_ENABLE);

        //7 Configure low power operations
        writeConfigRegisterAndValidate(
                "Configuring low power operations",
                "Low power operation config successfully written: ",
                Mpu6050Registers.MPU6050_RA_PWR_MGMT_2,
                Mpu6050RegisterValues.MPU6050_RA_PWR_MGMT_2);
        calibrateSensors();
        /*for (byte i = 1; i <= 120; i++) {
            byte registerData = Mpu6050Controller.readRegister(i);
            System.out.println(i + "\t\tRegisterData:" + Helper.formatBinary(registerData));
        }*/

       // System.exit(0);
    }
    
    
    
    private static void writeRegister(byte register, byte data) throws IOException {
        mpu6050.write(register, data);
    }

    public static byte readRegister(byte register) throws IOException {
        int data = mpu6050.read(register);
        return (byte) data;
    }

    public static byte readRegister() throws IOException {
        int data = mpu6050.read();
        return (byte) data;
    }
    public static void initialzeroposition() {
        ;
    }
   
    
    /*public static double get_angle_pinch() throws IOException {
        double angle_z = get_rawgyro_z() /65.5;
        return angle_z;
        }*/
    
    public static void writeConfigRegisterAndValidate(String initialText, String successText, byte register, byte registerData) throws IOException {
        System.out.println(initialText);
        writeRegister(register, registerData);
        byte returnedRegisterData = Mpu6050Controller.readRegister(register);
        if (returnedRegisterData == registerData) {
            System.out.println(successText + Helper.formatBinary(returnedRegisterData));
        } else {
            throw new RuntimeException("Tried to write " + Helper.formatBinary(registerData) + " to "
                    + register + ", but validiating value returned " + Helper.formatBinary(returnedRegisterData));
        }
    }
    public static int readWord2C(int registerAddress) throws IOException {
        int value = mpu6050.read(registerAddress);
        value = value << 8;
        value += mpu6050.read(registerAddress + 1);
        
        if (value >= 0x8000) {
            value = -(65536 - value);
        }
        return value;
    }
   
    public static void calibrateSensors() throws IOException, InterruptedException {
        System.out.println("Calibration starting in 5 seconds (don't move the sensor).");
       
        int nbReadings = 50;
        
        // Gyroscope offsets
       gyroAngularSpeedOffsetX = 0.;
        gyroAngularSpeedOffsetY = 0.;
        gyroAngularSpeedOffsetZ = 0.;
        for(int i = 0; i < nbReadings; i++) {
            double[] angularSpeeds = readScaledGyroscopeValues();
            gyroAngularSpeedOffsetX += angularSpeeds[0];
            gyroAngularSpeedOffsetY += angularSpeeds[1];
            gyroAngularSpeedOffsetZ += angularSpeeds[2];
            double[] accelerations = readScaledAccelerometerValues();
            double accelAccelerationX = accelerations[0];
            double accelAccelerationY = accelerations[1];
            double accelAccelerationZ = accelerations[2];
            double accelAngleX = getAccelXAngle(accelAccelerationX, accelAccelerationY, accelAccelerationZ);
            double accelAngleY = getAccelYAngle(accelAccelerationX, accelAccelerationY, accelAccelerationZ);
            initial_acc_angleX += accelAngleX;
            initial_acc_angleY += accelAngleY;
            Thread.sleep(100);
        }
        gyroAngularSpeedOffsetX /= nbReadings;
        gyroAngularSpeedOffsetY /= nbReadings;
        gyroAngularSpeedOffsetZ /= nbReadings;
        initial_acc_angleX/=nbReadings;
        initial_acc_angleY/=nbReadings;
       //////////absolute angle///// 
        filteredAngleX = initial_acc_angleX - offsetaccelangleX;
        filteredAngleY = initial_acc_angleY - offsetaccelangleY;
        ////////////measure zero/////
       // filteredAngleX=0;
        //filteredAngleY=0;
                
        System.out.println("Calibration ended");
    }

   
    
    public static void updateValues() throws IOException {
        // Accelerometer
        double[] accelerations = readScaledAccelerometerValues();
        double accelAccelerationX = accelerations[0];
        double accelAccelerationY = accelerations[1];
        double accelAccelerationZ = accelerations[2];
        double accelAngleX = getAccelXAngle(accelAccelerationX, accelAccelerationY, accelAccelerationZ);
        double accelAngleY = getAccelYAngle(accelAccelerationX, accelAccelerationY, accelAccelerationZ);
        double accelAngleZ = getAccelZAngle();
        
        // Gyroscope
        double[] angularSpeeds = readScaledGyroscopeValues();
        gyroAngularSpeedX = angularSpeeds[0] - gyroAngularSpeedOffsetX;
        gyroAngularSpeedY = angularSpeeds[1] - gyroAngularSpeedOffsetY;
        gyroAngularSpeedZ = angularSpeeds[2] - gyroAngularSpeedOffsetZ;
        // angular speed * time = angle
        double dt = Math.abs(System.currentTimeMillis() - lastUpdateTime) / 1000.;; // s
        double deltaGyroAngleX = gyroAngularSpeedX * dt;
        double deltaGyroAngleY = gyroAngularSpeedY * dt;
        double deltaGyroAngleZ = gyroAngularSpeedZ * dt;
        lastUpdateTime = System.currentTimeMillis();
        //System.out.println("gyro angle x speed is " + gyroAngularSpeedX);
        //System.out.println("gyro angle y speed is " + gyroAngularSpeedY);
        //System.out.println("gyro acc x speed is " + accelAngleX);
        //System.out.println("gyro acc y speed is " + accelAngleY);
        double gyroAngleX = deltaGyroAngleX;
        double gyroAngleY = deltaGyroAngleY;
        double gyroAngleZ = deltaGyroAngleZ;
        
        // Complementary Filter
        double alpha = 0.96;
            //////////absolute angle///// 
        filteredAngleX = alpha * (filteredAngleX + deltaGyroAngleX) + (1. - alpha) * (accelAngleX- offsetaccelangleX);
        filteredAngleY = alpha * (filteredAngleY + deltaGyroAngleY) + (1. - alpha) * (accelAngleY- offsetaccelangleY);
        ////////////measure zero/////
        //filteredAngleX = alpha * (filteredAngleX + deltaGyroAngleX) + (1. - alpha) * (accelAngleX-initial_acc_angleX);
        // filteredAngleY = alpha * (filteredAngleY + deltaGyroAngleY) + (1. - alpha) * (accelAngleY-initial_acc_angleY);
         filteredAngleZ = filteredAngleZ + deltaGyroAngleZ;
    }

    public static double[] readScaledAccelerometerValues() throws IOException {
        int accelX = readWord2C(Mpu6050Registers.MPU6050_RA_ACCEL_XOUT_H);
        double accelX_1 = accelX/accelLSBSensitivity;
        int accelY = readWord2C(Mpu6050Registers.MPU6050_RA_ACCEL_YOUT_H);
        double accelY_1 = accelY/accelLSBSensitivity;
        int accelZ = readWord2C(Mpu6050Registers.MPU6050_RA_ACCEL_ZOUT_H);
        double accelZ_1 = accelZ/accelLSBSensitivity;
        
        return new double[]{accelX_1, accelY_1, -accelZ_1};
    }

    private static double getAccelXAngle(double x, double y, double z) {
      /*  // v1 - 360
        double radians = Math.atan2(y, distance(x, z));
        double delta = 0.;
        if(y >= 0) {
            if(z >= 0) {
                // pass
            } else {
                radians *= -1;
                delta = 180.;
            }
        } else {
            if(z <= 0) {
                radians *= -1;
                delta = 180.;
            } else {
                delta = 360.;
            }
        }
        return radians * RADIAN_TO_DEGREE + delta;*/
        double acc_total_vector = Math.sqrt(x*x+y*y+z*z);
        double angle_roll_acc = Math.asin(x/acc_total_vector)* -57.296;
        return angle_roll_acc;
    }
    
    private static double getAccelYAngle(double x, double y, double z) {        
       /* // v2
        double tan = -1 * x / distance(y, z);
        double delta = 0.;
        if(x <= 0) {
            if(z >= 0) {
                // q1
                // nothing to do
            } else {
                // q2
                tan *= -1;
                delta = 180.;
            }
        } else {
            if(z <= 0) {
                // q3
                tan *= -1;
                delta = 180.;
            } else {
                // q4
                delta = 360.;
            }
        }
        
        return Math.atan(tan) * RADIAN_TO_DEGREE + delta;*/
        double acc_total_vector = Math.sqrt(x*x+y*y+z*z);
        double angle_pitch_acc = Math.asin(y/acc_total_vector)* 57.296;
        return angle_pitch_acc;
    }
    
    private static double getAccelZAngle() {
        return ACCEL_Z_ANGLE;
    }
    private static double distance(double a, double b) {
        return Math.sqrt(a * a + b * b);
    }
    public static double[] readScaledGyroscopeValues() throws IOException {
        double gyroX = readWord2C(Mpu6050Registers.MPU6050_RA_GYRO_XOUT_H);
        gyroX /= gyroLSBSensitivity;
        double gyroY = readWord2C(Mpu6050Registers.MPU6050_RA_GYRO_YOUT_H);
        gyroY /= gyroLSBSensitivity;
        double gyroZ = readWord2C(Mpu6050Registers.MPU6050_RA_GYRO_ZOUT_H);
        gyroZ /= gyroLSBSensitivity;
        
        return new double[]{gyroX, gyroY, gyroZ};
    }
    

}
