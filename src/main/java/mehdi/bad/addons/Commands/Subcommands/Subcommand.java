package mehdi.bad.addons.Commands.Subcommands;

import net.minecraft.command.ICommandSender;

import java.io.IOException;

public interface Subcommand {
    String getCommandName();
    boolean isHidden();
    String getCommandUsage();
    String getCommandDescription();
    boolean processCommand(ICommandSender sender, String[] args) throws IOException;
}