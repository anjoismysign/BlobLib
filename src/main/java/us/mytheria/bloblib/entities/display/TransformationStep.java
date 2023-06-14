package us.mytheria.bloblib.entities.display;

import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TransformationStep {
    protected final Transformation transformation;
    protected final float shadowRadius;
    protected final float shadowStrength;
    protected final int duration;

    public TransformationStep(Transformation transformation,
                              float shadowRadius,
                              float shadowStrength,
                              int duration) {
        this.transformation = transformation;
        this.shadowRadius = shadowRadius;
        this.shadowStrength = shadowStrength;
        this.duration = duration;
    }

    public static TransformationStep of(TransformationStep step, int duration) {
        return new TransformationStep(step.copy().transformation, step.shadowRadius,
                step.shadowStrength, duration);
    }

    /**
     * Will copy/clone itself to a new instance
     *
     * @return a new instance of TransformationStep
     */
    public TransformationStep copy() {
        Transformation current = transformation;
        Vector3f translation = current.getTranslation();
        Quaternionf leftRotation = current.getLeftRotation();
        Vector3f scale = current.getScale();
        Quaternionf rightRotation = current.getRightRotation();
        return new TransformationStep(new Transformation(
                new Vector3f(translation.x, translation.y, translation.z),
                new Quaternionf(leftRotation.x, leftRotation.y, leftRotation.z, leftRotation.w),
                new Vector3f(scale.x, scale.y, scale.z),
                new Quaternionf(rightRotation.x, rightRotation.y, rightRotation.z, rightRotation.w)),
                shadowRadius, shadowStrength,
                duration);
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public float getShadowRadius() {
        return shadowRadius;
    }

    public float getShadowStrength() {
        return shadowStrength;
    }

    public int getDuration() {
        return duration;
    }
}
