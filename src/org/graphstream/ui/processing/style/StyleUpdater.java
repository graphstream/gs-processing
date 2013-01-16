package org.graphstream.ui.processing.style;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.graphstream.stream.AttributeSink;
import org.graphstream.ui.graphicGraph.stylesheet.Rule;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleSheet;
import org.graphstream.ui.graphicGraph.stylesheet.StyleSheet.NameSpace;
import org.graphstream.ui.processing.data.DataSet;
import org.graphstream.ui.processing.data.EdgeData;
import org.graphstream.ui.processing.data.NodeData;
import org.graphstream.ui.processing.data.NodeData.Shape;

public class StyleUpdater implements AttributeSink, Observer {

	public static enum UIAction {
		HIDE, STYLE, STYLESHEET, COLOR, SIZE, CLASS, POINTS, POINTS_TYPE, LABEL, UNKNOWN, NONE
	}

	DataSet set;

	NodeStyle defaultNodeStyle;
	EdgeStyle defaultEdgeStyle;

	HashMap<String, NodeStyle> nodeClassStyle;
	HashMap<String, NodeStyle> nodeIdStyle;
	HashMap<String, EdgeStyle> edgeClassStyle;
	HashMap<String, EdgeStyle> edgeIdStyle;

	public StyleUpdater(DataSet set) {
		this.set = set;
		set.addObserver(this);

		nodeClassStyle = new HashMap<String, NodeStyle>();
		edgeClassStyle = new HashMap<String, EdgeStyle>();
		nodeIdStyle = new HashMap<String, NodeStyle>();
		edgeIdStyle = new HashMap<String, EdgeStyle>();

		defaultNodeStyle = new NodeStyle();
		defaultNodeStyle.defaults();
		defaultEdgeStyle = new EdgeStyle();
		defaultEdgeStyle.defaults();
	}

	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
		graphAttributeChanged(sourceId, timeId, attribute, null, value);
	}

	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		UIAction action = attribute2uiaction(attribute);

		if (action != UIAction.NONE) {
			switch (action) {
			case STYLESHEET:
				loadCSSStyle((String) newValue);
				break;
			default:
				break;
			}
		}
	}

	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
		UIAction action = attribute2uiaction(attribute);

		if (action != UIAction.NONE) {

		}
	}

	protected void loadCSSStyle(String stylesheet) {
		System.out.printf("* Load CSS Style\n");
		StyleSheet sheet = new StyleSheet();

		try {
			sheet.load(stylesheet);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		copy(sheet.getDefaultNodeRule(), defaultNodeStyle);
		copy(sheet.getDefaultEdgeRule(), defaultEdgeStyle);

		NameSpace ns = sheet.nodeRules;

		for (String clazz : ns.byClass.keySet()) {
			NodeStyle style = copy(ns.byClass.get(clazz), new NodeStyle());
			nodeClassStyle.put(clazz, style);
		}

		for (String id : ns.byId.keySet()) {
			NodeStyle style = copy(ns.byId.get(id), new NodeStyle());

			nodeIdStyle.put(id, style);
		}

		ns = sheet.edgeRules;

		for (String clazz : ns.byClass.keySet()) {
			EdgeStyle style = copy(ns.byClass.get(clazz), new EdgeStyle());
			edgeClassStyle.put(clazz, style);
		}

		for (String id : ns.byId.keySet()) {
			EdgeStyle style = copy(ns.byId.get(id), new EdgeStyle());
			edgeIdStyle.put(id, style);
		}

		allElementStyling();
	}

	protected NodeStyle copy(Rule rule, NodeStyle style) {
		style.fillMode = rule.style.getFillMode();

		Object p = disableParent(rule);

		if (rule.style.getFillColorCount() > 0) {
			style.fillARGBs = new int[rule.style.getFillColorCount()];

			for (int i = 0; i < rule.style.getFillColorCount(); i++)
				style.fillARGBs[i] = rule.style.getFillColor(i).getRGB();

			style.shouldFill = true;
		}

		if (rule.style.getStrokeColorCount() > 0) {
			style.strokeARGBs = new int[rule.style.getStrokeColorCount()];

			for (int i = 0; i < rule.style.getStrokeColorCount(); i++)
				style.strokeARGBs[i] = rule.style.getStrokeColor(i).getRGB();

			style.shouldStroke = true;
		}

		if (rule.style.getShape() != null) {
			switch (rule.style.getShape()) {
			case CIRCLE:
				style.shape = Shape.CIRCLE;
				break;
			case ROUNDED_BOX:
			case BOX:
				style.shape = Shape.SQUARE;
				break;
			case DIAMOND:
			case POLYGON:
			case TRIANGLE:
			default:
				style.shape = Shape.CIRCLE;
				break;
			}
			style.shouldShape = true;
		}

		restoreParent(rule, p);

		return style;
	}

	private Object disableParent(Rule rule) {
		try {
			Object r;
			Field f = Style.class.getDeclaredField("parent");
			f.setAccessible(true);
			r = f.get(rule.style);
			f.set(rule.style, null);
			return r;
		} catch (Exception e) {
			System.err.printf("some privileges are missing. ");
			System.err.printf("style may not look as it was expected.\n");
		}

		return null;
	}

	private void restoreParent(Rule rule, Object p) {
		try {
			Field f = Style.class.getDeclaredField("parent");
			f.setAccessible(true);
			f.set(rule.style, p);
		} catch (Exception e) {
			// quiet
		}
	}

	protected EdgeStyle copy(Rule rule, EdgeStyle style) {
		return style;
	}

	protected void allElementStyling() {
		for (int i = 0; i < set.getNodeDataCount(); i++)
			elementStyling(set.getNodeData(i));

		for (int i = 0; i < set.getEdgeDataCount(); i++)
			elementStyling(set.getEdgeData(i));
	}

	protected void elementStyling(NodeData data) {
		defaultNodeStyle.applyTo(data);

		if (data.uiClass != null && nodeClassStyle.containsKey(data.uiClass))
			nodeClassStyle.get(data.uiClass).applyTo(data);

		if (nodeIdStyle.containsKey(data.id))
			nodeIdStyle.get(data.id).applyTo(data);
	}

	protected void elementStyling(EdgeData data) {
		defaultEdgeStyle.applyTo(data);

		if (data.uiClass != null && edgeClassStyle.containsKey(data.uiClass))
			edgeClassStyle.get(data.uiClass).applyTo(data);

		if (edgeIdStyle.containsKey(data.id))
			edgeIdStyle.get(data.id).applyTo(data);
	}

	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		nodeAttributeChanged(sourceId, timeId, nodeId, attribute, null, value);
	}

	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		UIAction action = attribute2uiaction(attribute);

		if (action != UIAction.NONE) {
			NodeData data = set.getNodeData(nodeId);

			switch (action) {
			case HIDE:
				data.hide();
				break;
			case COLOR:
				check(newValue, Number.class);
				data.uiColor = ((Number) newValue).doubleValue();
				elementStyling(data);
				break;
			case LABEL:
				check(newValue, String.class);
				data.label = (String) newValue;
				break;
			case SIZE:
				break;
			case STYLE:
				break;
			case CLASS:
				check(newValue, String.class);
				data.uiClass = (String) newValue;
				elementStyling(data);
				break;
			default:
				break;
			}
		}
	}

	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
		UIAction action = attribute2uiaction(attribute);

		if (action != UIAction.NONE) {
			NodeData data = set.getNodeData(nodeId);

			switch (action) {
			case HIDE:
				data.show();
				break;
			case CLASS:
				data.uiClass = null;
				elementStyling(data);
				break;
			default:
				break;
			}
		}
	}

	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		edgeAttributeChanged(sourceId, timeId, edgeId, attribute, null, value);
	}

	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		UIAction action = attribute2uiaction(attribute);

		if (action != UIAction.NONE) {
			EdgeData data = set.getEdgeData(edgeId);

			switch (action) {
			case HIDE:
				data.show();
				break;
			case CLASS:
				check(newValue, String.class);
				data.uiClass = (String) newValue;
				elementStyling(data);
				break;
			case COLOR:
				check(newValue, Number.class);
				data.uiColor = ((Number) newValue).doubleValue();
				elementStyling(data);
				break;
			case LABEL:
				check(newValue, String.class);
				data.label = (String) newValue;
				break;
			case POINTS:
				// TODO
				break;
			case POINTS_TYPE:
				// TODO
				break;
			default:
				break;
			}
		}
	}

	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
		UIAction action = attribute2uiaction(attribute);

		if (action != UIAction.NONE) {
			EdgeData data = set.getEdgeData(edgeId);

			switch (action) {
			case HIDE:
				data.hide();
				break;
			case CLASS:
				data.uiClass = null;
				elementStyling(data);
				break;
			default:
				break;
			}
		}
	}

	protected void check(Object value, Class<?> expectedType) {
		
	}
	
	protected UIAction attribute2uiaction(String attribute) {
		if (attribute.charAt(0) == 'u' && attribute.charAt(1) == 'i'
				&& attribute.charAt(2) == '.') {

			attribute = attribute.substring(3).toUpperCase();
			attribute = attribute.replace('.', '_');

			try {
				return UIAction.valueOf(attribute);
			} catch (IllegalArgumentException e) {
				return UIAction.UNKNOWN;
			}
		}

		return UIAction.NONE;
	}

	public void update(Observable arg0, Object arg1) {
		if (arg0 == set) {
			if (arg1 == null)
				return;

			if (arg1 instanceof NodeData)
				elementStyling((NodeData) arg1);
			else
				elementStyling((EdgeData) arg1);
		}
	}
}
