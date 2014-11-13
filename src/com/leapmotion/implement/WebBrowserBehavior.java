/**
 * 
 */
package com.leapmotion.implement;

/**
 * @author Le Hong Quan
 * @version 1.0
 * @since 13-Nov-2014
 * 
 */
public interface WebBrowserBehavior {

    public void OpenNewTab();

    public void ScrollUp();

    public void ScrollDown();

    public void GoPrevious();

    public void GoNext();

    public void RefreshPage();

    public void ZoomInPage();

    public void ZoomOutPage();

    public void CopyTextSelection();

    public void PasteTextSelection();
}
