package org.graphstream.ui.processing;

import org.graphstream.ui.processing.data.DataSet;

public class Camera implements XYConverter {

	DataSet data;
	ProcessingEngine processing;
	float[] padding;

	public Camera(ProcessingEngine processing) {
		this.data = processing.getDataSet();
		this.processing = processing;
		this.padding = new float[] { 50, 50, 50, 50 };
	}

	public float xToScreenX(float x) {
		float[] area = data.getArea();
		return (int) (padding[3] + (processing.width - padding[1] - padding[3])
				* (x - area[0]) / (area[2] - area[0]));
	}

	public float yToScreenY(float y) {
		float[] area = data.getArea();
		return (int) (padding[2] + (processing.height - padding[0] - padding[2])
				* (y - area[1]) / (area[3] - area[1]));
	}
}
