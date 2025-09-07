package io.github.anjoismysign.bloblib.middleman.enginehub.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.factory.PatternFactory;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.World;

public class Found implements WorldEditWorker {
    @Override
    public EditSession editSession(World world) {
        return com.sk89q.worldedit.WorldEdit.getInstance().newEditSession(world(world));
    }

    @Override
    public com.sk89q.worldedit.world.World world(World world) {
        return BukkitAdapter.adapt(world);
    }

    @Override
    public Pattern pattern(String string) {
        PatternFactory factory = com.sk89q.worldedit.WorldEdit.getInstance().getPatternFactory();
        try {
            ParserContext context = new ParserContext();
            context.setRestricted(false);
            context.setTryLegacy(false);
            return factory.parseFromInput(string, context);
        } catch (InputParseException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean setBlocks(Object sessionObject, Object regionObject, Object patternObject) {
        if (!(sessionObject instanceof EditSession session))
            return false;
        if (!(regionObject instanceof Region region))
            return false;
        if (!(patternObject instanceof Pattern pattern))
            return false;
        try {
            session.setBlocks(region, pattern);
            Operation operation = session.commit();
            Operations.complete(operation);
            return true;
        } catch (WorldEditException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public CuboidRegion cuboidRegion(Location min, Location max) {
        return new CuboidRegion(blockVector3(min), blockVector3(max));
    }

    @Override
    public BlockVector3 blockVector3(Location location) {
        return BukkitAdapter.asBlockVector(location);
    }

    @Override
    public BlockVector2 blockVector2(Location location) {
        return blockVector3(location).toBlockVector2();
    }
}
