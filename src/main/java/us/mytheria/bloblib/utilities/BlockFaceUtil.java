package us.mytheria.bloblib.utilities;

import org.bukkit.block.BlockFace;
import org.bukkit.block.structure.StructureRotation;
import org.jetbrains.annotations.NotNull;

public class BlockFaceUtil {

    @NotNull
    public static BlockFace rotate(BlockFace face, StructureRotation rotation) {
        switch (rotation) {
            case NONE -> {
                return face;
            }
            case CLOCKWISE_90 -> {
                switch (face) {
                    case NORTH -> {
                        return BlockFace.EAST;
                    }
                    case EAST -> {
                        return BlockFace.SOUTH;
                    }
                    case SOUTH -> {
                        return BlockFace.WEST;
                    }
                    case WEST -> {
                        return BlockFace.NORTH;
                    }
                    default -> {
                        throw new IllegalArgumentException("Invalid BlockFace: " + face);
                    }
                }
            }
            case CLOCKWISE_180 -> {
                switch (face) {
                    case NORTH -> {
                        return BlockFace.SOUTH;
                    }
                    case EAST -> {
                        return BlockFace.WEST;
                    }
                    case SOUTH -> {
                        return BlockFace.NORTH;
                    }
                    case WEST -> {
                        return BlockFace.EAST;
                    }
                    default -> {
                        throw new IllegalArgumentException("Invalid BlockFace: " + face);
                    }
                }
            }
            case COUNTERCLOCKWISE_90 -> {
                switch (face) {
                    case NORTH -> {
                        return BlockFace.WEST;
                    }
                    case EAST -> {
                        return BlockFace.NORTH;
                    }
                    case SOUTH -> {
                        return BlockFace.EAST;
                    }
                    case WEST -> {
                        return BlockFace.SOUTH;
                    }
                    default -> {
                        throw new IllegalArgumentException("Invalid BlockFace: " + face);
                    }
                }
            }
        }
        throw new IllegalArgumentException("Invalid StructureRotation: " + rotation);
    }
}
