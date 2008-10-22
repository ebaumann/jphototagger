package de.elmar_baumann.imv.types;

public enum Force {

    Yes(true),
    No(false);
    private boolean force;

    private Force(boolean force) {
        this.force = force;
    }

    public boolean getForce() {
        return force;
    }
}
