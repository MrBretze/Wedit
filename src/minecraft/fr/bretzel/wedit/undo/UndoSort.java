package fr.bretzel.wedit.undo;

import java.util.Comparator;

import net.minecraft.util.math.BlockPos;

public class UndoSort implements Comparator<UndoBlock>
{
    /**
     * For sort the undo to set correctly the block by an a specific order.
     */
    @Override
    public int compare(UndoBlock pos1, UndoBlock pos2)
    {
        int x, y, z;
        y = pos1.getPosition().getY() - pos2.getPosition().getY();
        x = pos1.getPosition().getX() - pos2.getPosition().getX() + y;
        z = pos1.getPosition().getZ() - pos2.getPosition().getZ() + y;
        return (x - y) + (z - x);
    }
}
