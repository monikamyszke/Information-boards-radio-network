import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.io.FilenameUtils;

public class FileDisplay implements Runnable{
	
	private volatile boolean newFileReceived = false;
	private volatile String filename;
	private JFrame frame;
	
	private int screenWidth;
	private int screenHeight;
	
	public void displayNewFile(String filename) {
		this.filename = filename;
		newFileReceived = true;
	}
	
	@Override
	public void run() {
		screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		GraphicsDevice gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		
		// wy�wietlenie obrazu startowego
		if (frame == null) {
			frame = new JFrame(gs.getDefaultConfiguration());
			frame.setSize(screenWidth, screenHeight);
			displayImage("startImage.png");
			gs.setFullScreenWindow(frame);
		}
		
		while (true) {
			
			while (newFileReceived == false);
			
			newFileReceived = false;
			String fileExtension = FilenameUtils.getExtension(filename);
			
			// wywo�anie odpowiedniej funkcji wy�wietlaj�cej plik w zale�no�ci od jego rozszerzenia
			if (fileExtension.equals("pdf")) {
				displayPdf(filename);
			} else if (fileExtension.equals("html")){
				displayHtml(filename);
			} else if (fileExtension.equals("txt")){
				displayTxt(filename);
			} else if (fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png") ||  fileExtension.equals("gif") || fileExtension.equals("bmp")){
				displayImage(filename);
			}
		}
	}
	
	private void displayPdf(String filename) {
		// xpdf -fullscreen
		String [] command = new String[] {"xpdf", "-fullscreen", filename};
		runProcess(command);
	}
	
	private void displayHtml(String filename) {
		// chromium-browser  --start-fullscreen
		String [] command = new String[] {"chromium-browser", "--start-fullscreen", filename};
		runProcess(command);
	}
	
	private void displayTxt(String filename) {
		displayHtml(filename);
	}
	
	private void displayImage(String filename) {
		BufferedImage image = null;
		ImageIcon icon;
		Image newImage;
		JLabel label;
		URL url;
		float xi;
		float yi;
		float dx;
		float dy;
		float k;
		float kx;
		float ky;
		int xi_new;
		int yi_new;
		
		frame.getContentPane().removeAll();
		frame.getContentPane().setVisible(false);
		
		if (filename.equals("startImage.png")) {
			url = FileDisplay.class.getResource(filename);
			try {
				image = ImageIO.read(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				image = ImageIO.read(new File(filename));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		icon = new ImageIcon(image);
		
		xi = (float)icon.getIconWidth();
		yi = (float)icon.getIconHeight();
		dx = xi - screenWidth;
		dy = yi - screenHeight;
		
		// skalowanie wymiar�w pliku graficznego
		if ((dx <= 0) && (dy <= 0)) {
			k = 1;
		} else {
				kx = xi/screenWidth;
				ky = yi/screenHeight;

				if (kx >= ky) {
					k = kx;
				} else {
					k = ky;
				}
			}

		xi_new = (int) (xi/k);
		yi_new = (int) (yi/k);
		
		newImage = icon.getImage().getScaledInstance(xi_new, yi_new, Image.SCALE_SMOOTH);
		label = new JLabel(new ImageIcon(newImage), JLabel.CENTER);
		frame.add(label);
		frame.getContentPane().setVisible(true);
	}
	
	// funkcja uruchamiaj�ca podan� komend� w wierszu polece�
	private void runProcess(String[] command) {
		Process process;
		
		try {
			process = new ProcessBuilder(command).start();
			
			while (newFileReceived == false);
			
			process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
