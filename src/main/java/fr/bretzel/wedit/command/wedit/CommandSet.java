package fr.bretzel.wedit.command.wedit;

import fr.bretzel.wedit.BlockInteract;
import fr.bretzel.wedit.Wedit;
import fr.bretzel.wedit.api.command.wedit.IWeditCommand;
import fr.bretzel.wedit.block.BlockHelper;
import fr.bretzel.wedit.block.WeditBlock;
import fr.bretzel.wedit.selection.Selection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;

public class CommandSet extends IWeditCommand
{
    private boolean hollow;

    public CommandSet(boolean hollow)
    {
        this.hollow = hollow;
    }

    @Override
    public boolean execute(PlayerEntity sender, String label, String[] args)
    {
        Selection selection = Selection.getSelection(sender.getUuid()).clone();

        if (selection.getLocationOne() == null)
        {
            Wedit.sendMessage(TextFormat.AQUA + "Please select your first point.");
            return false;
        } else if (selection.getLocationTwo() == null)
        {
            Wedit.sendMessage(TextFormat.AQUA + "Please select your second point.");
            return false;
        }

        if (args.length <= 0)
        {
            Wedit.sendMessage(TextFormat.RED + getUsage());
            return false;
        }

        Block blk = Registry.BLOCK.get(new Identifier(args[0]));

        if (blk == null)
        {
            Wedit.sendMessage(TextFormat.RED + "Please chose a correct block name.");
            return false;
        }

        long speed;


        try
        {
            speed = Integer.valueOf(args[1]);
        } catch (Exception e)
        {
            speed = 0;
        }

        if (speed == 0 && Wedit.isServer())
            speed = 1;

        //Undo.addUndo(undo);

        ArrayList<WeditBlock> normalPos = new ArrayList<>();

        World world = sender.world;

        for (int z = selection.getMinZ(); z <= selection.getMaxZ(); z++)
        {
            for (int x = selection.getMinX(); x <= selection.getMaxX(); x++)
            {
                for (int y = selection.getMaxY(); y >= selection.getMinY(); y--)
                {
                    if (hollow)
                    {
                        if (x == selection.getMaxX() || x == selection.getMinX() || z == selection.getMaxZ() || z == selection.getMinZ()
                                || y == selection.getMaxY() || y == selection.getMinY())
                        {
                            BlockPos position = new BlockPos(x, y, z);
                            BlockState blockState = world.getBlockState(position);
                            normalPos.add(new WeditBlock(blockState.getBlock(), blockState, world, position));
                        }

                    } else
                    {

                        BlockPos position = new BlockPos(x, y, z);
                        BlockState blockState = world.getBlockState(position);
                        normalPos.add(new WeditBlock(blockState.getBlock(), blockState, world, position));
                    }
                }
            }
        }

        for (WeditBlock currentBlock : BlockHelper.sortByPriority(normalPos))
        {
            if (currentBlock.getBlock() == blk)
                continue;

            Wedit.setBlock(blk, currentBlock.getPosition(), BlockInteract.REPLACE);

            if (speed > 0)
            {
                try
                {
                    Thread.sleep(speed);
                } catch (InterruptedException e)
                {
                }
            }
        }

        Wedit.sendMessage(TextFormat.AQUA + "Your are set " + TextFormat.BLUE + (normalPos.size()) + TextFormat.AQUA + " block of " + TextFormat.BLUE + args[0].toUpperCase());
        return true;
    }

    @Override
    public String getUsage()
    {
        if (hollow)
            return "Usage: #hset <block> <skeep>";
        else return "Usage: #set <block> <skeep>";
    }
}
