package us.mytheria.bloblib.entities;

public interface VariableEditor<T> extends VariableFiller<T> {
    void add(T t);
}