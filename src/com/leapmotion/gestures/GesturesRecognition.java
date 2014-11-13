/**
 * 
 */
package com.leapmotion.gestures;

import java.io.IOException;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;

/**
 * @author Le Hong Quan
 * @version 1.0
 * @since 13-Nov-2014
 *
 */
public class GesturesRecognition extends Listener{

    /**
     * The Controller object is initialized.
     */
    public void onInit(Controller controller) {
        System.out.println("Initialized.");
    }

    /**
     * The Controller connects to the Leap Motion
     * service/daemon and the Leap Motion hardware is attached.
     */
    public void onConnect(Controller controller) {
        System.out.println("Connected.");
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
    }

    /**
     * The status of a Leap Motion hardware device changes.
     */
    public void onDeviceChange() {
        System.out.println("Device changes.");
    }

    /**
     * The Controller disconnects from the Leap Motion
     * service/daemon or the Leap Motion hardware is removed.
     */
    public void onDisconnect(Controller controller) {
        // Note: not dispatched when running in a debugger.
        System.out.println("Disconnected.");
    }

    /**
     * The Controller object is destroyed.
     */
    public void onExit(Controller controller) {
        System.out.println("Exit.");
    }

    /**
     * The application has lost operating system input
     * focus. The application will stop receiving tracking data
     * unless it has set the BACKGROUND_FRAMES_POLICY.
     */
    public void onFocusLost() {
        System.out.println("Focus lost.");
    }

    /**
     * The Controller has connected to the Leap Motion service/daemon.
     */
    public void onServiceConnect() {
        System.out.println("Service connected.");
    }

    /**
     * The Controller has lost its connection to the Leap Motion
     * service/daemon.
     */
    public void onServiceDisconnect() {
        System.out.println("Service disconnected.");
    }


    /**
     * The application has gained operating system input
     * focus and will start receiving tracking data.
     */
    public void onFocusGained(Controller controller) {
        System.out.println("Focus gained");
    } 

    /**
     * A new Frame of tracking data is available.
     */
    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();
        HandList hands = frame.hands();

        if (!hands.isEmpty()) {
            
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        // Create a sample listener and controller
        GesturesRecognition listener = new GesturesRecognition();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);

        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
    }

}
