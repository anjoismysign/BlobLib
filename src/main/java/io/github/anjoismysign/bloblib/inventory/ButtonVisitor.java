package io.github.anjoismysign.bloblib.inventory;

public interface ButtonVisitor {
    void visit(InventoryButton button);

    void visit(MetaInventoryButton button);
}
