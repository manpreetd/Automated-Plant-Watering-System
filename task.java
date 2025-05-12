package EECS1021;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import org.firmata4j.Pin;
import java.util.TimerTask;
public class task extends TimerTask {
    private final SSD1306 oledObject;
    private Pin mySensor;
    private Pin myMOSFET;
    private long sensorVal;

    // step up connection with arduino and pump/moisture sensor
    static final String usb = "COM3";
    static final int WATER_PUMP_PLUGGIN = 2;
    static final int MOISTURE_SENSOR_PLUGGIN = 15;
    public task(Pin mySensor, Pin myMOSFET, SSD1306 oledObject) {
        this.mySensor = mySensor;
        this.oledObject = oledObject;
        this.myMOSFET = myMOSFET;           // This the constructor

    }

    @Override
    public void run() {

        // soil variables and values
        int soilVal = (int) mySensor.getValue();
        int wetsoil = 500;
        int moistsoil = 600;
        int drysoil = 700;


        // if-else statements for the moisture readings, water pump and OLED Screen
        if (soilVal > drysoil) {
            oledObject.clear();
            oledObject.getCanvas().setTextsize(2);
            oledObject.getCanvas().drawString(0, 0, "IM DRY, PLEASE GIVE ME WATER!");
            oledObject.display();
            //Switch the water pump on, leave it on for 2 seconds, then switch on the pump.
            try {
                myMOSFET.setValue(1);
                Thread.sleep(2000);
                myMOSFET.setValue(0);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (soilVal > moistsoil) {
            oledObject.clear();
            oledObject.getCanvas().setTextsize(2);
            oledObject.getCanvas().drawString(0, 0, "I NEED MORE WATER!");
            oledObject.display();
            //Switch on the pump, leave it on a second, then turn it off.
            try {
                myMOSFET.setValue(1);
                Thread.sleep(1000);
                myMOSFET.setValue(0);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            oledObject.clear();
            oledObject.getCanvas().setTextsize(2);
            oledObject.getCanvas().drawString(0, 0, "IM HYDRATED, THANKS!");
            oledObject.display();
            //Switch off the water pump.
            try {
                myMOSFET.setValue(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}