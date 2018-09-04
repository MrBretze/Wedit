package fr.bretzel.wedit.command;

import fr.bretzel.wedit.Wedit;
import fr.bretzel.wedit.command.api.IWeditCommand;
import net.minecraft.client.entity.EntityPlayerSP;

public class CommandPos extends IWeditCommand {

	private boolean posOne = true;
	
	public CommandPos(boolean posOne) 
	{
		this.posOne = posOne;
	}
	
	@Override
	public boolean execute(EntityPlayerSP sender, String label, String[] args) {
		if (isPosOne())
		{
			Wedit.setFirstPosition(sender.getPosition());
		} else 
		{
			Wedit.setSecondPosition(sender.getPosition());
		}
		return false;
	}

	public boolean isPosOne() {
		return posOne;
	}
	
	@Override
	public String getUsage() {
		return isPosOne() ? "Usage: #pos1" : "Usage: #pos2";
	}

}
