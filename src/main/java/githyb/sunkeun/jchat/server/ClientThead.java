package githyb.sunkeun.jchat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.util.List;

public class ClientThead extends Thread{

	Socket sock;
	InputStream in;
	OutputStream out;
	String nickName ;
	ChatServer server;
	
	public ClientThead(ChatServer server, Socket s) throws IOException {
		this.server = server;
		this.sock =s;
		this.in = sock.getInputStream(); // 여기다가 쓰면 수화기 
		this.out = sock.getOutputStream(); // 마이크
	}
	@Override
	public void run() {
	
		Reader r= new InputStreamReader(this.in); // 023 023 02 -> '감', '자'
		BufferedReader br = new BufferedReader(r); // 줄바꿈 단위로 문자열을 잘라줌!
		
		while ( true) {
			String line ;
			try {
				line = br.readLine(); // LOGIN|NICK.... 
				String [] params = line.split("\\|"); // LOGIN, mynick
				
				if ( "LOGIN".equals(params[0])) {
					// 새로운 채팅 참여자
					String nickName = params[1];
					this.nickName = nickName;
					notifyNewChatter(this);
					sendChatters();
				} else if ( "LOGOUT".equals(params[0])) {
					break;
				} else if ( "MSG".equals(params[0])) {
					String data = params[1];
					broadcastMsg(nickName, data);	
				}else {
					throw new RuntimeException("알 수 없는 명령어: " + line);
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		} // end while
		
		server.removeClient(this);
		broadCastUser(nickName);
		
	}
	private void broadCastUser(String nickName2) {
		// TODO Auto-generated method stub
		List<ClientThead> clients = server.getChatters();
		for (ClientThead clientThead : clients) {
			clientThead.sendUserLogout(nickName2);
		}
		
	}
	private void sendUserLogout(String nickName2) {
		// TODO Auto-generated method stub
		String cmd = "LOGOUT|" + nickName2;
		PrintWriter pw = new PrintWriter(this.out);
		pw.println(cmd);
		pw.flush();
		
	}
	/*
	 * broadcastXXXX 
	 */
     void broadcastMsg(String sender, String data) {
		// TODO Auto-generated method stub
    	 List<ClientThead> clients = server.getChatters();
    	 for (ClientThead clientThead : clients) {
			clientThead.sendMessage(sender, data);
		}
	}
     
    void sendMessage (String sender, String msg) {
    	// MSG|sender|mesg
    	String cmd = "MSG|" + sender +"|" + msg;
		PrintWriter pw = new PrintWriter(this.out);
		pw.println(cmd);
		pw.flush();
    	
    }
	protected void notifyNewChatter(ClientThead joiner) {
		// 
		List<ClientThead> clients = server.getChatters(); // [ ...,  ]
		for(int i=0;i<clients.size();i++) {
			ClientThead t = clients.get(i);
			if ( t != joiner) {
				t.notifyJoiner(joiner.nickName);
			}
			
		}
	}
	void notifyJoiner(String joiner) {
		String cmd = "JOIN|" + joiner;
		PrintWriter pw = new PrintWriter(this.out);
		pw.println(cmd);
		pw.flush();
		
	}
	void sendChatters() {
		List<ClientThead> clients = server.getChatters(); // [ ..., .., ]
		// CHATTERS|dkdkd,sss,ccc,
		String cmd = "CHATTERS|";
		for(int i=0;i<clients.size();i++) {
			if(i==0) {
				cmd +=  clients.get(i).nickName ;
			}else {
				cmd += "," + clients.get(i).nickName;
			}
		}
		PrintWriter pw = new PrintWriter(this.out);
		pw.println(cmd);
		pw.flush();
		
		
		
	}
}
