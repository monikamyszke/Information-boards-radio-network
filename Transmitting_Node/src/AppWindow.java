import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JComboBox;
import java.awt.FlowLayout;

public class AppWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private Color grey = new Color(58, 68, 84);
	private Color white = new Color(255, 255, 255);
	private Color mint = new Color(17, 151, 150);
	
	private JButton searchButton;
	private JTextPane devices;
	private JComboBox<String> listOfdevices;
	private JButton sendButton;
	private JButton chooseFileButton;
	
	public JButton getSearchingButton() {
		return searchButton;
	}
	
	public void setLabel(String device) {
		this.devices.setText(devices.getText() + "\n" + device);
	}
	
	public void clearLabel() {
		this.devices.setText("");
	}
	
	public void setListOfDevices(String name) {
		this.listOfdevices.addItem(name);
	}
	
	public void clearListOfDevices() {
		this.listOfdevices.removeAllItems();
	}
	
	public int getDeviceNumber(){
		return this.listOfdevices.getSelectedIndex();
	}
	
	public JButton getFileChooserButton() {
		return chooseFileButton;
	}
	
	public JButton getSendingButton() {
		return sendButton;
	}

	public AppWindow() {
		setTitle("Transmitting Node");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		mainPanel.setBackground(mint);
		mainPanel.setLayout(new GridLayout(1, 2, 0, 0));
		setContentPane(mainPanel);
		
		JPanel panel1 = new JPanel();
		mainPanel.add(panel1);
		JPanel panel2 = new JPanel();
		mainPanel.add(panel2);
		panel2.setLayout(new BorderLayout(0, 0));
		
		JPanel top2 = new JPanel();
		top2.setBorder(new EmptyBorder(30, 0, 0, 0));
		top2.setPreferredSize(new Dimension(0, 100));
		panel2.add(top2, BorderLayout.NORTH);
		
		JPanel bottom2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) bottom2.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		bottom2.setBorder(new EmptyBorder(40, 0, 0, 0));
		bottom2.setPreferredSize(new Dimension(0, 150));
		panel2.add(bottom2, BorderLayout.SOUTH);
		
		sendButton = new JButton("WYŒLIJ DO URZ¥DZENIA");
		sendButton.setFont(new Font("Ebrima", Font.BOLD, 13));
		sendButton.setBackground(mint);
		sendButton.setForeground(white);
		sendButton.setFocusPainted(false);
		bottom2.add(sendButton);
		
		JPanel center2 = new JPanel();
		center2.setBorder(new EmptyBorder(20, 20, 30, 20));
		center2.setBackground(grey);
		bottom2.setBackground(grey);
		panel2.add(center2, BorderLayout.CENTER);
		
		panel1.setBackground(mint);
		top2.setBackground(grey);
		panel1.setLayout(new BorderLayout(0, 0));
		
		chooseFileButton = new JButton("Wybierz plik z dysku");
		chooseFileButton.setFont(new Font("Ebrima", Font.BOLD, 13));
		chooseFileButton.setFocusPainted(false);
		chooseFileButton.setBackground(grey);
		chooseFileButton.setForeground(white);
		chooseFileButton.setFocusPainted(false);
		chooseFileButton.setPreferredSize(new Dimension(200, 100));
		center2.add(chooseFileButton, BorderLayout.CENTER);
		
		JPanel top = new JPanel();
		top.setBorder(new EmptyBorder(30, 0, 0, 0));
		top.setPreferredSize(new Dimension(0, 100));
		top.setBackground(mint);
		panel1.add(top, BorderLayout.NORTH);
		
		searchButton = new JButton("ROZPOCZNIJ WYSZUKIWANIE URZ\u0104DZE\u0143");
		searchButton.setFont(new Font("Ebrima", Font.BOLD, 13));
		searchButton.setFocusPainted(false);
		searchButton.setBackground(grey);
		searchButton.setForeground(white);
		top.add(searchButton);
		
		
		JPanel center = new JPanel();
		center.setBorder(new EmptyBorder(20, 20, 20, 20));
		center.setBackground(mint);
		panel1.add(center, BorderLayout.CENTER);
		center.setLayout(new BorderLayout(0, 0));
		
		devices = new JTextPane();
		devices.setFont(new Font("Ebrima", Font.BOLD, 13));
		devices.setForeground(Color.WHITE);
		devices.setEditable(false);
		devices.setBackground(mint);
		center.add(devices, BorderLayout.CENTER);
	 
		JPanel bottom = new JPanel();
		bottom.setBorder(new EmptyBorder(40, 95, 90, 95));
		bottom.setBackground(mint);
		bottom.setPreferredSize(new Dimension(0, 200));
		panel1.add(bottom, BorderLayout.SOUTH);
		bottom.setLayout(new BorderLayout(0, 0));
		
		JLabel lblWybierzZListy = new JLabel("Wybierz z listy urz\u0105dzenie docelowe:");
		lblWybierzZListy.setBorder(new EmptyBorder(0, 0, 20, 0));
		lblWybierzZListy.setFont(new Font("Ebrima", Font.BOLD, 13));
		lblWybierzZListy.setHorizontalAlignment(SwingConstants.CENTER);
		lblWybierzZListy.setForeground(white);
		bottom.add(lblWybierzZListy, BorderLayout.NORTH);
		
		listOfdevices = new JComboBox<String>();
		bottom.add(listOfdevices, BorderLayout.CENTER);
	
	}
}


