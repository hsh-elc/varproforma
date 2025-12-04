package proforma.varproforma.fx.sample;

import javafx.application.Application;
import javafx.stage.Stage;
import proforma.varproforma.CV;
import proforma.varproforma.VarSpecRoot;
import proforma.varproforma.Vc;
import proforma.varproforma.Vi;
import proforma.varproforma.Vp;
import proforma.varproforma.Vs;

public class Sample5 extends Application {

	@Override
	public void start(Stage primaryStage) {
        Vp vpAB= Vp.s("AB");
        Vp vpCD= Vp.s("CD");
        Vp vp12= Vp.i("12");
        Vp vpxy= Vp.c("xy");
        Vp vp34= Vp.i("34");
        Vp vpnm= Vp.c("nm");
        VarSpecRoot cvSpec= VarSpecRoot.build(vpAB, vpCD, vp12, vpxy, vp34, vpnm)
			.collectGroup()
			    .combine("A", "C", 1, 'x', 3, 'n')    .combine("A", "C", 2, 'y', 3, 'n')
			    .combine("A", "C", 1, 'x', 4, 'm')    .combine("A", "C", 2, 'y', 4, 'm') 
			    .combine("B", "D", 1, 'x', 3, 'n')    .combine("B", "D", 2, 'y', 3, 'n')
			    .combine("B", "D", 1, 'x', 4, 'm')    .combine("B", "D", 2, 'y', 4, 'm')
			.endCollectGroup()
		.endBuild();
		CV defaultValue= CV.fromValues(
		        new Vs(vpAB, "B"), 
		        new Vs(vpCD, "D"), 
		        new Vi(vp12, 2), 
		        new Vc(vpxy, 'y'), 
		        new Vi(vp34, 4), 
		        new Vc(vpnm, 'm'));
		Sample.start("Sample5", cvSpec, defaultValue, primaryStage);
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}