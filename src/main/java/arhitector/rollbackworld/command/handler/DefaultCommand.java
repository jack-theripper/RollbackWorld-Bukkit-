package arhitector.rollbackworld.command.handler;

import com.google.common.collect.ImmutableMap;
import arhitector.rollbackworld.command.Command;
import arhitector.rollbackworld.command.CommandManager;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Default handler.
 */
public class DefaultCommand extends CommandHandler {
	
	@Getter
	private final CommandManager commandManager;
	
	/**
	 * The default command handler.
	 *
	 * @param commandManager Менеджер.
	 * @param sender         Источник команды
	 * @param args           Арументы.
	 */
	public DefaultCommand(CommandManager commandManager, CommandSender sender, String[] args) {
		super(sender, args);
		this.commandManager = commandManager;
	}
	
	@Override
	public void handle() {
		this.getSender().sendMessage(ChatColor.RED + "List of commands:");
		List<String> skip = new ArrayList<>();
		ImmutableMap<String, Command> commands = this.getCommandManager().getCommands();
		
		for (Map.Entry<String, Command> entry : commands.entrySet()) {
			if (!skip.contains(entry.getKey())) {
				this.getSender().sendMessage(ChatColor.GRAY + entry.getValue().getUsage());
				Collections.addAll(skip, entry.getValue().getAliases());
			}
		}
	}
	
}
