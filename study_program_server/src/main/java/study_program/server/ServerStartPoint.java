package study_program.server;

import java.io.IOException;

import javax.swing.JFrame;

import study_program.server.ui.swing.ServerFrame;

public class ServerStartPoint {

	public static void main(String[] args) throws IOException {
		ImageSender imgSender = new ImageSender();
		//imgSender.createFrame();
		JFrame frame = new ServerFrame();
		frame.setVisible(true);
		imgSender.handleCommandLineArgs(args);
		imgSender.sendImages();
	}

}
