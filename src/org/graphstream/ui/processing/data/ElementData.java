package org.graphstream.ui.processing.data;

public class ElementData {
	
	public static enum Visibility {
		VISIBLE, HIDDEN
	}
	
	public final String id;
	
	public int rgb;
	public float a;
	
	Visibility visibility;

	public ElementData(String id) {
		this.id = id;
		rgb = 0xFF222222;
		a = 255;
		visibility = Visibility.VISIBLE;
	}
	
	public boolean isVisible() {
		return visibility != Visibility.HIDDEN;
	}
}
