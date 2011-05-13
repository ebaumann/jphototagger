package org.jphototagger.lib.net;

import org.jphototagger.lib.util.Version;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elmar Baumann
 */
public class VersionCheckTest {
    public VersionCheckTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    /**
     * Test of existsNewer method, of class NetVersion.
     * @throws Exception
     */
    @Test
    public void testExistsNewer() throws Exception {
        System.out.println("existsNewer");

        final String urlHtml =
            "http://localhost/fotografie/tipps/computer/lightroom/imagemetadataviewer.html";
        final String versionDelimiter = ".";
        Version      compareToVersion = new Version(0, 7, 2);
        boolean      expResult        = true;
        boolean      result           =
            compareToVersion.compareTo(NetVersion.getOverHttp(urlHtml,
                versionDelimiter)) < 0;

        assertEquals(expResult, result);
    }
}
