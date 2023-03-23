package jp.mochisystems.core.util;

import jp.mochisystems.core._mc._core.Logger;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class byteZip {

	private final ByteArrayOutputStream compressedData = new ByteArrayOutputStream();
	private final ByteArrayOutputStream orgData = new ByteArrayOutputStream();
	int writedLength;

	private Deflater compresser = new Deflater();

	public byteZip()
	{
	}
	
	public void clear()
	{
		orgData.reset();
		compressedData.reset();
		writedLength = 0;
	}
	
	public void setByte(byte data)
	{
		orgData.write(data);
	}
	
	public void setByteArray(byte[] ba)
	{
		orgData.write(ba,0,ba.length);
	}
	
	public void setShort(short value)
	{
        int arraySize = Short.SIZE / Byte.SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(arraySize);
        setByteArray(buffer.putShort(value).array() );
    }
	
	public void setInt(int value)
	{
		int arraySize = Integer.SIZE / Byte.SIZE;
		ByteBuffer buffer = ByteBuffer.allocate(arraySize);
		setByteArray( buffer.putInt(value).array() );
	}
	
	public void setFloat(float value)
	{
		int arraySize = Float.SIZE / Byte.SIZE;
		ByteBuffer buffer = ByteBuffer.allocate(arraySize);
		setByteArray( buffer.putFloat(value).array() );
	}
	
	public void compress()
	{
		compresser = new Deflater();
		byte[] outBuf = new byte[1024];
		byte[] src = orgData.toByteArray();
		compresser.setInput(src);
//		int i = 0;
//		while (src.length <= i) {
//			compresser.setInput(src, i, 1024);
//			i += 1024;
//		}
		compresser.finish();

		while (!compresser.finished()) {
			int n = compresser.deflate(outBuf);
			writedLength = compresser.getTotalIn();
			compressedData.write(outBuf, 0, n);
		}

		compresser.end();
	}
	
	public static int decompress(byte[] out, byte[] in) throws DataFormatException
	{
		int outnum = -1;
		final Inflater decompresser = new Inflater();
		try
		{
			decompresser.setInput(in);
			outnum = decompresser.inflate(out);
		}
		catch(DataFormatException e)
		{
			Logger.warn("failed to decompress data. : "+e.toString());
		}
		finally
		{
            decompresser.end();
        }
		return outnum;
	}

	public float currentProgress()
	{
		return (orgData.size()==0) ? 0 : writedLength / (float) orgData.size();
	}
	public byte[] getOutput()
	{
		return compressedData.toByteArray();
	}
	public int getOrgSize()
	{
		return orgData.size();
	}
}
