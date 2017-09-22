package de.buschbaum.java.pathfinder;

import de.buschbaum.java.pathfinder.device.mpu6050.Mpu6050Controller;
import java.io.IOException;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

/**
 * Hello world!
 *
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, UnsupportedBusNumberException {
        initialize();
        while (true) {
            run();
        }
    }

    private static void run() {

    }

    private static void initialize() throws IOException, InterruptedException, UnsupportedBusNumberException {
        System.out.println("Initializing Mpu6050");
        Mpu6050Controller.initialize();
        System.out.println("Mpu6050 initialized!");
    }
}
