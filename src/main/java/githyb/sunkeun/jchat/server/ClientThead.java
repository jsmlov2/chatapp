package githyb.sunkeun.jchat.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import githyb.sunkeun.jchat.common.IO;

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
	
//		Reader r= new InputStreamReader(this.in); // 023 023 02 -> '감', '자'
//		BufferedReader br = new BufferedReader(r); // 줄바꿈 단위로 문자열을 잘라줌!
		
		while ( true) {
			String cmd ;
			try {
//				line =  br.readLine(); // LOGIN|NICK....
//				String [] params = line.split("\\|"); // LOGIN, mynick
				cmd = IO.readText(in);
//				String [] params = new String[1];
//				params[0] = line ;
				String [] params = {}; /// 일단 오류 안나게 넣음 !
				
				if ( "LOGIN".equals(cmd)) {
					// 새로운 채팅 참여자
					String nickName = IO.readText(in);
					this.nickName = nickName;
					notifyNewChatter(this);
					sendChatters();
				} else if ( "LOGOUT".equals(cmd)) {
					break;
				} else if ( "MSG".equals(cmd)) {
					String msg = IO.readText(in);
					broadcastMsg(nickName, msg);	
					//String data = params[1];
				} else if( "FILE".equals(cmd)) {
					String file = IO.readText(in); //file 이름
					byte [] data = IO.readBytes(in);
					broadcastFile(this.nickName, file, data);
				} else{
					throw new RuntimeException("알 수 없는 명령어: " + cmd);
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		} // end while
		
		server.removeClient(this);
		broadCastUser(nickName);
		
	}
	void broadcastFile(String sender, String fileName, byte[] data) {
		
		List<ClientThead> clients = server.getChatters();
		for (ClientThead clientThead : clients) {
			if ( clientThead.nickName.equals(sender)) {
				continue;
			}
			clientThead.sendFile(sender, fileName, data);
		}
	}
	void sendFile(String sender, String fileName, byte[] data) {
		/*
		 * File
		 * sender
		 * filename
		 * data.....
		 * 
		 */
		try {
			IO.writeText("FILE", out);
			IO.writeText(sender, out);
			IO.writeText(fileName, out);
			IO.writeBytes(data, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
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
		try {
			IO.writeText("LOGOUT", out);
			IO.writeText(nickName2, out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		String cmd = "LOGOUT|" + nickName2;
//		PrintWriter pw = new PrintWriter(this.out);
//		pw.println(cmd);
//		pw.flush();
//		
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
    	try {
			IO.writeText("MSG", out);
			IO.writeText(sender, out);
			IO.writeText(msg, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
//    	String cmd = "MSG|" + sender +"|" + msg;
//		PrintWriter pw = new PrintWriter(this.out);
//		pw.println(cmd);
//		pw.flush();
    	
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
//		String cmd = "JOIN|" + joiner;
//		PrintWriter pw = new PrintWriter(this.out);
//		pw.println(cmd);
//		pw.flush();
		try {
			IO.writeText("JOIN", out);
			IO.writeText(joiner, out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	void sendChatters() {
		List<ClientThead> clients = server.getChatters(); // [ ..., .., ]
		String [] nicknames = new String[clients.size()];
		for (int i = 0; i < nicknames.length; i++) {
			nicknames[i] = clients.get(i).nickName;
		}
		try {
			IO.writeText("CHATTERS", out);
			IO.writeTextArray(nicknames, out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
