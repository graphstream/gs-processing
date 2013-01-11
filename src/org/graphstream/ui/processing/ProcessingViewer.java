package org.graphstream.ui.processing;

import java.awt.BorderLayout;
import java.awt.Frame;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.Source;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.processing.data.DataSet;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

public class ProcessingViewer {
	public static enum ThreadingModel {
		GRAPH_IN_ANOTHER_THREAD, GRAPH_IN_VIEWER_THREAD
	}

	ProcessingEngine engine;
	Frame frame;
	DataSet data;
	Layout layout;
	ProxyPipe pipe;

	public ProcessingViewer(Source source) {
		this(source, ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
	}

	public ProcessingViewer(Source source, ThreadingModel threadingModel) {
		if (threadingModel == ThreadingModel.GRAPH_IN_ANOTHER_THREAD) {
			pipe = new ThreadProxyPipe(source);
			source = pipe;
		}

		data = new DataSet(source);
		engine = new ProcessingEngine(data);
		layout = new SpringBox();

		source.addSink(layout);
		layout.addAttributeSink(data.getXYZUpdater());

		engine.setLayout(layout);

		if (pipe != null)
			engine.addProxyPipeToPump(pipe);
		
		engine.init();
	}

	public Frame getFrame() {
		if (frame == null) {
			frame = new Frame();
			frame.setLayout(new BorderLayout());
			frame.add(BorderLayout.CENTER, engine);
			frame.pack();
		}

		frame.setVisible(true);
		return frame;
	}

	public static void main(String... args) throws Exception {
		BarabasiAlbertGenerator gen = new BarabasiAlbertGenerator();
		ProcessingViewer viewer = new ProcessingViewer(gen);

		gen.begin();

		viewer.getFrame();

		for (int i = 0; i < 1000; i++) {
			gen.nextEvents();
			Thread.sleep(50);
		}

		gen.end();
	}
}
