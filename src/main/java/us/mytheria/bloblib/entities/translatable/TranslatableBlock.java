package us.mytheria.bloblib.entities.translatable;

import java.util.List;

public interface TranslatableBlock extends Translatable<List<String>> {
    default TranslatableBlockModder modder() {
        return TranslatableBlockModder.mod(this);
    }
}
