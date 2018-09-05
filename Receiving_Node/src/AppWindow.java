import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.BorderLayout;

public class AppWindow extends JFrame {
	
	private JPanel mainPanel;
	private JTextPane textPane;
	private static final long serialVersionUID = 1L;
	private Color mint = new Color(17, 151, 150);
	
	public AppWindow() {
		setTitle("Receiving Node");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 628, 395);
		
		mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		mainPanel.setLayout(new BorderLayout(0, 0));
		
		textPane = new JTextPane();
		textPane.setBackground(mint);
		textPane.setEditable(false);
		mainPanel.add(textPane, BorderLayout.NORTH);
		setContentPane(mainPanel);
	}
	
	public void setMessage(String text) {
		this.textPane.setText(text);
	}
	
}
