package us.mytheria.bloblib.entities.inventory;

public interface ButtonVisitor {
    void visit(InventoryButton button);

    void visit(MetaInventoryButton button);
}
