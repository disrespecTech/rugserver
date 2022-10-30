package github.totorewa.rugserver.helper;

import net.minecraft.util.math.Direction;

import static net.minecraft.util.math.Direction.*;

public class QuickDirections {
    public final static Direction[] DIRECTIONS = Direction.values();
    public final static Direction.AxisDirection[] AXIS = Direction.AxisDirection.values();
    public final static Direction[] HORIZONTAL = new Direction[]{NORTH, EAST, SOUTH, WEST};
    public final static Direction[] VERTICAL = new Direction[]{UP, DOWN};
}
