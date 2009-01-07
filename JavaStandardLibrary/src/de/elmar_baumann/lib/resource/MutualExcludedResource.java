package de.elmar_baumann.lib.resource;

/**
 * Ressource, die nicht von mehreren Objekten zur gleichen Zeit benutzt
 * werden kann.
 * 
 * Spezialisierte Klassen sind Singletons und setzen spezialisierte Objekte
 * mit {@link #setResource(java.lang.Object)}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/16
 */
public final class MutualExcludedResource {

    private Object resource = null;
    private boolean locked = false;
    private Object owner = null;

    /**
     * Liefert, ob die Ressource gesperrt ist. Im Gegensatz zu
     * {@link #isAvailable()} wird nicht geprüft, ob die Ressource
     * existiert (ungleich null ist).
     * 
     * @return true, wenn gesperrt.
     */
    synchronized public boolean isLocked() {
        return locked;
    }

    synchronized private void setLocked(boolean lock) {
        this.locked = lock;
    }

    /**
     * Liefert, ob die Ressource verfügbar ist: Sie existiert (ist ungleich null)
     * und wird nicht von einem anderen Objekt benutzt.
     * 
     * @return true, wenn verfügbar.
     */
    synchronized public boolean isAvailable() {
        return !isLocked() && resource != null;
    }

    /**
     * Liefert die Ressource.
     * 
     * <em>Wird die Ressource nicht mehr gebraucht, ist sie mit
     * {@link #releaseResource(java.lang.Object)} wieder freizugeben!</em>
     * 
     * @param  owner  Eigentümer. Nur der Eigentümer kann die Ressource
     *                wieder freigeben.
     * @return Ressource oder null, wenn nicht gesetzt oder nicht verfügbar
     */
    synchronized public Object getRessource(Object owner) {
        if (isAvailable()) {
            setLocked(true);
            setOwner(owner);
            return resource;
        }
        return null;
    }

    private synchronized Object getOwner() {
        return owner;
    }

    private synchronized void setOwner(Object owner) {
        this.owner = owner;
    }

    /**
     * Gibt die Ressource wieder frei.
     * 
     * @param  o     Eigentümer der Ressource
     * @return true, wenn freigegeben
     */
    synchronized public boolean releaseResource(Object o) {
        if (isLocked() && o != null && o == getOwner()) {
            setOwner(null);
            setLocked(false);
        }
        return false;
    }

    /**
     * Setzt die Ressource. Vorher kann sie nicht benutzt werden.
     * 
     * @param resource Ressource
     */
    synchronized protected void setResource(Object resource) {
        this.resource = resource;
    }

    protected MutualExcludedResource() {
    }
}
