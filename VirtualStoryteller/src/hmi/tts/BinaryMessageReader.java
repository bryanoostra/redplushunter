package hmi.tts;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

/**
 * This reader can read binary data from an input stream and convert it to Java
 * data types.
 * 
 * @author Dennis Hofs
 */
public class BinaryMessageReader {
	private InputStream in;
	private Charset charset = Charset.forName("UTF-8");
	
	/**
	 * Constructs a new reader that will read from the specified input stream.
	 * 
	 * @param in the input stream
	 */
	public BinaryMessageReader(InputStream in) {
		this.in = in;
	}
	
	/**
	 * Reads a specified number of bytes.
	 * 
	 * @param n the number of desired bytes
	 * @return the bytes
	 * @throws IOException if an error occurs while reading
	 */
	public byte[] readBytes(int n) throws IOException {
		byte[] bs = new byte[n];
		int off = 0;
		int len = 0;
		while (off < n && (len = in.read(bs,off,n-off)) > 0) {
			off += len;
		}
		if (off < n)
			throw new IOException("Input stream closed");
		return bs;
	}
	
	/**
	 * Reads a byte array. It will first read an integer (see {@link #readInt()
	 * readInt()}) that specifies the number of bytes to read. Then it reads
	 * that number of bytes.
	 * 
	 * @return the bytes
	 * @throws IOException if an error occurs while reading
	 */
	public byte[] readByteArray() throws IOException {
		int n = readInt();
		return readBytes(n);
	}
	
	/**
	 * Reads one byte.
	 * 
	 * @return the byte
	 * @throws IOException if an error occurs while reading
	 */
	public byte readByte() throws IOException {
		return readBytes(1)[0];
	}
	
	/**
	 * Reads an integer. The integer should be specified as four bytes in
	 * big-endian order.
	 * 
	 * @return the integer
	 * @throws IOException if an error occurs while reading
	 */
	public int readInt() throws IOException {
		byte[] bs = readBytes(4);
		int result = 0;
		for (int i = 0; i < 4; i++) {
			result = result | ((bs[i]&0xFF) << ((3-i)*8));
		}
		return result;
	}
	
	/**
	 * Reads a long integer. The long integer should be specified as eight
	 * bytes in big-endian order.
	 * 
	 * @return the long integer
	 * @throws IOException if an error occurs while reading
	 */
	public long readLong() throws IOException {
		byte[] bs = readBytes(8);
		long result = 0;
		for (int i = 0; i < 8; i++) {
			result = result | ((long)(bs[i]&0xFF) << ((7-i)*8));
		}
		return result;
	}
	
	/**
	 * Reads a boolean. It reads one byte. If the byte is 0, this method
	 * returns false. Otherwise it returns true.
	 * 
	 * @return the boolean
	 * @throws IOException if an error occurs while reading
	 */
	public boolean readBoolean() throws IOException {
		byte b = readByte();
		if (b == 0)
			return false;
		else
			return true;
	}
	
	/**
	 * Reads a string. It will first read an integer that specifies the number
	 * of bytes in the string encoding. Then it reads that number of bytes and
	 * converts the bytes to characters using UTF-8 decoding.
	 * 
	 * @return the string
	 * @throws IOException if an error occurs while reading
	 */
	public String readString() throws IOException {
		int n = readInt();
		return readChars(n);
	}
	
	/**
	 * Reads a specified number of ASCII characters. The characters must be
	 * ASCII in order that every character corresponds to exactly one byte.
	 * This method actually reads the specified number of bytes and then
	 * converts them to ASCII characters.
	 * 
	 * @param n the number of characters to read
	 * @return the characters
	 * @throws IOException if an error occurs while reading
	 */
	public String readChars(int n) throws IOException {
		byte[] bs = readBytes(n);
		ByteBuffer bb = ByteBuffer.wrap(bs);
		return charset.decode(bb).toString();
	}
}
