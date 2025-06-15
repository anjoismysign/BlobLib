package io.github.anjoismysign.bloblib.objects;

public class BoundaryDriver {
    private String regionID;
    private Runnable runnable;

    public BoundaryDriver(String regionID, Runnable runnable) {
        this.regionID = regionID;
        this.runnable = runnable;
    }

    public String getRegionID() {
        return regionID;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
