package us.mytheria.bloblib.entities;

public interface VariableFiller {
    VariableValue[] page(int page, int itemsPerPage);

    default VariableValue[] page(int page) {
        return page(page, 45);
    }

    int totalPages(int itemsPerPage);
}
