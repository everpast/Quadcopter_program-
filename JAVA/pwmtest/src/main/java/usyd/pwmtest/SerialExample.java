package usyd.pwmtest;

import com.pi4j.io.serial.*;
import java.io.IOException;

/**
 * This example code demonstrates how to perform serial communications using the Raspberry Pi.
 *
 * @author Robert Savage
 */
public class SerialExample implements Runnable{

    
    public void run() {

        final Serial serial = SerialFactory.createInstance();

        serial.addListener(new SerialDataEventListener() {
         
            public void dataReceived(SerialDataEvent event) {

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
            while(true) {
                try {
                    
                 
                    serial.writeln(pid_controller.timer[0]+","+pid_controller.timer[1]+","+pid_controller.timer[2]+","+pid_controller.timer[3]);
                }
                catch(IllegalStateException ex){
                    ex.printStackTrace();
                }

                // wait 250 microsecond before continuing
                Thread.sleep(250);
            }

        }
        catch(IOException ex) {
            System.out.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            return;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}