package arhitector.rollbackworld.command;

import arhitector.rollbackworld.command.handler.*;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The command manager.
 */
public class CommandManager implements CommandExecutor {
	
	@Getter
	private final ImmutableMap<String, Command> commands;
	
	{
		Map<String, Command> map = new LinkedHashMap<>();
		
		this.addRef(map, "select", SelectCommand.class, "basic", "", 0, false);
		this.addRef(map, "create", CreateCommand.class, "basic", "<label>", 1, false);
		this.addRef(map, "restore", RestoreCommand.class, "basic", "<label>", 1, true);
		
		this.commands = ImmutableMap.copyOf(map);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		if (args.length > 0) {
			final String subCommand = args[0].toLowerCase();
			
			if (this.getCommands().containsKey(subCommand)) {
				this.getCommands().get(subCommand).invoke(sender, args);
			} else {
				sender.sendMessage("There is no such subcommand.");
			}
		} else {
			new DefaultCommand(this, sender, args).handle();
		}
		
		return true;
	}
	
	/**
	 * Creates a wrapper over the command.
	 *
	 * @param map            Object to add a list of commands.
	 * @param command        The subcommand.
	 * @param clazz          The subcommand handler.
	 * @param permission     Permissions for the command.
	 * @param usage          Usage hint.
	 * @param minArgs        Number of required arguments.
	 * @param consoleAllowed You are allowed to use the command in the console or not.
	 * @param aliases        List of aliases.
	 */
	private void addRef(Map<String, Command> map, String command, Class<? extends CommandHandler> clazz, String permission, String usage, int minArgs, boolean consoleAllowed, String... aliases) {
		command = command.toLowerCase();
		Command cr = new Command(command, clazz, permission, "/rw " + command + " " + usage, 1 + minArgs, consoleAllowed, aliases);
		map.put(command, cr);
  
		for (String alias : aliases) {
			map.put(alias, cr);
		}
	}
	
}