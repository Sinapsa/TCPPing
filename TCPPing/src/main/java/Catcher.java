import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Date;

public class Catcher {

	int port;
	String bind;
	public static long timeReceivedCatcher;
	public static long timeSentCatcher;
	private long timeOffset;
	private final int MAX_BUFFER_SIZE = 3000;

	public Catcher(int port, String bind, long timeOffset) {
		this.port = port;
		this.bind = bind;
		this.timeOffset = timeOffset;
	}

	public void run() {

		try {
			// Opening connection
			ServerSocket serverSocket;
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(bind, port));
			Socket connectionSocket = serverSocket.accept();
			DataInputStream inFromPitcher = new DataInputStream(connectionSocket.getInputStream());
			DataOutputStream outToPitcher = new DataOutputStream(connectionSocket.getOutputStream());

			while (true) {

				// Creating max size buffer because we do not yet know size of the message.
				byte[] byteArray = new byte[MAX_BUFFER_SIZE];

				// Reading received message into byteArray and wrapping it into ByteBuffer.
				inFromPitcher.read(byteArray);
				ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);

				// Getting time when message was received.
				long timeCatcherReceived = new Date().getTime() + timeOffset;

				// Receiving data from the message.
				int size = byteBuffer.getInt();
				int seqNum = byteBuffer.getInt();
				long timeSent = byteBuffer.getLong();

				// Creating new buffer exact the same size as received one.
				if (size != 0) {
					ByteBuffer byteBuffer2 = ByteBuffer.allocate(size);
					byteBuffer2.putInt(seqNum);
					byteBuffer2.putLong(timeSent);
					byteBuffer2.putLong(timeCatcherReceived);
					byteArray = byteBuffer2.array();

					// Sending response to the Pitcher.
					outToPitcher.write(byteArray);
					
				} else
					continue;

			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
