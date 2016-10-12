package hmi.tts.loquendo;

import java.io.*;
import hmi.tts.*;

/**
 * A confirm message is sent in reply to another message. It indicates
 * success or failure of the original message. In case of failure, the
 * message includes an error message.
 * 
 * @author Dennis Hofs
 */
public class ConfirmMessage extends ServerMessage {
	private boolean result;
	private String error;
	
	/**
	 * Constructs a new confirm message.
	 * 
	 * @param result true for success, false for failure
	 * @param error an error message (in case of failure) or null (in case
	 * of success)
	 */
	public ConfirmMessage(byte type, boolean result, String error) {
		super(type);
		this.result = result;
		this.error = error;
	}
	
	/**
	 * Returns the result: true for success, false for failure.
	 */
	public boolean getResult() {
		return result;
	}
	
	/**
	 * Returns an error message in case of failure.
	 */
	public String getError() {
		return error;
	}

	@Override
	public void write(OutputStream out) throws Exception {
		super.write(out);
		BinaryMessageWriter writer = new BinaryMessageWriter(out);
		writer.writeBoolean(result);
		if (!result)
			writer.writeString(error);
	}
	
	/**
	 * Reads the contents of a confirm message. The message type should already
	 * have been read.
	 * 
	 * @param type the message type
	 * @param in the input stream
	 * @return the confirm message
	 */
	public static ConfirmMessage read(byte type, InputStream in)
	throws Exception {
		BinaryMessageReader reader = new BinaryMessageReader(in);
		boolean result = reader.readBoolean();
		String error = null;
		if (!result)
			error = reader.readString();
		return new ConfirmMessage(type, result, error);
	}
}
