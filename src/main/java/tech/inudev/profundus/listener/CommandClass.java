package tech.inudev.profundus.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import tech.inudev.profundus.utils.database.PFChunk;
import tech.inudev.profundus.utils.database.User;

public class CommandClass implements CommandExecutor,TabCompleter {

	public CommandClass() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String[] args) {
		// TODO Auto-generated method stub
		switch(command.getName().toLowerCase()) {
		case "sellchunk":
			if(!(sender instanceof Player)) {return false;}
			User user = null;
			int price = -1;
			int z;
			int x;
			PFChunk c;
			
			switch(args.length) {
			case 5:
				user = User.getByPlayer(sender.getServer().getPlayer(args[3])); //online user only;
			case 4:
				price = Integer.parseInt(args[2]);
			case 3:
				z = Integer.parseInt(args[1]);
				x = Integer.parseInt(args[0]);
				c = PFChunk.getByCoord(((Player)sender).getWorld(), x, z);
				if(args[2].equalsIgnoreCase("stop")) {c.createTransaction(User.getByPlayer(((Player)sender))).removeSale();return true;}
				if(args[2].equalsIgnoreCase("start")) {
					if(c.createTransaction(User.getByPlayer(((Player)sender))).setPrice(price).sellTo(user)) {
						sender.sendMessage(command.getUsage() + " Price must be at 0 or above");
				}} //owner not checked
			default:

			}
			return false;
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
	    if(command.getName().equalsIgnoreCase("sellChunk")){
	    	if(!(sender instanceof Player)) {return null;}
	    	Player p = (Player) sender;
        	List<String> ret = new ArrayList<>();

	    	switch(args.length) {
	    	case 0:
	    		int x = p.getChunk().getX();
	    		int z = p.getChunk().getZ();

	    		return Collections.singletonList(x + " " + z);
	    	case 2:
	    		ret.add("start");
	    		ret.add("stop");
	    		return ret;
	    	case 4:
	    		ret.clear();
	        	sender.getServer().getOnlinePlayers().forEach(i -> ret.add(i.getName()));
	        	return ret;
	        default:
	        	return null;
	        }
	    }
	    return null;
	}

}
