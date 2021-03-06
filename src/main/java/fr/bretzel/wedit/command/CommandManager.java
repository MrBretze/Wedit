package fr.bretzel.wedit.command;

import fr.bretzel.wedit.Wedit;
import fr.bretzel.wedit.api.command.wedit.IWeditCommand;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TextFormat;

import java.util.*;


public class CommandManager
{
    //Map of custom command String = Label | IWeditCommand = command
    private static HashMap<String, IWeditCommand> commands = new HashMap<>();
    //List of command thread
    public static List<Thread> commandThread = new ArrayList<>();

    /**
     * Used by GuiChat to broadcast a command.
     * @param player
     * @param command
     * @return
     */
    public static boolean processCommand(PlayerEntity player, String command)
    {
        //args = all argument in command: "#demo args[0] args[1] args[2]...
        String[] args = command.substring(1).trim().split("\\p{Z}");
        //label = The name of command.
        String label = args[0];

        if(commands.containsKey(args[0])) {
            //for all command get the name of command and compare by the text entered by the player, if truc execute command.
            for (String s : commands.keySet())
            {
                if (label.equalsIgnoreCase(s))
                {
                    Thread t = new Thread(() ->
                    {
                        //set command feed back to false if cmdFeedBack is true
                        //if(cmdFeedBack)
                            //Wedit.setCommandFeedBack(false);
                        //get the command
                        IWeditCommand cmd = commands.get(s);

                        try {
                            //execute the command
                            boolean sucess = cmd.execute(player, label, dropFirstString(args));

                            if(!sucess)
                                Wedit.sendMessage(TextFormat.RED + cmd.getUsage());
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            //if error print the error to the player and stop the thread
                            Wedit.sendMessage(TextFormat.DARK_RED + "Error in command: " + cmd.getClass().getSimpleName());
                            Wedit.sendMessage(TextFormat.DARK_RED + "Log: ");
                            Wedit.sendMessage(TextFormat.DARK_RED + "" + e.fillInStackTrace());
                            e.printStackTrace();
                            try {
                                Thread.sleep(100L);
                            } catch (InterruptedException e1) {}
                            Thread.currentThread().interrupt();
                        }


                        //remove the current thread
                        commandThread.remove(Thread.currentThread());

                        //if a another command is running to prevent a massive spam message of setblock
                        //if (commandThread.isEmpty() && Undo.undoThread.isEmpty())
                            //Wedit.setCommandFeedBack(cmdFeedBack);
                    });
                    //start the thread of command
                    t.start();
                    //add the Thread of the list to detect if a another command is running.
                    commandThread.add(t);
                    return true;
                }
            }
            return false;
        }

        Wedit.sendMessage(TextFormat.GREEN + "Available command:");

        for (String cmd : commands.keySet())
        {
            Wedit.sendMessage(TextFormat.AQUA + "      " + cmd);
            Wedit.sendMessage(TextFormat.AQUA + "           " + commands.get(cmd).getUsage());
        }

        return false;
    }

    /**
     * Basic method to register a command.
     */
    public static void registerCommand(IWeditCommand cmd, String label)
    {
        if (!commands.containsKey(label))
        {
            commands.put(label, cmd);
        }
    }

    /**
     * creates a new array and sets elements 0..n-2 to be 0..n-1 of the input (n elements)
     */
    private static String[] dropFirstString(String[] input)
    {
        String[] astring = new String[input.length - 1];
        System.arraycopy(input, 1, astring, 0, input.length - 1);
        return astring;
    }
}
