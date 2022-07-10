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

import tech.inudev.profundus.utils.PFChunk;
import tech.inudev.profundus.utils.User;

public class CommandClass implements CommandExecutor,TabCompleter {

	public CommandClass() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String[] args) {
		// TODO Auto-generated method stub
		if(command.getName().equalsIgnoreCase("sell_chunk")) {
			if(!(sender instanceof Player)) {return false;}
			User user = null;
			int price;
			int z;
			int x;
			PFChunk c;
			
			switch(args.length) {
			case 4:
				user = User.getByPlayer(sender.getServer().getPlayer(args[3])); //online user only;
			case 3:
				price = Integer.parseInt(args[2]);
				z = Integer.parseInt(args[1]);
				x = Integer.parseInt(args[0]);
				c = PFChunk.getByCoord(((Player)sender).getWorld(), x, z);
				return c.setPrice(price).startSelling(user); //owner not checked
			default:
					return false;
				
			}
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
	    if(command.getName().equalsIgnoreCase("sell_chunk")){
	    	switch(args.length) {
	    	case 0:
	    	case 1:
	    		int x = ((Player)sender).getChunk().getX();
	    		int z = ((Player)sender).getChunk().getZ();

	    		return Collections.singletonList(x + " " + z);
	    	case 3:
	        	List<String> ret = new ArrayList<>();
	        	sender.getServer().getOnlinePlayers().forEach(i -> ret.add(i.getName()));
	        	return ret;
	        }
	    }
	    return null;
	}

}
