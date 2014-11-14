/**
 * 
 */
package com.leapmotion.interfaces;

/**
 * @author Le Hong Quan
 * @version 1.0
 * @since 13-Nov-2014
 * 
 */
public interface IWebBrowser {

    public void openNewTab();

    public void scrollUp();

    public void scrollDown();

    public void goPrevious();

    public void goNext();

    public void refreshPage();

    public void zoomInPage();

    public void zoomOutPage();

    public void copyTextSelection();

    public void pasteTextSelection();
}
