package org.graphstream.ui.processing;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;

public class Demo {
	public static void main(String... args) {
		BarabasiAlbertGenerator gen = new BarabasiAlbertGenerator();
		ProcessingViewer viewer = new ProcessingViewer(gen);

		viewer.enableLayout();
		gen.begin();

		viewer.openFrame();

		for (int i = 0; i < 1000; i++) {
			gen.nextEvents();
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		gen.end();
	}
}
