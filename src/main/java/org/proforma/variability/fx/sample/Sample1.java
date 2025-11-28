package org.proforma.variability.fx.sample;

import org.proforma.variability.transfer.CV;
import org.proforma.variability.transfer.VarSpecRoot;
import org.proforma.variability.transfer.Vi;
import org.proforma.variability.transfer.Vp;
import org.proforma.variability.transfer.Vs;

import javafx.application.Application;
import javafx.stage.Stage;

public class Sample1 extends Application {

	@Override
	public void start(Stage primaryStage) {
		Vp a= Vp.s("a");
		Vp b= Vp.i("b");
		
		VarSpecRoot cvSpec= VarSpecRoot.build(a, b)
			.collectGroup()
			    .combineGroup()
				    .val("A")
				    .collect(1,2,3)
			    .endCombineGroup()
			    .combine("B", 2)
			.endCollectGroup()
		.endBuild();
		CV defaultValue= CV.fromValues(new Vs(a, "A"), new Vi(b,2));
		Sample.start("Sample1", cvSpec, defaultValue, primaryStage);

	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}