/**
 * 
 */
package com.leapmotion.controller;

import com.leapmotion.interfaces.IWebBrowser;

/**
 * @author Johan Gusten
 * @version 1.0
 * @since 13-Nov-2014
 * 
 */
public class WebBrowserController implements IWebBrowser {

    @Override
    public void openNewTab() {
        System.out.println("Open new tab for web browser.");

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
    public void scrollUp() {
        // TODO Auto-generated method stub
        System.out.println("Scroll Up.");
    }

    @Override
    public void scrollDown() {
        // TODO Auto-generated method stub
        System.out.println("RScroll Down.");
    }

    @Override
    public void goPrevious() {
        // TODO Auto-generated method stub
        System.out.println("Go to previous page.");
    }

    @Override
    public void goNext() {
        // TODO Auto-generated method stub
        System.out.println("Go to next page.");
    }

    @Override
    public void refreshPage() {
        // TODO Auto-generated method stub
        System.out.println("Refresh the current web page.");
    }

    @Override
    public void zoomInPage() {
        // TODO Auto-generated method stub
        System.out.println("Zoom in current page.");
    }

    @Override
    public void zoomOutPage() {
        // TODO Auto-generated method stub
        System.out.println("Zoom out current page.");
    }

    @Override
    public void copyTextSelection() {
        // TODO Auto-generated method stub
        System.out.println("Copy selection text.");
    }

    @Override
    public void pasteTextSelection() {
        // TODO Auto-generated method stub
        System.out.println("Paste selection text.");
    }

    @Override
    public void closeWebBrowser() {
        // TODO Auto-generated method stub
        System.out.println("Close web browser.");
    }

}
