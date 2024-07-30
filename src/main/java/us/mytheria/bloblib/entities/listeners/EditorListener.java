package us.mytheria.bloblib.entities.listeners;

import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.BlobEditor;
import us.mytheria.bloblib.entities.inventory.VariableSelector;

import java.util.function.Consumer;

public class EditorListener<T> extends InputListener {
    private T input;
    private final BlobEditor<T> editor;

    public EditorListener(String owner, Runnable inputRunnable, BlobEditor<T> editor) {
        super(owner, inputListener -> {
            inputRunnable.run();
        });
        this.editor = editor;
        register();
        editor.open();
    }

    @SuppressWarnings("unchecked")
    public EditorListener(String owner, Consumer<EditorListener<T>> inputConsumer,
                          BlobEditor<T> editor) {
        super(owner, inputListener -> {
            inputConsumer.accept((EditorListener<T>) inputListener);
        });
        this.editor = editor;
        register();
        editor.open();
    }

    private void register() {
        BlobLib.getInstance().getVariableSelectorManager().addEditorSelector(editor);
    }

    @Override
    public T getInput() {
        return input;
    }

    public void setInput(T input) {
        this.input = input;
        cancel();
        inputConsumer.accept(this);
    }

    /**
     * Sets the input from the editor slot
     *
     * @param selector the selector
     * @param slot     the slot
     * @return true if successful, false otherwise
     */
    @SuppressWarnings("unchecked")
    public boolean setInputFromSlot(VariableSelector<?> selector, int slot) {
        if (!getEditor().equals(this.editor))
            return false;
        @Nullable T input = (T) selector.getValue(slot);
        if (input == null) {
            setInput(null);
            return false;
        }
        setInput(input);
        return true;
    }

    public BlobEditor<T> getEditor() {
        return editor;
    }
}