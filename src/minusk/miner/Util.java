package minusk.miner;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by MinusKelvin on 2015-07-11.
 */
public class Util {
	public static String read(InputStream input) {
		BufferedInputStream in = new BufferedInputStream(input);
		StringBuilder str = new StringBuilder();

		try {
			int c;
			while ((c = in.read()) != -1)
				str.append((char) c);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return str.toString();
	}
}
