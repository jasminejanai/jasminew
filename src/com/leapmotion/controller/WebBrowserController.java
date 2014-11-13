/**
 * 
 */
package com.leapmotion.controller;

import com.leapmotion.implement.WebBrowserBehavior;

/**
 * @author Johan Gusten
 * @version 1.0
 * @since 13-Nov-2014
 * 
 */
public class WebBrowserController implements WebBrowserBehavior {

    @Override
    public void OpenNewTab() {
        String url = "http://www.google.com";
        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();

        try {
            if (os.indexOf("win") >= 0) {
                /*
                 * This does not support showing urls in
                 * the form of "page.html#nameLink"
                 */
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.indexOf("mac") >= 0) {
                rt.exec("open " + url);
            } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
                /*
                 * Do a best guess on unix until we get a platform
                 * independent way. Build a list of browsers to try, in this order.
                 */
                String[] browsers = { "epiphany", "firefox", "mozilla", "konqueror", "netscape", "opera", "links", "lynx" };
                /*
                 * Build a command string which looks like
                 * "browser1 "url" || browser2 "url" ||..."
                 */
                StringBuffer cmd = new StringBuffer();
                for (int i = 0; i < browsers.length; i++)
                    cmd.append((i == 0 ? "" : " || ") + browsers[i] + " \"" + url + "\" ");
                rt.exec(new String[] { "sh", "-c", cmd.toString() });
            } else {
                return;
            }
        } catch (Exception e) {
            return;
        }
        return;
    }

    @Override
    public void ScrollUp() {
        // TODO Auto-generated method stub

    }

    @Override
    public void ScrollDown() {
        // TODO Auto-generated method stub

    }

    @Override
    public void GoPrevious() {
        // TODO Auto-generated method stub

    }

    @Override
    public void GoNext() {
        // TODO Auto-generated method stub

    }

    @Override
    public void RefreshPage() {
        // TODO Auto-generated method stub

    }

    @Override
    public void ZoomInPage() {
        // TODO Auto-generated method stub

    }

    @Override
    public void ZoomOutPage() {
        // TODO Auto-generated method stub

    }

    @Override
    public void CopyTextSelection() {
        // TODO Auto-generated method stub

    }

    @Override
    public void PasteTextSelection() {
        // TODO Auto-generated method stub

    }

}
