package hmi.tts.loquendo;

import java.util.*;
import java.io.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;



public class LoquendoServerTest {
	private String host="ewi1224.ewi.utwente.nl";
	private int port=22093;
 	private static final int	EXTERNAL_BUFFER_SIZE = 128000;

	
	public LoquendoServerTest(String host, int port) {
		this.host = host;
		this.port = port;
		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 1; i <= 1; i++) {
			Thread t = new SingleClientTest(i);
			t.start();
			threads.add(t);
		}
		try {
			for (Thread t : threads) {
				t.join();
			}
			System.out.println("Test finished");
			System.exit(0);
		} catch (Exception ex) {
			System.err.println("Test interrupted");
			System.exit(1);
		}
	}
	
	private boolean runSingleTest(int index, LoquendoClient client,
			ClientListener l) {
		try {
			client.sendSentence("Test " + index + ".");
		} catch (Exception ex) {
			System.err.println("Test " + index + " failed");
			ex.printStackTrace();
			client.close();
			return false;
		}
		boolean ok = true;
		boolean finished = false;
		boolean recvSpeech = false;
		while (!finished) {
			try {
				ClientEvent ev = l.receiveEvent(5000);
				switch (ev.type) {
				case NEW_AUDIO_DATA:
					recvSpeech = true;
					break;
				case CLOSED:
					System.err.println("Client closed");
					ok = false;
					finished = true;
					break;
				case ERROR:
					System.err.println("Client error: " + ev.content);
					ok = false;
					finished = true;
					break;
				}
			} catch (InterruptedException ex) {
				finished = true;
			}
		}
		if (!ok || !recvSpeech) {
			if (!recvSpeech)
				System.err.println("No speech received");
			System.err.println("Test " + index + " failed");
			return false;
		} else {
			System.out.println("Test " + index + " synthesised sentence");
			return true;
		}
	}
	
	private boolean runSingleClientTest(int index) {
		LoquendoClient client = new LoquendoClient(host, port);
		ClientListener l = new ClientListener();
		client.addLoquendoClientListener(l);
		try {
			client.open();
			client.setVoice("Mary");
		} catch (Exception ex) {
			System.err.println("Test " + index + " failed");
			ex.printStackTrace();
			client.close();
			return false;
		}
		boolean ok = true;
		Random rnd = new Random();
		for (int i = 0; ok && i < 100; i++) {
			try {
				Thread.sleep(rnd.nextInt(1000));
			} catch (Exception ex) {}
			ok = runSingleTest(index, client, l);
		}
		client.close();
		return ok;
	}
	
	private enum ClientEventType {
		NEW_AUDIO_DATA,
		CLOSED,
		ERROR
	}
	
	private class ClientEvent {
		public ClientEventType type;
		public Object content;
		
		public ClientEvent(ClientEventType type, Object content) {
			this.type = type;
			this.content = content;
		}
	}
	
	private class ClientListener implements LoquendoClientListener {
		
		private List<ClientEvent> eventQueue = new ArrayList<ClientEvent>();
		

		@Override
		public void newAudioData(LoquendoClient client, AudioFormat format,
				byte[] data) {
			System.out.println(format);
			try {
		        	FileOutputStream fs = new FileOutputStream("c:\\temp\\testaudio", true);
				fs.write(data);
				fs.close();
			} catch (Exception eee) {
				eee.printStackTrace();
			}
			synchronized (eventQueue) {
				eventQueue.add(new ClientEvent(ClientEventType.NEW_AUDIO_DATA,
						null));
				eventQueue.notifyAll();
			}
		}

		@Override
		public void clientClosed(LoquendoClient client) {
			synchronized (eventQueue) {
				eventQueue.add(new ClientEvent(ClientEventType.CLOSED,
						null));
				eventQueue.notifyAll();
			}
		}

		@Override
		public void clientError(LoquendoClient client, String error) {
			synchronized (eventQueue) {
				eventQueue.add(new ClientEvent(ClientEventType.ERROR,
						error));
				eventQueue.notifyAll();
			}
		}
		
		public ClientEvent receiveEvent(long timeout)
		throws InterruptedException {
			long waited = 0;
			synchronized (eventQueue) {
				while (waited < timeout && eventQueue.isEmpty()) {
					long before = System.currentTimeMillis();
					eventQueue.wait(timeout-waited);
					long after = System.currentTimeMillis();
					waited += after - before;
				}
				if (eventQueue.isEmpty())
					throw new InterruptedException("Time-out");
				return eventQueue.remove(0);
			}
		}
	}
	
	private class SingleClientTest extends Thread {
		private int index;
		
		public SingleClientTest(int i) {
			this.index = i;
		}
		
		@Override
		public void run() {
			if (!runSingleClientTest(index)) {
				System.exit(1);
			}
		}
	}
	
	private static void usage() {
		System.out.println("Usage: LoquendoServerTest OPTIONS");
		System.out.println("");
		System.out.println("Options:");
		System.out.println("-host HOST-NAME");
		System.out.println("   Host name of the Loquendo server.");
		System.out.println("-port PORT-NUMBER");
		System.out.println("   Port number of the Loquendo server.");
	}
	
	public static void main(String[] args) {
		String host = "ewi1424.ewi.utwente.nl";
		Integer port = 22093;
		int i = 0;
		while (i < args.length) {
			String arg = args[i++];
			if (arg.equals("-host") && host == null && i < args.length) {
				host = args[i++];
			} else if (arg.equals("-port") && port == null &&
					i < args.length) {
				String s = args[i++];
				try {
					port = new Integer(s);
				} catch (Exception ex) {
					usage();
					System.exit(1);
				}
			} else {
				usage();
				System.exit(1);
			}
		}
		if (host == null || port == null) {
			usage();
			System.exit(1);
		}
		new LoquendoServerTest(host, port.intValue());
	}
}
