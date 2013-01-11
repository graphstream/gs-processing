package org.graphstream.ui.processing.data;

public class ElementData {
	
	public static enum Visibility {
		VISIBLE, HIDDEN
	}
	
	public final String id;

	public int stroke;
	public int strokeRGB;
	public float strokeA;
	
	Visibility visibility;

	public ElementData(String id) {
		this.id = id;
		
		stroke = 1;
		strokeRGB = 0xFF333333;
		strokeA = 255;
		visibility = Visibility.VISIBLE;
	}
	
	public boolean isVisible() {
		return visibility != Visibility.HIDDEN;
	}
}
