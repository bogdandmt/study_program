package study_program.client;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import study_program.client.ui.swing.ClientFrame;
import study_program.interfaces.ImageReceiver;
import study_program.interfaces.ImageSender;

public class ClientStartPoint {

	public static void connectToAndQueryDatabase(String username, String password) {
		try {
			Connection con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9001", "SA", "");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Accounts");

			while (rs.next()) {
				System.out.println(rs.getString(4));
				// int x = rs.getInt("a");
				// String s = rs.getString("b");
				// float f = rs.getFloat("c");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Connection con = DriverManager.getConnection(
		// "jdbc:myDriver:myDatabase",
		// username,
		// password);
		//
		// Statement stmt = con.createStatement();
		// ResultSet rs = stmt.executeQuery("SELECT a, b, c FROM Table1");
		//
		// while (rs.next()) {
		// int x = rs.getInt("a");
		// String s = rs.getString("b");
		// float f = rs.getFloat("c");
		// }
	}

	public static void main(String[] args) throws IOException {
		// connectToAndQueryDatabase(null, null);

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
