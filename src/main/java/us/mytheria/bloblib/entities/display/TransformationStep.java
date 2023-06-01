package us.mytheria.bloblib.entities.display;

import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record TransformationStep(Transformation transformation, int duration) {

    public static TransformationStep of(TransformationStep step, int duration) {
        return new TransformationStep(step.copy().transformation, duration);
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
                duration);
    }
}
