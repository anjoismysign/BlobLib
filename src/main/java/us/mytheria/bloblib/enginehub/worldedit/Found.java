package us.mytheria.bloblib.enginehub.worldedit;

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
    public Pattern parse(String string) {
        PatternFactory factory = com.sk89q.worldedit.WorldEdit.getInstance().getPatternFactory();
        try {
            return factory.parseFromInput(string, new ParserContext());
        } catch (InputParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean setBlocks(EditSession session, Region region, Pattern pattern) {
        try {
            session.setBlocks(region, pattern);
            Operation operation = session.commit();
            Operations.complete(operation);
            return true;
        } catch (WorldEditException e) {
            e.printStackTrace();
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
