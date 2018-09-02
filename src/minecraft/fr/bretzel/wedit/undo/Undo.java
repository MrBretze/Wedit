package fr.bretzel.wedit.undo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.bretzel.wedit.Wedit;
import fr.bretzel.wedit.util.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class Undo
{
    private BlockPos pos1;
    private BlockPos pos2;
    private EntityPlayerSP sender;
    private World world;

    private List<UndoBlock> undoBlocks = new ArrayList<>();

    //return a futur list to block posed after
    private static List<Material> endPriorityList = new ArrayList<>();
    //return a thread of undo
    public static List<Thread> undoThread = new ArrayList<>();
    //return to the list of undo
    private static ArrayList<Undo> lists = new ArrayList<>();

    static
    {
        endPriorityList.add(Material.DEADBUSH);
        endPriorityList.add(Material.REEDS);
        endPriorityList.add(Material.CACTUS);
        endPriorityList.add(Material.FARMLAND);
        endPriorityList.add(Material.WATER);
        endPriorityList.add(Material.LAVA);
        endPriorityList.add(Material.CARROTS);
        endPriorityList.add(Material.BEETROOTS);
        endPriorityList.add(Material.POTATOES);
        endPriorityList.add(Material.WHEAT);
        endPriorityList.add(Material.TALLGRASS);
        endPriorityList.add(Material.DOUBLE_PLANT);
        endPriorityList.add(Material.YELLOW_FLOWER);
        endPriorityList.add(Material.RED_FLOWER);
        endPriorityList.add(Material.RED_MUSHROOM_BLOCK);
        endPriorityList.add(Material.BROWN_MUSHROOM_BLOCK);
        endPriorityList.add(Material.GRAVEL);
        endPriorityList.add(Material.SAND);
        endPriorityList.add(Material.CONCRETE_POWDER);
        endPriorityList.add(Material.LEAVES);
        endPriorityList.add(Material.LEAVES2);
        endPriorityList.add(Material.VINE);
        endPriorityList.add(Material.COCOA);
        endPriorityList.add(Material.ANVIL);
    }

    public Undo(BlockPos pos1, BlockPos pos2, EntityPlayerSP sender)
    {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.sender = sender;
        this.world = sender.getEntityWorld();
    }

    /**
     * to the first block pos
     * @return
     */
    public BlockPos getPos1()
    {
        return pos1;
    }

    /**
     * to the second block pos
     * @return
     */
    public BlockPos getPos2()
    {
        return pos2;
    }

    /**
     * To all undo block
     * @return
     */
    public List<UndoBlock> getUndoBlocks()
    {
        return undoBlocks;
    }

    /**
     * Get the undo block by position
     * @param pos
     * @return
     */
    public UndoBlock getBlock(BlockPos pos)
    {
        for (UndoBlock b : undoBlocks)
        {
            if (b.getPosition().toString().equalsIgnoreCase(pos.toString()))
            {
                return b;
            }
        }

        return null;
    }

    public EntityPlayerSP getSender()
    {
        return this.sender;
    }

    public World getWorld()
    {
        return this.world;
    }

    /**
     * To add a new block to the list.
     * @param pos
     */
    public void addUndo(BlockPos pos)
    {
        undoBlocks.add(new UndoBlock(pos));
    }

    /**
     * STATIC MEMBER
     */

    //To add a undo
    public static void addUndo(Undo undo)
    {
        getAllUndo().add(undo);
    }

    //Return on all undo
    public static ArrayList<Undo> getAllUndo()
    {
        return lists;
    }

    //Process on last undo
    public static void processLastUndo(long speed)
    {
        int last = lists.size() - 1;

        //if last is superior of 0 to prevent a crash
        if (last >= 0)
        {
            // create new Thread
            Thread undoThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    //Get the undo
                    Undo undo = getAllUndo().get(last);
                    //Remove the undo
                    getAllUndo().remove(last);
                    //Get all undo block
                    List<UndoBlock> blocks = undo.getUndoBlocks();
                    //Sort block
                    Collections.sort(blocks, new UndoSort());
                    //New list for the end block
                    List<UndoBlock> endBlock = new ArrayList<>();

                    //Run the main list
                    for (UndoBlock block : undo.getUndoBlocks())
                    {
                        if (undo.getWorld().getBlockState(block.getPosition()).getBlock().getLocalizedName().equalsIgnoreCase("air") && block.getMaterial() == Material.AIR)
                        {
                            continue;
                        }
                        
                        //if is end block
                        if(isEndBlock(block.getMaterial()))
                        {
                        	Wedit.setBlock(block.getPosition(), "air", 0);
                            endBlock.add(block);
                            continue;
                        }

                        //if the speed
                        if (speed > 0)
                        {
                            try
                            {
                                //Skeep a time
                                Thread.sleep(speed);
                            }
                            catch (InterruptedException e) {}
                        }
                        
                        //set the undo block
                        Wedit.setBlock(block);
                    }

                    endBlock.sort(new Comparator<UndoBlock>()
                    {
                        @Override
                        public int compare(UndoBlock o1, UndoBlock o2)
                        {
                        	if (o1.getPosition().getY() == o2.getPosition().getY())
                        		return 0;
                        	
                        	if (o1.getPosition().getY() > o2.getPosition().getY())
                        		return 1;
                        	
                        	if (o1.getPosition().getY() < o2.getPosition().getY())
                        		return -1;
                        	
                            return 0;
                        }
                    });

                    //Proceed to the end list
                    for (UndoBlock block : endBlock)
                    {
                        try
                        {
                            //Skeep a time to make sure block is correctly actualised.
                            Thread.sleep(speed / 2);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }

                        //set the undo block
                        Wedit.setBlock(block);
                    }

                    //send a message and remove the thread to the list
                    Wedit.sendMessage(TextFormatting.AQUA + "Undo ! " + TextFormatting.BLUE + undo.getUndoBlocks().size() + TextFormatting.AQUA + " Blocks !");
                    Undo.undoThread.remove(Thread.currentThread());
                }
            });
            //Start the thread
            undoThread.start();
            //Add the thread to the list
            Undo.undoThread.add(undoThread);
        }
    }

    private static boolean isEndBlock(Material material)
    {
        return endPriorityList.contains(material);
    }
}
