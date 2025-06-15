package io.github.anjoismysign.bloblib.entities.inventory;

public interface ButtonVisitor {
    void visit(InventoryButton button);

    void visit(MetaInventoryButton button);
}
