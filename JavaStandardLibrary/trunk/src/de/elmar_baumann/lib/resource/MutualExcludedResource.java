package de.elmar_baumann.lib.resource;

/**
 * Resource that should't be used by two different objects at the same time. The
 * owner gets the resource only <em>once</em> by calling
 * {@link #getResource(java.lang.Object)}. After that call the resource is
 * locked until {@link #releaseResource(java.lang.Object)} will be called.
 *
 * <em>The protection of the object is very weak! An object that keeps a
 * reference to the resource can do anything with it. That means, You have
 * to call {@link #isAvailable()} every time before using the resource,
 * even if You got previously a reference to it or You set the reference to
 * null before You call {@link #releaseResource(java.lang.Object)}!</em>
 * 
 * Specialized classes are singletons and set spezialized objects
 * through {@link #setResource(java.lang.Object)}.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-16
 */
public class MutualExcludedResource {

    private Object resource = null;
    private boolean locked = false;
    private Object owner = null;

    /**
     * Returns, whether a resource can be used: It exists (is not null)
     * and it is not locked.
     * 
     * @return true, if the resource can be used
     */
    public synchronized boolean isAvailable() {
        return !isLocked() && resource != null;
    }

    /**
     * Returns the resource, locks it and sets the owner of the resource.
     * 
     * <em>If the resource isn't needed anymore, it has to be released with
     * {@link #releaseResource(java.lang.Object)}!</em>
     * 
     * @param  owner  owner. Only the owner can unlock the resource an has to
     *                do that!
     * @return Resource or null, if not available. If the returned resource
     *         is not null, {@link #isAvailable()} returns <code>false</code>.
     * @see #isAvailable()
     */
    public synchronized Object getResource(Object owner) {
        if (owner == null)
            throw new NullPointerException("owner == null"); // NOI18N

        if (isAvailable()) {
            setLocked(true);
            setOwner(owner);
            return resource;
        }
        return null;
    }

    /**
     * Releases (unlocks) the resource.
     * 
     * @param  o     owner of the resource. Only the owner can release the
     *               resource.
     * @return true, if released. If the return value is true,
     *         {@link #isAvailable()} returns <code>true</code>.
     */
    public synchronized boolean releaseResource(Object o) {
        if (o == null)
            throw new NullPointerException("o == null"); // NOI18N

        if (isLocked() && o != null && o == getOwner()) {
            owner = null;
            setLocked(false);
            return true;
        }
        return false;
    }

    /**
     * Sets the ressource. Until this method is called,
     * {@link #isAvailable()} will be <code>false</code>.
     *
     * @param resource resource
     */
    protected synchronized void setResource(Object resource) {
        if (resource == null)
            throw new NullPointerException("resource == null"); // NOI18N

        this.resource = resource;
    }

    /**
     * Locks or unlocks the resource.
     *
     * @param lock  true, if the resource shall be locked and false if it
     *              shall be unlocked
     */
    private synchronized void setLocked(boolean lock) {
        this.locked = lock;
    }

    /**
     * Returns, whether the resource is locked. Does not test, whether the
     * resource exists (is not null) contrary to {@link #isAvailable()}.
     *
     * @return true, if the resource is locked
     */
    private synchronized boolean isLocked() {
        return locked;
    }

    /**
     * Sets the owner of the resource.
     *
     * @param owner  owner
     */
    private synchronized void setOwner(Object owner) {
        assert owner != null;
        this.owner = owner;
    }

    /**
     * Returns the owner of the resource.
     *
     * @return owner or null if nobody owns the resource
     */
    private synchronized Object getOwner() {
        return owner;
    }

    protected MutualExcludedResource() {
    }
}
