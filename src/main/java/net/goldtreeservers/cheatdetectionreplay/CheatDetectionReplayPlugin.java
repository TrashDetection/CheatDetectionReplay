package net.goldtreeservers.cheatdetectionreplay;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.goldtreeservers.cheatdetectionreplay.commands.CheatDetectionReplayCommand;
import net.goldtreeservers.cheatdetectionreplay.listeners.PlayerListener;
import net.goldtreeservers.cheatdetectionreplay.trackers.EntityMovementTracker;
import net.goldtreeservers.cheatdetectionreplay.utils.ChunkedByteArray;

public class CheatDetectionReplayPlugin extends JavaPlugin
{
	private int nextTrackerId = 0;
	
	private final Map<Integer, EntityMovementTracker> entityTrackers;
	
	public CheatDetectionReplayPlugin()
	{
		this.entityTrackers = new HashMap<>();
	}
	
	@Override
	public void onEnable()
	{
		this.getServer().getPluginCommand("cdr").setExecutor(new CheatDetectionReplayCommand(this));
		
		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	}
	
	public int createTracker(Player player, ChunkedByteArray data)
	{
		int id = this.nextTrackerId++;
		
		this.entityTrackers.put(id, new EntityMovementTracker(player, data));
		
		return id;
	}
	
	public EntityMovementTracker getTracker(int id)
	{
		return this.entityTrackers.get(id);
	}
}
