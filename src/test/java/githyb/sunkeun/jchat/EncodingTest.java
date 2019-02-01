package githyb.sunkeun.jchat;

import java.util.Arrays;

public class EncodingTest {

	public static void main(String[] args) {
		String s = "가나다라마"; //
		byte [] data = s.getBytes();
		System.out.println(s);
		System.out.println(data.length);
		System.out.println(Arrays.toString(data));
		
		// byte [] d2 = [-19, -107, -100, -22, -72, -128, -20, -98, -123, -21, -117, -120, -21, -117, -92];
		
		int ch1 = 61; // A
		int ch2 = 62;
		int ch3 = 63;
		int ch4 = 64;
		
		int v = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
		System.out.println(v);
		
		
	}
}
