package EECS1021;
import java.util.Timer;
import org.firmata4j.IODevice;
import edu.princeton.cs.introcs.StdDraw;
import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;
import java.util.HashMap;
import java.io.IOException;
import org.firmata4j.I2CDevice;
import org.firmata4j.firmata.FirmataDevice;

public class MinorProject {
    static final String usb = "COM3";
    static final int WATER_PUMP_PLUGGIN = 2;
    static final int MOISTURE_SENSOR_PLUGGIN = 15;

    public static void main(String[] args) throws IOException, InterruptedException {
        int sample = 1;

        System.out.println("Initiating Board, Arduino task, Starting. ");

        IODevice myArduinoBoard = new FirmataDevice(usb);
        myArduinoBoard.start();
        myArduinoBoard.ensureInitializationIsDone();

        // setup water pump along with moisture sensor
        Pin myMOSFET = myArduinoBoard.getPin(WATER_PUMP_PLUGGIN);
        myMOSFET.setMode(Pin.Mode.OUTPUT);

        Pin mySensor = myArduinoBoard.getPin(MOISTURE_SENSOR_PLUGGIN);
        mySensor.setMode(Pin.Mode.ANALOG);

        // setup OLED screen
        I2CDevice i2cObject = myArduinoBoard.getI2CDevice((byte) 0x3C);
        SSD1306 oledObject = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);
        oledObject.init();

        var myTask = new task(mySensor, myMOSFET, oledObject);

        // execute the task every 1 second or 1000 milliseconds
        new Timer().schedule(myTask, 0, 1000);

        // Setup X and Y axis
        StdDraw.setYscale(-26, 1100);          // values up to 1023
        StdDraw.setXscale(-2, 100);            // up to 100 samples

        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.GREEN);

        // Sketch the axis for the graph
        StdDraw.line(0, 0, 100, 0);
        StdDraw.line(0, 0, 0, 1000);  //horizontal & vertical lines

        // labels for horizontal and vertical lines, along with graph label
        StdDraw.text(50, 1100, "Voltage vs Time Graph");
        StdDraw.text(-3, 500, "Voltage(V)");
        StdDraw.text(50, -25, "Time(S)");

        int time = 0;

        // get a reading from the scheduled task above
        while (true) {

            int soilValue = (int) mySensor.getValue();
            System.out.println("Voltage Reading from Soil: " + soilValue);

            //collect data from sensor using a hashmap
            HashMap<Integer, Integer> moistData = new HashMap<>();

            //insert data to hashmap
            moistData.put(time, soilValue);

            moistData.forEach((xValue, yValue) -> StdDraw.text(xValue, yValue, "*"));

            Thread.sleep(1000);

            time++;
        }
    }
}