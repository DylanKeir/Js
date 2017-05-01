package eu.dyl4n.js.command;

import eu.dyl4n.js.JsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandJs implements CommandExecutor
{

	@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!sender.isOp())
		{
			return false;
		}
		sender.sendMessage("[Js] Reloading scripts...");
		long startTime = System.currentTimeMillis();
		try
		{
			JsPlugin.get().getLoader().close();
			JsPlugin.get().getLoader().run();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		sender.sendMessage("[Js] Reloaded scripts in " + (System.currentTimeMillis() - startTime) + "ms.");
		return false;
	}

}
