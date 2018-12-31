package arhitector.rollbackworld.command.handler;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.command.CommandSender;

/**
 * Command handler.
 */
public abstract class CommandHandler {
	
	@Getter
	private CommandSender sender;
	
	@Getter
	private String[] args;
	
	/**
	 * Command handler.
	 *
	 * @param sender Source of the command
	 * @param args   Arguments.
	 */
	public CommandHandler(@NonNull CommandSender sender, @NonNull String[] args) {
		this.sender = sender;
		this.args = args;
	}
	
	/**
	 * The handler.
	 */
	public abstract void handle();
	
	/**
	 * Print help for the command.
	 */
	protected void printInvalidArgsError() {
		this.getSender().sendMessage("Invalid arguments.");
	}
	
	/**
	 * Check if the string is a number.
	 */
	protected boolean isInt(String string) {
		try {
			Integer.parseInt(string);
			
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
}