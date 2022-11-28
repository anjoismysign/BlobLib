package us.mytheria.bloblib.entities;

import java.util.List;

public interface VariableFiller<T> {
    List<VariableValue<T>> page(int page, int itemsPerPage);

    default List<VariableValue<T>> page(int page) {
        return page(page, 45);
    }

    int totalPages(int itemsPerPage);
}
