package org.jphototagger.domain.metadata.xmp;

import java.util.Calendar;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class XmpIptc4XmpCoreDateCreatedMetaDataValueTest {

    @Test
    public void testCreateTimestamp() {
        assertNull(XmpIptc4XmpCoreDateCreatedMetaDataValue.createTimestamp(null));
        assertNull(XmpIptc4XmpCoreDateCreatedMetaDataValue.createTimestamp(""));
        assertNull(XmpIptc4XmpCoreDateCreatedMetaDataValue.createTimestamp("2012-28"));
        assertNull(XmpIptc4XmpCoreDateCreatedMetaDataValue.createTimestamp("2012-25-37"));
        assertEquals(toTimestamp(2012, 6, 15), XmpIptc4XmpCoreDateCreatedMetaDataValue.createTimestamp("2012"));
        assertEquals(toTimestamp(2012, 4, 15), XmpIptc4XmpCoreDateCreatedMetaDataValue.createTimestamp("2012-04"));
        assertEquals(toTimestamp(2012, 4, 1), XmpIptc4XmpCoreDateCreatedMetaDataValue.createTimestamp("2012-04-01"));
    }

    private Long toTimestamp(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        return cal.getTime().getTime();
    }
}
