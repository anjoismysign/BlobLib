package us.mytheria.bloblib.entities.listeners;

import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.BlobEditor;

import java.util.function.Consumer;

public class EditorListener<T> extends InputListener {
    private EditorActionType input;
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
    public EditorListener(String owner, Consumer<EditorActionType> inputConsumer,
                          BlobEditor<T> editor) {
        super(owner, inputListener -> {
            inputConsumer.accept(((EditorListener<T>) inputListener).getInput());
        });
        this.editor = editor;
        register();
        editor.open();
    }

    private void register() {
        BlobLib.getInstance().getVariableSelectorManager().addVariableSelector(editor);
    }

    @Override
    public EditorActionType getInput() {
        return input;
    }

    public void setInput(EditorActionType input) {
        this.input = input;
        cancel();
        inputConsumer.accept(this);
    }

    @SuppressWarnings("unchecked")
    public void setInputFromSlot(BlobEditor<?> editor, int slot) {
        if (!getEditor().equals(this.editor))
            return;
        if (editor.isNextPageButton(slot))
            setInput(EditorActionType.NEXT_PAGE);
        else if (editor.isPreviousPageButton(slot))
            setInput(EditorActionType.PREVIOUS_PAGE);
        else if (editor.getButton("Add").containsSlot(slot))
            setInput(EditorActionType.ADD);
        else if (editor.getButton("Remove").containsSlot(slot))
            setInput(EditorActionType.REMOVE);
        else
            setInput(EditorActionType.NONE);
    }

    public BlobEditor<T> getEditor() {
        return editor;
    }
}