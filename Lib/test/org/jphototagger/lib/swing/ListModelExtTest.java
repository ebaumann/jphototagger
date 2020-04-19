package org.jphototagger.lib.swing;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class ListModelExtTest {

    @Test
    public void testRemoveFromElements() {
        ListModelExt<Integer> model = new ListModelExt<>();

        model.setElements(Arrays.asList(1, 2, 3, 4));
        model.removeFromElements(Arrays.asList(1, 2, 4));

        Assert.assertEquals(1, model.getSize());
        Integer actual = model.get(0);
        Assert.assertEquals(Integer.valueOf(3), actual);
    }
}
