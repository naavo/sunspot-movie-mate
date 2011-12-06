/*
 * SunSpotHostApplication.java
 *
 * Created on Nov 24, 2011 12:09:20 AM;
 */

package org.sunspotworld;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.peripheral.ota.OTACommandServer;
import java.text.DateFormat;
import java.util.Date;

import java.io.*;
import javax.microedition.io.*;


/**
 * Sample Sun SPOT host application
 */
public class SunSpotHostApplication {

  // Broadcast port on which we listen for sensor samples
    private static final int HOST_PORT = 67;
        
    private void run() throws Exception {
        RadiogramConnection rCon;
        Datagram dg;
        DateFormat fmt = DateFormat.getTimeInstance();
         
        try {
            // Open up a server-side broadcast radiogram connection
            // to listen for sensor readings being sent by different SPOTs
            rCon = (RadiogramConnection) Connector.open("radiogram://:" + HOST_PORT);
            dg = rCon.newDatagram(rCon.getMaximumLength());
        } catch (Exception e) {
             System.err.println("setUp caught " + e.getMessage());
             throw e;
        }

        // Main data collection loop
        while (true) {
            try {
                // Read sensor sample received over the radio
                rCon.receive(dg);
                String addr = dg.getAddress();  // read sender's Id
                int val = 0 ;
                 Runtime rt = Runtime.getRuntime();
                try{
                    
                    val = dg.readInt();
                
                }catch(Exception e){
                    System.err.println(e);
                }
                if (val == 1) {
                    System.out.println("  value is 1 : Sw1 of spot 1 : Play  the video");
                    Process pr = rt.exec("C:\\Program Files (x86)\\Winamp\\clamp /play");
                    
                }
                else if (val == 2) {
                    System.out.println("  value is 2 : sw2 of spot1 : pause  the video");
                    Process pr = rt.exec("C:\\Program Files (x86)\\Winamp\\clamp /pause");
                }
                else if(val == 3){
                    System.out.println("  value is 3 : left of spot1 : fwd  the video");
                    Process pr = rt.exec("C:\\Program Files (x86)\\Winamp\\clamp /fwd"); 
                }
                else if(val == 4){
                    System.out.println("  value is 4 : sw1 of spot 2 : Exit winamp");
                    Process pr = rt.exec("C:\\Program Files (x86)\\Winamp\\clamp /QUIT");
                }
                else if(val == 5){
                    System.out.println("  value is 5 : spot 2 left : Volume up");
                    Process pr = rt.exec("C:\\Program Files (x86)\\Winamp\\clamp /VOLUP 1");
                }
                else if(val == 6){
                    System.out.println("  value is 6 : spot 2 right : Volume down");
                    Process pr = rt.exec("C:\\Program Files (x86)\\Winamp\\clamp /VOLDN 1");
                }
                else if(val == 7){
                    System.out.println("  value is 7 : spot 1 right : backward the video");
                    Process pr = rt.exec("C:\\Program Files (x86)\\Winamp\\clamp /REW");
                }
                else if(val == 8){
                    System.out.println("  value is 8 : sw2 of spot 2 : mute the video");
                    Process pr = rt.exec("C:\\Program Files (x86)\\Winamp\\clamp /VOLMIN");
                }
                 else if(val == 9){
                    System.out.println("  value is 9 : shake spot 2 : mumaximum valume");
                    Process pr = rt.exec("C:\\Program Files (x86)\\Winamp\\clamp /VOLMAX");
                }
                
            } catch (Exception e) {
                System.err.println("Caught " + e +  " while reading sensor samples.");
                throw e;
            }
        }
    }
    
    /**
     * Start up the host application.
     *
     * @param args any command line arguments
     */
    public static void main(String[] args) throws Exception {
        // register the application's name with the OTA Command server & start OTA running
        OTACommandServer.start("SendDataDemo");

        SunSpotHostApplication app = new SunSpotHostApplication();
        app.run();
    }
}
