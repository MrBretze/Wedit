package fr.bretzel.wedit.selection;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.UUID;

public class Selection
{
    private static HashMap<UUID, Selection> selections = new HashMap<>();
    private BlockPos locationOne;
    private BlockPos locationTwo;

    private Selection()
    {
    }

    public Selection(BlockPos locationOne, BlockPos locationTwo)
    {
        setLocationOne(locationOne);
        setLocationTwo(locationTwo);
    }

    public static Selection getSelection(UUID player)
    {
        if (selections.containsKey(player))
        {
            return selections.get(player);
        } else
        {
            Selection selection = new Selection();
            selections.put(player, selection);
            return selection;
        }
    }

    public static Selection removeSelection(UUID player)
    {
        if (selections.containsKey(player))
        {
            return selections.remove(player);
        }
        return null;
    }

    public BlockPos getLocationTwo()
    {
        return locationTwo;
    }

    public void setLocationTwo(BlockPos locationTwo)
    {
        this.locationTwo = locationTwo;
    }

    public BlockPos getLocationOne()
    {
        return locationOne;
    }

    public void setLocationOne(BlockPos locationOne)
    {
        this.locationOne = locationOne;
    }

    public boolean isSelectioned(BlockPos location)
    {
        return isLocationTwo(location) || isLocationOne(location);
    }

    public boolean isLocationTwo(BlockPos location)
    {
        if (getLocationTwo() == null)
            return false;

        return getLocationTwo().getX() == location.getX() &&
                getLocationTwo().getY() == location.getY() &&
                getLocationTwo().getZ() == location.getZ();
    }

    public boolean isLocationOne(BlockPos location)
    {
        if (getLocationOne() == null)
            return false;

        return getLocationOne().getX() == location.getX() &&
                getLocationOne().getY() == location.getY() &&
                getLocationOne().getZ() == location.getZ();
    }

    public boolean isLocationSet()
    {
        return getLocationOne() != null && getLocationTwo() != null;
    }

    public BlockPos getMaxPos()
    {
        return new BlockPos(getMaxX(), getMaxY(), getMaxZ());
    }

    public BlockPos getMinPos()
    {
        return new BlockPos(getMinX(), getMinY(), getMinZ());
    }

    public int getMinX()
    {
        return getMin(getLocationOne().getX(), getLocationTwo().getX());
    }

    public int getMaxX()
    {
        return getMax(getLocationOne().getX(), getLocationTwo().getX());
    }

    public int getMinY()
    {
        return getMin(getLocationOne().getY(), getLocationTwo().getY());
    }

    public int getMaxY()
    {
        return getMax(getLocationOne().getY(), getLocationTwo().getY());
    }

    public int getMinZ()
    {
        return getMin(getLocationOne().getZ(), getLocationTwo().getZ());
    }

    public int getMaxZ()
    {

        return getMax(getLocationOne().getZ(), getLocationTwo().getZ());
    }

    private int getMax(int pos, int pos1)
    {
        return pos > pos1 ? pos : pos1;
    }

    private int getMin(int pos, int pos1)
    {
        return pos < pos1 ? pos : pos1;
    }

    @Override
    public Selection clone()
    {
        return new Selection(new BlockPos(getMaxX(), getMaxY(), getMaxZ()), new BlockPos(getMinX(), getMinY(), getMinZ()));
    }
}
