package org.jphototagger.lib.image.metadata.gps.kml;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Elmar Baumann
 */
public class KMLPointTest {
    private static final double DELTA_EXCEED = 0.0000001;

    public KMLPointTest() {}

    @Test
    public void testGetAltitude() {
        double   altitude = 100;
        KMLPoint pointOk  = new KMLPoint(0, 0, altitude);
        double   result   = pointOk.getAltitude();

        assertEquals(altitude, result, 0.0);

        try {
            KMLPoint pointFail = new KMLPoint(0, 0, 0 - DELTA_EXCEED);

            fail("Created point with negative altitude: " + pointFail);
        } catch (IllegalArgumentException ignore) {

            // ok
        }

        try {
            KMLPoint pointFail = new KMLPoint(0, 0);

            pointFail.getAltitude();
            fail("Got not set altitude: " + pointFail);
        } catch (IllegalStateException ignore) {

            // ok
        }
    }

    /**
     * Test of getLongitude method, of class KMLPoint.
     */
    @Test
    public void testGetLongitude() {
        double longitude = 100;

        // Constructor 2 Params
        KMLPoint pointOk = new KMLPoint(longitude, 0);
        double   result  = pointOk.getLongitude();

        assertEquals(longitude, result, 0.0);

        // Constructor 3 Params
        pointOk = new KMLPoint(longitude, 0, 0);
        result  = pointOk.getLongitude();
        assertEquals(longitude, result, 0.0);

        // Limits for all constructors
        longitude = -180;
        pointOk   = new KMLPoint(longitude, 0);
        result    = pointOk.getLongitude();
        assertEquals(longitude, result, 0.0);
        pointOk = new KMLPoint(longitude, 0, 0);
        result  = pointOk.getLongitude();
        assertEquals(longitude, result, 0.0);
        longitude = 180;
        pointOk   = new KMLPoint(longitude, 0);
        result    = pointOk.getLongitude();
        pointOk   = new KMLPoint(longitude, 0, 0);
        result    = pointOk.getLongitude();
        assertEquals(longitude, result, 0.0);

        // Limits exceeded for all constructors
        longitude = -180 - DELTA_EXCEED;

        try {
            KMLPoint pointFail = new KMLPoint(longitude, 0);

            fail("Created longitude with value exceeding the limits: "
                 + pointFail);
        } catch (IllegalArgumentException ignore) {

            // Ok
        }

        try {
            KMLPoint pointFail = new KMLPoint(longitude, 0, 0);

            fail("Created longitude with value exceeding the limits: "
                 + pointFail);
        } catch (IllegalArgumentException ignore) {

            // Ok
        }

        longitude = 180 + DELTA_EXCEED;

        try {
            KMLPoint pointFail = new KMLPoint(longitude, 0);

            fail("Created longitude with value exceeding the limits: "
                 + pointFail);
        } catch (IllegalArgumentException ignore) {

            // Ok
        }

        try {
            KMLPoint pointFail = new KMLPoint(longitude, 0, 0);

            fail("Created longitude with value exceeding the limits: "
                 + pointFail);
        } catch (IllegalArgumentException ignore) {

            // Ok
        }
    }

    @Test
    public void testGetLatitude() {
        double latitude = 10;

        // Constructor 2 Params
        KMLPoint pointOk = new KMLPoint(0, latitude);
        double   result  = pointOk.getLatitude();

        assertEquals(latitude, result, 0.0);

        // Constructor 3 Params
        pointOk = new KMLPoint(0, latitude, 0);
        result  = pointOk.getLatitude();
        assertEquals(latitude, result, 0.0);

        // Limits for all constructors
        latitude = -90;
        pointOk  = new KMLPoint(0, latitude);
        result   = pointOk.getLatitude();
        assertEquals(latitude, result, 0.0);
        pointOk = new KMLPoint(0, latitude, 0);
        result  = pointOk.getLatitude();
        assertEquals(latitude, result, 0.0);
        latitude = 90;
        pointOk  = new KMLPoint(0, latitude);
        result   = pointOk.getLatitude();
        pointOk  = new KMLPoint(0, latitude, 0);
        result   = pointOk.getLatitude();
        assertEquals(latitude, result, 0.0);

        // Limits exceeded for all constructors
        latitude = -90 - DELTA_EXCEED;

        try {
            KMLPoint pointFail = new KMLPoint(0, latitude);

            fail("Created latitude with value exceeding the limits: "
                 + pointFail);
        } catch (IllegalArgumentException ignore) {

            // Ok
        }

        try {
            KMLPoint pointFail = new KMLPoint(0, latitude, 0);

            fail("Created latitude with value exceeding the limits: "
                 + pointFail);
        } catch (IllegalArgumentException ignore) {

            // Ok
        }

        latitude = 90 + DELTA_EXCEED;

        try {
            KMLPoint pointFail = new KMLPoint(0, latitude);

            fail("Created latitude with value exceeding the limits: "
                 + pointFail);
        } catch (IllegalArgumentException ignore) {

            // Ok
        }

        try {
            KMLPoint pointFail = new KMLPoint(0, latitude, 0);

            fail("Created latitude with value exceeding the limits: "
                 + pointFail);
        } catch (IllegalArgumentException ignore) {

            // Ok
        }
    }

    @Test
    public void testHasAltitude() {
        KMLPoint p = new KMLPoint(0, 0);

        assertFalse(p.hasAltitude());
        p = new KMLPoint(0, 0, 0);
        assertTrue(p.hasAltitude());
    }

    @Test
    public void testToXML() {

        // Fragile test!
        KMLPoint p = new KMLPoint(-90.86948943473118, 48.25450093195546);
        String   expected =
            "<Point><coordinates>-90.86948943473118,48.25450093195546</coordinates></Point>";
        String result = p.toXML();

        assertEquals(result, expected);
        p = new KMLPoint(-90.86948943473118, 48.25450093195546, 200.22);
        expected =
            "<Point><coordinates>-90.86948943473118,48.25450093195546,200.22</coordinates></Point>";
        result = p.toXML();
        assertEquals(result, expected);
    }
}
