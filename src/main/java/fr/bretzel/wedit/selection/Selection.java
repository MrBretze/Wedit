package fr.bretzel.wedit.selection;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.UUID;

public class Selection
{
    private static HashMap<UUID, Selection> selections = new HashMap<>();
    private BlockPos locationOne;
    private BlockPos locationTwo;

    public Selection()
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

    public boolean isADoubleLocation(BlockPos location)
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
}
