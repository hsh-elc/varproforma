package proforma.varproforma.fx.sample;

import java.io.InputStream;
import java.util.Scanner;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import proforma.varproforma.CV;
import proforma.varproforma.VarSpecRoot;
import proforma.varproforma.fx.VarSpec;
import proforma.varproforma.fx.VariabilityView;

class Sample  {

	static String readJavascriptResource(Class<?> clazz, String resource) {
		StringBuilder sb= new StringBuilder();
		InputStream is= null;
		Scanner sc= null;
		try {
			is= clazz.getResourceAsStream(resource);
			if (is == null) throw new AssertionError("Fatal error: cannot find resource '"+resource+"'");
			sc= new Scanner(is);
			while (sc.hasNextLine()) {
				sb.append(sc.nextLine()).append("\n");
			}
		} finally {
			try {
				if (is != null) is.close();
				if (sc != null) sc.close();
			} catch (Exception e) {}
		}
		return sb.toString();
	}
	
	static void start(String title, VarSpecRoot root, CV defaultValue, Stage primaryStage) {
		VariabilityView view= new VariabilityView();
		view.setPrefWidth(400);
		view.setPrefHeight(300);
		view.setMinWidth(Region.USE_COMPUTED_SIZE);
		view.setMinHeight(Region.USE_COMPUTED_SIZE);
		view.setMaxWidth(Double.MAX_VALUE);
		view.setMaxHeight(Double.MAX_VALUE);
		
		VarSpec spec= new VarSpec(root, defaultValue);
		view.setVarSpec(spec);
		view.setPadding(new Insets(0));
		

		VBox pane= new VBox(view);
		pane.setStyle("	-fx-border-color: darkgray;"
				+  "-fx-border-width: 2;"
				+  "-fx-border-insets: 4 4 4 4;"
				+  "-fx-border-radius: 5;"
				+  "-fx-padding: 7;"
				+  "-fx-margin: 20;"
				+  "-fx-background-color: white;"
			    +  "-fx-background-radius: 10;");
		pane.setPrefWidth(Region.USE_COMPUTED_SIZE);
		pane.setPrefHeight(Region.USE_COMPUTED_SIZE);
		pane.setMinWidth(Region.USE_COMPUTED_SIZE);
		pane.setMinHeight(Region.USE_COMPUTED_SIZE);
		pane.setMaxWidth(Double.MAX_VALUE);
		pane.setMaxHeight(Double.MAX_VALUE);
		pane.setFillWidth(true);

		VBox.setVgrow(view, Priority.ALWAYS);

		Scene scene = new Scene(pane);
		primaryStage.setTitle(title);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
	}
	
}