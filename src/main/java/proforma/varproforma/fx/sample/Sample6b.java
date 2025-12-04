package proforma.varproforma.fx.sample;

import javafx.application.Application;
import javafx.stage.Stage;
import proforma.varproforma.CV;
import proforma.varproforma.VarSpecRoot;
import proforma.varproforma.Vp;
import proforma.varproforma.util.SpecRandomHelper;

public class Sample6b extends Application {

	@Override
	public void start(Stage primaryStage) {
		
	    VarSpecRoot cvSpec= VarSpecRoot.build(Vp.d("a", 1E-5))
		    .collectGroup()
		    .range(0.0, Math.sqrt(2), 13L)
//  			  .combineGroup()
//    		    .range('E', 'I', 3L)
//			  .endCombineGroup()
//  			  .combineGroup()
//    		    .range('A', 'J', 4L)
//			  .endCombineGroup()
//  			  .combineGroup()
//  			  	.range('M', 'S', 4L)
//			  .endCombineGroup()
//			  .val('Y')
//  			  .combineGroup()
//    		    .val('X')
//			  .endCombineGroup()
//			  .val('Z')
//			  .val('K')
//			  .range('A', 'Z')
			.endCollectGroup()
		.endBuild();
		CV defaultValue= SpecRandomHelper.random(cvSpec).getCV();
		Sample.start("Sample6b", cvSpec, defaultValue, primaryStage);
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}