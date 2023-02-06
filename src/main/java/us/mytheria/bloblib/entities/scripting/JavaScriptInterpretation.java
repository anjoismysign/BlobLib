package us.mytheria.bloblib.entities.scripting;

import us.mytheria.bloblib.entities.InputContainer;
import us.mytheria.bloblib.managers.ScriptManager;

public class JavaScriptInterpretation<T> extends ScriptInterpretation<T> {
    public JavaScriptInterpretation(InputContainer<T> inputContainer) {
        super(inputContainer, ScriptManager.getEngine("JavaScript").value());
    }
}
