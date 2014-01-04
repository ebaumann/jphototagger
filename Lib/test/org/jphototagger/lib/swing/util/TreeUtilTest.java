package org.jphototagger.lib.swing.util;

import javax.swing.tree.DefaultMutableTreeNode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 *
 * @author elmar
 */
public class TreeUtilTest {

    @Test
    public void testFindNodeContainingUserObjectRecursive() {
        Object userObject = "Icecream";
        DefaultMutableTreeNode tn1 = new DefaultMutableTreeNode();
        DefaultMutableTreeNode tn2 = new DefaultMutableTreeNode();
        DefaultMutableTreeNode tn3 = new DefaultMutableTreeNode();
        DefaultMutableTreeNode tn4 = new DefaultMutableTreeNode();
        DefaultMutableTreeNode tn5 = new DefaultMutableTreeNode();
        DefaultMutableTreeNode tn6 = new DefaultMutableTreeNode();
        tn4.add(tn5);
        tn4.add(tn6);
        tn2.add(tn3);
        tn2.add(tn4);
        tn1.add(tn2);
        DefaultMutableTreeNode foundNode = TreeUtil.findNodeContainingUserObjectRecursive(tn1, userObject);
        assertNull(foundNode);
        foundNode = TreeUtil.findNodeContainingUserObjectRecursive(tn1, null);
        assertEquals(tn1, foundNode);
        tn6.setUserObject(userObject);
        foundNode = TreeUtil.findNodeContainingUserObjectRecursive(tn1, userObject);
        assertEquals(tn6, foundNode);
        foundNode = TreeUtil.findNodeContainingUserObjectRecursive(tn4, userObject);
        assertEquals(tn6, foundNode);
        foundNode = TreeUtil.findNodeContainingUserObjectRecursive(tn6, userObject);
        assertEquals(tn6, foundNode);
    }
}
