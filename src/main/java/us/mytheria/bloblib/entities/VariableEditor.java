package us.mytheria.bloblib.entities;

/**
 * @author anjoismysign
 * <p>
 * A VariableEditor can add and remove
 * objects of a collection inside a GUI.
 */
public interface VariableEditor<T> extends VariableFiller<T> {
    /**
     * Adds a new object to the editor.
     *
     * @param t The object to add.
     */
    void add(T t);
}