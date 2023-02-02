package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Result;

public class BlobChildCommand {
    private final String name;
    private final String[] args;
    private final int index;

    /**
     * Will construct a child command by passing
     * arguments and the index of the child command.
     * You need to be sure that the index is a valid
     * and contains a not null String.
     *
     * @param args  The arguments, provided inside onCommand() method.
     * @param index The index of the child command of the arguments.
     */
    public BlobChildCommand(String[] args, int index) {
        this.name = args[index];
        this.args = args;
        this.index = index;
    }

    /**
     * Will attempt to retrieve a child command.
     * If the child command does not exist, because arguments
     * length doesn't allow it, or because the child command
     * is not the same as inputted, it will return an invalid
     * result. Otherwise, will provide a valid result.
     * ALWAYS CHECK IF THE RESULT IS VALID BEFORE USING IT!
     *
     * @param childCommand The child command to retrieve.
     * @return A Result containing the child command if it exists.
     */
    public Result<BlobChildCommand> isChildCommand(String childCommand) {
        int size = index + 1;
        if (args.length <= size)
            return Result.invalidBecauseNull();
        if (!args[size].equalsIgnoreCase(childCommand))
            return Result.invalidBecauseNull();
        return Result.valid(new BlobChildCommand(args, size));
    }
}
