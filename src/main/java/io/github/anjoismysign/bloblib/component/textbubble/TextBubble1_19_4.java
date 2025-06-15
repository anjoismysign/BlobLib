package io.github.anjoismysign.bloblib.component.textbubble;

import io.github.anjoismysign.bloblib.entities.MutableAddress;
import io.github.anjoismysign.bloblib.utilities.TextColor;
import io.papermc.paper.math.Rotation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.function.Consumer;

public record TextBubble1_19_4(@NotNull Player belongsTo,
                               @NotNull MutableAddress<TextDisplay> displayAddress,
                               @NotNull MutableAddress<String> textAddress) implements TextBubbleComponent {

    @NotNull
    public static TextBubbleComponent of(@NotNull Player belongsTo) {
        Objects.requireNonNull(belongsTo, "'belongsTo' cannot be null");
        TextBubbleComponent bubble = new TextBubble1_19_4(belongsTo, MutableAddress.nullable(), MutableAddress.nullable());
        playerOwnerTracking.put(belongsTo.getUniqueId(), bubble);
        return bubble;
    }

    private void ifDisplay(@Nullable Consumer<TextDisplay> consumer) {
        ifDisplayOrElse(consumer, null);
    }

    private void ifDisplayOrElse(@Nullable Consumer<TextDisplay> consumer,
                                 @Nullable Runnable runnable) {
        @Nullable TextDisplay display = displayAddress().look();
        if (display == null || !display.isValid()) {
            if (runnable != null)
                runnable.run();
            return;
        }
        if (consumer != null)
            consumer.accept(display);
    }

    @Override
    public void remove() {
        ifDisplay(display -> {
            entityTracking.remove(display);
            display.remove();
            displayAddress.set(null);
        });
    }

    @Override
    public void spawn() {
        if (!isValid())
            return;
        ifDisplayOrElse(null, () -> {
            Location location = belongsTo.getLocation().setRotation(Rotation.rotation(0, 0));
            World world = Objects.requireNonNull(location.getWorld(), "'location' has no world related");
            TextDisplay textDisplay = (TextDisplay) world.spawnEntity(location, EntityType.TEXT_DISPLAY);
            textDisplay.setText(getText());
            belongsTo.addPassenger(textDisplay);
            textDisplay.setBillboard(Display.Billboard.VERTICAL);
            textDisplay.setTransformation(new Transformation(new Vector3f(0F, 0.35F, 0F), new AxisAngle4f(), new Vector3f(1, 1, 1), new AxisAngle4f()));
            displayAddress.set(textDisplay);
            entityTracking.put(textDisplay, this);
        });
    }

    @Override
    public @Nullable String getText() {
        return textAddress.look();
    }

    @Override
    public void setText(@Nullable String text) {
        if (belongsTo.getGameMode().isInvulnerable())
            return;
        if (belongsTo.isSneaking())
            return;
        text = text == null ? null : TextColor.PARSE(text);
        textAddress.set(text);
        @Nullable String finalText = text;
        ifDisplayOrElse(
                display -> {
                    display.setText(finalText);
                },
                () -> {
                    spawn();
                    setText(finalText);
                });
    }
}
