/**
 * 
 */
package com.leapmotion.controller;

import com.leapmotion.interfaces.IWebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.io.File;
import java.util.Scanner;

/**
 * @author Johan Gustafsson
 * @version 1.0
 * @since 13-Nov-2014
 * 
 */
public class WebBrowserController implements IWebBrowser {

    private WebDriver driver;
    private JavascriptExecutor jse;

    public WebBrowserController() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            driver = getWindowsBrowserDriver();

            //Do a "fancy" wait loop so we are sure there is a window to maximize before trying to.
            while(driver.getWindowHandles().isEmpty()) {}
            driver.manage().window().maximize();
        }

        //If there is a driver, cast it and save it for javascript execution.
        if(driver != null) {
            jse = (JavascriptExecutor)driver;
        }
    }

    @Override
    public void openNewTab() {
        System.out.println("Open a new tab.");

        //TODO: Find a better way to do this, I don't like relying on shortcuts.
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "t");
    }

    @Override
    public void scrollUp() {
        // TODO: Do scrolling a bit more responsive to how fast the user swiped.
        System.out.println("Scroll Up.");

        jse.executeScript("window.scrollBy(0,-250)", "");
    }

    @Override
    public void scrollDown() {
        // TODO: Do scrolling a bit more responsive to how fast the user swiped.
        System.out.println("RScroll Down.");

        jse.executeScript("window.scrollBy(0,250)", "");
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

    @Override
    public void zoomInPage() {
        System.out.println("Zoom in current page.");

        //TODO: Find a better way to do this, I don't like relying on shortcuts.
        driver.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL, Keys.ADD));
    }

    @Override
    public void zoomOutPage() {
        System.out.println("Zoom out current page.");

        //TODO: Find a better way to do this, I don't like relying on shortcuts.
        driver.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL, Keys.SUBTRACT));
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
        System.out.println("Close web browser.");

        driver.close();
    }

    /*
     * Returns a new WebDriver of the default browser on a Windows machine.
     */
    private WebDriver getWindowsBrowserDriver() {
        try
        {
            // Get registry entry containing browser information
            Process process = Runtime.getRuntime().exec("REG QUERY HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\Shell\\Associations\\UrlAssociations\\http\\UserChoice");
            Scanner kb = new Scanner(process.getInputStream());
            while (kb.hasNextLine())
            {
                String regOutput = kb.nextLine();
                if(regOutput.contains("ProgId")) {
                    String browserName = regOutput.substring(regOutput.lastIndexOf(" ")+1);
                    browserName = browserName.toLowerCase();

                    //Find what browser is set to default and create a new driver for that browser for further use.
                    if(browserName.contains("ie")) {
                        kb.close();
                        File driverFile = new File("libs/IEDriverServer.exe");
                        System.setProperty("webdriver.ie.driver", driverFile.getAbsolutePath());
                        System.out.println("InternetExplorer should start");
                        return new InternetExplorerDriver();
                    }
                    else if(browserName.contains("chrome")) {
                        kb.close();
                        File driverFile = new File("libs/chromedriver.exe");
                        System.setProperty("webdriver.chrome.driver", driverFile.getAbsolutePath());
                        System.out.println("Chrome should start");
                        return new ChromeDriver();
                    }
                    else if(browserName.contains("firefox")) {
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
        //If there is a window handle attached to the driver, it means the browser is still open.
        if(driver.getWindowHandles().isEmpty()) {
            killController();
            return false;
        }
        return true;
    }
}
