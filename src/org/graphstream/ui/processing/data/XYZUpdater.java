package org.graphstream.ui.processing.data;

import org.graphstream.stream.AttributeSink;

public class XYZUpdater implements AttributeSink {

	final DataSet set;

	float maxX, maxY, minX, minY;
	String minXOwner, minYOwner, maxXOwner, maxYOwner;
	boolean computeAreaNeeded;

	public XYZUpdater(DataSet set) {
		this.set = set;

		maxX = maxY = Float.MIN_VALUE;
		minX = minY = Float.MAX_VALUE;
		minXOwner = minYOwner = "";
		maxXOwner = maxYOwner = "";
		computeAreaNeeded = true;
	}

	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		//
		// First condition is used to reduce call to matches()
		//
		if ((attribute.charAt(0) == 'x' || attribute.charAt(0) == 'X')
				&& attribute.matches("x|y|z|xy|xyz|X|Y|Z|XY|XYZ"))
			update(nodeId, attribute, value);
	}

	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		//
		// First condition is used to reduce call to matches()
		//
		if ((attribute.charAt(0) == 'x' || attribute.charAt(0) == 'X')
				&& attribute.matches("x|y|z|xy|xyz|X|Y|Z|XY|XYZ"))
			update(nodeId, attribute, newValue);
	}

	private void update(String nodeId, String xyzKey, Object value) {
		NodeData data = set.getNodeData(nodeId);
		float tmp;
		boolean changed = false;

		if (data == null)
			return;

		xyzKey = xyzKey.toLowerCase();

		switch (xyzKey.length()) {
		case 1:
			switch (xyzKey.charAt(0)) {
			case 'x':
				tmp = (float) checkAndGetDouble(value);
				changed = changed || (data.x != tmp);
				data.x = tmp;
				break;
			case 'y':
				tmp = (float) checkAndGetDouble(value);
				changed = changed || (data.y != tmp);
				data.y = tmp;
				break;
			case 'z':
				tmp = (float) checkAndGetDouble(value);
				changed = changed || (data.z != tmp);
				data.z = tmp;
				break;
			}
			break;
		default:
			double[] xyz = checkAndGetDoubleArray(value);
			changed = changed || (data.x != xyz[0]) || (data.y != xyz[1]);
			data.x = (float) xyz[0];
			data.y = (float) xyz[1];

			if (xyz.length > 2) {
				changed = changed || (data.z != xyz[2]);
				data.z = (float) xyz[2];
			}

			break;
		}

		if (changed) {
			set.dataUpdated(data);

			if (computeAreaNeeded || minXOwner.equals(nodeId)
					|| minYOwner.equals(nodeId) || maxXOwner.equals(nodeId)
					|| maxYOwner.equals(nodeId))
				computeArea();
		}
	}

	private void computeArea() {
		if (set.getNodeDataCount() == 0) {
			maxX = maxY = 1;
			minX = minY = -1;

			minXOwner = minYOwner = "";
			maxXOwner = maxYOwner = "";

			computeAreaNeeded = true;
		} else {
			maxX = maxY = Float.MIN_VALUE;
			minX = minY = Float.MAX_VALUE;

			for (int i = 0; i < set.getNodeDataCount(); i++) {
				NodeData data = set.getNodeData(i);

				if (data.x < minX) {
					minX = data.x;
					minXOwner = data.id;
				}

				if (data.x > maxX) {
					maxX = data.x;
					maxXOwner = data.id;
				}

				if (data.y < minY) {
					minY = data.y;
					minYOwner = data.id;
				}

				if (data.y > maxY) {
					maxY = data.y;
					maxYOwner = data.id;
				}

				set.setArea(minX, minY, maxX, maxY);
				computeAreaNeeded = false;
			}
		}
	}

	private double checkAndGetDouble(Object value) {
		if (value instanceof Double)
			return (Double) value;

		if (value instanceof Number)
			return ((Number) value).doubleValue();

		throw new RuntimeException(String.format(
				"invalid xyz value with type %s", value.getClass().getName()));
	}

	private double[] checkAndGetDoubleArray(Object value) {
		double[] r = null;

		if (value instanceof double[])
			r = (double[]) value;

		if (value instanceof Double[]) {
			Double[] rO = (Double[]) value;
			r = new double[rO.length];

			for (int i = 0; i < r.length; i++)
				r[i] = rO[i];
		}

		if (value instanceof Object[]) {
			Object[] rO = (Object[]) value;
			r = new double[rO.length];

			for (int i = 0; i < r.length; i++)
				r[i] = checkAndGetDouble(rO[i]);
		}

		if (r == null)
			throw new RuntimeException(String.format(
					"invalid xyz value with type %s", value.getClass()
							.getName()));

		return r;
	}

	//
	// Not used :
	//

	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
	}

	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
	}

	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
	}

	@Override
	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
	}

	@Override
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
	}

	@Override
	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
	}

	@Override
	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
	}

}
