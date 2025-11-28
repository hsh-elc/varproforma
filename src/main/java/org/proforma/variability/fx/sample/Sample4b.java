package org.proforma.variability.fx.sample;

import java.io.IOException;
import java.util.stream.IntStream;

import javax.xml.bind.JAXBException;

import org.proforma.variability.transfer.CV;
import org.proforma.variability.transfer.VarSpecRoot;
import org.proforma.variability.transfer.VarSpecNode;
import org.proforma.variability.transfer.V;
import org.proforma.variability.transfer.Vp;

import javafx.application.Application;
import javafx.stage.Stage;

public class Sample4b extends Application {

	private static String JS_SRC;
	static {
		JS_SRC= Sample.readJavascriptResource(Sample4b.class, "4b.js");
	}

	
	@Override
	public void start(Stage primaryStage) throws JAXBException, IOException {
		
		final int vpNum= Integer.parseInt(a1);
		
		Vp[] vps= (Vp[])IntStream.range(1, vpNum+1).mapToObj(i -> Vp.i(String.format("p%03d", i))).toArray(sz -> new Vp[sz]);

		VarSpecNode node= VarSpecRoot.build(vps);
		node= node.combineGroup();
		node= node.collect(IntStream.range(1, vpNum+1).boxed().toArray(sz -> new Object[sz]));
		for (int i=2; i<=vpNum; i++) {
			node= node.deriveCollect(JS_SRC.replace("%MAX%", String.valueOf(vpNum)));
		}
		node= node.endCombineGroup();
		VarSpecRoot cvSpec= node.endBuild();
		
		Integer[] val= new Integer[vpNum];
		for (int i=0; i<vpNum; i++) val[i]= i+1;

        V[] vs= new V[val.length];
        for (int i=0; i<vs.length; i++) {
            vs[i]= V.fromValue(val[i], vps[i]);
        }

		CV defaultValue= CV.fromValues((Object[])vs);
//		Vt defaultValue= Vt.create(IntStream.range(1, dimNum+1).boxed().toArray());

		Sample.start("Sample4b", cvSpec, defaultValue, primaryStage);
	}
	
	private static String a1;
	public static void main(String[] args) {
		
		a1= args[0];
		launch(args);
	}
}