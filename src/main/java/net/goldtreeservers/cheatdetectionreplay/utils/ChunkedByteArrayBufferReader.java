package net.goldtreeservers.cheatdetectionreplay.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public abstract class ChunkedByteArrayBufferReader
{
	private int lastPosition;
	
	public ChunkedByteArrayBufferReader()
	{
	}
	
	public void read(ChunkedByteArray array)
	{
		int size = Math.toIntExact(array.size64() - this.lastPosition);
		
		ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(size, size);
		
		try
		{
			ByteBufUtils.chunkedByteArrayToByteBuf(buf, array, this.lastPosition);
			
			int start = buf.readerIndex();
			
			this.read0(buf);
			
			this.lastPosition += (buf.readerIndex() - start);
		}
		finally
		{
			buf.release();
		}
	}
	
	protected abstract void read0(ByteBuf buffer);
}
