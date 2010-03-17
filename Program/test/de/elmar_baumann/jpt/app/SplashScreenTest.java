/*
 * @(#)SplashScreenTest.java    2010/01/15
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.app;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the Class {@link de.elmar_baumann. }.
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
