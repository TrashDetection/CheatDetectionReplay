package net.goldtreeservers.cheatdetectionreplay.commands;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import io.netty.buffer.Unpooled;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.cheatdetectionreplay.CheatDetectionReplayPlugin;
import net.goldtreeservers.cheatdetectionreplay.utils.ChunkedByteArray;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public class CheatDetectionReplayCommand implements CommandExecutor
{
	private final CheatDetectionReplayPlugin plugin;
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage("Only player");
			
			return true;
		}
		
		if (args.length <= 0)
		{
			sender.sendMessage("Bruh, give command");
			
			return true;
		}
		
		Player player = (Player)sender;
		
		switch(args[0])
		{
			case "trackmovement":
			{
				if (args.length != 2)
				{
					sender.sendMessage("Give file to load the data");
					
					return true;
				}
				
				File file = new File(args[1]);
				if (!file.exists())
				{
					sender.sendMessage("Bruh, no file exists");
					
					return true;
				}
				
				if (!file.isFile())
				{
					sender.sendMessage("Bruh, this aint a file");
					
					return true;
				}
				
				ChunkedByteArray data = new ChunkedByteArray();
				
				try(ZipFile zipFile = new ZipFile(file))
			    {
			    	ZipEntry outgoingEntry = zipFile.getEntry("outgoing.data");
			    	if (outgoingEntry == null)
			    	{
			    		sender.sendMessage("The file is missing outgoing.data");
			    		
			    		return true;
			    	}

			    	byte[] buffer = new byte[1024];
			    	try (InputStream input = zipFile.getInputStream(outgoingEntry))
			    	{
			    		while (true)
			    		{
				    		int amount = input.read(buffer);
				    		if (amount > 0)
				    		{
				    			byte[] bytes = new byte[amount];
				    			
				    			System.arraycopy(buffer, 0, bytes, 0, amount);
				    			
				    			data.write(Unpooled.wrappedBuffer(bytes));
				    		}
				    		else
				    		{
				    			break;
				    		}
			    		}
			    	}
			    }
				catch (IOException e)
				{
					e.printStackTrace();
					
					sender.sendMessage("Error!");
					
					return true;
				}
				
				int id = this.plugin.createTracker(player, data);
				
				ItemStack item = new ItemStack(Material.DIAMOND);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Track Movement");
				meta.setLore(Arrays.asList(Integer.toString(id)));
				item.setItemMeta(meta);
				
				PlayerInventory inventory = player.getInventory();
				inventory.addItem(item);
				
				sender.sendMessage("Here you go!");
			}
			break;
			default:
				sender.sendMessage("Boo");
				break;
		}
		
		return true;
	}
}
