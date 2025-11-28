package org.proforma.variability.fx.sample;

import org.proforma.variability.transfer.CV;
import org.proforma.variability.transfer.VarSpecRoot;
import org.proforma.variability.transfer.Vi;
import org.proforma.variability.transfer.Vs;
import org.proforma.variability.transfer.Vp;

import javafx.application.Application;
import javafx.stage.Stage;

public class Sample1b extends Application {

	@Override
	public void start(Stage primaryStage) {
	    Vp a= Vp.s("a");
	    Vp b= Vp.i("b");

	    VarSpecRoot root= VarSpecRoot.build(a, b)
			.collectGroup()
			    .combineGroup()
				    .val("A")
				    .collect(0, 1, 4, 5)
			    .endCombineGroup()
			    .combine("B", 2)
			.endCollectGroup()
		.endBuild();
		CV defaultValue= CV.fromValues(new Vs(a, "B"), new Vi(b,2));
		Sample.start("Sample1b", root, defaultValue, primaryStage);
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}