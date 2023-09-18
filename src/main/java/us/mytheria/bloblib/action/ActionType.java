package us.mytheria.bloblib.action;

/**
 * The type of action.
 */
public enum ActionType {
    /**
     * A command that will perform with a null actor.
     */
    NO_ACTOR,
    /**
     * A command that is run by the actor.
     */
    ACTOR_COMMAND,
    /**
     * A command that is run by the console.
     */
    CONSOLE_COMMAND
}
