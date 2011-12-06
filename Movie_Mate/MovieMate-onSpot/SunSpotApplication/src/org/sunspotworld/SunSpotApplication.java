/*
 * SunSpotApplication.java
 *
 * Created on Nov 23, 2011 8:11:25 PM;
 * SPOT 1
 */
package org.sunspotworld;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.resources.Resources;
//import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.LEDColor;
import com.sun.spot.resources.transducers.IAccelerometer3D;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.service.BootloaderListenerService;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import com.sun.spot.io.j2me.radiogram.*;
import java.io.IOException;
import javax.microedition.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ISwitchListener;
import com.sun.spot.sensorboard.EDemoBoard;

/**
 * The startApp method of this class is called by the VM to start the
 * application.
 * 
 * The manifest specifies this class as MIDlet-1, which means it will
 * be selected for execution.
 */
public class SunSpotApplication extends MIDlet implements ISwitchListener {

    private IAccelerometer3D accel = (IAccelerometer3D) Resources.lookup(IAccelerometer3D.class);
    private ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    private static final int HOST_PORT = 67;
    private EDemoBoard demoboard = EDemoBoard.getInstance();
    private ISwitch sw[] = demoboard.getSwitches();
    private ISwitch sw1 = sw[0];
    private ISwitch sw2 = sw[1];
    RadiogramConnection rCon = null;
    Datagram dg = null;
    String ourAddress = System.getProperty("IEEE_ADDRESS");

    // The value for any particular SPOT may vary by as much as 10%. For more accurate results
    // each SPOT can be calibrated to determine the zero offset and conversion factor for each axis.
    /**
     * Simple accelerometer demo to measure the tilt of the SPOT.
     * Tilt is displayed by lighting LEDs like a bubble in a level.
     */
    public void demoBubbleLevel() {





        try {
            // Open up a broadcast connection to the host port
            // where the 'on Desktop' portion of this demo is listening
            rCon = (RadiogramConnection) Connector.open("radiogram://broadcast:" + HOST_PORT);
            dg = rCon.newDatagram(50);  // only sending 12 bytes of data
        } catch (Exception e) {
            System.err.println("Caught " + e + " in connection initialization.");
            notifyDestroyed();
        }

        leds.setOff();		       // turn off all LEDs
        leds.setColor(LEDColor.BLUE);  // set them to be blue when lit



        int oldOffset = 3;
        while (true) {
            try {
                int tiltX = (int) Math.toDegrees(accel.getTiltX()); // returns [-90, +90]
                int offset = -tiltX / 15;                // convert angle to range [3, -3] - bubble goes to higher side

                double ax = accel.getAccelX();


       /*         if (ax >= 1.1) {
                    System.out.println(" acceleration above threshold : " + ax);
                    dg.reset();
                    dg.writeInt(3);
                    rCon.send(dg);
                    // Utils.sleep(250);

                }       */

                if (offset < -3) {
                    offset = -3;
                    dg.reset();
                    dg.writeInt(3);
                    System.out.println(" Offset greater than 3 : Sending 3");
                    rCon.send(dg);
                    Utils.sleep(750);
                } else if (offset > 3) {
                    offset = 3;
                    dg.reset();
                    dg.writeInt(7);
                    System.out.println(" Offset greater than 3 : Sending 7");
                    rCon.send(dg);
                    Utils.sleep(750);
                }

                if (oldOffset != offset) {
                    leds.getLED(3 + oldOffset).setOff(); // clear display
                    leds.getLED(4 + oldOffset).setOff();
                    leds.getLED(3 + offset).setOn();     // use 2 LEDs to display "bubble""
                    leds.getLED(4 + offset).setOn();
                    oldOffset = offset;
                }
                Utils.sleep(50);                         // update 20 times per second
            } catch (IOException ex) {
                System.out.println("Error reading accelerometer: " + ex);
            }
        }
    }

    /*
     * If we wanted to implement our own version of getTiltX() that returns the
     * tilt value in degrees instead of radians, then here's one way to do so
     * based on the code in the LIS3L02AQAccelerometer class:
     *
     *   public int getTiltXDegrees() {
     *       double x = getAccelX();        // get current acceleration along each axis
     *       double y = getAccelY();
     *       double z = getAccelZ();
     *       double a = Math.sqrt(x*x + y*y + z*z);     // acceleration magnitude
     *       double tilt = x / a;                       // normalize
     *       double tiltRadians = MathUtils.asin(tilt); // tilt angle in radians
     *       return (int)(Math.toDegrees(tiltRadians) + 0.5);   // rounded result in degrees
     *   }
     *
     */
    /**
     * MIDlet call to start our application.
     */
    protected void startApp() throws MIDletStateChangeException {
        // Listen for downloads/commands over USB connection
        new com.sun.spot.service.BootloaderListenerService().getInstance().start();
        initialize();
        demoBubbleLevel();
    }

    protected void pauseApp() {
        // This will never be called by the Squawk VM
    }

    /**
     * Called if the MIDlet is terminated by the system.
     *
     * @param unconditional If true the MIDlet must cleanup and release all resources.
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        leds.setOff();
    }

    public void initialize() {
        sw1.addISwitchListener(this);
        sw2.addISwitchListener(this);
    }

    public void switchPressed(ISwitch sw) {
        try {
            // Open up a broadcast connection to the host port
            // where the 'on Desktop' portion of this demo is listening
            rCon = (RadiogramConnection) Connector.open("radiogram://broadcast:" + HOST_PORT);
            dg = rCon.newDatagram(50);  // only sending 12 bytes of data
        } catch (Exception e) {
            System.err.println("Caught " + e + " in connection initialization.");
            notifyDestroyed();
        }
        try {
            if (sw == sw1) {
                System.out.println("sw1isClosed. Send 1 to play");
                dg.reset();
                dg.writeInt(1);
                rCon.send(dg);
            } else {
                System.out.println("sw2isClosed Send 2 to pause");
                dg.reset();
                dg.writeInt(2);
                rCon.send(dg);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void switchReleased(ISwitch sw) {

        if (sw == sw1) {
            System.out.println("sw1isOpen");
        } else {
            System.out.println("sw2isOpen");
        }
    }
}
