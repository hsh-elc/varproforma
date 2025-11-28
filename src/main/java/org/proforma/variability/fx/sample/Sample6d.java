package org.proforma.variability.fx.sample;

import org.proforma.variability.fx.VarSpec;
import org.proforma.variability.fx.VariabilityView;
import org.proforma.variability.transfer.CV;
import org.proforma.variability.transfer.VarSpecRoot;
import org.proforma.variability.transfer.Vc;
import org.proforma.variability.transfer.Vd;
import org.proforma.variability.transfer.Vi;
import org.proforma.variability.transfer.Vp;

import javafx.application.Application;
import javafx.stage.Stage;

public class Sample6d extends Application {

	@Override
	public void start(Stage primaryStage) {
        Vp a= Vp.c("a");
        Vp b= Vp.i("b");
        Vp c= Vp.d("c", 1E-5);
        VarSpecRoot cvSpec1= VarSpecRoot.build(a, b, c)
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
		CV defaultValue= CV.fromValues(
		        new Vc(a,'C'), new Vi(b,6), new Vd(c,Math.sqrt(2)/2));
		Sample.start("Sample6d", cvSpec1, defaultValue, primaryStage);

	
		VarSpecRoot cvSpec2= VarSpecRoot.build(Vp.c("a"), Vp.i("b"), Vp.d("c", 1E-5))
			    .collectGroup()
	  			  .combineGroup()
	    		    .range('A', 'Z')
				    .range(1, 9, 5L)
				    .range(0.0, Math.sqrt(2), 13L)
				  .endCombineGroup()
				.endCollectGroup()
			.endBuild();

		new Thread(){
			@Override public void run() {
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				VariabilityView v= (VariabilityView)primaryStage.getScene().getRoot().getChildrenUnmodifiable().get(0);
				v.setVarSpec(new VarSpec(cvSpec2, defaultValue));
			}
		}.start();
		
	
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}