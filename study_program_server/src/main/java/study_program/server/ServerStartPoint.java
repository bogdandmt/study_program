package study_program.server;

import java.io.IOException;

import study_program.common.ImageReceiver;
import study_program.common.ImageSender;
import study_program.server.ui.swing.ServerFrame;

public class ServerStartPoint {

	public static void main(String[] args) throws IOException {
		ImageSender imgSender = new ImageSender("225.4.5.6", 4444);
		ServerFrame frame = new ServerFrame();
		frame.setVisible(true);
		imgSender.handleCommandLineArgs(args);
		Thread senderThread = new Thread(imgSender);
		senderThread.start();
		
		ImageReceiver receiver = new ImageReceiver("225.4.5.6", 5555);
		receiver.setFrame(frame);
		Thread receiverThread = new Thread(receiver);
		receiverThread.start();
		
	}

}
