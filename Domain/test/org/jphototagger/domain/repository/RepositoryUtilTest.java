package org.jphototagger.domain.repository;

import org.jphototagger.api.event.PropertyEvent;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class RepositoryUtilTest {

    @Test
    public void testIsSaveOrUpdate_Object() {
        assertFalse(RepositoryUtil.isSaveOrUpdate((Object) null));
        assertFalse(RepositoryUtil.isSaveOrUpdate("bla"));
        for (SaveOrUpdate saveOrUpdate : SaveOrUpdate.values()) {
            if (saveOrUpdate != SaveOrUpdate.NONE) {
                assertTrue(RepositoryUtil.isSaveOrUpdate(saveOrUpdate));
            } else {
                assertFalse(RepositoryUtil.isSaveOrUpdate(saveOrUpdate));
            }
        }
    }

    @Test
    public void testIsSaveOrUpdate_AbstractEvent() {
        PropertyEvent evt = new PropertyEvent(this);
        assertFalse(RepositoryUtil.isSaveOrUpdate(evt));
        for (SaveOrUpdate saveOrUpdate : SaveOrUpdate.values()) {
            evt.putProperty(SaveOrUpdate.class, saveOrUpdate);
            if (saveOrUpdate != SaveOrUpdate.NONE) {
                assertTrue(RepositoryUtil.isSaveOrUpdate(evt));
            } else {
                assertFalse(RepositoryUtil.isSaveOrUpdate(evt));
            }
        }
    }
}
