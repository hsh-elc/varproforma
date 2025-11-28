package org.proforma.variability.fx.sample;

import org.proforma.variability.fx.VariabilityView;
import org.proforma.variability.transfer.CV;
import org.proforma.variability.transfer.VarSpecRoot;
import org.proforma.variability.transfer.Vc;
import org.proforma.variability.transfer.Vi;
import org.proforma.variability.transfer.V;
import org.proforma.variability.transfer.Vp;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.stage.Stage;

public class Sample6c extends Application {

	@Override
	public void start(Stage primaryStage) {
        Vp a= Vp.c("a");
        Vp b= Vp.i("b");
        VarSpecRoot cvSpec= VarSpecRoot.build(a, b)
		    .collectGroup()
  			  .combineGroup()
    		    .range('A', 'Z')
			    .range(1, 10)
			  .endCombineGroup()
  			  .combineGroup()
    		    .val('X')
			    .val(15)
			  .endCombineGroup()
			.endCollectGroup()
		.endBuild();
		CV defaultValue= CV.fromValues(new Vc(a,'A'), new Vi(b,1));
		Sample.start("Sample6c", cvSpec, defaultValue, primaryStage);
		
		VariabilityView v= (VariabilityView)primaryStage.getScene().getRoot().getChildrenUnmodifiable().get(0);
		
		Thread t= new Thread(){
			@Override public void run() {
				while (true){
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
					}
					System.out.println(v.getSelected());
				}
			}
		};
		t.start();
		
//		v.selectedProperty().addListener(new ChangeListener<ObservableList<Vp>>(){
//			@Override
//			public void changed(ObservableValue<? extends ObservableList<Vp>> observable, ObservableList<Vp> oldValue,
//					ObservableList<Vp> newValue) {
//				System.out.println("ChangeListener");
//				t.interrupt();
//			}
//			
//		});
		v.selectedProperty().addListener(new ListChangeListener<V>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends V> c) {
				System.out.println("listChangeListener");
				t.interrupt();
			}
		});
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}