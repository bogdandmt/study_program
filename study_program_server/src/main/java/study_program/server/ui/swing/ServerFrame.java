package study_program.server.ui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import study_program.common.ImageIconFrame;

public class ServerFrame extends JFrame implements ActionListener, ImageIconFrame {

	private static final long serialVersionUID = 1L;
	private JLabel normalWindowLabel;
	private JDesktopPane desktopPane = new JDesktopPane();

	public ServerFrame() {
		super();
		init();
	}

	private void init() {
		setTitle("Study Program - Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 300);

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}

		JMenuBar menuBar = createMenuBar();
		setJMenuBar(menuBar);
		normalWindowLabel = new JLabel();

		JInternalFrame internalFrame = new JInternalFrame("Image", true, true, true);
		internalFrame.setSize(200, 100);
		internalFrame.setVisible(true);
		internalFrame.add(normalWindowLabel);

		desktopPane.add(internalFrame);
		setContentPane(desktopPane);
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menuFile;
		JMenuItem menuItemExit;
		menuBar = new JMenuBar();
		menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menuFile);
		menuItemExit = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItemExit.addActionListener(this);
		menuFile.add(menuItemExit);
		return menuBar;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem) (e.getSource());
		switch (source.getText()) {
		case "Exit":
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			break;
		}
	}

	@Override
	public void setIconForNormalWindowLabel(ImageIcon icon) {
		normalWindowLabel.setIcon(icon);
	}

	@Override
	public void setIconForFullScrWindowLabel(ImageIcon fullScrIcon) {
		// TODO Auto-generated method stub
	}

}
