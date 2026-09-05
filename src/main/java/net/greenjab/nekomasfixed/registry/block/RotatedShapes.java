package net.greenjab.nekomasfixed.registry.block;

import com.google.common.collect.Maps;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

// spins a shape authored facing north around the Y axis, the way vanilla's wall-mounted blocks hand-write theirs
public final class RotatedShapes {
    private RotatedShapes() {
    }

    /** Keyed by the direction the block faces; {@code north} is the shape as authored. */
    public static Map<Direction, VoxelShape> horizontal(VoxelShape north) {
        Map<Direction, VoxelShape> shapes = Maps.newEnumMap(Direction.class);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            shapes.put(direction, rotate(north, direction));
        }
        return shapes;
    }

    public static VoxelShape rotate(VoxelShape north, Direction facing) {
        if (facing == Direction.NORTH) {
            return north;
        }
        VoxelShape[] rotated = {Shapes.empty()};
        north.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            VoxelShape box = switch (facing) {
                case SOUTH -> Shapes.box(1.0 - maxX, minY, 1.0 - maxZ, 1.0 - minX, maxY, 1.0 - minZ);
                case WEST -> Shapes.box(minZ, minY, minX, maxZ, maxY, maxX);
                case EAST -> Shapes.box(1.0 - maxZ, minY, 1.0 - maxX, 1.0 - minZ, maxY, 1.0 - minX);
                default -> Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
            };
            rotated[0] = Shapes.or(rotated[0], box);
        });
        return rotated[0].optimize();
    }
}
