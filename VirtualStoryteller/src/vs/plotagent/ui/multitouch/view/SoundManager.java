package vs.plotagent.ui.multitouch.view;

import hmi.tts.loquendo.LoquendoClient;
import hmi.tts.loquendo.LoquendoClientListener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import vs.communication.Finished;
import vs.debug.LogFactory;
import vs.plotagent.ui.multitouch.MultitouchInterfaceSettings;
import vs.plotagent.ui.multitouch.model.NarratedOperatorResult;

/**
 * Handles everything sound-related: loading sounds, streaming sound, TTS, etc.
 * 
 * @author swartjes
 *
 */
public class SoundManager implements Observer, LineListener, LoquendoClientListener {

	public static int BUFSIZE = 100; 
	
	private byte[] ttsBuffer;
		
	private Logger logger;
	private static SoundManager soundManager;
	
	private Map<String, AudioInputStream> sounds;
	
	private Map<String, byte[]> sounds2;
	
	private Vector<Clip> audioClipQueue = new Vector<Clip>();
	
	private LoquendoClient loqClient;
	//private String host="ewi1224.ewi.utwente.nl";
	//private int port=22093;
	
	public static String soundPath = "sound" + File.separator;
	
	public static SoundManager getInstance() {
		if (soundManager == null) {
			soundManager = new SoundManager();
		}
		
		return soundManager;
	}
	
	private SoundManager() {
		logger = LogFactory.getLogger(this);
		
		sounds = new HashMap<String, AudioInputStream>();
		sounds2 = new HashMap<String, byte[]>();
		
		ttsBuffer = new byte[0];
		
		if (MultitouchInterfaceSettings.USE_TEXT_TO_SPEECH) {
			this.connect();			
		}
	}
	
	public void connect() {
		// Setup Loquendo client.
		loqClient = new LoquendoClient();
		loqClient.addLoquendoClientListener(this);
		
		try {
			logger.fine("Start to open LoquendoClient and to connect to server...");
			System.out.println("\nStart to open LoquendoClient and to connect to server...\n");
			loqClient.open();
			loqClient.setVoice("Saskia"); // Mary is English?
		} catch (Exception e) {
			logger.severe("EXCEPTION in SoundManager.java while setting up a Loquendo client. " + e.toString());
			logger.severe("Probably the Loquendo server needs to be restarted, contact Thijs Alofs or Hendri Hondorp.\n");
			System.out.println("EXCEPTION in SoundManager.java while setting up a Loquendo client. " + e.toString());
			System.out.println("Probably the Loquendo server needs to be restarted, contact Thijs Alofs or Hendri Hondorp.\n");

			loqClient.close();
			e.printStackTrace();
		}

	}
	
	/**
	 * Registers a sound to a given action
	 * @param action
	 * @param sound
	 */
	public void registerActionSound(String action, File sound) {
			
		/* Create our clip object */
		try {
			 
			/* Open our URL as an AudioStream */
			AudioInputStream ais = AudioSystem.getAudioInputStream(sound);
			
			sounds.put(action, ais);
			
			sounds2.put(action, readAudio(ais));
			
			logger.info("Registering sound for action " + action + ": \n" + sound);
			
		} catch (UnsupportedAudioFileException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Plays the sound of a given action
	 * 
	 * @param action the action that has a sound registered to it
	 */
/*	public void playActionSoundOld(String action) {
				
		AudioInputStream ais = sounds.get(action);
		
		if (ais != null) {
			
			try {
				Clip clip = AudioSystem.getClip();
				
				 Put out audio input stream into our clip 
				clip.open(ais);
								 
				 Play the clip 
				clip.start();
				
			} catch (LineUnavailableException e) {
				logger.severe(e.getMessage());
				e.printStackTrace();
			}  catch (IOException e) {
				logger.severe(e.getMessage());
				e.printStackTrace();
			}
		}
	}*/
	
	/**
	 * Play the sound that was registered to a given action
	 * @param action the action that has a sound registered to it
	 */
	public void playActionSound(String action) {
		AudioInputStream ais = sounds.get(action);
		byte[] sound = sounds2.get(action);
		
		if (sound != null) {
			logger.fine("Playing audio...");
			playAudio(sound, ais.getFormat());
		}
	}
	
	/**
	 * Use a TTS engine to speak a given line of text (non-blocking)
	 * 
	 * @param line the line of text to be spoken.
	 */
	public void speakLine(String line) {
		if (! MultitouchInterfaceSettings.USE_TEXT_TO_SPEECH) return;
		
		try {
			loqClient.sendSentence(line);
			logger.fine("debug SoundManager | sendSentence: " + line); 
		} catch (Exception e) {
			logger.severe("SoundManager.java | Exception while sending sentence to loquendo client: " + line + e);
			System.out.println("SoundManager.java | Exception while sending sentence to loquendo client: \'" + line +"\' " + e);
			loqClient.close();
			e.printStackTrace();
		}
	}
	
	private byte[] readAudio(AudioInputStream ais) {
		int bufSize = (int)ais.getFrameLength() * ais.getFormat().getFrameSize();
		byte[] data = new byte[bufSize];
		
		try {
			
			byte[] buf = new byte[bufSize];
			for (int i=0; i<data.length; i += bufSize) {
			    int r = ais.read(buf, 0, bufSize);
			    if (i+r >= data.length) {
			        r = data.length - i;
			    }
			    System.arraycopy(buf, 0, data, i, r);
			}
			ais.close();
		} catch (IOException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		
		return data;
	}
	
	private void playAudio(byte[] audio, AudioFormat format) {
		logger.fine("Playing audio with format:\n" + format + "\nBytes: " + audio.length);
		//System.out.println("Playing audio with format:\n" + format + "\nBytes: " + audio.length);
		try {
			Clip clip = AudioSystem.getClip();			
			clip.open(format, audio, 0, audio.length);
			
			//ignore clips with length 0
			if(clip.getMicrosecondLength()>1) {
			
				//start listening to lineEvents 
				clip.addLineListener(this);
				
				if(audioClipQueue.isEmpty()) {
					audioClipQueue.add(clip);
					clip.start();
				} else {
					audioClipQueue.add(clip);
				}
			}
		} catch (LineUnavailableException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	public void update(LineEvent l) {
		//wait for lineEvent of type STOP before the next clip is started
		if(l.getType()==LineEvent.Type.STOP) {
			//remove source from queue
			Object source = l.getSource();
			audioClipQueue.removeElement(source);
			
			//start new clip if queue is not empty
			if(!audioClipQueue.isEmpty()) {
				Clip clip = audioClipQueue.firstElement();
				clip.start();
			}
		}
	}
	
	public void update(Observable obs, Object o) {
		
		NarratedOperatorResult nor = (NarratedOperatorResult) o;	
	
		// Sound
		if (nor.getOperatorResult().getStatus() instanceof Finished) {
			logger.fine("Playing action sound for action type " + nor.getOperatorResult().getOperator().getType());
			playActionSound(nor.getOperatorResult().getOperator().getType());
			speakLine(nor.getNarration());
		}
	}
	
	public void clientClosed(LoquendoClient c) {
	}
	
	public void clientError(LoquendoClient c, String error) {
		logger.severe(error);
	}
	
	public void newAudioData(LoquendoClient c, AudioFormat format, byte[] data) {
			
		if (data.length == 0) {
			// End of data (server always sends a package of size 0 last, in order to
			// indicate this)
			playAudio(ttsBuffer, format);
			ttsBuffer = new byte[0];
		} else {
			
			// Append the audio data
			byte[] temp = new byte[ttsBuffer.length + data.length];
						
			System.arraycopy(ttsBuffer, 0, temp, 0, ttsBuffer.length);
			System.arraycopy(data, 0, temp, ttsBuffer.length, data.length);
						
			ttsBuffer = temp;
		}
	}
	
	public static void main(String args[]) {
		System.out.println("Speaking test line.");
		SoundManager.getInstance().speakLine("Ik ben Willem en als het goed is spreek ik Nederlands.");
		//SoundManager.getInstance().speakLine("Everything is working as it should, hurray!");
	}	
}
