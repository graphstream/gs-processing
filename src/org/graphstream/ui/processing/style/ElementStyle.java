package org.graphstream.ui.processing.style;

import org.graphstream.ui.processing.data.ElementData;

public class ElementStyle {

	int stroke;
	int[] strokeARGBs;

	boolean shouldStroke;

	public void defaults() {
		shouldStroke = true;
		stroke = 1;
		strokeARGBs = new int[] { 0xFF333333 };
	}

	public void applyTo(ElementData data) {

	}
}
