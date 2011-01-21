package org.jphototagger.program.app;

import org.jphototagger.program.app.SplashScreen;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Elmar Baumann
 */
public class SplashScreenTest {
    public SplashScreenTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {
        SplashScreen.INSTANCE.init();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        SplashScreen.INSTANCE.close();
    }

    /**
     * Test of setProgress method, of class SplashScreen.
     */
    @Test
    public void testSetProgress() {
        System.out.println("setProgress");
        SplashScreen.INSTANCE.setMessage("5 %");
        SplashScreen.INSTANCE.setProgress(5);
        sleep(500);
        SplashScreen.INSTANCE.setMessage("10 %");
        SplashScreen.INSTANCE.setProgress(10);
        sleep(500);
        SplashScreen.INSTANCE.setMessage("15 %");
        SplashScreen.INSTANCE.setProgress(15);
        sleep(500);
        SplashScreen.INSTANCE.setMessage("20 %");
        SplashScreen.INSTANCE.setProgress(20);
        sleep(500);
        SplashScreen.INSTANCE.setMessage("25 %");
        SplashScreen.INSTANCE.setProgress(25);
        sleep(500);
        SplashScreen.INSTANCE.setMessage("30 %");
        SplashScreen.INSTANCE.setProgress(30);
        sleep(500);
        SplashScreen.INSTANCE.setMessage("50 %");
        SplashScreen.INSTANCE.setProgress(50);
        sleep(500);
        SplashScreen.INSTANCE.setMessage("75 %");
        SplashScreen.INSTANCE.setProgress(75);
        sleep(500);
        SplashScreen.INSTANCE.setMessage("100 %");
        SplashScreen.INSTANCE.setProgress(100);
        sleep(500);
    }

    /**
     * Test of setMessage method, of class SplashScreen.
     */
    @Test
    public void testSetMessage() {
        System.out.println("setMessage");
        SplashScreen.INSTANCE.setMessage("Testmessage 1");
        sleep(2000);
        SplashScreen.INSTANCE.setMessage(
            "Testmessage 2 (should have replaced previous)");
        sleep(2000);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
