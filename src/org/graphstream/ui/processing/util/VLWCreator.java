package org.graphstream.ui.processing.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import processing.core.PFont;

public class VLWCreator {

	int type;
	boolean smooth;
	String charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789àäâçéèêëôöùµÀÄÂÉÈËÊ&#\"'-_(){}[]%*$!?,;.:/\\€+=";

	public VLWCreator() {
		type = Font.TRUETYPE_FONT;
		smooth = true;
	}

	public void disableSmoothing() {
		smooth = false;
	}

	public void enableSmoothing() {
		smooth = true;
	}

	public void setFontType(int type) {
		this.type = type;
	}

	public void create(String input, String output)
			throws FileNotFoundException, FontFormatException, IOException {
		FileInputStream in = new FileInputStream(input);
		FileOutputStream out = new FileOutputStream(output);

		create(in, out);

		out.flush();
		out.close();
		in.close();
	}

	public void create(InputStream in, OutputStream out)
			throws FontFormatException, IOException {
		Font f = Font.createFont(type, in);
		f = f.deriveFont(Font.PLAIN, 30);

		PFont pf = new PFont(f, true, charset.toCharArray());

		pf.save(out);
	}

	public static void main(String[] args) throws Exception {
		VLWCreator c = new VLWCreator();

		c.create("src/org/graphstream/ui/processing/resource/DefaultFont.otf",
				"src/org/graphstream/ui/processing/resource/DefaultFont.vlw");
	}

}
