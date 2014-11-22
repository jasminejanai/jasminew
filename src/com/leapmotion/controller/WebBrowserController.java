/**
 * 
 */
package com.leapmotion.controller;

import java.io.File;
import java.util.Scanner;
import java.util.Stack;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.leapmotion.interfaces.IWebBrowser;
import com.leapmotion.utilities.Common;

/**
 * @author Johan Gustafsson
 * @version 1.0
 * @since 13-Nov-2014
 * 
 */
public class WebBrowserController implements IWebBrowser {

    public Common cm = new Common();
    private WebDriver driver;
    private JavascriptExecutor jse;
    private boolean wantsToQuit;
    private Stack<String> previousWindowHandles;

    public WebBrowserController() {
        wantsToQuit = false;

        if (cm.isWindows()) {
            driver = getWindowsBrowserDriver();

            // Do a "fancy" wait loop so we are sure there is a window to
            // maximize before trying to.
            while (driver.getWindowHandles().isEmpty()) {
            }
            previousWindowHandles = new Stack<String>();
            driver.manage().window().maximize();
            // TODO: Remove test code
            driver.navigate().to("http://www.bbc.com/");
            // driver.navigate().back();
        }

        // If there is a driver, cast it and save it for javascript execution.
        if (driver != null) {
            jse = (JavascriptExecutor) driver;
        }
    }

    /**
     * Note: This does not work, just open new window. Refer:
     * GestureHandler.java.
     */
    @Override
    public void openNewTab() {
        System.out.println("Open a new tab.");

        // TODO: Find a better way to do this, I don't like relying on shortcuts.

        //Store previous window handle so we cannot return to that once we are done.
        previousWindowHandles.push(driver.getWindowHandle());

        if (driver instanceof ChromeDriver) {
            jse.executeScript("window.open(\"https://www.google.com\");");
        } else {
            driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "t");
        }

        // Switch to last(new) window handle.
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
    }

    @Override
    public void scrollUp() {
        // TODO: Do scrolling a bit more responsive to how fast the user swiped.
        System.out.println("Scroll Up.");

        jse.executeScript("window.scrollBy(0,-100)", "");
    }

    @Override
    public void scrollDown() {
        // TODO: Do scrolling a bit more responsive to how fast the user swiped.
        System.out.println("RScroll Down.");

        jse.executeScript("window.scrollBy(0,100)", "");
    }

    @Override
    public void goPrevious() {
        System.out.println("Go to previous page.");

        driver.navigate().back();
    }

    @Override
    public void goNext() {
        System.out.println("Go to next page.");

        driver.navigate().forward();
    }

    @Override
    public void refreshPage() {
        System.out.println("Refresh the current web page.");

        driver.navigate().refresh();
    }

    /**
     * Note: This throws error. Refer: GestureHandler.java.
     */
    @Override
    public void zoomInPage() {
        System.out.println("Zoom in current page.");

        // TODO: Find a better way to do this, I don't like relying on shortcuts.
        driver.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL, Keys.SUBTRACT));
    }

    /**
     * Note: This throws error. Refer: GestureHandler.java.
     */
    @Override
    public void zoomOutPage() {
        System.out.println("Zoom out current page.");

        // TODO: Find a better way to do this, I don't like relying on shortcuts.
        driver.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL, Keys.ADD));
    }

    /**
     * Note: Pending
     * 
     * @return
     */
    @Override
    public void copyTextSelection() {
        // TODO Auto-generated method stub
        System.out.println("Copy selection text.");
    }

    /**
     * Note: Pending.
     */
    @Override
    public void pasteTextSelection() {
        // TODO Auto-generated method stub
        System.out.println("Paste selection text.");
    }

    @Override
    public void closeWebBrowser() {
        System.out.println("Close web browser.");

        wantsToQuit = true;
        driver.quit();
    }

    /*
     * Returns a new WebDriver of the default browser on a Windows machine.
     */
    private WebDriver getWindowsBrowserDriver() {
        try {
            // Get registry entry containing browser information
            Process process = Runtime.getRuntime().exec("REG QUERY HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\Shell\\Associations\\UrlAssociations\\http\\UserChoice");
            Scanner kb = new Scanner(process.getInputStream());
            while (kb.hasNextLine()) {
                String regOutput = kb.nextLine().toLowerCase();
                if (regOutput.contains("progid")) {
                    System.out.println("regOutput: " + regOutput);
                    String browserName = regOutput.substring(regOutput.lastIndexOf(" ") + 1);
                    browserName = browserName.toLowerCase();

                    // Find what browser is set to default and create a new
                    // driver for that browser for further use.
                    if (browserName.contains("ie")) {
                        kb.close();
                        File driverFile = new File("libs/IEDriverServer.exe");
                        System.setProperty("webdriver.ie.driver", driverFile.getAbsolutePath());
                        System.out.println("InternetExplorer should start");
                        return new InternetExplorerDriver();
                    } else if (browserName.contains("chrome")) {
                        System.out.println("browserName: " + browserName);
                        kb.close();
                        File driverFile = new File("libs/chromedriver.exe");
                        System.setProperty("webdriver.chrome.driver", driverFile.getAbsolutePath());
                        System.out.println("Chrome should start");
                        return new ChromeDriver();
                    } else if (browserName.contains("firefox")) {
                        kb.close();
                        System.out.println("Firefox should start");
                        return new FirefoxDriver();
                    }
                }
            }
            kb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // No browser was found, abandon ship.
        return null;
    }

    /*
     * Returns the default browser on a Linux machine.
     */
    private WebDriver getLinuxDefaultBrowser() {
        return null;
    }

    /*
     * Returns the default browser on a Mac machine.
     */
    private WebDriver getMacDefaultBrowser() {
        return null;
    }

    public void killController() {
        driver.quit();
    }

    public boolean browserIsOpen() {
        // If there is a window handle attached to the driver, it means the
        // browser is still open.
        if (wantsToQuit) {
            // killController();
            return false;
        }
        return true;
    }
}
