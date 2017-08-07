import java.net.*;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import java.io.*;

public class Pitcher {

	int port;
	String hostname;
	int mps = 1;
	int size = 300;
	private DataOutputStream outToCatcher = null;
	private DataInputStream inFromCatcher = null;
	private Socket clientSocket;
	private Timer timer;
	private int currentSec = 0;
	private long timeOffset;
	private long timeSent;

	// Holds data from received messages
	private ArrayList<ReceivedMsgData> receivedMessagesData = new ArrayList<>();

	// Used for detecting lost messages
	private ArrayList<Integer> sentMessages = new ArrayList<>();
	private ArrayList<Integer> receivedMessages = new ArrayList<>();

	public Pitcher(int port, String hostname, int mps, int size, long timeOffset) {
		this.port = port;
		this.hostname = hostname;
		this.mps = mps;
		this.size = size;
		this.timeOffset = timeOffset;

		// Opening connection
		try {
			clientSocket = new Socket(hostname, port);
			outToCatcher = new DataOutputStream(clientSocket.getOutputStream());
			inFromCatcher = new DataInputStream(clientSocket.getInputStream());
			System.out.println(
					"TCP pinging [" + hostname + "] with " + size + " bytes of data " + mps + " times a second: ");
			System.out.println("");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Adding message about lost packages when program ends
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {

				System.out.format("Sent = %d, Received = %d, Lost = %d (%.0f%% loss) \n", sentMessages.size(),
						receivedMessages.size(), (sentMessages.size() - receivedMessages.size()),
						(1 - ((float) receivedMessages.size() / sentMessages.size())) * 100);

				try {
					timer.cancel();
					outToCatcher.close();
					inFromCatcher.close();
					clientSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}));

	}

	public void run() {
		// Starting all timers and threads.
		Thread receiverThread = new ReceiverThread();
		receiverThread.start();
		timer = new Timer();
		timer.schedule(new SenderTask(), 0, 1000 / mps);
		timer.schedule(new StatisticsTask(), 1000, 1000);

	}

	/*
	 * ReceiverThread is used for receiving messages back from the Catcher. After
	 * message is received, data is being extracted and relevant informations are
	 * calculated.
	 *
	 */
	class ReceiverThread extends Thread {

		@Override
		public void run() {
			while (true) {
				int A_B = 0;
				int B_A = 0;
				int A_B_A = 0;

				try {

					byte[] byteArray = new byte[size];

					// Reading message into byte array.
					inFromCatcher.read(byteArray);

					// Wrapping byte array into ByteBuffer to provide easier data manipulation.
					ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);

					// Time when message arrived from Catcher back to Pitcher.
					long timeReceived = new Date().getTime() + timeOffset;

					// Message number used for detecting lost messages.
					int seqNum = byteBuffer.getInt();

					// Time when message was originally sent to the Catcher.
					long timeSent = byteBuffer.getLong();

					// Time when Catcher received message from Pitcher.
					// NOTE: This time is very close to the time message was sent back to Pitcher,
					// therefore, due to simplicity, only one is being used.
					long timeCatcherReceived = byteBuffer.getLong();

					receivedMessages.add(seqNum);

					// Calculating A->B, B->A and A->B->A times
					A_B = (int) (timeCatcherReceived - timeSent);
					A_B_A = (int) (timeReceived - timeSent);  
					B_A = (int) (timeReceived - timeCatcherReceived);

					ReceivedMsgData receivedMsgData = new ReceivedMsgData(seqNum, currentSec, A_B, B_A, A_B_A);
					receivedMessagesData.add(receivedMsgData);

				} catch (IOException e) {

					e.printStackTrace();
				}

			}

		}

	}

	/*
	 * SenderTask is used for sending given number of new messages each second from
	 * Pitcher to Catcher.
	 */
	class SenderTask extends TimerTask {
		int seqNum = 0;

		@Override
		public void run() {

			timeSent = new Date().getTime() + timeOffset;

			// Initial message contains message number and time when message was sent.
			// Making ByteBuffer given message size for easier data manipulation.
			ByteBuffer byteBuffer = ByteBuffer.allocate(size);
			byteBuffer.putInt(size);
			byteBuffer.putInt(seqNum);
			byteBuffer.putLong(timeSent);
			byte[] buffer = byteBuffer.array();

			try {
				// Sending message
				outToCatcher.write(buffer);

				sentMessages.add(seqNum);
				seqNum++;

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	/*
	 * StaticticsTask is used for calculating required statistics each second for
	 * the data received in the previous second.
	 */
	class StatisticsTask extends TimerTask {

		@Override
		public void run() {
			currentSec++;
			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			Calendar cal = Calendar.getInstance();

			int A_B = 0;
			int B_A = 0;
			int A_B_A = 0;

			int maxA_B = 0;
			int maxB_A = 0;
			int maxA_B_A = 0;

			int msgsReceived = 0;

			// Going trough each received message and getting data from the ones received in
			// previous second.
			for (ReceivedMsgData msg : receivedMessagesData) {
				if (msg.getSecondReceived() == (currentSec - 1)) {

					if (msg.getA_B() > maxA_B)
						maxA_B = msg.getA_B();
					if (msg.getB_A() > maxB_A)
						maxB_A = msg.getB_A();
					if (msg.getA_B_A() > maxA_B_A)
						maxA_B_A = msg.getA_B_A();
					A_B += msg.getA_B();
					B_A += msg.getB_A();
					A_B_A += msg.getA_B_A();
					msgsReceived++;
				}
			}

			// Formated printing
			System.out.format("%s %10" + "s %10s %10s %10s %10s %10s %10s %15s \n", "HH:MM:SS", "msgNum", "mps", "A->B",
					"B->A", "A->B->A", "max A->B", "max B->A", "max A->B->A");
			System.out.format("%s %10d %10d %8dms %8dms %8dms %8dms %8dms %13dms \n", dateFormat.format(cal.getTime()),
					sentMessages.size(), mps, (A_B / msgsReceived), (B_A / msgsReceived), (A_B_A / msgsReceived),
					maxA_B, maxB_A, maxA_B_A);
			System.out.println(
					"------------------------------------------------------------------------------------------------------");

		}

	}

}
