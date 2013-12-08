package study_program.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import study_program.client.ui.swing.ClientFrame;

public class ImageReceiver {
	private static int HEADER_SIZE = 8;
	private static int SESSION_START = 128;
	private static int SESSION_END = 64;

	/*
	 * The absolute maximum datagram packet size is 65507, The maximum IP packet
	 * size of 65535 minus 20 bytes for the IP header and 8 bytes for the UDP
	 * header.
	 */
	private static int DATAGRAM_MAX_SIZE = 65507;

	private static String IP_ADDRESS = "225.4.5.6";
	private static int PORT = 4444;
	private ClientFrame frame;
	private boolean fullScreen = false;

	public void receiveImages() {
		boolean debug = false;
		InetAddress ia = null;
		MulticastSocket ms = null;

		try {
			ia = InetAddress.getByName(IP_ADDRESS);
			ms = new MulticastSocket(PORT);
			ms.joinGroup(ia);

			int currentSession = -1;
			int storedSlicesCount = 0;
			int[] slicesCol = null;
			byte[] imageData = null;
			boolean sessionAvailable = false;

			byte[] buffer = new byte[DATAGRAM_MAX_SIZE];

			/* Receiving loop */
			while (true) {
				/* Receive a UDP packet */
				DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
				ms.receive(datagramPacket);
				byte[] data = datagramPacket.getData();

				/* Read header infomation */
				short session = (short) (data[1] & 0xff);
				short slicesCount = (short) (data[2] & 0xff);
				int maxPacketSize = (int) ((data[3] & 0xff) << 8 | (data[4] & 0xff)); // mask
				// the
				// sign
				// bit
				short sliceNumber = (short) (data[5] & 0xff);
				int size = (int) ((data[6] & 0xff) << 8 | (data[7] & 0xff)); // mask
				// the
				// sign
				// bit

				if (debug) {
					System.out.println("------------- PACKET -------------");
					System.out.println("SESSION_START = " + ((data[0] & SESSION_START) == SESSION_START));
					System.out.println("SSESSION_END = " + ((data[0] & SESSION_END) == SESSION_END));
					System.out.println("SESSION NR = " + session);
					System.out.println("SLICES = " + slicesCount);
					System.out.println("MAX PACKET SIZE = " + maxPacketSize);
					System.out.println("SLICE NR = " + sliceNumber);
					System.out.println("SIZE = " + size);
					System.out.println("------------- PACKET -------------\n");
				}
				/* If SESSION_START flag is set, setup start values */
				if ((data[0] & SESSION_START) == SESSION_START) {
					if (session != currentSession) {
						currentSession = session;
						storedSlicesCount = 0;
						imageData = new byte[slicesCount * maxPacketSize];
						slicesCol = new int[slicesCount];
						sessionAvailable = true;
					}
				}
				/* If package belogs to current session */
				if (sessionAvailable && session == currentSession) {
					if (slicesCol != null && slicesCol[sliceNumber] == 0) {
						slicesCol[sliceNumber] = 1;
						System.arraycopy(data, HEADER_SIZE, imageData, sliceNumber * maxPacketSize, size);
						storedSlicesCount++;
					}
				}
				if (storedSlicesCount == slicesCount) {
					displayCompleteImage(imageData);
				}
				if (debug) {
					System.out.println("STORED SLICES: " + storedSlicesCount);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ms != null) {
				try {
					ms.leaveGroup(ia);
					ms.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void displayCompleteImage(byte[] imageData) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
		BufferedImage image;
		image = ImageIO.read(bis);
		ImageIcon normalIcon = new ImageIcon(image);
		frame.setIconForNormalWindowLabel(normalIcon);
		ImageIcon fullScrIcon = new ImageIcon(image);
		frame.setIconForFullScrWindowLabel(fullScrIcon);
		//frame.pack();
	}

	public void handleCommandLineArgs(String[] args) {
		switch (args.length) {
		case 2:
			IP_ADDRESS = args[1];
		case 1:
			PORT = Integer.parseInt(args[0]);
		}
	}

	public boolean isFullScreen() {
		return fullScreen;
	}

	public void setFullScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;
	}

	public void setFrame(ClientFrame frame) {
		this.frame = frame;
	}

}
