package arhitector.rollbackworld.command;

import arhitector.rollbackworld.command.handler.CommandHandler;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Provides information on the subcommand.
 */
public class Command {
	
	@Getter
	private String label;
	
	@Getter
	private Constructor<? extends CommandHandler> constructor;
	
	@Getter
	private String permission;
	
	@Getter
	private String usage;
	
	@Getter
	private int argCount;
	
	@Getter
	private boolean consoleAllowed;
	
	@Getter
	private String[] aliases;
	
	public Command(String label, Class<? extends CommandHandler> handlerClass, String perm, String usage, int argCount, boolean consoleAllowed, String... aliases) {
		this.label = label;
		
		try {
			this.constructor = handlerClass.getConstructor(CommandSender.class, String[].class);
		} catch (NoSuchMethodException ex) {
			throw new AssertionError(ex);
		}
		
		this.permission = perm;
		this.usage = usage;
		this.argCount = argCount;
		this.consoleAllowed = consoleAllowed;
		this.aliases = aliases;
	}
	
	/**
	 * The constructor of the handler.
	 */
	public Constructor<? extends CommandHandler> getHandlerConstructor() {
		return constructor;
	}
	
	/**
	 * Execution of the handler.
	 */
	public void invoke(CommandSender sender, String[] args) {
		if (!doAssertions(sender, args)) {
			return;
		}
		
		try {
			this.getHandlerConstructor().newInstance(sender, args).handle();
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new AssertionError(ex);
		} catch (InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Выполнить тесты и проверки полученных данных.
	 */
	private boolean doAssertions(CommandSender sender, String[] args) {
		return assertPermission(sender) && assertPlayer(sender) && assertArgumentCount(sender, args);
	}
	
	/**
	 * Разрешение на использование подкоманды.
	 */
	private boolean assertPermission(CommandSender sender) {
		if (getPermission() != null && !sender.hasPermission(getPermission())) {
			sender.sendMessage("У Вас нет прав на выполнение команды.");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Разрешение на использование субъектом.
	 */
	private boolean assertPlayer(CommandSender sender) {
		if (!isConsoleAllowed() && !(sender instanceof Player)) {
			sender.sendMessage("Команду разрешено выполнять только игрокам.");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Проверка синтаксиса.
	 */
	private boolean assertArgumentCount(CommandSender sender, String[] args) {
		if (args.length < getArgCount()) {
			sender.sendMessage("Не правильный синтаксис команды. " + this.getUsage());
			return false;
		}
		
		return true;
	}
	
}
