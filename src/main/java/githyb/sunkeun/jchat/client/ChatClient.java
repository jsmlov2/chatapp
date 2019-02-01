package githyb.sunkeun.jchat.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
/**
 * 
 *  [              ] [    ]
 *  [              ]
 *  ----------------
 *  [             ]
 * @author 조선근
 *
 */
public class ChatClient extends JFrame{

	JTextField inputField;
	JTextArea chatArea;
	JButton logoutButton;
	
	ConnectionHandler2 handler ;
	
	DefaultListModel<String> chatters;
	public ChatClient(String ip, int port) throws UnknownHostException, IOException {
		
		Container root = this.getContentPane();
		
		chatArea = new JTextArea();
		root.add(chatArea, BorderLayout.CENTER);
		
		inputField = new JTextField();
		inputField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				processSendMessage();
				
			}
		});
		root.add(inputField, BorderLayout.SOUTH);
		
		logoutButton = new JButton("LOGOUT");
		logoutButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				processLogout();
				
			}
		});
		root.add(logoutButton, BorderLayout.NORTH);
		
		chatters = new DefaultListModel<>(); // 순수한 데이터 자체를 갖고 있음
		JList<String> chatList = new JList<>(chatters);
		
		
		root.add(chatList, BorderLayout.EAST);
		
		intallMenu();
		// 입력 다이얼로그
		String nick = JOptionPane.showInputDialog("nickame");
//		String nick = "abvc";
		System.out.println("[CLIENT] " + nick);
		
		handler = new ConnectionHandler2(this, ip, port, nick);
		
	}
	
	void intallMenu() {
		JMenuBar bar = new JMenuBar();
		{
			JMenu file = new JMenu("File");
			JMenuItem upload = new JMenuItem("Upload");
			upload.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					processFileUpload();
				}
			});
			file.add(upload);
			bar.add(file);
		}
		
		this.setJMenuBar(bar);
		
	}
	/**
	 * 파일을 선택해서 업로드 합니다.
	 */
	void processFileUpload() {
		JFileChooser chooser = new JFileChooser(new File("."));
		int mode = chooser.showOpenDialog(this);
		if ( mode != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		File selectedFile = chooser.getSelectedFile(); // 내가 선택한 파일을 가져올 수 잇음!
		System.out.println(selectedFile.getAbsolutePath());
		/*
		 * 파일 내용을 바이트로 읽어들여서 소켓 out 스트림으로 보내면 됨
		 */
		handler.sendFile(selectedFile);
	}

	void processLogout() {
		this.handler.sendLogout();
		this.exit();
	}

	/**
	 * processXXXXX =- 단위작업
	 */
	void processSendMessage() {
		String msg = inputField.getText();
		handler.sendMessage(msg);
		inputField.setText("");
		
	}

	public static void main(String[] args) throws InterruptedException, UnknownHostException, IOException {
		String ip = "192.168.1.164";
		int port = 9999;
		
		ChatClient frame = new ChatClient(ip, port);
		frame.setSize(400, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // X 누르면 프로세스 종료! 
		frame.setVisible(true); // 이거 호출해야 창이 뜸
		
		
		/*
		String ip = "192.168.1.164";
		int port = 9999;
		
		System.out.println("[CLIENT] try to connect.....");
		Socket sock = new Socket(ip, port);
		sock.close();
		
		System.out.println("[CLIENT]BYE");
		*/
		
	}

	public void updateChatters(String ... nicknames) {
		for (String name : nicknames) {
			this.chatters.addElement(name);
		}
		
	}

	public void addMsg(String sender, String msg) {
		this.chatArea.append(sender + ":" + msg + "\n");
	}

	public void removeChatter(String nickName) {
		// TODO Auto-generated method stub
		this.chatters.removeElement(nickName);
		this.chatArea.append(nickName + "님이 로그아웃했습니다\n");
	}

	public void exit() {
		System.exit(0);//프로세스 죽이기
	}

	/**
	 * 파일을 저장합니다.
	 * @param sender
	 * @param fileName
	 * @param data
	 */
	public void saveFile(String sender, String fileName, byte[] data) {
		JFileChooser chooser = new JFileChooser(new File("."));
		chooser.setSelectedFile(new File(fileName));
		int mode = chooser.showSaveDialog(this);
		if ( mode == JFileChooser.APPROVE_OPTION) {
			/*
			 * File :잘못 만든 이름! 경로입니다. 이 위ㅣ에 파일이 있을 수도 있고, 없을 수도 있음!
			 */
			File saveTo = chooser.getSelectedFile();
			System.out.println(sender + " : " + fileName );
			System.out.println(saveTo.getAbsolutePath());
			System.out.println(saveTo.exists());
			
			try {
				if(!saveTo.exists()) {
					// 파일 디스크에 만들어주세요 
					saveTo.createNewFile();
				}
				Files.write(
						saveTo.toPath(), 
						data, 
						StandardOpenOption.TRUNCATE_EXISTING);//기존 파일 존재시 새로 만들기
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// TODO Auto-generated method stub
		
	}
}
