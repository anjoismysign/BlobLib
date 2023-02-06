package us.mytheria.bloblib.entities.scripting;

import us.mytheria.bloblib.entities.InputContainer;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public abstract class ScriptInterpretation<T> {
    private final InputContainer<T> inputContainer;
    private final ScriptEngine engine;

    public ScriptInterpretation(InputContainer<T> inputContainer, ScriptEngine engine) {
        this.inputContainer = inputContainer;
        this.engine = engine;
    }

    public Object interpretAndExecute() throws ScriptException {
        engine.put("value", inputContainer.value());
        return engine.eval(inputContainer.input());
    }
}