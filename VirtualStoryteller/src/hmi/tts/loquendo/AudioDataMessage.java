package hmi.tts.loquendo;

import java.io.*;
import hmi.tts.*;

/**
 * An audio data message contains a block of audio data. If the block is an
 * empty array, it indicates the end of the audio stream. Before the first
 * audio data message, an {@link AudioFormatMessage AudioFormatMessage} should
 * be sent.
 * 
 * @author Dennis Hofs
 */
public class AudioDataMessage extends ServerMessage {
	private byte[] data;
	
	/**
	 * Constructs a new audio data message. To indicate the end of the audio
	 * stream, set <code>len</code> to 0. The other two parameters are
	 * ignored in that case. This constructor will copy data from the
	 * <code>data</code> parameter to a new array.
	 * 
	 * @param data the data
	 * @param off the start index
	 * @param len the number of bytes (0 for end of stream)
	 */
	public AudioDataMessage(byte[] data, int off, int len) {
		super(ServerMessage.AUDIO_DATA);
		this.data = new byte[len];
		for (int i = 0; i < len; i++) {
			this.data[i] = data[i + off];
		}
	}
	
	/**
	 * Returns the audio data. If this message indicates the end of the audio
	 * stream, then this method returns an empty array.
	 * 
	 * @return the audio data or an empty array
	 */
	public byte[] getAudioData() {
		return data;
	}

	@Override
	public void write(OutputStream out) throws Exception {
		super.write(out);
		BinaryMessageWriter writer = new BinaryMessageWriter(out);
		writer.writeByteArray(data);
	}
	
	/**
	 * Reads the contents of an audio data message. The message type should
	 * already have been read.
	 * 
	 * @param type the message type
	 * @param in the input stream
	 * @return the audio data message
	 */
	public static AudioDataMessage read(byte type, InputStream in)
	throws Exception {
		BinaryMessageReader reader = new BinaryMessageReader(in);
		byte[] data = reader.readByteArray();
		return new AudioDataMessage(data, 0, data.length);
	}
}
