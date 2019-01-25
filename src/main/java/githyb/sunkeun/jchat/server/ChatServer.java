package githyb.sunkeun.jchat.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

	List<ClientThead> clients = new ArrayList<>();
	
	public ChatServer() {
	}
	
	void startServer() {
		try {
			// 9999번 포트에 서버 소켓을 열어놓음
			ServerSocket sock = new ServerSocket(9999);
			while ( true ) {
				System.out.println("[server] WAITING CLIENTS....");
				
				Socket client = sock.accept(); // blocking! 되어있음!!(기다리고있음)
				ClientThead cthread = new ClientThead(this, client);
				clients.add(cthread);
				cthread.start();
				
//				InputStream in = client.getInputStream();
//				OutputStream out = client.getOutputStream();
		
				
				
				// client.close();
				System.out.println("[SERVER] BYTE CLIENT!");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws InterruptedException {
		ChatServer server = new ChatServer();
		server.startServer();
		
		
	}

	public List<ClientThead> getChatters() {
		return this.clients;
	}

	public void removeClient(ClientThead clientThead) {
		if(clients.remove(clientThead)) {
			System.out.println("사용자 로그아웃 완료 :" + clientThead.nickName);
		}else {
			
		}
	}
}
