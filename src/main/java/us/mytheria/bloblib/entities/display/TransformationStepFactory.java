package us.mytheria.bloblib.entities.display;

import org.bukkit.util.Transformation;
import org.joml.Quaternionf;

import java.util.List;

public record TransformationStepFactory(TransformationStep step) {

    /**
     * Will do a rotation on the left Axis
     * Only supported Y axis steply
     *
     * @param axis    the axis to rotate on
     * @param degrees the degrees to rotate
     * @return a new instance of TransformationBuilder
     */
    public TransformationStepFactory rotateAxisLeft(RotationAxis axis,
                                                    float degrees,
                                                    int duration) {
        degrees = (float) Math.toRadians(degrees);
        Transformation transformation = step.copy().transformation;
        Quaternionf rotation = transformation.getLeftRotation();
        switch (axis) {
            case X -> {
                return new TransformationStepFactory(new TransformationStep(new Transformation(
                        transformation.getTranslation(),
                        transformation.getLeftRotation().rotateX(degrees),
                        transformation.getScale(),
                        transformation.getRightRotation()),
                        step.shadowRadius,
                        step.shadowStrength,
                        duration));
            }
            case Y -> {
                return new TransformationStepFactory(new TransformationStep(new Transformation(
                        transformation.getTranslation(),
                        transformation.getLeftRotation().rotateY(degrees),
                        transformation.getScale(),
                        transformation.getRightRotation()),
                        step.shadowRadius,
                        step.shadowStrength,
                        duration));
            }
            case Z -> {
                return new TransformationStepFactory(new TransformationStep(new Transformation(
                        transformation.getTranslation(),
                        transformation.getLeftRotation().rotateZ(degrees),
                        transformation.getScale(),
                        transformation.getRightRotation()),
                        step.shadowRadius,
                        step.shadowStrength,
                        duration));
            }
            default -> throw new UnsupportedOperationException("Not implemented yet");
        }
    }

    /**
     * Will do a rotation on the right Axis
     * Only supported Y axis steply
     *
     * @param axis    the axis to rotate on
     * @param degrees the degrees to rotate
     * @return a new instance of TransformationBuilder
     */
    public TransformationStepFactory rotateAxisRight(RotationAxis axis,
                                                     float degrees,
                                                     int duration) {
        degrees = (float) Math.toRadians(degrees);
        Transformation transformation = step.copy().transformation;
        Quaternionf rotation = transformation.getRightRotation();
        switch (axis) {
            case X -> {
                return new TransformationStepFactory(new TransformationStep(new Transformation(
                        transformation.getTranslation(),
                        transformation.getLeftRotation(),
                        transformation.getScale(),
                        transformation.getRightRotation().rotateX(degrees)),
                        step.shadowRadius,
                        step.shadowStrength,
                        duration));
            }
            case Y -> {
                return new TransformationStepFactory(new TransformationStep(new Transformation(
                        transformation.getTranslation(),
                        transformation.getLeftRotation(),
                        transformation.getScale(),
                        transformation.getRightRotation().rotateY(degrees)),
                        step.shadowRadius,
                        step.shadowStrength,
                        duration));
            }
            case Z -> {
                return new TransformationStepFactory(new TransformationStep(new Transformation(
                        transformation.getTranslation(),
                        transformation.getLeftRotation(),
                        transformation.getScale(),
                        transformation.getRightRotation().rotateZ(degrees)),
                        step.shadowRadius,
                        step.shadowStrength,
                        duration));
            }
            default -> throw new UnsupportedOperationException("Not implemented yet");
        }
    }

    /**
     * Will uniformly scale the display entity.
     * Expect/assert the same behavior as {@link #scale(float, float, float, int)}
     *
     * @param scale    the scale to set
     * @param duration the duration of the transformation (in ticks)
     * @return a new instance of TransformationStep
     */
    public TransformationStepFactory uniformScale(float scale,
                                                  int duration) {
        TransformationStep copy = TransformationStep.of(step, duration);
        copy.getTransformation().getScale().set(scale);
        return new TransformationStepFactory(copy);
    }

    /**
     * Will scale the display entity.
     * Has more freedom than {@link #uniformScale(float, int)}
     * such as for doing sticks,tubes,etc.
     *
     * @param x        the x scale
     * @param y        the y scale
     * @param z        the z scale
     * @param duration the duration of the transformation (in ticks)
     * @return a new instance of TransformationStep
     */
    public TransformationStepFactory scale(float x, float y, float z,
                                           int duration) {
        TransformationStep copy = TransformationStep.of(step, duration);
        copy.getTransformation().getScale().set(x, y, z);
        return new TransformationStepFactory(copy);
    }

    /**
     * Will create a full rotation of 360 degrees
     * facing all four possible directions
     *
     * @param axis     the axis to rotate on
     * @param duration the total duration of the transformation (in ticks)
     * @return an immutable list of TransformationStep
     */
    public List<TransformationStep> fullQuadRotation(RotationAxis axis, int duration) {
        if (duration % 4 != 0) throw new IllegalArgumentException("Duration must be a multiple of 4");
        int tick = duration / 4;
        TransformationStep a = rotateAxisLeft(axis, 90,
                tick).step();
        TransformationStep b = new TransformationStepFactory(a.copy())
                .rotateAxisLeft(axis, 90,
                        tick).step();
        TransformationStep c = new TransformationStepFactory(b.copy())
                .rotateAxisLeft(axis, 90,
                        tick).step();
        TransformationStep d = new TransformationStepFactory(c.copy())
                .rotateAxisLeft(axis, 90,
                        tick).step();
        return List.of(a, b, c, d);
    }

    /**
     * Will create a full rotation of 360 degrees
     * skipping a direction (instead of doing 0,90,180,270
     * it will do 0,120,240)
     *
     * @param axis     the axis to rotate on
     * @param duration the total duration of the transformation (in ticks)
     * @return an immutable list of TransformationStep
     */
    public List<TransformationStep> fullTriRotation(RotationAxis axis, int duration) {
        if (duration % 3 != 0) throw new IllegalArgumentException("Duration must be a multiple of 3");
        int tick = duration / 3;
        TransformationStep a = rotateAxisLeft(axis, 120,
                tick).step();
        TransformationStep b = new TransformationStepFactory(a.copy())
                .rotateAxisLeft(axis, 120,
                        tick).step();
        TransformationStep c = new TransformationStepFactory(b.copy())
                .rotateAxisLeft(axis, 120,
                        tick).step();
        return List.of(a, b, c);
    }
}