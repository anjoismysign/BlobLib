package us.mytheria.bloblib.action;

/**
 * The type of action.
 */
public enum ActionType {
    /**
     * No action.
     */
    NONE,
    /**
     * A command that is run by the actor.
     */
    ACTOR_COMMAND,
    /**
     * A command that is run by the console.
     */
    CONSOLE_COMMAND
}
