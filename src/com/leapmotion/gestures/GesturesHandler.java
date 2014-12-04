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
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;
import com.leapmotion.utilities.Common;

/**
 * @author Quan Le
 * @version 1.0
 * @since 13-Nov-2014
 * 
 */
public class GesturesHandler extends Listener {

    public WebBrowserController webCtrl;
    public Common cm = new Common();
    public int counter = 0;
    public int timer = 0;
    public int delay = 0;
    public int handIdZooming = 0;
    public int handIdPausing = 0;
    public int HORIZONTAL_SCREENS = 2;
    public int VERTICAL_SCREENS = 1;
    public int handIdGoBack = 0;
    public int handIdGoNext = 0;
    public float cur_x = 0, cur_y = 0;
    public float zLeftHandStart = 0.0f;
    public float zHandStartGoPrev = 0.0f;
    public boolean isTrackingStarted = false;
    public boolean isFinished = false;
    public boolean isZooming = true;
    public boolean isMoved = false;
    public boolean isSwipe = false;
    public boolean isContinueGoBack = true;
    public boolean isContinueGoNext = true;
    public boolean isGoBack = false;
    public boolean isGoNext = false;
    public HashMap<String, String> handMap = new HashMap<>();
    public HashMap<String, String> rightHandMap = new HashMap<>();
    public HashMap<String, String> leftHandMap = new HashMap<>();
    public ArrayList<Float> tempX = new ArrayList<Float>();
    public ArrayList<Float> tempY = new ArrayList<Float>();
    public ArrayList<Float> tempZ = new ArrayList<Float>();
    public ArrayList<Float> tempXR = new ArrayList<Float>();
    public ArrayList<Float> tempYR = new ArrayList<Float>();
    public ArrayList<Float> tempXL = new ArrayList<Float>();
    public ArrayList<Float> tempYL = new ArrayList<Float>();
    public Hand leftHand = null;
    public Hand rightHand = null;
    public Hand hand = null;

    /*
     * Screen resolution, it should match the current screen
     * resolution for more precise movements
     */
    int SCREEN_X = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
    int SCREEN_Y = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;

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
        Frame frame = controller.frame();
        HandList hands = frame.hands();
        GestureList gestures = frame.gestures();

        // Initialize handMap with default value
        handMap.put("handId", String.valueOf(0));
        handMap.put("xAxis", String.valueOf(0.0f));
        handMap.put("yAxis", String.valueOf(0.0f));
        handMap.put("zAxis", String.valueOf(0.0f));

        // Get hands
        if (!hands.isEmpty()) {
            // for (Hand hand : frame.hands()) {
            for (int i = 0; i < hands.count(); i++) {
                hand = hands.get(i);
                counter++;

                // After 100 milliseconds, starts to track data
                if (counter == 100) {
                    System.out.println("counter: " + counter);
                    isTrackingStarted = true;
                }

                if (isTrackingStarted) {

                    /*
                     * If handId is create new, allows to goBack.
                     */
                    if (isGoBack) {
                        if (hand.id() == handIdGoBack) {
                            isContinueGoBack = false;
                        } else {
                            isContinueGoBack = true;
                        }
                    }

                    /*
                     * If handId is create new, allows to goNext.
                     */
                    if (isGoNext) {
                        if (hand.id() == handIdGoNext) {
                            isContinueGoNext = false;
                        } else {
                            isContinueGoNext = true;
                        }
                    }

                    switch (numberOfHand(frame)) {
                    // One hand.
                    case 1:
                        float translationIntentFactor = hand.translationProbability(frame);
                        /*
                         * If translationIntentFactor is larger than 0.5, then
                         * hand is moving, whereas, hand is holding.
                         */
                        if (isMoving(translationIntentFactor)) {
                            float minimumDistance = hand.palmPosition().distanceTo(Vector.zero());
                            //System.out.println("minimumDistance: " + minimumDistance);

                            // Push hand information into a map to compare
                            handMap = pushHandInfo(hand);
                            // Get coordinates of hand
                            tempX.add(Float.parseFloat(handMap.get("xAxis")));
                            tempY.add(Float.parseFloat(handMap.get("yAxis")));
                            tempZ.add(Float.parseFloat(handMap.get("zAxis")));

                            /*
                             * If distance between the hand and vector zero of
                             * Leap devices is larger than 150, then allows to
                             * zoom, whereas, swipe.
                             */
                            if (minimumDistance > 250.0f) {
                                System.out.println("Start for Zooming.");
                                handIdZooming = hand.id();
                                if (handIdZooming != handIdPausing) {
                                    isZooming = true;
                                }
                                switch (checkZoom(1, tempZ, isZooming)) {
                                case 0:
                                    System.out.println("Cannot recognize zooming.");
                                    break;
                                case 1:
                                    System.out.println("Down. Zoom in.");
                                    timer = 0;
                                    zoomIn();
                                    break;
                                case 2:
                                    System.out.println("Up. Zoom out.");
                                    timer = 0;
                                    zoomOut();
                                    handIdZooming = hand.id();
                                    break;
                                default:
                                    break;
                                }
                            } else {
                                switch (checkSwipe(tempX, tempY)) {
                                case 0:
                                    System.out.println("Cannot recognize Swiping.");
                                    break;
                                case 1:
                                    if (isContinueGoBack) {
                                        System.out.println("This is the first time to go back.");
                                        System.out.println("Left. Go Previous.");
                                        webCtrl.goPrevious();
                                        isGoBack = true;
                                        handIdGoBack = hand.id();
                                    }
                                    break;
                                case 2:
                                    if (isContinueGoNext) {
                                        System.out.println("This is the first time to go next.");
                                        System.out.println("Right. Go Next.");
                                        webCtrl.goNext();
                                        isGoNext = true;
                                        handIdGoNext = hand.id();
                                    }
                                    break;
                                case 3:
                                    System.out.println("Swipe Down. Scroll Down.");
                                    webCtrl.scrollDown();
                                    break;
                                case 4:
                                    System.out.println("Swipe Up. Srcoll Up.");
                                    webCtrl.scrollUp();
                                    break;
                                default:
                                    break;
                                }
                            }
                        } else {
                            /*
                             * If user pauses/holds his hand longer than 100
                             * milliseconds, then stop zooming.
                             */
                            timer++;
                            if (timer >= 100 && isZooming) {
                                isZooming = false;
                                handIdPausing = hand.id();
                                if (handIdZooming == handIdPausing) {
                                    isZooming = false;
                                }
                            }
                        }
                        break;
                    // Two hands
                    case 2:
                        Hand hand1 = frame.hands().get(0);
                        Hand hand2 = frame.hands().get(1);
                        Vector normal1 = hand1.palmNormal();
                        Vector normal2 = hand2.palmNormal();

                        // Define left-hand and right-hand
                        if (hand1.isLeft()) {
                            leftHand = hand1;
                            rightHand = hand2;
                        } else if (hand1.isRight()) {
                            leftHand = hand2;
                            rightHand = hand1;
                        }

                        // Initialize right-hand map with default value
                        rightHandMap.put("rightHandId", String.valueOf(0));
                        rightHandMap.put("xAxis", String.valueOf(0.0f));
                        rightHandMap.put("yAxis", String.valueOf(0.0f));
                        rightHandMap.put("zAxis", String.valueOf(0.0f));

                        // Initialize left-hand map with default value
                        leftHandMap.put("leftHandId", String.valueOf(0));
                        leftHandMap.put("xAxis", String.valueOf(0.0f));
                        leftHandMap.put("yAxis", String.valueOf(0.0f));
                        leftHandMap.put("zAxis", String.valueOf(0.0f));

                        float translationIntentFactor_L = leftHand.translationProbability(frame);
                        float translationIntentFactor_R = rightHand.translationProbability(frame);
                        float minimumDistance_L = leftHand.palmPosition().distanceTo(Vector.zero());
                        float minimumDistance_R = rightHand.palmPosition().distanceTo(Vector.zero());
                        float angleBetweenTwoHands = normal1.angleTo(normal2);
                        System.out.println("minimumDistance_L: " + minimumDistance_L);
                        System.out.println("minimumDistance_R: " + minimumDistance_R);

                        // Push right-hand and left-hand info into a map
                        rightHandMap = pushHandInfo(rightHand);
                        leftHandMap = pushHandInfo(leftHand);

                        // Get x-axis and y-axis of right-hand
                        tempXR.add(Float.parseFloat(rightHandMap.get("xAxis")));
                        tempYR.add(Float.parseFloat(rightHandMap.get("yAxis")));

                        // Get x-axis and y-axis of left-hand
                        tempXL.add(Float.parseFloat(leftHandMap.get("xAxis")));
                        tempYL.add(Float.parseFloat(leftHandMap.get("yAxis")));

                        /*
                         * If holds on left-hand and swipe right-hand to right,
                         * then open new tab, whereas, two hands cross together,
                         * then refresh the page.
                         */
                        if (minimumDistance_L < 250.0f && minimumDistance_R < 250.0f) {
                            if (!isMoving(translationIntentFactor_L)
                                    && isMoving(translationIntentFactor_R)&& angleBetweenTwoHands > 0.5f) {
                                if (checkSwipe(tempXR, tempYR) == 2) {
                                    System.out.println("Open new tab.");
                                    webCtrl.openNewTab();
                                } else {
                                    System.out.println("Do nothing!");
                                }
                            } else if (isMoving(translationIntentFactor_L)
                                    && isMoving(translationIntentFactor_R) && angleBetweenTwoHands < 0.5f) {
                                if (isCrossedHand(normal1.roll(), normal2.roll()) && leftHand.palmPosition().distanceTo(rightHand.palmPosition()) < 100.0f) {
                                    System.out.println("Cross hands. Refresh the page.");
                                    webCtrl.refreshPage();
                                }
                            }
                        } else if (minimumDistance_L > 250.0f && minimumDistance_R > 250.0f) {
                            if (isMoving(translationIntentFactor_L) && isMoving(translationIntentFactor_R)) {
                                if (checkSwipe(tempXR, tempYR) == 3 && checkSwipe(tempXL, tempYL) == 3) {
                                    isSwipe = true;
                                    zLeftHandStart = leftHand.palmPosition().getZ();
                                }
                            }
                        }
                        break;
                    default:
                        break;
                    }
                }
            }
        } else {
            isFinished = true;
            isTrackingStarted = false;
            counter = 0;

            /*
             * If the distance between left-hand position when user starts to
             * swipe and the position when user finishes swiping is larger /
             * equals 10, then allows to close the web page.
             */
            if (isSwipe) {
                if (Math.abs((zLeftHandStart - leftHand.palmPosition().getZ())) >= 10.0f) {
                    System.out.println("Right-hand and left-hand are moving down. Close the web.");
                    webCtrl.closeWebBrowser();
                }
            }
        }

        if (!frame.hands().isEmpty() || !gestures.isEmpty()) {
            // TODO
        }
    }

    /**
     * Move mouse on Leap Motion.
     * 
     * @param x
     * @param y
     */
    public void moveMouse(float x, float y) {
        Robot mouseHandler;

        if (cur_x != x || cur_y != y) {
            cur_x = x;
            cur_y = y;
            try {
                mouseHandler = new Robot();
                mouseHandler.mouseMove((int) x, (int) y);
            } catch (AWTException e) {
                e.getMessage();
            }
        }
    }

    /**
     * Push hand information into a map.
     * 
     * @param _hand
     * @return
     */
    public HashMap<String, String> pushHandInfo(Hand _hand) {
        while (_hand.id() != Integer.parseInt(handMap.get("handId"))) {
            handMap.put("handId", String.valueOf(_hand.id()));
            handMap.put("xAxis", String.valueOf(_hand.palmPosition().getX()));
            handMap.put("yAxis", String.valueOf(_hand.palmPosition().getY()));
            handMap.put("zAxis", String.valueOf(_hand.palmPosition().getZ()));
        }
        return handMap;
    }

    /**
     * Cross hand gesture handler.
     * 
     * @param x
     * @param y
     * @return
     */
    public boolean isCrossedHand(float x, float y) {
        if ((x < 5 || x > -5) && (y < 5 || y > -5)) {
            // if (Math.abs(x) < 2 && Math.abs(y) < 2) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Swipe gesture handler.
     * 
     * @param arrX
     * @param arrY
     * @return
     */
    public int checkSwipe(ArrayList<Float> arrX, ArrayList<Float> arrY) {
        float _latestX = 0f;
        float _prevX = 0f;
        float _latestY = 0f;
        float _prevY = 0f;
        float subY = 0f;

        try {
            _latestX = arrX.get(arrX.size() - 1);
            _prevX = arrX.get(arrX.size() - 2);
            _latestY = arrY.get(arrY.size() - 1);
            _prevY = arrY.get(arrY.size() - 2);
            int xRetval = Float.compare(_prevX, _latestX);
            int yRetval = Float.compare(_prevY, _latestY);

            // Calculate the average hand's movement along y and x axis
            int sumX = Math.abs(Math.round(cm.average(arrX)));
            int sumY = Math.abs(Math.round(cm.average(arrY)));
            System.out.println("sumX: " + sumX);
            System.out.println("sumX: " + sumY);

            /*
             * If subtraction between current position and the previous one is
             * larger than 2, allows to scroll.
             */
            subY = Math.abs((_latestY - _prevY));

            // Horizontal swiping
            if (sumX > sumY) {
                if (xRetval > 0) {
                    return 1;
                } else if (xRetval < 0) {
                    return 2;
                }
            } else if (sumX < sumY) {
                if (yRetval > 0 && subY >= 2) { // Vertical swiping.
                    return 3;
                } else if (yRetval < 0 && subY >= 2) {
                    return 4;
                }
            }

            arrX.clear();
            arrY.clear();
            System.gc();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.getMessage();
        }
        return 0;
    }

    /**
     * Zooming gesture handler.
     * 
     * @param arr
     * @param isZoom
     * @return
     */
    public int checkZoom(int n, ArrayList<Float> arr, boolean isZoom) {
        float _latest = 0f;
        float _prev = 0f;
        float sub = 0f;

        try {
            _latest = arr.get(arr.size() - 1);
            _prev = arr.get(arr.size() - 2);
            int retval = Float.compare(_prev, _latest);
            /*
             * If subtraction between current position and the previous one is
             * larger than 2, allows to zoom.
             */
            sub = Math.abs((_latest - _prev));
            if (n == 1) {
                if (retval > 0 && sub >= 2 && isZoom) {
                    return 1;
                } else if (retval < 0 && sub >= 2 && isZoom) {
                    return 2;
                }
                arr.clear();
                System.gc();
            } else if (n == 2) {
                if (retval > 0 && sub >= 2 && !isZoom) {
                    return 3;
                } else if (retval < 0 && sub >= 2 && !isZoom) {
                    return 4;
                }
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            e.getMessage();
        }
        return 0;
    }

    /**
     * Check how many hands in a frame.
     * 
     * @param ctr
     * @return
     */
    public int numberOfHand(Frame fr) {
        if (fr.hands().count() == 1) {
            return 1;
        } else if (fr.hands().count() == 2) {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * Check hand is moving or not.
     * 
     * @param x
     * @return
     */
    public boolean isMoving(float x) {
        if (x > 0.5f) {
            return true;
        } else
            return false;
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
        GesturesHandler listener = new GesturesHandler();
        Controller controller = new Controller();

        controller.addListener(listener);

        // Tell controller that application runs in the background
        controller.setPolicyFlags(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);

        listener.webCtrl = new WebBrowserController();

        // Keep this process running until the user closes the browser
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!listener.webCtrl.browserIsOpen()) {
                break;
            }
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
    }

}