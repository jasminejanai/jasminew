/**
 * 
 */
package com.leapmotion.utilities;

import java.util.ArrayList;

/**
 * @author Le Hong Quan
 * @author Johan Gusten
 * @version 1.0
 * @since 13-Nov-2014
 * 
 */
public class Common {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public boolean isMac() {
        return (OS.indexOf("mac") >= 0);

    }

    public boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);

    }

    public boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);

    }

    /**
     * Calculate the average of all element in array list.
     * 
     * @param arr
     * @return
     */
    public float average(ArrayList<Float> arr) {
        float sum = 0.0f;
        int size = arr.size();

        if (arr == null || arr.isEmpty()) {
            return 0.0f;
        } else {
            for (int i = 0; i < size; i++) {
                sum += arr.get(i);
            }
        }
        return sum = sum / size;
    }
}
