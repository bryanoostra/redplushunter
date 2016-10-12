package hmi.tts.loquendo;

import java.io.*;
import hmi.tts.*;

/**
 * An audio format message is sent before the first {@link AudioDataMessage
 * AudioDataMessage}. It contains a sample rate, sample size in bits and number
 * of channels. It is assumed that the audio format is signed PCM, little
 * endian.
 * 
 * @author Dennis Hofs
 */
public class AudioFormatMessage extends ServerMessage {
	private int sampleRate;
	private int sampleSize;
	private int channels;
	
	/**
	 * Constructs a new audio format message.
	 * 
	 * @param sampleRate the sample rate
	 * @param sampleSize the sample size in bits
	 * @param channels the number of channels
	 */
	public AudioFormatMessage(int sampleRate, int sampleSize, int channels) {
		super(ServerMessage.AUDIO_FORMAT);
		this.sampleRate = sampleRate;
		this.sampleSize = sampleSize;
		this.channels = channels;
	}
	
	/**
	 * Returns the sample rate.
	 */
	public int getSampleRate() {
		return sampleRate;
	}
	
	/**
	 * Returns the sample size in bits.
	 */
	public int getSampleSize() {
		return sampleSize;
	}
	
	/**
	 * Returns the number of channels.
	 */
	public int getChannels() {
		return channels;
	}
	
	@Override
	public void write(OutputStream out) throws Exception {
		super.write(out);
		BinaryMessageWriter writer = new BinaryMessageWriter(out);
		writer.writeInt(sampleRate);
		writer.writeByte((byte)(sampleSize & 0xFF));
		writer.writeByte((byte)(channels & 0xFF));
	}
	
	/**
	 * Reads the contents of an audio format message. The message type should
	 * already have been read.
	 * 
	 * @param type the message type
	 * @param in the input stream
	 * @return the audio format message
	 */
	public static AudioFormatMessage read(byte type, InputStream in)
	throws Exception {
		BinaryMessageReader reader = new BinaryMessageReader(in);
		int sampleRate = reader.readInt();
		int sampleSize = reader.readByte() & 0xFF;
		int channels = reader.readByte() & 0xFF;
		return new AudioFormatMessage(sampleRate, sampleSize, channels);
	}
}
