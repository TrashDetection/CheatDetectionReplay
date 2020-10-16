package net.goldtreeservers.cheatdetectionreplay.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.RequiredArgsConstructor;
import net.goldtreeservers.cheatdetectionreplay.CheatDetectionReplayPlugin;
import net.goldtreeservers.cheatdetectionreplay.trackers.EntityMovementTracker;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public class PlayerListener implements Listener
{
	private final CheatDetectionReplayPlugin plugin;
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteractEvent(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		
		ItemStack item = event.getItem();
		if (item == null || item.getType() != Material.DIAMOND)
		{
			return;
		}
		
		ItemMeta meta = item.getItemMeta();
		if (!meta.getDisplayName().equals(ChatColor.AQUA + ChatColor.BOLD.toString() + "Track Movement"))
		{
			return;
		}
		
		List<String> lore = meta.getLore();
		if (lore.isEmpty())
		{
			return;
		}
		
		int id = Integer.parseInt(lore.get(0));
		
		EntityMovementTracker tracker = this.plugin.getTracker(id);
		if (tracker == null)
		{
			player.sendMessage("No tracker with this id");
			
			return;
		}
		
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			tracker.forward();
		}
		else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			tracker.forward();
		}
	}
}
