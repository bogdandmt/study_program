package study_program.common;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.ImageFormatException;

@SuppressWarnings("restriction")
public class ImageSender implements Runnable {

	public static int HEADER_SIZE = 8;
	public static int MAX_PACKETS = 255;
	public static int SESSION_START = 128;
	public static int SESSION_END = 64;
	public static int DATAGRAM_MAX_SIZE = 65507 - HEADER_SIZE;
	public static int MAX_SESSION_NUMBER = 255;

	/*
	 * The absolute maximum datagram packet size is 65507, The maximum IP packet
	 * size of 65535 minus 20 bytes for the IP header and 8 bytes for the UDP
	 * header.
	 */
	public static String OUTPUT_FORMAT = "jpg";

	public static int COLOUR_OUTPUT = BufferedImage.TYPE_INT_RGB;

	public String ipAddress;
	public int port;
	public static boolean SHOW_MOUSEPOINTER = true;

	public ImageSender(String ipAddress, int port) {
		super();
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public static BufferedImage getScreenshot() throws AWTException, ImageFormatException, IOException {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		Rectangle screenRect = new Rectangle(screenSize);

		Robot robot = new Robot();
		BufferedImage image = robot.createScreenCapture(screenRect);

		return image;
	}

	public static byte[] bufferedImageToByteArray(BufferedImage image, String format) throws IOException {
		// byte[] imageBytes = ((DataBufferByte)
		// image.getData().getDataBuffer()).getData();
		// return ((DataBufferByte)
		// image.getRaster().getDataBuffer()).getData();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, format, baos);
		return baos.toByteArray();
	}

	private boolean sendImage(byte[] imageData) {
		InetAddress inetAddress;

		boolean result = false;
//		int ttl = 2;

		try {
			inetAddress = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return result;
		}

		DatagramSocket datagramScocket = null;

		try {
			datagramScocket = new DatagramSocket();
			//datagramScocket.setTimeToLive(ttl);
			DatagramPacket dp = new DatagramPacket(imageData, imageData.length, inetAddress, port);
			datagramScocket.send(dp);
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		} finally {
			if (datagramScocket != null) {
				datagramScocket.close();
			}
		}

		return result;
	}

	public void handleCommandLineArgs(String[] args) {
		switch (args.length) {
		case 3:
			ipAddress = args[2];
		case 2:
			port = Integer.parseInt(args[1]);
		case 1:
			SHOW_MOUSEPOINTER = Integer.parseInt(args[0]) == 1 ? true : false;
		}
	}

	public void sendImages() {
		//ImageSender sender = new ImageSender(ipAddress, port);
		int sessionNumber = 0;

		try {
			/* Continuously send images */
			BufferedImage image;
			while (true) {
				image = getScreenshot();

				if (SHOW_MOUSEPOINTER) {
					drawMousePointer(image);
				}

				byte[] imageByteArray = bufferedImageToByteArray(image, OUTPUT_FORMAT);
				int packets = (int) Math.ceil(imageByteArray.length / (float) DATAGRAM_MAX_SIZE);

				/* If image has more than MAX_PACKETS slices -> error */
				if (packets > MAX_PACKETS) {
					System.out.println("Image is too large to be transmitted!");
					continue;
				}

				/* Loop through slices */
				for (int i = 0; i <= packets; i++) {
					int flags = 0;
					flags = i == 0 ? flags | SESSION_START : flags;
					flags = (i + 1) * DATAGRAM_MAX_SIZE > imageByteArray.length ? flags | SESSION_END : flags;

					int size = (flags & SESSION_END) != SESSION_END ? DATAGRAM_MAX_SIZE : imageByteArray.length - i
							* DATAGRAM_MAX_SIZE;

					/* Set additional header */
					byte[] data = new byte[HEADER_SIZE + size];
					data[0] = (byte) flags;
					data[1] = (byte) sessionNumber;
					data[2] = (byte) packets;
					data[3] = (byte) (DATAGRAM_MAX_SIZE >> 8);
					data[4] = (byte) DATAGRAM_MAX_SIZE;
					data[5] = (byte) i;
					data[6] = (byte) (size >> 8);
					data[7] = (byte) size;

					/* Copy current slice to byte array */
					System.arraycopy(imageByteArray, i * DATAGRAM_MAX_SIZE, data, HEADER_SIZE, size);
					/* Send multicast packet */
					sendImage(data);

					/* Leave loop if last slice has been sent */
					if ((flags & SESSION_END) == SESSION_END)
						break;
				}
				sessionNumber = sessionNumber < MAX_SESSION_NUMBER ? ++sessionNumber : 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void drawMousePointer(BufferedImage image) {
		PointerInfo p = MouseInfo.getPointerInfo();
		int mouseX = p.getLocation().x;
		int mouseY = p.getLocation().y;

		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.red);
		Polygon polygon1 = new Polygon(new int[] { mouseX, mouseX + 10, mouseX, mouseX }, new int[] { mouseY,
				mouseY + 10, mouseY + 15, mouseY }, 4);

		Polygon polygon2 = new Polygon(new int[] { mouseX + 1, mouseX + 10 + 1, mouseX + 1, mouseX + 1 }, new int[] {
				mouseY + 1, mouseY + 10 + 1, mouseY + 15 + 1, mouseY + 1 }, 4);
		g2d.setColor(Color.black);
		g2d.fill(polygon1);

		g2d.setColor(Color.red);
		g2d.fill(polygon2);
		g2d.dispose();
	}

	@Override
	public void run() {
		sendImages();
	}
}