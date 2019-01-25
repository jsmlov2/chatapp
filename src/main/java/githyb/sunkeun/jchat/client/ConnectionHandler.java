package githyb.sunkeun.jchat.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConnectionHandler {

	String host; //ip 
	int port ; 
	
	Socket sock;
	InputStream in;
	OutputStream out;
	
	Thread thisThread;
	
	ChatClient ui;
	
	String nickName;
	
	public ConnectionHandler(ChatClient ui, String host, int port, String nickName) throws UnknownHostException, IOException {
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
			
			BufferedReader br= new BufferedReader(new InputStreamReader(in));
			while ( true ) {
				String line ;
				try {
					// MSG|nick|dlaksdjf;dlkj
					line = br.readLine(); // {"cmd": "MSG", "sener" : "감자", "msg" : " d;aslksdfjasd;lkfjads;lfkjdsa;flkj" }
					//Map<String> p = new HashMap<>();// params.cmd 
//					String cmd = p.get("cmd");
//					if ( "CHATTERS".equals(cmd)) {
//						
//					}
					String [] params = line.split("\\|");
					if ( "CHATTERS".equals(params[0])) {
						// chatters|ddd,xxx,ggg
						String [] chatters = params[1].split(",");
						ui.updateChatters(chatters);
					} 
					else if( "JOIN".equals(params[0])){
						ui.updateChatters(params[1]);
						
					}else if("MSG".equals(params[0])) {
						ui.addMsg(params[1],params[2]);
					}else if("LOGOUT".equals(params[0])){
						ui.removeChatter(params[1]);
						ui.exit();
					}else {
						throw new RuntimeException("알 수 없는 명령어: " + line);
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
		String cmd = "LOGIN|" + nickName;
		
		// BufferedWriter bw = new BufferedWriter(this.out);
		
		PrintWriter pw = new PrintWriter(this.out);  // text! encoding!
		// pw.println("감자"); // 02 32 32 12 
				
		
		// this.out.write(cmd.getBytes("UTF-8"));
		// 0..0 0..0 0..0 0..0
		pw.println(cmd); // asldkfsdalf\r\n
		pw.flush(); // 지금 빨리 다 써내려라!
		
	}

	public void sendMessage(String msg) {
		String cmd = "MSG|" + msg;
		PrintWriter pw = new PrintWriter(this.out);
		pw.println(cmd);
		pw.flush();
		
	}

	public void sendLogout() {
		// TODO Auto-generated method stub
		String cmd = "LOGOUT";
		PrintWriter pw = new PrintWriter(this.out);
		pw.println(cmd);
		pw.flush();
	}
}
