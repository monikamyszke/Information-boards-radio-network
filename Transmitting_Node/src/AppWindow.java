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
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JComboBox;

public class AppWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private Color white = new Color(255, 255, 255);
	private Color orange = new Color(255, 180, 69);
	private Color navy = new Color(0, 55, 103);
	private Font font40 = new Font("Raleway", Font.PLAIN, 40);
	private Font font15 = new Font("Raleway", Font.PLAIN, 15);
	private Font font13b = new Font("Raleway SemiBold", Font.PLAIN, 13);
	private Font font15b = new Font("Raleway SemiBold", Font.PLAIN, 15);
	private Font font16b = new Font("Raleway SemiBold", Font.PLAIN, 16);
	private Font font18b = new Font("Raleway SemiBold", Font.PLAIN, 18);
	
	private JButton searchDevicesButton;
	private JTextPane searchedDevicesLabel;
	private JComboBox<String> listOfDevices;
	private JButton chooseFileButton;
	private JTextPane fileSizeLabel;
	private JButton sendButton;
	
	// funkcja zwracaj¹ca przycisk, którego wciœniêcie rozpoczyna wyszukiwanie urz¹dzeñ
	public JButton getSearchingButton() {
		return searchDevicesButton;
	}
	
	// funkcja wypisuj¹ca nazwy i adresy urz¹dzeñ w momencie ich wykrycia w oknie aplikacji
	public void setLabel(RemoteDevice device, String name) {
		this.searchedDevicesLabel.setText(searchedDevicesLabel.getText() + "Wykryto urz¹dzenie!\nNazwa: " + name + "\nAdres MAC: " + device +"\n\n");
	}
	
	// funkcja usuwaj¹ca z okna aplikacji wypisane nazwy i adresy wykrytych urz¹dzeñ
	public void clearLabel() {
		this.searchedDevicesLabel.setText("");
	}
	
	// funkcja dodaj¹ca do listy rozwijanej nowo wykryte urz¹dzenie
	public void setListOfDevices(String name) {
		this.listOfDevices.addItem(name);
	}
	
	// funkcja usuwaj¹ca z rozwijanej listy wszystkie wykryte urz¹dzenia
	public void clearListOfDevices() {
		this.listOfDevices.removeAllItems();
	}
	
	// funkcja umo¿liwiaj¹ca pobranie pozycji wybranego urz¹dzenia z rozwijanej listy
	public int getDeviceNumber(){
		return this.listOfDevices.getSelectedIndex();
	}
	
	// funkcja zwracaj¹ca przycisk, którego wciœniêcie umo¿liwia wybranie pliku z dysku komputera
	public JButton getFileChooserButton() {
		return chooseFileButton;
	}
	
	// funkcja wypisuj¹ca rozmiar wybranego pliku w oknie aplikacji
	public void setFileSize(long fileSize) {
		this.fileSizeLabel.setText("Rozmiar pliku:  " + fileSize +  " B");
	}
	
	// funkcja zwracaj¹ca przycisk, którego wciœniêcie umo¿liwia wys³anie pliku
	public JButton getSendingButton() {
		return sendButton;
	}

	public AppWindow() {
		
		JPanel mainPanel;
		JPanel topPanel;
		ImageIcon signalIcon;
		JLabel title;
		JLabel subtitle;
		JPanel bottomPanel;
		JPanel leftPanel;
		JPanel centerPanel;
		JPanel rightPanel;
		JPanel leftTopPanel;
		JLabel searchDevicesLabel;
		JPanel leftCenterPanel;
		JPanel searchDevicesButtonPanel;
		JPanel searchDevicesPanel;
		JPanel centerTopPanel;
		JLabel chooseDeviceLabel;
		JPanel centerCenterPanel;
		JPanel rightTopPanel;
		JLabel chooseFileLabel;
		JPanel rightCenterPanel;
		JPanel chooseFileButtonPanel;
		JPanel fileSizePanel;
		JPanel fileInfoPanel;
		JTextArea fileInfoLabel;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(0, 0));
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		mainPanel.setBackground(navy);
		setContentPane(mainPanel);
		
		topPanel = new JPanel();
		topPanel.setBackground(navy);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		
		signalIcon = new ImageIcon(getClass().getResource("radiosignal.png"));
		topPanel.setLayout(new BorderLayout(0, 0));
		
		title = new JLabel("Radiowa sieæ tablic informacyjnych", signalIcon, SwingConstants.CENTER);
		title.setFont(font40);
		title.setForeground(white);
		title.setIconTextGap(20);
		title.setVerticalTextPosition(JLabel.CENTER);
		title.setHorizontalTextPosition(JLabel.LEFT);
		topPanel.add(title);
		
		subtitle = new JLabel("zrealizowana z wykorzystaniem platformy Raspberry Pi", SwingConstants.CENTER);
		subtitle.setFont(font15);
		subtitle.setForeground(white);
		topPanel.add(subtitle, BorderLayout.SOUTH);
		
		leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout(0, 0));
		leftPanel.setPreferredSize(new Dimension(320, 100));
		leftPanel.setBackground(navy);
		mainPanel.add(leftPanel, BorderLayout.WEST);
		
		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout(0, 0));
		centerPanel.setBackground(navy);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		
		rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout(0, 0));
		rightPanel.setPreferredSize(new Dimension(320, 100));
		rightPanel.setBackground(navy);
		mainPanel.add(rightPanel, BorderLayout.EAST);
		
		leftTopPanel = new JPanel();
		leftPanel.add(leftTopPanel, BorderLayout.NORTH);
		leftTopPanel.setBorder(new EmptyBorder(100, 0, 0, 0));
		leftTopPanel.setBackground(navy);
		
		searchDevicesLabel = new JLabel("1. Wyszukaj urz¹dzenia w zasiêgu", SwingConstants.CENTER);
		searchDevicesLabel.setFont(font16b);
		searchDevicesLabel.setForeground(white);
		leftTopPanel.add(searchDevicesLabel);
		
		leftCenterPanel = new JPanel();
		leftPanel.add(leftCenterPanel, BorderLayout.CENTER);
		leftCenterPanel.setBorder(new EmptyBorder(45, 0, 0, 0));
		leftCenterPanel.setBorder(new MatteBorder(0, 0, 0, 1, white));
		leftCenterPanel.setBackground(navy);
		leftCenterPanel.setLayout(new BorderLayout(0, 0));
		
		searchDevicesButtonPanel = new JPanel();
		searchDevicesButtonPanel.setBorder(new EmptyBorder(30, 0, 0, 0));
		searchDevicesButtonPanel.setBackground(navy);
		leftCenterPanel.add(searchDevicesButtonPanel, BorderLayout.NORTH);
		
		searchDevicesButton = new JButton("Rozpocznij");
		searchDevicesButton.setFont(font16b);
		searchDevicesButton.setBackground(orange);
		searchDevicesButton.setForeground(white);
		searchDevicesButton.setFocusPainted(false);
		searchDevicesButton.setPreferredSize(new Dimension(200, 40));
		searchDevicesButtonPanel.add(searchDevicesButton);
		
		searchDevicesPanel = new JPanel();
		searchDevicesPanel.setBackground(navy);
		leftCenterPanel.add(searchDevicesPanel, BorderLayout.CENTER);
		
		searchedDevicesLabel = new JTextPane();
		searchedDevicesLabel.setFont(font15b);
		searchedDevicesLabel.setBackground(navy);
		searchedDevicesLabel.setForeground(white);
		searchedDevicesLabel.setEditable(false);
		searchDevicesPanel.add(searchedDevicesLabel);
		
		centerTopPanel = new JPanel();
		centerTopPanel.setBorder(new EmptyBorder(100, 0, 0, 0));
		centerTopPanel.setBackground(navy);
		centerPanel.add(centerTopPanel, BorderLayout.NORTH);
		
		chooseDeviceLabel = new JLabel("2. Wybierz z listy urz¹dzenie docelowe", SwingConstants.CENTER);
		chooseDeviceLabel.setFont(font16b);
		chooseDeviceLabel.setForeground(white);
		centerTopPanel.add(chooseDeviceLabel);
		
		centerCenterPanel = new JPanel();
		centerCenterPanel.setBorder(new EmptyBorder(35, 0, 0, 0));
		centerCenterPanel.setBackground(navy);
		centerPanel.add(centerCenterPanel, BorderLayout.CENTER);
		
		listOfDevices = new JComboBox<String>();
		listOfDevices.setPreferredSize(new Dimension(230, 30));
		centerCenterPanel.add(listOfDevices);
		
		rightTopPanel = new JPanel();
		rightTopPanel.setBorder(new EmptyBorder(100, 0, 0, 0));
		rightTopPanel.setBackground(navy);
		rightPanel.add(rightTopPanel, BorderLayout.NORTH);
		
		chooseFileLabel = new JLabel("3. Wybierz plik do wys³ania *", SwingConstants.CENTER);
		chooseFileLabel.setFont(font16b);
		chooseFileLabel.setForeground(white);
		rightTopPanel.add(chooseFileLabel);
		
		rightCenterPanel = new JPanel();;
		rightCenterPanel.setLayout(new BorderLayout(0, 0));
		rightCenterPanel.setBorder(new EmptyBorder(45, 0, 0, 0));
		rightCenterPanel.setBorder(new MatteBorder(0, 1, 0, 0, white));
		rightCenterPanel.setBackground(navy);
		rightPanel.add(rightCenterPanel, BorderLayout.CENTER);
		
		chooseFileButtonPanel = new JPanel();
		chooseFileButtonPanel.setBorder(new EmptyBorder(30, 0, 0, 0));
		chooseFileButtonPanel.setBackground(navy);
		rightCenterPanel.add(chooseFileButtonPanel, BorderLayout.NORTH);
		
		chooseFileButton = new JButton("Wybierz plik z dysku");
		chooseFileButton.setFont(font16b);
		chooseFileButton.setBackground(orange);
		chooseFileButton.setForeground(white);
		chooseFileButton.setFocusPainted(false);
		chooseFileButton.setPreferredSize(new Dimension(200, 40));
		chooseFileButtonPanel.add(chooseFileButton);
		
		fileSizePanel = new JPanel();
		fileSizePanel.setBorder(new EmptyBorder(45, 0, 0, 0));
		fileSizePanel.setBackground(navy);
		rightCenterPanel.add(fileSizePanel, BorderLayout.CENTER);
		
		fileSizeLabel = new JTextPane();
		fileSizeLabel.setFont(font16b);
		fileSizeLabel.setBackground(navy);
		fileSizeLabel.setForeground(white);
		fileSizeLabel.setEditable(false);
		fileSizePanel.add(fileSizeLabel);
		
		fileInfoPanel = new JPanel();
		fileInfoPanel.setBackground(navy);
		rightCenterPanel.add(fileInfoPanel, BorderLayout.SOUTH);
		
		fileInfoLabel = new JTextArea();
		fileInfoLabel.setText("* Plik powinien posiadaæ rozszerzenie:\njpg, .jpeg, .png, .gif, .bmp, .pdf, .txt, lub .html");
		fileInfoLabel.setFont(font13b);
		fileInfoLabel.setBackground(navy);
		fileInfoLabel.setForeground(white);
		fileInfoLabel.setEditable(false);
		fileInfoPanel.add(fileInfoLabel);
		
		bottomPanel = new JPanel();
		bottomPanel.setBackground(navy);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		
		sendButton = new JButton("Wyœlij do tablicy!");
		sendButton.setFont(font18b);
		sendButton.setForeground(white);
		sendButton.setBackground(orange);
		sendButton.setFocusPainted(false);
		sendButton.setPreferredSize(new Dimension(200, 50));
		bottomPanel.add(sendButton);
	}
}


