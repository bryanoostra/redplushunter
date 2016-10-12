package hmi.tts;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

import vs.debug.LogFactory;

/**
 * This writer can convert Java data types to binary data and write it to an
 * output stream.
 * 
 * @author Dennis Hofs
 */
public class BinaryMessageWriter {
	private OutputStream out;
	private Charset charset = Charset.forName("UTF-8");
	
	/**
	 * Constructs a new writer that will write to the specified output stream.
	 * 
	 * @param out the output stream
	 */
	public BinaryMessageWriter(OutputStream out) {
		this.out = out;
	}
	
	/**
	 * Writes a number of bytes. The number is not written. Use this method
	 * only for fixed numbers of bytes, so when you read the bytes back, you
	 * know how many bytes to read.
	 * 
	 * @param bs the bytes
	 * @see #writeByteArray(byte[])
	 * @throws IOException if an error occurs while writing
	 */
	public void writeBytes(byte[] bs) throws IOException {
		out.write(bs);
	}
	
	/**
	 * Writes a byte array. It will first write the number of bytes in the
	 * array.
	 * 
	 * @param bs the byte array
	 * @throws IOException if an error occurs while writing
	 */
	public void writeByteArray(byte[] bs) throws IOException {
		writeInt(bs.length);
		writeBytes(bs);
	}
	
	/**
	 * Writes one byte.
	 * 
	 * @param b the byte
	 * @throws IOException if an error occurs while writing
	 */
	public void writeByte(byte b) throws IOException {
		byte[] bs = new byte[1];
		bs[0] = b;
		out.write(bs);
	}
	
	/**
	 * Writes an integer. The integer is encoded as four bytes in big-endian
	 * order.
	 * 
	 * @param n the integer
	 * @throws IOException if an error occurs while writing
	 */
	public void writeInt(int n) throws IOException {
		byte[] bs = new byte[4];
		for (int i = 0; i < 4; i++) {
			bs[3-i] = (byte)(n & 0xFF);
			n = n >> 8;
		}
		writeBytes(bs);
	}
	
	/**
	 * Writes a long integer. The long integer is encoded as eight bytes in
	 * big-endian order.
	 * 
	 * @param n the long integer
	 * @throws IOException if an error occurs while writing
	 */
	public void writeLong(long n) throws IOException {
		byte[] bs = new byte[8];
		for (int i = 0; i < 8; i++) {
			bs[7-i] = (byte)(n & 0xFF);
			n = n >> 8;
		}
		writeBytes(bs);
	}

	/**
	 * Writes a boolean. It writes a byte: 1 for true and 0 for false.
	 * 
	 * @param b the boolean
	 * @throws IOException if an error occurs while writing
	 */
	public void writeBoolean(boolean b) throws IOException {
		if (b)
			writeByte((byte)1);
		else
			writeByte((byte)0);
	}

	/**
	 * Writes a string. The characters are converted to bytes using UTF-8
	 * encoding. This method will first write the number of bytes in the
	 * encoded string.
	 * 
	 * @param s a string
	 * @throws IOException if an error occurs while writing
	 */
	public void writeString(String s) throws IOException {
		byte[] bs = getChars(s);
		writeInt(bs.length);
		writeBytes(bs);
		LogFactory.getLogger(this).info("method writeString in tts.BinaryMessageWriter wrote: \"" + s + "\"");
	}

	/**
	 * Writes a number of characters. The characters are converted to bytes
	 * using UTF-8 encoding. The number of bytes is not written. Use this
	 * method only for fixed numbers of characters and use only ASCII
	 * characters. Using ASCII characters ensures that each character is
	 * encoded with exactly one byte. When you want to read the characters
	 * back, you need to know how many bytes were written.
	 * 
	 * @param s the characters
	 * @throws IOException if an error occurs while writing
	 */
	public void writeChars(String s) throws IOException {
		byte[] bs = getChars(s);
		writeBytes(bs);
	}
	
	/**
	 * Converts a string to bytes using UTF-8 encoding.
	 * 
	 * @param s the string
	 * @return the bytes
	 */
	private byte[] getChars(String s) {
		ByteBuffer bb = charset.encode(s);
		byte[] bs = new byte[bb.remaining()];
		bb.get(bs);
		return bs;
	}
}
