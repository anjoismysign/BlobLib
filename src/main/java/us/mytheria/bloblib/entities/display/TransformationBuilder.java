package us.mytheria.bloblib.entities.display;

import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

public record TransformationBuilder(Transformation build) {

    /**
     * Will copy/clone to a new instance
     *
     * @return a new instance of TransformationBuilder
     */
    public TransformationBuilder copy() {
        return new TransformationBuilder(build);
    }

    /**
     * Will do a rotation on the left Axis
     *
     * @param axis    the axis to rotate on
     * @param degrees the degrees to rotate
     * @param ticks   the amount of ticks to rotate
     * @return a new instance of TransformationBuilder
     */
    public TransformationBuilder rotateAxisLeft(RotationAxis axis,
                                                float degrees,
                                                int ticks) {
        degrees = (float) Math.toRadians(degrees);
        TransformationBuilder copy = copy();
        Transformation transformation = copy.build;
        Quaternionf rotation = transformation.getLeftRotation();
        switch (axis) {
            case X -> {
                rotation.set(new AxisAngle4f(degrees, 1, 0, 0));
            }
            case Y -> {
                rotation.set(new AxisAngle4f(degrees, 0, 1, 0));
            }
            case Z -> {
                rotation.set(new AxisAngle4f(degrees, 0, 0, 1));
            }
            case XY -> {
                rotation.set(new AxisAngle4f(degrees, 1, 1, 0));
            }
            case YZ -> {
                rotation.set(new AxisAngle4f(degrees, 0, 1, 1));
            }
            case HORIZONTAL -> {
                rotation.set(new AxisAngle4f(degrees, 1, 0, 1));
            }
            case ALL -> {
                rotation.set(new AxisAngle4f(degrees, 1, 1, 1));
            }
        }
        return copy;
    }

    /**
     * Will do a rotation on the right Axis
     *
     * @param axis    the axis to rotate on
     * @param degrees the degrees to rotate
     * @param ticks   the amount of ticks to rotate
     * @return a new instance of TransformationBuilder
     */
    public TransformationBuilder rotateRightLeft(RotationAxis axis,
                                                 float degrees,
                                                 int ticks) {
        degrees = (float) Math.toRadians(degrees);
        TransformationBuilder copy = copy();
        Transformation transformation = copy.build;
        Quaternionf rotation = transformation.getRightRotation();
        switch (axis) {
            case X -> {
                rotation.set(new AxisAngle4f(degrees, 1, 0, 0));
            }
            case Y -> {
                rotation.set(new AxisAngle4f(degrees, 0, 1, 0));
            }
            case Z -> {
                rotation.set(new AxisAngle4f(degrees, 0, 0, 1));
            }
            case XY -> {
                rotation.set(new AxisAngle4f(degrees, 1, 1, 0));
            }
            case YZ -> {
                rotation.set(new AxisAngle4f(degrees, 0, 1, 1));
            }
            case HORIZONTAL -> {
                rotation.set(new AxisAngle4f(degrees, 1, 0, 1));
            }
            case ALL -> {
                rotation.set(new AxisAngle4f(degrees, 1, 1, 1));
            }
        }
        return copy;
    }

    /**
     * Should do a horizontal rotation on the left axis (clockwise)
     * Picture it as spinning a coin on a table,
     * wooden spinning tops, bay-blades, etc.
     *
     * @param degrees the degrees to rotate
     * @param ticks   the amount of ticks to rotate
     * @return a new instance of TransformationBuilder
     */
    public TransformationBuilder horizontalRotationLeft(float degrees,
                                                        int ticks) {
        return rotateAxisLeft(RotationAxis.HORIZONTAL, degrees, ticks);
    }

    /**
     * Should do a horizontal rotation on the right axis (counter-clockwise)
     * Picture it as spinning a coin on a table,
     * wooden spinning tops, bay-blades, etc.
     *
     * @param degrees the degrees to rotate
     * @param ticks   the amount of ticks to rotate
     * @return a new instance of TransformationBuilder
     */
    public TransformationBuilder horizontalRotationRight(float degrees,
                                                         int ticks) {
        return rotateRightLeft(RotationAxis.HORIZONTAL, degrees, ticks);
    }

    public TransformationBuilder verticalRotationLeft(float degrees,
                                                      int ticks) {
        return rotateAxisLeft(RotationAxis.X, degrees, ticks);
    }

    public TransformationBuilder verticalRotationLeft2(float degrees,
                                                       int ticks) {
        return rotateAxisLeft(RotationAxis.Y, degrees, ticks);
    }

    public TransformationBuilder verticalRotationLeft3(float degrees,
                                                       int ticks) {
        return rotateAxisLeft(RotationAxis.Z, degrees, ticks);
    }

    public TransformationBuilder verticalRotationLeft4(float degrees,
                                                       int ticks) {
        return rotateAxisLeft(RotationAxis.YZ, degrees, ticks);
    }

    public TransformationBuilder verticalRotationLeft5(float degrees,
                                                       int ticks) {
        return rotateAxisLeft(RotationAxis.XY, degrees, ticks);
    }

    /**
     * Will uniformly scale the display entity.
     * Expect/assert the same behavior as {@link #scale(float, float, float)}
     *
     * @param scale the scale to set
     * @return a new instance of TransformationBuilder
     */
    public TransformationBuilder uniformScale(float scale) {
        TransformationBuilder copy = copy();
        copy.build.getScale().set(scale);
        return copy;
    }

    /**
     * Will scale the display entity.
     * Has more freedom than {@link #uniformScale(float)}
     * such as for doing sticks,tubes,etc.
     *
     * @param x the x scale
     * @param y the y scale
     * @param z the z scale
     * @return a new instance of TransformationBuilder
     */
    public TransformationBuilder scale(float x, float y, float z) {
        TransformationBuilder copy = copy();
        copy.build.getScale().set(x, y, z);
        return copy;
    }
}