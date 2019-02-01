package githyb.sunkeun.jchat.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
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
		
		// 입력 다이얼로그
		String nick = JOptionPane.showInputDialog("nickame");
		System.out.println("[CLIENT] " + nick);
		
		handler = new ConnectionHandler2(this, ip, port, nick);
		
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
}
