package mehdi.bad.addons.Commands.Subcommands;

import mehdi.bad.addons.Features.General.ReplacedWordsConfig;
import mehdi.bad.addons.utils.ChatLib;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.Map;

public class ReplaceWordSubcommand implements Subcommand {

	@Override
	public String getCommandName() {
		return "replaceword";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public String getCommandUsage() {
		return "";
	}

	@Override
	public String getCommandDescription() {
	return "Allows you to replace words.";
	}
	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {
		ReplacedWordsConfig.load();
		if (args.length == 0) {
			// Display help message when no arguments are provided
			ChatLib.chat("§b/bb replaceword §a<original> §6<replacement> §7- Replace words");
			ChatLib.chat("§b/bb replaceword §cdelete §e<original> §7- Deletes replaced words");
			ChatLib.chat("§b/bb replaceword list §7- List of replaced words");
		} else if (args.length == 1 && args[0].equals("list")) {
			// Display a list of replaced words
			ChatLib.chat("§6Replaced words:");
			for (Map.Entry<String, String> entry : ReplacedWordsConfig.wordReplacements.entrySet()) {
				ChatLib.chat("§e[BadAddons] " + entry.getKey() + " §7-> §e" + entry.getValue());
			}
		} else if (args.length >= 2) {
			if (args[0].equals("delete") && args.length >= 2) {
				// Delete a word from the HashMap, join the arguments to create the key
				String key = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
				if (ReplacedWordsConfig.wordReplacements.containsKey(key)) {
					ReplacedWordsConfig.wordReplacements.remove(key);
					ChatLib.chat("§cDeleted entry with key: §e" + key);
				} else {
					ChatLib.chat("§cEntry with key §e\"" + key + "\" §cnot found.");
				}
			} else {
				// Join the arguments into a single string with spaces and handle quotes
				String originalWord = args[0];
				String newWord = args[1];

				if (args.length > 2) {
					for (int i = 2; i < args.length; i++) {
						newWord += " " + args[i];
					}
				}

				// Remove quotes if present
				originalWord = originalWord.replaceAll("^\"|\"$", "");
				newWord = newWord.replaceAll("^\"|\"$", "");

				// Replace a word and store it in a HashMap
				ReplacedWordsConfig.wordReplacements.put(originalWord, newWord.replace("&", "§"));
				ReplacedWordsConfig.save();
				ChatLib.chat("§aReplaced §a\"" + originalWord + "\" §awith §6\"" + newWord + "\"");
			}
		} else {
			ChatLib.chat("§b/bb replaceword §a<original> §6<replacement> §7- Replace words");
		}

		return true;

	}
	
}
