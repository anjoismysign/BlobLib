package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.anjo.entities.Result;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptManager {
    private final ScriptEngineManager engineManager;

    /**
     * Get a script engine by name
     *
     * @param name The name of the script engine
     * @return A valid result if the script engine exists, otherwise an invalid result
     */
    public static Result<ScriptEngine> getEngine(String name) {
        return BlobLib.getInstance().getScriptManager().getScriptEngine(name);
    }

    public ScriptManager() {
        engineManager = new ScriptEngineManager();
    }

    /**
     * Get a script engine by name
     *
     * @param name The name of the script engine
     * @return A valid result if the script engine exists, otherwise an invalid result
     */
    public Result<ScriptEngine> getScriptEngine(String name) {
        return Result.ofNullable(engineManager.getEngineByName(name));
    }
}
