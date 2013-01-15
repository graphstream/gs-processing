package org.graphstream.ui.processing.data;

import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.VisibilityMode;

public class ElementData {
	
	public final String id;

	public int stroke;
	public int strokeARGB;
	
	VisibilityMode visibility;
	
	public String uiClass;

	public ElementData(String id) {
		this.id = id;
		
		stroke = 1;
		strokeARGB = 0xFF333333;
		visibility = VisibilityMode.NORMAL;
	}
	
	public boolean isVisible() {
		return visibility != VisibilityMode.HIDDEN;
	}
	
	public void hide() {
		visibility = VisibilityMode.HIDDEN;
	}
	
	public void show() {
		visibility = VisibilityMode.NORMAL;
	}
}
