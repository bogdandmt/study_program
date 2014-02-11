package study_program.client;

import java.io.IOException;

import study_program.client.ui.swing.ClientFrame;
import study_program.common.ImageReceiver;
import study_program.common.ImageSender;

public class ClientStartPoint {


	public static void main(String[] args) throws IOException {

		ImageReceiver receiver = new ImageReceiver("225.4.5.6", 4444);
		ClientFrame frame = new ClientFrame(receiver);
		receiver.setFrame(frame);
		frame.setVisible(true);
		receiver.handleCommandLineArgs(args);
		Thread receiverThread = new Thread(receiver);
		receiverThread.start();
		
		ImageSender imgSender = new ImageSender("225.4.5.6", 5555);
		imgSender.handleCommandLineArgs(args);
		Thread senderThread = new Thread(imgSender);
		senderThread.start();
	}
}
