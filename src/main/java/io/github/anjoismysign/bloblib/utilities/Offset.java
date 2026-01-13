package io.github.anjoismysign.bloblib.utilities;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Offset {

    public void applyTo(@NotNull Vector vector){
        vector.add(new Vector(x,y,z));
    }

    public void applyTo(@NotNull Location location){
        location.add(new Vector(x,y,z));
    }

    private double x;
    private double y;
    private double z;

    public Offset(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Offset(){}

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

}
