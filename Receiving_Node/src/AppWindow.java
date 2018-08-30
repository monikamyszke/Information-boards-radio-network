import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.BorderLayout;

public class AppWindow extends JFrame {
	
	private JPanel mainPanel;
	private JTextPane text;
	private static final long serialVersionUID = 1L;
	private Color mint = new Color(17, 151, 150);
	
	public AppWindow() {
		setTitle("Receiving Node");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 628, 395);
		
		mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		mainPanel.setLayout(new BorderLayout(0, 0));
		setContentPane(mainPanel);
		
		text = new JTextPane();
		text.setBackground(mint);
		text.setEditable(false);
		mainPanel.add(text, BorderLayout.NORTH);
	}
	
	public void setMessage(String text) {
		this.text.setText(text);
	}
	
}
