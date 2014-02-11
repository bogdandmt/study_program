package study_program.client.ui.swing;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JWindow;

import study_program.common.ImageIconFrame;
import study_program.common.ImageReceiver;

public class ClientFrame extends JFrame implements ImageIconFrame {

	private static final long serialVersionUID = 1L;
	private ImageReceiver imageReceiver;
	private JMenuBar menuBar;
	private JWindow fullscreenWindow;
	private JLabel normalWindowLabel;
	private JLabel fullScrWindowLabel;

	public ClientFrame(ImageReceiver imageReceiver) {
		super();
		this.imageReceiver = imageReceiver;
		init();
	}

	private void init() {
		setTitle("Study Program - Client");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 200);

		ClientFrameListener listener = new ClientFrameListener();

		normalWindowLabel = new JLabel();
		add(normalWindowLabel);

		fullScrWindowLabel = new JLabel();
		fullscreenWindow = new JWindow();
		fullscreenWindow.getContentPane().add(fullScrWindowLabel);
		fullscreenWindow.addKeyListener(listener);

		menuBar = createMenuBar(listener);
		setJMenuBar(menuBar);

		addKeyListener(listener);
	}

	private JMenuBar createMenuBar(ClientFrameListener listener) {
		JMenuBar menuBar;
		JMenu menuFile;
		JMenuItem menuItemExit;
		menuBar = new JMenuBar();
		menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menuFile);
		menuItemExit = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItemExit.addActionListener(listener);
		menuFile.add(menuItemExit);
		return menuBar;
	}

	@Override
	public void setIconForNormalWindowLabel(ImageIcon icon) {
		normalWindowLabel.setIcon(icon);
	}

	@Override
	public void setIconForFullScrWindowLabel(ImageIcon icon) {
		fullScrWindowLabel.setIcon(icon);
	}

	private class ClientFrameListener implements ActionListener, KeyListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem source = (JMenuItem) (e.getSource());
			switch (source.getText()) {
			case "Exit":
				dispatchEvent(new WindowEvent(ClientFrame.this, WindowEvent.WINDOW_CLOSING));
				break;
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				if (imageReceiver.isFullScreen()) {
					device.setFullScreenWindow(null);
					fullscreenWindow.setVisible(false);
					imageReceiver.setFullScreen(false);
				} else {
					device.setFullScreenWindow(fullscreenWindow);
					fullscreenWindow.setVisible(true);
					imageReceiver.setFullScreen(true);
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	}
}
