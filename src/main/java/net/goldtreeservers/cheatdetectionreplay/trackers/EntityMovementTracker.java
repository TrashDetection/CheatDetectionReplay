package net.goldtreeservers.cheatdetectionreplay.trackers;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.goldtreeservers.cheatdetectionreplay.utils.ByteBufUtils;
import net.goldtreeservers.cheatdetectionreplay.utils.ChunkedByteArray;
import net.goldtreeservers.cheatdetectionreplay.utils.ChunkedByteArrayBufferReader;

public class EntityMovementTracker
{
	private final Player player;
	
	private final ChunkedByteArray data;
	private final ChunkedByteArrayBufferReader reader;
	
	public EntityMovementTracker(Player player, ChunkedByteArray data)
	{
		this.player = player;

		this.data = data;
		this.reader = new ChunkedByteArrayBufferReader()
		{
			@Override
			protected void read0(ByteBuf buffer)
			{
				if (buffer.isReadable())
				{
					int length = ByteBufUtils.readVarInt(buffer);
					
					ByteBuf buf = buffer.readSlice(length);
					
					//EntityPlayer nmsPlayer = ((CraftPlayer)EntityMovementTracker.this.player).getHandle();
					//nmsPlayer.playerConnection.networkManager.channel.writeAndFlush(buf.retain());
					
					int id = ByteBufUtils.readVarInt(buf);
					
					//Data after
					buf.markReaderIndex();
					
					byte[] bytes = new byte[buf.readableBytes()];
					
					buf.readBytes(bytes);

					if (id == 0x0C) //Spawn player
					{
						buf.resetReaderIndex();
						
						ByteBufUtils.readVarInt(buf); //entity id
						
						UUID uniqueId = new UUID(buf.readLong(), buf.readLong()); //entity uuid

						WrappedGameProfile gameProfile = new WrappedGameProfile(uniqueId, "test");
						
						PlayerInfoData playerInfo = new PlayerInfoData(gameProfile, 0, NativeGameMode.NOT_SET, WrappedChatComponent.fromText("test"));
						
						PacketContainer playerListPacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
						playerListPacket.getPlayerInfoAction().write(0, PlayerInfoAction.ADD_PLAYER);
						playerListPacket.getPlayerInfoDataLists().write(0, Lists.newArrayList(playerInfo));
						
						//EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
						//nmsPlayer.playerConnection.networkManager.channel.writeAndFlush(playerListPacket);
						
						try
						{
							ProtocolLibrary.getProtocolManager().sendServerPacket(player, playerListPacket);
						}
						catch (InvocationTargetException e)
						{
							e.printStackTrace();
						}
					}
					
					try
					{
						ProtocolLibrary.getProtocolManager().sendWirePacket(EntityMovementTracker.this.player, id, bytes);
					}
					catch (InvocationTargetException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					EntityMovementTracker.this.player.sendMessage("End!");
				}
			}
		};
	}

	public void forward()
	{
		this.reader.read(this.data);
	}
}
