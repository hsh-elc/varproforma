package proforma.varproforma.fx.sample;

import javafx.application.Application;
import javafx.stage.Stage;
import proforma.varproforma.CV;
import proforma.varproforma.VarSpecRoot;
import proforma.varproforma.Vp;
import proforma.varproforma.util.SpecRandomHelper;

public class Sample6 extends Application {

	@Override
	public void start(Stage primaryStage) {
		
	    VarSpecRoot cvSpec= VarSpecRoot.build(Vp.c("a"), Vp.i("b"), Vp.d("c", 1E-5))
		    .collectGroup()
  			  .combineGroup()
    		    .range('A', 'Z')
			    .range(1, 10)
			    .range(0.0, Math.sqrt(2), 13L)
			  .endCombineGroup()
  			  .combineGroup()
    		    .range('D', 'H', 3L)
			    .range(1, 7, 3L)
			    .range(1.0, Math.sqrt(3))
			  .endCombineGroup()
  			  .combineGroup()
    		    .val('X')
			    .collect(17)
			    .val(8.88888)
			  .endCombineGroup()
			.endCollectGroup()
		.endBuild();
		CV defaultValue= SpecRandomHelper.random(cvSpec).getCV();
		Sample.start("Sample6", cvSpec, defaultValue, primaryStage);
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}