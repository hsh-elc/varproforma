package proforma.varproforma.fx.sample;

import javafx.application.Application;
import javafx.stage.Stage;
import proforma.varproforma.CV;
import proforma.varproforma.VarSpecRoot;
import proforma.varproforma.Vd;
import proforma.varproforma.Vi;
import proforma.varproforma.Vp;

public class Sample6e extends Application {

	@Override
	public void start(Stage primaryStage) {
        Vp b= Vp.i("b");
        Vp c= Vp.d("c", 1E-5);

        VarSpecRoot cvSpec= VarSpecRoot.build(b, c)
		    .collectGroup()
  			  .combineGroup()
			    .range(1, 3)
			    .range(0.0, Math.sqrt(2), 3L)
			  .endCombineGroup()
  			  .combineGroup()
			    .collect(17)
			    .val(8.88888)
			  .endCombineGroup()
			.endCollectGroup()
		.endBuild();
		CV defaultValue= CV.fromValues(new Vi(b,17), new Vd(c,8.88888));
		Sample.start("Sample6", cvSpec, defaultValue, primaryStage);
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}