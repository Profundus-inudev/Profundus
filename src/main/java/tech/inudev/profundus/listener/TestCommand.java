package tech.inudev.profundus.listener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.inudev.profundus.utils.HelpUtil;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args) {
        if (!command.getName().equalsIgnoreCase("test")) {
            return false;
        }

        if (args.length == 1) {
            HelpUtil.HelpType help;
            if (args[0].equals("test")) {
                help = HelpUtil.HelpType.Test;
            } else if (args[0].equals("sample")) {
                help = HelpUtil.HelpType.Sample;
            } else if (args[0].equals("error")) {
                help = HelpUtil.HelpType.ErrorText;
            } else if (args[0].equals("error2")) {
                help = HelpUtil.HelpType.ErrorText2;
            } else {
                help = HelpUtil.HelpType.Test;
            }
            HelpUtil.openHelp(((Player) sender).getUniqueId(), help);
        }
        return false;
    }
}