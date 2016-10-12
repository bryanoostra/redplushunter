package hmi.tts.loquendo;

import javax.sound.sampled.*;

public interface LoquendoClientListener {
	public void newAudioData(LoquendoClient client, AudioFormat format,
			byte[] data);
	public void clientClosed(LoquendoClient client);
	public void clientError(LoquendoClient client, String error);
}
