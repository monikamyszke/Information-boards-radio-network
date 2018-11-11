import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.Color;

import javax.bluetooth.RemoteDevice;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JComboBox;

public class AppWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private Color orange = new Color(253, 180, 69);
	private Color white = new Color(255, 255, 255);
	private Color navy = new Color(0, 55, 103);
	
	private JButton searchDevicesButton;
	private JTextPane searchedDevicesLabel;
	private JComboBox<String> listOfDevices;
	private JButton chooseFileButton;
	private JTextPane fileSizeLabel;
	private JButton sendButton;
	
	public JButton getSearchingButton() {
		return searchDevicesButton;
	}
	
	public void setLabel(RemoteDevice device, String name) {
		this.searchedDevicesLabel.setText(searchedDevicesLabel.getText() + "Wykryto urz¹dzenie!\nNazwa: " + name + "\nAdres MAC: " + device +"\n\n");
	}
	
	public void clearLabel() {
		this.searchedDevicesLabel.setText("");
	}
	
	public void setListOfDevices(String name) {
		this.listOfDevices.addItem(name);
	}
	
	public void clearListOfDevices() {
		this.listOfDevices.removeAllItems();
	}
	
	public int getDeviceNumber(){
		return this.listOfDevices.getSelectedIndex();
	}
	
	public JButton getFileChooserButton() {
		return chooseFileButton;
	}
	
	public void setFileSize(long fileSize) {
		this.fileSizeLabel.setText("Rozmiar pliku:  " + fileSize +  " B");
	}
	
	public JButton getSendingButton() {
		return sendButton;
	}

	public AppWindow() {
		setTitle("");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		mainPanel.setBackground(navy);
		setContentPane(mainPanel);
		mainPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel topPanel = new JPanel();
		topPanel.setForeground(new Color(0, 0, 0));
		topPanel.setBackground(navy);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		
		ImageIcon signalIcon;
		signalIcon = new ImageIcon(getClass().getResource("radiosignal.png"));
		topPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel title = new JLabel("Radiowa sieæ tablic informacyjnych", signalIcon, SwingConstants.CENTER);
		title.setVerticalTextPosition(JLabel.CENTER);
		title.setHorizontalTextPosition(JLabel.LEFT);
		title.setIconTextGap(20);
		title.setFont(new Font("Raleway", Font.PLAIN, 40));
		title.setForeground(white);
		topPanel.add(title);
		
		JLabel subtitle = new JLabel("zrealizowana z wykorzystaniem platformy Raspberry Pi", SwingConstants.CENTER);
		subtitle.setFont(new Font("Raleway", Font.PLAIN, 15));
		subtitle.setForeground(white);
		topPanel.add(subtitle, BorderLayout.SOUTH);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(navy);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		
		sendButton = new JButton("Wyœlij do tablicy!");
		sendButton.setFont(new Font("Raleway SemiBold", Font.PLAIN, 16));
		sendButton.setForeground(white);
		sendButton.setBackground(orange);
		sendButton.setFocusPainted(false);
		sendButton.setPreferredSize(new Dimension(200, 50));
		bottomPanel.add(sendButton);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setPreferredSize(new Dimension(320, 100));
		
		leftPanel.setBackground(navy);
		mainPanel.add(leftPanel, BorderLayout.WEST);
		leftPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel centerPanel = new JPanel();
		centerPanel.setBackground(navy);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel rightPanel = new JPanel();
		rightPanel.setPreferredSize(new Dimension(320, 100));
		rightPanel.setBackground(navy);
		mainPanel.add(rightPanel, BorderLayout.EAST);
		rightPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel leftTopPanel = new JPanel();
		leftTopPanel.setBackground(navy);
		leftTopPanel.setBorder(new EmptyBorder(100, 0, 0, 0));
		leftPanel.add(leftTopPanel, BorderLayout.NORTH);
		JLabel searchDevicesLabel = new JLabel("1. Wyszukaj urz¹dzenia w zasiêgu", SwingConstants.CENTER);
		searchDevicesLabel.setFont(new Font("Raleway SemiBold", Font.PLAIN, 15));
		searchDevicesLabel.setForeground(white);
		leftTopPanel.add(searchDevicesLabel);
		
		JPanel leftCenterPanel = new JPanel();
		leftCenterPanel.setBackground(navy);
		leftCenterPanel.setBorder(new EmptyBorder(45, 0, 0, 0));
		leftCenterPanel.setBorder(new MatteBorder(0, 0, 0, 1, white));
		leftPanel.add(leftCenterPanel, BorderLayout.CENTER);
		leftCenterPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel searchDevicesButtonPanel = new JPanel();
		searchDevicesButtonPanel.setBackground(navy);
		leftCenterPanel.add(searchDevicesButtonPanel, BorderLayout.NORTH);
		
		searchDevicesButton = new JButton("Rozpocznij");
		searchDevicesButton.setFont(new Font("Raleway SemiBold", Font.PLAIN, 15));
		searchDevicesButton.setFocusPainted(false);
		searchDevicesButton.setBackground(orange);
		searchDevicesButton.setForeground(white);
		searchDevicesButton.setFocusPainted(false);
		searchDevicesButton.setPreferredSize(new Dimension(200, 40));
		searchDevicesButtonPanel.add(searchDevicesButton);
		
		JPanel searchDevicesPanel = new JPanel();
		searchDevicesPanel.setBackground(navy);
		leftCenterPanel.add(searchDevicesPanel, BorderLayout.CENTER);
		
		searchedDevicesLabel = new JTextPane();
		searchedDevicesLabel.setBackground(navy);
		searchedDevicesLabel.setFont(new Font("Raleway SemiBold", Font.PLAIN, 13));
		searchedDevicesLabel.setForeground(white);
		searchedDevicesLabel.setEditable(false);
		searchDevicesPanel.add(searchedDevicesLabel);
		
		JPanel centerTopPanel = new JPanel();
		centerTopPanel.setBackground(navy);
		centerTopPanel.setBorder(new EmptyBorder(100, 0, 0, 0));
		centerPanel.add(centerTopPanel, BorderLayout.NORTH);
		JLabel chooseDeviceLabel = new JLabel("2. Wybierz z listy urz¹dzenie docelowe", SwingConstants.CENTER);
		chooseDeviceLabel.setFont(new Font("Raleway SemiBold", Font.PLAIN, 15));
		chooseDeviceLabel.setForeground(white);
		centerTopPanel.add(chooseDeviceLabel);
		
		JPanel centerCenterPanel = new JPanel();
		centerCenterPanel.setBackground(navy);
		centerCenterPanel.setBorder(new EmptyBorder(50, 0, 0, 0));
		centerPanel.add(centerCenterPanel, BorderLayout.CENTER);
		
		listOfDevices = new JComboBox<String>();
		listOfDevices.setPreferredSize(new Dimension(230, 30));
		centerCenterPanel.add(listOfDevices);
		
		JPanel rightTopPanel = new JPanel();
		rightTopPanel.setBackground(navy);
		rightTopPanel.setBorder(new EmptyBorder(100, 0, 0, 0));
		rightPanel.add(rightTopPanel, BorderLayout.NORTH);
		
		JLabel chooseFileLabel = new JLabel("3. Wybierz plik do wys³ania", SwingConstants.CENTER);
		chooseFileLabel.setFont(new Font("Raleway SemiBold", Font.PLAIN, 15));
		chooseFileLabel.setForeground(white);
		rightTopPanel.add(chooseFileLabel);
		
		JPanel rightCenterPanel = new JPanel();
		rightCenterPanel.setBackground(navy);
		rightCenterPanel.setLayout(new BorderLayout(0, 0));
		rightCenterPanel.setBorder(new EmptyBorder(45, 0, 0, 0));
		rightCenterPanel.setBorder(new MatteBorder(0, 1, 0, 0, white));
		rightPanel.add(rightCenterPanel, BorderLayout.CENTER);
		
		JPanel chooseFileButtonPanel = new JPanel();
		chooseFileButtonPanel.setBackground(navy);
		chooseFileButtonPanel.setBorder(new EmptyBorder(45, 0, 0, 0));
		rightCenterPanel.add(chooseFileButtonPanel, BorderLayout.NORTH);
		
		chooseFileButton = new JButton("Wybierz plik z dysku");
		chooseFileButton.setFont(new Font("Raleway SemiBold", Font.PLAIN, 15));
		chooseFileButton.setFocusPainted(false);
		chooseFileButton.setBackground(orange);
		chooseFileButton.setForeground(white);
		chooseFileButton.setFocusPainted(false);
		chooseFileButton.setPreferredSize(new Dimension(200, 40));
		chooseFileButtonPanel.add(chooseFileButton);
		
		JPanel fileSizePanel = new JPanel();
		fileSizePanel.setBackground(navy);
		fileSizePanel.setBorder(new EmptyBorder(45, 0, 0, 0));
		rightCenterPanel.add(fileSizePanel, BorderLayout.CENTER);
		
		fileSizeLabel = new JTextPane();
		fileSizeLabel.setBackground(navy);
		fileSizeLabel.setForeground(white);
		fileSizeLabel.setFont(new Font("Raleway SemiBold", Font.PLAIN, 15));
		fileSizeLabel.setEditable(false);
		fileSizePanel.add(fileSizeLabel);
		
	
	}
}


