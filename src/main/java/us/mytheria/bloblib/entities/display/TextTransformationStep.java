package us.mytheria.bloblib.entities.display;

import org.bukkit.Color;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;

public class TextTransformationStep extends TransformationStep {
    private final @Nullable Color backgroundColor;
    private final byte textOpacity;

    public TextTransformationStep(Transformation transformation,
                                  float shadowRadius,
                                  float shadowStrength,
                                  int duration,
                                  @Nullable Color backgroundColor,
                                  byte textOpacity) {
        super(transformation, shadowRadius, shadowStrength, duration);
        this.backgroundColor = backgroundColor;
        this.textOpacity = textOpacity;
    }

    @Override
    public TextTransformationStep copy() {
        return new TextTransformationStep(new Transformation(transformation.getTranslation(),
                transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation()),
                shadowRadius, shadowStrength,
                duration, backgroundColor, textOpacity);
    }
}
