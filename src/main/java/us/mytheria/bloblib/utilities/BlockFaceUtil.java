package us.mytheria.bloblib.utilities;

import org.bukkit.block.BlockFace;
import org.bukkit.block.structure.StructureRotation;
import org.jetbrains.annotations.NotNull;

public class BlockFaceUtil {

    @NotNull
    public static BlockFace rotateCardinalDirection(BlockFace face, StructureRotation rotation) {
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
                    case NORTH_EAST -> {
                        return BlockFace.SOUTH_EAST;
                    }
                    case SOUTH_EAST -> {
                        return BlockFace.SOUTH_WEST;
                    }
                    case SOUTH_WEST -> {
                        return BlockFace.NORTH_WEST;
                    }
                    case NORTH_WEST -> {
                        return BlockFace.NORTH_EAST;
                    }
                    default -> {
                        return face;
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
                    case NORTH_EAST -> {
                        return BlockFace.SOUTH_WEST;
                    }
                    case SOUTH_EAST -> {
                        return BlockFace.NORTH_WEST;
                    }
                    case SOUTH_WEST -> {
                        return BlockFace.NORTH_EAST;
                    }
                    case NORTH_WEST -> {
                        return BlockFace.SOUTH_EAST;
                    }
                    default -> {
                        return face;
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
                    case NORTH_EAST -> {
                        return BlockFace.NORTH_WEST;
                    }
                    case SOUTH_EAST -> {
                        return BlockFace.NORTH_EAST;
                    }
                    case SOUTH_WEST -> {
                        return BlockFace.SOUTH_EAST;
                    }
                    case NORTH_WEST -> {
                        return BlockFace.SOUTH_WEST;
                    }
                    default -> {
                        return face;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Invalid StructureRotation: " + rotation);
    }
}
