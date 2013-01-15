package org.graphstream.ui.processing.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;

import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.Source;
import org.graphstream.ui.processing.style.StyleUpdater;

public class DataSet extends Observable implements ElementSink {

	public static final int INITIAL_SIZE = 100;
	public static final int GROW_STEP = 100;

	HashMap<String, Integer> nMapping;
	NodeData[] nodes;
	int nIndex;

	HashMap<String, Integer> eMapping;
	EdgeData[] edges;
	int eIndex;

	XYZUpdater xyzUpdater;
	StyleUpdater styleUpdater;

	final float[] area;

	public DataSet(Source source) {
		nMapping = new HashMap<String, Integer>();
		nodes = new NodeData[INITIAL_SIZE];
		nIndex = 0;

		eMapping = new HashMap<String, Integer>();
		edges = new EdgeData[INITIAL_SIZE];
		eIndex = 0;

		xyzUpdater = new XYZUpdater(this);
		styleUpdater = new StyleUpdater(this);

		area = new float[] { -1, -1, 1, 1 };

		source.addElementSink(this);
		source.addAttributeSink(xyzUpdater);
		source.addAttributeSink(styleUpdater);
	}

	public AttributeSink getXYZUpdater() {
		return xyzUpdater;
	}

	public void setArea(float minX, float minY, float maxX, float maxY) {
		if (minX != area[0] || minY != area[1] || maxX != area[2] || maxY != area[3]) {
			area[0] = minX;
			area[1] = minY;
			area[2] = maxX;
			area[3] = maxY;
			
			setChanged();
			notifyObservers();
		}
	}

	public float[] getArea() {
		return area;
	}
	
	public int indexOfNode(String nodeId) {
		Integer idx = nMapping.get(nodeId);

		if (idx == null)
			return -1;

		return idx;
	}

	public NodeData getNodeData(String nodeId) {
		int idx = indexOfNode(nodeId);

		if (idx < 0)
			return null;

		return nodes[idx];
	}

	public NodeData getNodeData(int idx) {
		return nodes[idx];
	}

	public int getNodeDataCount() {
		return nIndex;
	}

	public int indexOfEdge(String edgeId) {
		Integer idx = eMapping.get(edgeId);

		if (idx == null)
			return -1;

		return idx;
	}

	public EdgeData getEdgeData(String edgeId) {
		int idx = indexOfEdge(edgeId);

		if (idx < 0)
			return null;

		return edges[idx];
	}

	public EdgeData getEdgeData(int idx) {
		return edges[idx];
	}

	public int getEdgeDataCount() {
		return eIndex;
	}

	public void dataUpdated(ElementData data) {
		setChanged();
		notifyObservers(data);
	}

	@Override
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		if (nIndex >= nodes.length)
			nodes = Arrays.copyOf(nodes, nodes.length + GROW_STEP);

		nMapping.put(nodeId, nIndex);
		nodes[nIndex++] = new NodeData(nodeId);

		setChanged();
		notifyObservers(nodes[nIndex - 1]);
	}

	@Override
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		int nodeIndex = indexOfNode(nodeId);

		if (nodeIndex >= 0) {
			nIndex--;

			if (nodeIndex < nIndex - 1) {
				nodes[nodeIndex] = nodes[nIndex];
				nMapping.put(nodes[nodeIndex].id, nodeIndex);
			}

			nodes[nIndex] = null;
			setChanged();
		}

		notifyObservers();
	}

	@Override
	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		if (eIndex >= edges.length)
			edges = Arrays.copyOf(edges, edges.length + GROW_STEP);

		NodeData src = getNodeData(fromNodeId);
		NodeData trg = getNodeData(toNodeId);

		eMapping.put(edgeId, eIndex);
		edges[eIndex++] = new EdgeData(edgeId, src, trg, directed);

		setChanged();
		notifyObservers(edges[eIndex - 1]);
	}

	@Override
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		int edgeIndex = indexOfNode(edgeId);

		if (edgeIndex >= 0) {
			eIndex--;

			if (edgeIndex < eIndex - 1) {
				edges[edgeIndex] = edges[eIndex];
				eMapping.put(edges[edgeIndex].id, edgeIndex);
			}

			edges[eIndex] = null;
			setChanged();
		}

		notifyObservers();
	}

	@Override
	public void graphCleared(String sourceId, long timeId) {
		Arrays.fill(nodes, null);
		nIndex = 0;
		nMapping.clear();

		Arrays.fill(edges, null);
		eIndex = 0;
		eMapping.clear();

		setChanged();
		notifyObservers();
	}

	@Override
	public void stepBegins(String sourceId, long timeId, double step) {
	}
}
