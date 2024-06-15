package com.github.datasamudaya.operator;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Utils {
	private Utils() {
		
	}
	public static String readResource(String resource) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream fstream = Utils.class.getResourceAsStream(resource);
			while (fstream.available() > 0) {
				baos.write(fstream.read());
			}
			return baos.toString();			
		} catch(Exception e) {
			
		}
		return null;
	}
	
}
