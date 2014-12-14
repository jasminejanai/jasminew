/**
 * 
 */
package com.leapmotion.controller;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

import javax.imageio.ImageIO;
import javax.swing.*;

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
    private InvisibleFrame visFeedbackFrame;

    public WebBrowserController() {
        wantsToQuit = false;
        visFeedbackFrame = new InvisibleFrame();

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
        jse.executeScript("window.scrollBy(0,-100)", "");
    }

    @Override
    public void scrollDown() {
        // TODO: Do scrolling a bit more responsive to how fast the user swiped.
        jse.executeScript("window.scrollBy(0,100)", "");
    }

    @Override
    public void goPrevious() {
        //System.out.println("Go to previous page.");

        visFeedbackFrame.displayImage("res/swipeLeft.png");
        driver.navigate().back();
    }

    @Override
    public void goNext() {
        visFeedbackFrame.displayImage("res/swipeRight.png");
        driver.navigate().forward();
    }

    @Override
    public void refreshPage() {
        visFeedbackFrame.displayImage("res/refresh.png");
        driver.navigate().refresh();
    }

    /**
     * Note: This throws error. Refer: GestureHandler.java.
     */
    @Override
    public void zoomInPage() {
        // TODO: Find a better way to do this, I don't like relying on shortcuts.
        driver.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL, Keys.SUBTRACT));
    }

    /**
     * Note: This throws error. Refer: GestureHandler.java.
     */
    @Override
    public void zoomOutPage() {
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

    public static class InvisibleFrame extends JFrame {

        private DrawPane drawPanel;
        private int width;
        private int height;

        public InvisibleFrame() {
            super("InvisibleFrame");

            drawPanel = new DrawPane();
            setContentPane(drawPanel);

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setDefaultLookAndFeelDecorated(false);
            setUndecorated(true);
            setAlwaysOnTop(true);
            setBackground(new Color(0, 0, 0, 0));

            setFrameSize();

            setVisible(true);
        }

        public void displayImage(BufferedImage image) {
            setFrameSize();
            drawPanel.displayImage(image, width, height);
        }

        public void displayImage(String path) {
            BufferedImage image = null;

            try {
                image = ImageIO.read(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            setFrameSize();
            drawPanel.displayImage(image, width, height);
        }

        private void setFrameSize() {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            width = (int) screenSize.getWidth();
            height = (int) screenSize.getHeight();
            setSize(width, height);
            setLocationRelativeTo(null);
        }

        //create a component that you can actually draw on.
        private static class DrawPane extends JPanel implements ActionListener {

            private float alpha = 0f;
            private final float DELTA = -0.025f;
            private final Timer timer = new Timer(10, null);
            private int width;
            private int height;
            private BufferedImage image;

            public DrawPane() {
                super();
                image = null;
                setOpaque(true);
                timer.setInitialDelay(0);
                timer.addActionListener(this);

                try {
                    image = ImageIO.read(new File("res/swipeRight.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                width = (int) screenSize.getWidth();
                height = (int) screenSize.getHeight();
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += DELTA;
                if (alpha < 0) {
                    alpha = 0;
                    timer.stop();
                }
                repaint();
            }

            public void paintComponent(Graphics g) {
                //draw on g here e.g.
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
                int relativeHeight = (int) ((float) image.getHeight() / (float) image.getWidth() * width / 2);
                g2d.drawImage(image, width / 2 - width / 4, height / 2 - relativeHeight / 2, width / 2, relativeHeight, this);
            }

            public void displayImage(BufferedImage image, int width, int height) {
                timer.stop();
                alpha = 1f;
                this.width = width;
                this.height = height;
                this.image = image;
                timer.restart();
            }
        }
    }
}
