/**
 * 
 */
package com.leapmotion.gestures;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import com.leapmotion.controller.WebBrowserController;
import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.utilities.Common;

/**
 * @author Le Hong Quan
 * @version 1.0
 * @since 13-Nov-2014
 * 
 */
public class GesturesHandler extends Listener {

    public WebBrowserController webCtrl;
    public Common cm = new Common();
    public int counter = 0;
    public boolean isTrackingStarted = false;
    public boolean isFinished = false;
    public HashMap<String, String> handMap = new HashMap<>();
    public ArrayList<Float> tempY = new ArrayList<Float>();
    public ArrayList<Float> tempX = new ArrayList<Float>();

    // Screen resolution, it should match the current screen resolution for more precise movements - pending
    //public int SCREEN_X = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
    //public int SCREEN_Y = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;

    /**
     * The Controller object is initialized.
     */
    public void onInit(Controller controller) {
        System.out.println("Initialized.");
    }

    /**
     * The Controller connects to the Leap Motion service/daemon and the Leap
     * Motion hardware is attached.
     */
    public void onConnect(Controller controller) {
        System.out.println("Connected.");
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);

    }

    /**
     * The status of a Leap Motion hardware device changes.
     */
    public void onDeviceChange() {
        System.out.println("Device changes.");
    }

    /**
     * The Controller disconnects from the Leap Motion service/daemon or the
     * Leap Motion hardware is removed.
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
     * The application has lost operating system input focus. The application
     * will stop receiving tracking data unless it has set the
     * BACKGROUND_FRAMES_POLICY.
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
     * The Controller has lost its connection to the Leap Motion service/daemon.
     */
    public void onServiceDisconnect() {
        System.out.println("Service disconnected.");
    }

    /**
     * The application has gained operating system input focus and will start
     * receiving tracking data.
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
        GestureList gestures = frame.gestures();

        float xAbs = 0.0f;
        float yAbs = 0.0f;
        float handSphereRadius = 2 * hands.get(0).sphereRadius();

        handMap.put("handId", String.valueOf(0));
        handMap.put("zAxis", String.valueOf(0.0f));
        handMap.put("xAxis", String.valueOf(0.0f));
        //Get hands
        if (!hands.isEmpty()) {
            for (Hand hand : frame.hands()) {
                counter++;

                // After 200 milliseconds, starts to track data
                if (counter == 200) {
                    System.out.println("counter: " + counter);
                    isTrackingStarted = true;
                }

                if (isTrackingStarted) {
                    float translationIntentFactor = hand.translationProbability(frame);
                    System.out.println("translationIntentFactor: " + translationIntentFactor);

                    // This makes sure the hand is moving
                    if (translationIntentFactor > 0.5f) {

                        // Put handId, y-axis and x-axis in to a map to compare
                        while (hand.id() != Integer.parseInt(handMap.get("handId"))) {
                            handMap.put("handId", String.valueOf(hand.id()));
                            handMap.put("yAxis", String.valueOf(hand.palmPosition().getY()));
                            handMap.put("xAxis", String.valueOf(hand.palmPosition().getX()));
                        }
                        tempY.add(Float.parseFloat(handMap.get("yAxis")));
                        tempX.add(Float.parseFloat(handMap.get("xAxis")));

                        float _latestY = 0f;
                        float _prevY = 0f;
                        float subtract = 0f;

                        try {
                            _latestY = tempY.get(tempY.size() - 1);
                            _prevY = tempY.get(tempY.size() - 2);
                            /*
                             * If subtraction between current position and the previous one
                             * is larger than 2, allows to zoom.
                             */
                            subtract = Math.abs((_latestY - _prevY));
                            int yRetval = Float.compare(_prevY, _latestY);

                            // Calculate the average movement along y and x axis
                            int sumY = Math.abs(Math.round(cm.average(tempY)));
                            int sumX = Math.abs(Math.round(cm.average(tempX)));

                            // This makes sure user moves hand along y-axis more than x-axis
                            if (sumX < sumY) {
                                if (yRetval > 0 && subtract >= 2) { // prev > latest
                                    System.out.println("Down. Zoom in.");
                                    zoomIn();
                                } else if (yRetval < 0 && subtract >= 2) { // prev < latest
                                    System.out.println("Up. Zoom out.");
                                    zoomOut();
                                }

                                // Force system to clear array list
                                tempX.clear();
                                tempY.clear();
                                System.gc();
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            ex.getMessage();
                        }
                        System.out.println("");
                    } else {
                        System.out.println("No tracking.");
                        System.out.println("");
                    }
                }
            }
        } else {
            isFinished = true;
            isTrackingStarted = false;
            counter = 0;
        }

        // Get gestures
        // Note: DO NOT use hand sphere, since user has no fingers.
        for (int i = 0; i < gestures.count(); i++) {
            Gesture gesture = gestures.get(i);

            switch (gesture.type()) {
            case TYPE_CIRCLE:
                CircleGesture circle = new CircleGesture(gesture);

                /*System.out.println("  Circle id: " + circle.id()
                        + ", " + circle.state()
                        + ", progress: " + circle.progress());*/

                if (circle.state() == State.STATE_STOP) {
                    System.out.println("circle stop.");
                    /*
                     * Calculate clock direction using the angle between circle
                     * normal and pointable.
                     */
                    System.out.println("circle-" + Math.round(handSphereRadius));
                  //  if (Math.round(handSphereRadius) <= Constants.HAND_SPHERE) {
                        if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 2) {
                            // Clockwise if angle is less than 90 degrees
                            System.out.println("clockwise");
                            webCtrl.refreshPage();
                        } else {
                            System.out.println("counterclockwise");
                            openNewTab();
                        }
                    //}
                }
                break;

            case TYPE_SWIPE:
                SwipeGesture swipe = new SwipeGesture(gesture);
                float lifeTime = swipe.pointable().timeVisible();

                /*System.out.println("  Swipe id: " + swipe.id()
                        + ", start_position: " + swipe.startPosition()
                        + ", swipe_state: " + swipe.state()
                        + ", current_position: " + swipe.position()
                        + ", direction: " + swipe.direction()
                        + ", speed: " + swipe.speed()
                        + ", durationSeconds: " + swipe.durationSeconds()
                        + ", lifeTime: " + swipe.pointable().timeVisible());*/

                // Get absolute movement along the x-axis and y-axis
                xAbs = Math.abs(swipe.direction().getX());
                yAbs = Math.abs(swipe.direction().getY());
                System.out.println();

                if (swipe.state() == State.STATE_STOP) {
                    System.out.println("End swipe.");

                   // if (Math.round(handSphereRadius) > Constants.HAND_SPHERE) {
                        if (lifeTime > 1.5f) {
                            System.out.println("It's waving...");
                             webCtrl.closeWebBrowser();
                        } else if (lifeTime <= 1.5f && xAbs > 0.3) {
                            if (swipe.direction().getX() < 0) {
                                System.out.println("Swipe Left.");
                                webCtrl.goPrevious();
                            } else {
                                System.out.println("Swipe Right.");
                                webCtrl.goNext();
                            }
                        } else if (lifeTime <= 1.5f && yAbs > 0.3) {
                            if (swipe.direction().getY() < 0) {
                                System.out.println("Swipe Down.");
                                webCtrl.scrollDown();
                            } else {
                                System.out.println("Swipe Up.");
                                webCtrl.scrollUp();
                            }
                        }
                    //}
                }
                break;
            default:
                System.out.println("Unknown gesture type.");
                break;
            }
        }

        if (!frame.hands().isEmpty() || !gestures.isEmpty()) {
            // TODO
        }
    }

    /**
     * Open new tab using hotkey.
     * Note: This depends on the OS system and browser settings.
     */
    public void openNewTab() {
        Robot keyHandler;
        try {
            keyHandler = new Robot();
            if (cm.isWindows() || cm.isUnix()) {
                keyHandler.keyPress(KeyEvent.VK_CONTROL);
                keyHandler.keyPress(KeyEvent.VK_T);
                keyHandler.keyRelease(KeyEvent.VK_CONTROL);
                keyHandler.keyRelease(KeyEvent.VK_T);
            } else if (cm.isMac()) {
                // TODO
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * Zoom in the current web page.
     * Note: This depends on the OS system and browser settings.
     */
    public void zoomIn() {
        Robot keyHandler;
        try {
            keyHandler = new Robot();
            if (cm.isWindows() || cm.isUnix()) {
                keyHandler.keyPress(KeyEvent.VK_CONTROL);
                keyHandler.keyPress(KeyEvent.VK_MINUS);
                keyHandler.keyRelease(KeyEvent.VK_CONTROL);
                keyHandler.keyRelease(KeyEvent.VK_MINUS);
            } else if (cm.isMac()) {
                // TODO
            }
            
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * Zoom out the current web page.
     * Note: This depends on the OS system and browser settings.
     */
    public void zoomOut() {
        Robot keyHandler;
        try {
            keyHandler = new Robot();
            if (cm.isWindows() || cm.isUnix()) {
                keyHandler.keyPress(KeyEvent.VK_CONTROL);
                keyHandler.keyPress(KeyEvent.VK_ADD);
                keyHandler.keyRelease(KeyEvent.VK_CONTROL);
                keyHandler.keyRelease(KeyEvent.VK_ADD);
            } else if (cm.isMac()) {
                // TODO
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // Create a sample listener and controller
        GesturesHandler listener = new GesturesHandler();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);
        // Tell controller that application runs in the background
        controller.setPolicyFlags(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);

        listener.webCtrl = new WebBrowserController();

        // Keep this process running until the user closes the browser
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (!listener.webCtrl.browserIsOpen()) {
                break;
            }
        }
        // Keep this process running until Enter is pressed
        /*
         * System.out.println("Press Enter to quit..."); try { System.in.read();
         * } catch (IOException e) { e.printStackTrace(); }
         */

        // Remove the sample listener when done
        controller.removeListener(listener);
    }

}
