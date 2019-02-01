package githyb.sunkeun.jchat.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IO {

	// encoder
	public static void writeText(String s, OutputStream out) throws IOException {
		byte [] data = s.getBytes(); // ABCDEF 
		writeBytes(data, out);
		// 길이
//		DataOutputStream dos = new DataOutputStream(out);
//		int len = data.length; 
//		// 0..0 0..0 0..0 0..0
//		// 
//		dos.writeInt(len); // 숫자 15를 4byte로 전송
//		// 가나다라마
//		// [61, 62, 63, 64, 65, 66]
//		dos.write(data); // 문자열을 구성하는 숫자들이 전송됨
//		dos.flush();
	}
	
//	public static String readCmd (InputStream in) {
//		// byte [] cmd = new byte[8];
//	}
	
	// decoder
	public static String readText(InputStream in ) throws IOException {
		DataInputStream dis = new DataInputStream(in);
		int len = dis.readInt(); // [61 62 63 64] 00 00 00 00
		byte [] data = new byte[len];
		dis.readFully(data); // blocking!!! data 배열에 데이터 넣음
		
		String s = new String(data); // 5 숫자를 문자열로 
		return s;
	}

	public static void writeTextArray(String[] values, OutputStream out) throws IOException {
		/*
		 * [jack, Bob, CL] 
		 * 3
		 * 00 00 00 03,
		 */
		DataOutputStream dos = new DataOutputStream(out);
		dos.writeInt(values.length); // 인원수를 알려줌... 
		for (int i = 0; i < values.length; i++) {
			writeText(values[i], out);
		}
	}
	
	public static String [] readTextArray(InputStream in) throws IOException {
		DataInputStream din = new DataInputStream(in);
		int len = din.readInt(); 
		String [] names = new String[len];
		for (int i = 0; i < names.length; i++) {
			names[i] = readText(din);
		}
		return names;
	}

	public static void writeBytes(byte[] data, OutputStream out) throws IOException {

		DataOutputStream dos = new DataOutputStream(out);
		dos.writeInt(data.length);
		dos.write(data);
		
		dos.flush();
	}

	public static byte[] readBytes(InputStream in) {
		// TODO Auto-generated method stub
		DataInputStream dis = new DataInputStream(in);
		try {
			int len = dis.readInt();
			byte [] data = new byte[len];
			dis.readFully(data);
			return data;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	
}
