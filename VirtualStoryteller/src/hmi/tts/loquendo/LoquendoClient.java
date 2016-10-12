package hmi.tts.loquendo;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.sound.sampled.*;

import vs.debug.LogFactory;

public class LoquendoClient {
	private String host;
	private int port;
	private Socket client = null;
	private InputStream in;
	private OutputStream out;
	private boolean closing = false;
	private boolean closed = false;
	private Object lock = new Object();
	private List<LoquendoClientListener> listeners =
		new ArrayList<LoquendoClientListener>();
	private AudioFormat format;
	
	public LoquendoClient() {
		this.host = "loquendo.ewi.utwente.nl";
		this.port = 22093;
	}

	public LoquendoClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void addLoquendoClientListener(LoquendoClientListener l) {
		listeners.add(l);
	}
	
	public void removeLoquendoClientListener(LoquendoClientListener l) {
		listeners.remove(l);
	}

	public void open() throws Exception {
		if (closing)
			throw new Exception("Client closed");
		try {
			client = new Socket(host, port);
			in = client.getInputStream();
			out = client.getOutputStream();
			new ReadThread().start();
		} catch (Exception ex) {
			close();
			throw ex;
		}
	}
	
	public void close() {
		synchronized (lock) {
			if (closing) {
				try {
					while (!closed) {
						lock.wait();
					}
				} catch (Exception ex) {}
				return;
			}
			closing = true;
			lock.notifyAll();
		}
		try {
			if (client != null)
				client.close();
		} catch (Exception ex) {}
		notifyClientClosed();
		synchronized (lock) {
			closed = true;
			lock.notifyAll();
		}
	}
	
	private ConfirmMessage setVoiceCnf;
	
	public void setVoice(String voice) throws Exception {
		if (client == null)
			throw new Exception("Client not opened");
		setVoiceCnf = null;
		try {
			send(new SetVoiceMessage(voice));
		} catch (Exception ex) {
			close();
			throw ex;
		}
		synchronized (lock) {
			while (!closing && setVoiceCnf == null) {
				lock.wait();
			}
			if (closing)
				throw new Exception("Client closed");
		}
		if (!setVoiceCnf.getResult())
			throw new Exception(setVoiceCnf.getError());
	}
	
	public void sendSentence(String sentence) throws Exception {
		if (client == null)
			throw new Exception("Client not opened");
		try {
			send(new SentenceMessage(sentence));
		} catch (Exception ex) {
			close();
			throw ex;
		}
	}

	private ConfirmMessage interruptCnf;
	
	public void interrupt() throws Exception {
		if (client == null)
			throw new Exception("Client not opened");
		interruptCnf = null;
		try {
			send(new ServerMessage(ServerMessage.INTERRUPT));
		} catch (Exception ex) {
			close();
			throw ex;
		}
		synchronized (lock) {
			while (!closing && interruptCnf == null) {
				lock.wait();
			}
			if (closing)
				throw new Exception("Client closed");
		}
		if (!interruptCnf.getResult())
			throw new Exception(interruptCnf.getError());
	}
	
	private void send(ServerMessage msg) throws Exception {
		msg.write(out);
	}
	
	private void notifyClientError(String error) {
		for (LoquendoClientListener l : listeners) {
			l.clientError(this, error);
		}
	}
	
	private void notifyClientClosed() {
		final LoquendoClient instance = this;
		for (final LoquendoClientListener l : listeners) {
			new Thread() {
				@Override
				public void run() {
					l.clientClosed(instance);
				}
			}.start();
		}
	}
	
	private void notifyNewAudioData(AudioFormat format, byte[] data) {
		for (LoquendoClientListener l : listeners) {
			l.newAudioData(this, format, data);
		}
	}
	
	private void processAudioFormat(AudioFormatMessage msg) {
		format = new AudioFormat(msg.getSampleRate(), msg.getSampleSize(),
				msg.getChannels(), true, false);
	}
	
	private void processAudioData(AudioDataMessage msg) {
		notifyNewAudioData(format, msg.getAudioData());
	}
	
	private void processMessage(ServerMessage msg) {
		
		if(msg instanceof ConfirmMessage) {
			ConfirmMessage cm = (ConfirmMessage)msg;
			String err = cm.getError();
			//boolean result = cm.getResult();
			
			//problem or no problem?
			if(err==null) {
				LogFactory.getLogger(this).info("LoquendoClient appears succesfully connected to server!");
				System.out.println("LoquendoClient appears succesfully connected to server!\n");
			} else {
				//this error happens when the Loquendo server needs to be restarted
				LogFactory.getLogger(this).warning("LoquendoClient recieved an error message from server: " + err);
				LogFactory.getLogger(this).warning("Probably the Loquendo server ("+ host +") needs to be restarted, contact Thijs Alofs or Hendri Hondorp.\n");
				//it's an external error and so important we also need a message on the command line
				System.out.println("LoquendoClient recieved an error message from Loquendo TTS server: " + err);
				System.out.println("Probably the Loquendo server ("+ host +") needs to be restarted, contact Thijs Alofs or Hendri Hondorp.\n");
			}
		}
		
		byte type = msg.getType();
		switch (type) {
		case ServerMessage.AUDIO_FORMAT:
			processAudioFormat((AudioFormatMessage)msg);
			break;
		case ServerMessage.AUDIO_DATA:
			processAudioData((AudioDataMessage)msg);
			break;
		case ServerMessage.SET_VOICE_CNF:
			synchronized (lock) {
				setVoiceCnf = (ConfirmMessage)msg;
				lock.notifyAll();
			}
			break;
		case ServerMessage.STREAM_ERROR:
			notifyClientError(((ConfirmMessage)msg).getError());
			break;
		case ServerMessage.INTERRUPT_CNF:
			synchronized (lock) {
				interruptCnf = (ConfirmMessage)msg;
				lock.notifyAll();
			}
			break;
		}
	}
	
	private class ReadThread extends Thread {
		@Override
		public void run() {
			try {
				while (!closing) {
					ServerMessage msg = ServerMessage.read(in);
					processMessage(msg);
				}
			} catch (Exception ex) {}
			close();
		}
	}
}
