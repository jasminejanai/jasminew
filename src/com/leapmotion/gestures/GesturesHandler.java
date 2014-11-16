/**
 * 
 */
package com.leapmotion.gestures;

import java.io.IOException;

import com.leapmotion.controller.WebBrowserController;
import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.utilities.Constants;

/**
 * @author Le Hong Quan
 * @version 1.0
 * @since 13-Nov-2014
 * 
 */
public class GesturesHandler extends Listener {

    public WebBrowserController webCtrl;

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

        for (int i = 0; i < gestures.count(); i++) {
            Gesture gesture = gestures.get(i);

            switch (gesture.type()) {
            case TYPE_CIRCLE:
                CircleGesture circle = new CircleGesture(gesture);

                switch (circle.state()) {
                case STATE_START:
                    // Handle starting circle gesture
                    break;
                case STATE_UPDATE:
                    // Handle continuing gestures
                    break;
                case STATE_STOP:
                    /*
                     * Calculate clock direction using the angle between circle
                     * normal and pointable
                     */
                    if (Math.round(handSphereRadius) <= Constants.HAND_SPHERE) {
                        if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 2) {
                            // Clockwise if angle is less than 90 degrees
                            System.out.println("clockwise");

                            // Call function to refresh the current page
                            webCtrl.refreshPage(); // TODO
                        } else {
                            System.out.println("counterclockwise");

                            // Call function to open new tab
                            webCtrl.openNewTab(); // TODO
                        }
                    }
                    break;
                case STATE_INVALID:
                    // Handle invalid circle gestures
                    break;
                default:
                    break;
                }
                break;

            case TYPE_SWIPE:
                SwipeGesture swipe = new SwipeGesture(gesture);
                float lifeTime = swipe.pointable().timeVisible();

                System.out.println("  Swipe id: " + swipe.id()
                // + ", start_position: " + swipe.startPosition()
                // + ", swipe_state: " + swipe.state()
                // + ", current_position: " + swipe.position()
                        + ", direction: " + swipe.direction() + ", speed: " + swipe.speed() + ", duration: " + swipe.duration());

                switch (gesture.state()) {
                case STATE_START:
                    // Handle starting gestures
                    break;
                case STATE_UPDATE:
                    // Handle continuing gestures
                    break;
                case STATE_STOP:
                    // Get absolute movement along the x and y axis
                    xAbs = Math.abs(swipe.direction().getX());
                    yAbs = Math.abs(swipe.direction().getY());

                    if (Math.round(handSphereRadius) > Constants.HAND_SPHERE) {
                        if (lifeTime > 0.3) {
                            System.out.println("Waving.");

                            // Call function to close web browser
                            webCtrl.closeWebBrowser(); // TODO
                        } else {
                            if (xAbs > 0.3) {
                                if (swipe.direction().getX() < 0) {
                                    System.out.println("Swipe Left.");

                                    // Call function to Go Previous
                                    webCtrl.goPrevious(); // TODO
                                } else {
                                    System.out.println("Swipe Right.");

                                    // Call function to Go Next
                                    webCtrl.goNext(); // TODO
                                }
                            } else if (yAbs > 0.3) {
                                if (swipe.direction().getY() < 0) {
                                    System.out.println("Swipe Down.");

                                    // Call function to Scroll Down.
                                    webCtrl.scrollDown(); // TODO
                                } else {
                                    System.out.println("Swipe Up.");

                                    // Call function to Scroll Up
                                    webCtrl.scrollUp(); // TODO
                                }
                            }
                        }
                    }
                    break;
                default:
                    // Handle unrecognized states
                    break;
                }
                break;
            default:
                System.out.println("Unknown gesture type.");
                break;
            }
        }

        if (!frame.hands().isEmpty() || !gestures.isEmpty()) {
            System.out.println();
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

        listener.webCtrl = new WebBrowserController();

        // Keep this process running until the user closes the browser
        while(true) {
            if(!listener.webCtrl.browserIsOpen()) {
                break;
            }
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
    }

}
