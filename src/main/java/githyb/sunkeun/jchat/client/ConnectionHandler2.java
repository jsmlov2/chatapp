package githyb.sunkeun.jchat.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import githyb.sunkeun.jchat.common.IO;

public class ConnectionHandler2 {

	String host; //ip 
	int port ; 
	
	Socket sock;
	InputStream in;
	OutputStream out;
	
	Thread thisThread;
	
	ChatClient ui;
	
	String nickName;
	
	public ConnectionHandler2(ChatClient ui, String host, int port, String nickName) throws UnknownHostException, IOException {
		this.ui = ui;
		this.host = host;
		this.port = port;
		this.nickName = nickName;
		makeConnection();
	}

	private void makeConnection() throws UnknownHostException, IOException {
		System.out.println("[CLIENT] try to connect.....");
		
		sock = new Socket(host, port);
		this.in = sock.getInputStream(); // 여기다가 쓰면 수화기 
		this.out = sock.getOutputStream(); // 마이크
		
		thisThread = new ListenerThread();
		thisThread.start(); // spawing new thread!
		
		System.out.println("[CLIENT]CONNECTED!");
	}
	
	class ListenerThread extends Thread {
		@Override
		public void run() {
			sendLoginCmd(nickName);
			// text line 기반 프로토콜
			// BufferedReader br= new BufferedReader(new InputStreamReader(in));
			while ( true ) {
				String cmd ;
				try {
					// MSG|nick|dlaksdjf;dlkj
					cmd = IO.readText(in); // {"cmd": "MSG", "sener" : "감자", "msg" : " d;aslksdfjasd;lkfjads;lfkjdsa;flkj" }
					//Map<String> p = new HashMap<>();// params.cmd 
//					String cmd = p.get("cmd");
//					if ( "CHATTERS".equals(cmd)) {
//						
//					}
//					String [] params = line.split("\\|");
					String [] params = { cmd };
					
					if ( "CHATTERS".equals(params[0])) {
						// chatters|ddd,xxx,ggg
						// String [] chatters = params[1].split(",");
						String [] chatters = IO.readTextArray(in);
						ui.updateChatters(chatters);
					} 
					else if( "JOIN".equals(params[0])){
						String joiner = IO.readText(in);
						ui.updateChatters(joiner);
						
					}else if("MSG".equals(params[0])) {
						String sender = IO.readText(in);
						String msg = IO.readText(in);
						ui.addMsg(sender, msg);
					}else if("LOGOUT".equals(params[0])){
						String nickName = IO.readText(in);
						ui.removeChatter(nickName);
						// ui.exit();
					}else if("FILE".equals(params[0])) {
						String sender = IO.readText(in);
						String fileName = IO.readText(in);
						byte [] data = IO.readBytes(in);
						
						ui.saveFile(sender, fileName, data);
						
					}else {
						throw new RuntimeException("알 수 없는 명령어: " + cmd);
					}
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
				
			}
		}
	}

	public void sendLoginCmd(String nickName) {
		/*
		 * C -> S 
					LOGIN|<nickname>
		 */
		
		try {
			// String cmd = "LOGIN|" + nickName;   
			IO.writeText("LOGIN", this.out);
			IO.writeText(nickName, this.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void sendMessage(String msg) {
		try {
			IO.writeText("MSG", out);
			IO.writeText(msg, out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		String cmd = "MSG|" + msg;
//		PrintWriter pw = new PrintWriter(this.out);
//		pw.println(cmd);
//		pw.flush();
//		
	}

	public void sendLogout() {
		// TODO Auto-generated method stub
		try {
			IO.writeText("LOGOUT", out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		String cmd = "LOGOUT";
//		PrintWriter pw = new PrintWriter(this.out);
//		pw.println(cmd);
//		pw.flush();
	}

	public void sendFile(File file) {
		/*
		 * FILE 
		 * file name
		 * byte[]
		 */
		try {
			IO.writeText("FILE", out);
			IO.writeText(file.getName(), out);

			// 1. 제일 편한 방법
			byte [] data = Files.readAllBytes(file.toPath());
			IO.writeBytes(data, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
