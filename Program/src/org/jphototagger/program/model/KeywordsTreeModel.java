package org.jphototagger.program.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.keywords.Keyword;
import org.jphototagger.domain.repository.KeywordsRepository;
import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.model.SortedChildrenTreeNode;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.helper.KeywordsHelper;

/**
 * Elements are {@code DefaultMutableTreeNode}s with the user objects listed
 * below.
 *
 * <ul>
 * <li>The root user object is a {@code String}</li>
 * <li>All other user objects are {@code Keyword}s</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class KeywordsTreeModel extends DefaultTreeModel {

    private static final long serialVersionUID = -1044898256327030256L;
    private static final Logger LOGGER = Logger.getLogger(KeywordsTreeModel.class.getName());
    private final KeywordsRepository repo = Lookup.getDefault().lookup(KeywordsRepository.class);
    private final DefaultMutableTreeNode ROOT;

    public KeywordsTreeModel() {
        super(new SortedChildrenTreeNode(Bundle.getString(KeywordsTreeModel.class, "KeywordsTreeModel.DisplayName.Root")));
        ROOT = (DefaultMutableTreeNode) getRoot();
        createTree();
    }

    /**
     * Returns a child with a specific name ({@code Keyword#getName()}.
     *
     * @param  parent parent node
     * @param  name   name
     * @return        first child node with that name or null if no child has
     *                that name
     */
    @SuppressWarnings("unchecked")
    public DefaultMutableTreeNode findChildByName(DefaultMutableTreeNode parent, String name) {
        if (parent == null) {
            throw new NullPointerException("parent == null");
        }

        if (name == null) {
            throw new NullPointerException("name == null");
        }

        for (Enumeration<DefaultMutableTreeNode> e = parent.children(); e.hasMoreElements();) {
            DefaultMutableTreeNode child = e.nextElement();
            Object userObject = child.getUserObject();

            if (userObject instanceof Keyword) {
                if (((Keyword) userObject).getName().equalsIgnoreCase(name)) {
                    return child;
                }
            }
        }

        return null;
    }

    /**
     * Adds a keyword to a node.
     *
     * @param  parentNode          parent node; the keyword becomes a child
     * @param  keyword             keyword to add
     * @param  real                true if the keyword is a real keyword
     * @param errorMessageIfExists true, if to display an error message if the
     *                             keyword already existsKeyword
     * @return                     inserted node or null if no node was inserted
     */
    public synchronized DefaultMutableTreeNode insert(DefaultMutableTreeNode parentNode, String keyword, boolean real,
            boolean errorMessageIfExists) {
        if (parentNode == null) {
            throw new NullPointerException("parentNode == null");
        }

        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }

        if (!ensureIsNotChild(parentNode, keyword, errorMessageIfExists)) {
            return null;
        }

        Object userObject = parentNode.getUserObject();
        boolean parentIsRoot = parentNode.equals(ROOT);

        assert parentIsRoot || (userObject instanceof Keyword) : parentNode;

        if (parentIsRoot || (userObject instanceof Keyword)) {
            Long idParent = parentIsRoot
                    ? null
                    : ((Keyword) userObject).getId();
            Keyword child = new Keyword(null, idParent, keyword, real);

            if (repo.saveKeyword(child)) {
                KeywordsHelper.insertDcSubject(keyword);

                SortedChildrenTreeNode node = new SortedChildrenTreeNode(child);

                insertNode(parentNode, node);

                return node;
            } else {
                String message = Bundle.getString(KeywordsTreeModel.class, "KeywordsTreeModel.Error.DbInsert", keyword);
                MessageDisplayer.error(null, message);
            }
        }

        return null;
    }

    public synchronized void copySubtree(final DefaultMutableTreeNode source, final DefaultMutableTreeNode target) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        if (target == null) {
            throw new NullPointerException("target == null");
        }

        if (!ensureIsNotChild(target, source.getUserObject().toString(), true)
                || !ensureTargetIsNotBelowSource(source, target)) {
            return;
        }

        cpySubtree(source, target);
    }

    @SuppressWarnings("unchecked")
    private synchronized void cpySubtree(DefaultMutableTreeNode source, DefaultMutableTreeNode target) {
        DefaultMutableTreeNode newTarget = deepCopy(source, target);

        for (Enumeration<DefaultMutableTreeNode> e = source.children(); e.hasMoreElements();) {
            cpySubtree(e.nextElement(), newTarget);    // Recursive
        }
    }

    private synchronized DefaultMutableTreeNode deepCopy(DefaultMutableTreeNode source, DefaultMutableTreeNode target) {
        Keyword srcKeyword = (Keyword) source.getUserObject();
        Keyword targetKeyword = (Keyword) target.getUserObject();
        Keyword keyword = new Keyword(null, targetKeyword.getId(), srcKeyword.getName(), srcKeyword.isReal());

        if (repo.saveKeyword(keyword)) {
            KeywordsHelper.insertDcSubject(keyword.getName());

            DefaultMutableTreeNode node = new SortedChildrenTreeNode(keyword);

            target.add(node);
            fireTreeNodesInserted(this, target.getPath(), new int[]{target.getIndex(node)}, new Object[]{node});

            return node;
        } else {
            String message = Bundle.getString(KeywordsTreeModel.class, "KeywordsTreeModel.Error.DbCopy", keyword.getName(), targetKeyword.getName());
            MessageDisplayer.error(null, message);
        }

        return null;
    }

    private void insertNode(DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
        parent.add(child);

        int childIndex = parent.getIndex(child);

        fireTreeNodesInserted(this, parent.getPath(), new int[]{childIndex}, new Object[]{child});
        KeywordsHelper.expandAllTreesTo(child);
    }

    private boolean ensureIsNotChild(DefaultMutableTreeNode parentNode, String keyword, boolean errorMessage) {
        if (childHasKeyword(parentNode, keyword)) {
            if (errorMessage) {
                String message = Bundle.getString(KeywordsTreeModel.class, "KeywordsTreeModel.Error.KeywordExists", keyword, parentNode);
                MessageDisplayer.error(null, message);
            }

            return false;
        }

        return true;
    }

    private boolean ensureTargetIsNotBelowSource(DefaultMutableTreeNode source, DefaultMutableTreeNode target) {
        boolean isBelow = TreeUtil.isAbove(source, target);

        if (isBelow) {
            String message = Bundle.getString(KeywordsTreeModel.class, "KeywordsTreeModel.Error.TargetBelowSource");
            MessageDisplayer.error(null, message);

            return false;
        }

        return true;
    }

    private boolean childHasKeyword(DefaultMutableTreeNode parentNode, String keyword) {
        for (Enumeration<?> children = parentNode.children(); children.hasMoreElements();) {
            Object o = children.nextElement();

            if (o instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) o).getUserObject();

                if (userObject instanceof Keyword) {
                    String s = ((Keyword) userObject).getName();

                    if ((s != null) && s.equals(keyword)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public synchronized void delete(final DefaultMutableTreeNode keywordNode) {
        if (keywordNode == null) {
            throw new NullPointerException("keywordNode == null");
        }

        List<Keyword> delKeywords = new ArrayList<Keyword>();

        for (Enumeration<?> e = keywordNode.preorderEnumeration(); e.hasMoreElements();) {
            Object el = e.nextElement();

            if (el instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) el).getUserObject();

                if (userObject instanceof Keyword) {
                    delKeywords.add((Keyword) userObject);
                }
            }
        }

        if (repo.deleteKeywords(delKeywords)) {
            removeNodeFromParent(keywordNode);
        } else {
            String message = Bundle.getString(KeywordsTreeModel.class, "KeywordsTreeModel.Error.DbRemove", keywordNode.toString());
            MessageDisplayer.error(null, message);
        }
    }

    /**
     * Notifies this model that a keyword has been changed.
     *
     * Updates the database and fires that nodes were changed.
     *
     * @param node    node where the keyword was changed
     * @param keyword keyword that was changed
     */
    public void changed(final DefaultMutableTreeNode node, final Keyword keyword) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }

        assert node.getUserObject().equals(keyword) : node.getUserObject();

        final Object src = this;
        TreeNode parent = node.getParent();

        if (parent instanceof DefaultMutableTreeNode) {
            if (repo.updateKeyword(keyword)) {
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parent;

                fireTreeNodesChanged(src, parentNode.getPath(), new int[]{parentNode.getIndex(node)},
                        new Object[]{node});
            } else {
                String message = Bundle.getString(KeywordsTreeModel.class, "KeywordsTreeModel.Error.DbUpdate", keyword);
                MessageDisplayer.error(null, message);
            }
        }
    }

    /**
     * Moves a node to another node.
     *
     * @param source  node to move
     * @param target  new parent of <code>source</code>
     * @param keyword keyword of <code>source</code>
     */
    public void move(final DefaultMutableTreeNode source, final DefaultMutableTreeNode target, final Keyword keyword) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        if (target == null) {
            throw new NullPointerException("target == null");
        }

        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }

        if (ensureIsNotChild(target, keyword.getName(), true) && ensureTargetIsNotBelowSource(source, target)
                && setIdParent(keyword, target)) {
            if (repo.updateKeyword(keyword)) {
                DefaultMutableTreeNode removeNode = TreeUtil.findNodeWithUserObject(ROOT, source.getUserObject());

                if (removeNode != null) {
                    removeNodeFromParent(removeNode);
                }

                insertNode(target, source);
            }
        }
    }

    private boolean setIdParent(Keyword keyword, DefaultMutableTreeNode parentNode) {
        if (parentNode.equals(ROOT)) {
            keyword.setIdParent(null);

            return true;
        } else {
            Object userObject = parentNode.getUserObject();

            if (userObject instanceof Keyword) {
                keyword.setIdParent(((Keyword) userObject).getId());

                return true;
            }
        }

        LOGGER.log(Level.WARNING, "''{0}'' is not root and does not contain a keyword to move ''{1}'' below!", new Object[]{parentNode, keyword});

        return false;
    }

    private void createTree() {
        Collection<Keyword> roots = repo.findRootKeywords();

        for (Keyword rootKeyword : roots) {
            DefaultMutableTreeNode rootNode = new SortedChildrenTreeNode(rootKeyword);

            insertNode(ROOT, rootNode);
            insertChildren(rootNode);
        }
    }

    private void insertChildren(DefaultMutableTreeNode parentNode) {
        Keyword parent = (Keyword) parentNode.getUserObject();
        Collection<Keyword> children = repo.findChildKeywords(parent.getId());

        for (Keyword child : children) {
            DefaultMutableTreeNode childNode = new SortedChildrenTreeNode(child);

            insertNode(parentNode, childNode);
            insertChildren(childNode);    // recursive
        }
    }

    public void removeAllKeywords() {
        TreeUtil.removeAllChildren(this, ROOT);
    }
}
