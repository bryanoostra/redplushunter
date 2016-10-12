package hmi.tts.loquendo;

import java.io.*;
import hmi.tts.*;

/**
 * A server message is a message sent to or received from a Loquendo server.
 * Each message has a type. The possible types are defined as constants in this
 * class. This class has several subclasses. The documentation of the message
 * type constants specifies which message class is used for which type.
 * 
 * @author Dennis Hofs
 */
public class ServerMessage {
	/**
	 * A set voice message contains the name of the voice that Loquendo should
	 * use. It's an instance of {@link SetVoiceMessage SetVoiceMessage}.
	 */
	public static final byte SET_VOICE = 0x01;
	
	/**
	 * The reply to {@link #SET_VOICE SET_VOICE}. It's an instance of {@link
	 * ConfirmMessage ConfirmMessage}.
	 */
	public static final byte SET_VOICE_CNF = 0x02;
	
	/**
	 * A sentence message contains a sentence to be converted to speech. It's
	 * an instance of {@link SentenceMessage SentenceMessage}.
	 */
	public static final byte SENTENCE = 0x03;
	
	/**
	 * A stream error message is sent when an error occurs after a {@link
	 * SENTENCE SENTENCE} message. It's an instance of {@link ConfirmMessage
	 * ConfirmMessage}. The result in the confirm message is always false for
	 * failure.
	 */
	public static final byte STREAM_ERROR = 0x04;
	
	/**
	 * An audio format message contains an audio format. It's an instance of
	 * {@link AudioFormatMessage AudioFormatMessage}.
	 */
	public static final byte AUDIO_FORMAT = 0x05;
	
	/**
	 * An audio data message contains audio data for speech. It's an instance
	 * of {@link AudioDataMessage AudioDataMessage}.
	 */
	public static final byte AUDIO_DATA = 0x06;
	
	/**
	 * An interrupt message indicates that the audio stream should be
	 * interrupted. It's an instance of <code>ServerMessage</code>.
	 */
	public static final byte INTERRUPT = 0x07;
	
	/**
	 * The reply to {@link #INTERRUPT INTERRUPT}. It's an instance of {@link
	 * ConfirmMessage ConfirmMessage}.
	 */
	public static final byte INTERRUPT_CNF = 0x08;
	
	private byte type;
	
	/**
	 * Constructs a new server message.
	 * 
	 * @param type the message type (one of the constants defined in this
	 * class)
	 */
	public ServerMessage(byte type) {
		this.type = type;
	}
	
	/**
	 * Returns the message type (one of the constants defined in this class).
	 * Depending on the message type, you can cast this object to a more
	 * specific message class to get the message contents.
	 */
	public byte getType() {
		return type;
	}
	
	/**
	 * Writes this message to an output stream. This method only writes the
	 * message type. Subclasses override this method.
	 */
	public void write(OutputStream out) throws Exception {
		BinaryMessageWriter writer = new BinaryMessageWriter(out);
		writer.writeByte(type);
	}
	
	/**
	 * Reads a message from the specified input stream. This method first
	 * reads the message type. Depending on the type, it may use a subclass
	 * to read more info. This method returns an instance of this class or
	 * one of its subclasses.
	 * 
	 * @param in the input stream
	 * @return the message
	 */
	public static ServerMessage read(InputStream in) throws Exception {
		BinaryMessageReader reader = new BinaryMessageReader(in);
		byte type = reader.readByte();
		switch (type) {
		case SET_VOICE:
			return SetVoiceMessage.read(type, in);
		case SENTENCE:
			return SentenceMessage.read(type, in);
		case AUDIO_FORMAT:
			return AudioFormatMessage.read(type, in);
		case AUDIO_DATA:
			return AudioDataMessage.read(type, in);
		case SET_VOICE_CNF:
		case STREAM_ERROR:
		case INTERRUPT_CNF:
			return ConfirmMessage.read(type, in);
		case INTERRUPT:
			return new ServerMessage(type);
		}
		String hex = Integer.toHexString(type & 0xFF).toUpperCase();
		if (hex.length() == 1)
			hex += "0";
		throw new Exception("Unknown message type: 0x" + hex);
	}
}
