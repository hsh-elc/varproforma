package proforma.varproforma.fx;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlendMode;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import proforma.varproforma.CV;
import proforma.varproforma.CVList;
import proforma.varproforma.V;
import proforma.varproforma.Vp;
import proforma.varproforma.util.Log;
import proforma.varproforma.util.RenderHtml;

public class VariabilityView extends TableView<VariabilityRowModel> {

	private TableColumn<VariabilityRowModel,Vp> tblVariabilityColVariable;
	private TableColumn<VariabilityRowModel,V> tblVariabilityColSelect;
	private TableColumn<VariabilityRowModel,V> tblVariabilityColValue;

	private ArrayList<VpSelectView> vpViews;
	
	private StringProperty colHeaderVariable;
	private StringProperty colHeaderSelect;
	private StringProperty colHeaderValue;
	private ObjectProperty<VarSpec> varSpec;
	private VariabilityModel model;
	
	private ReadOnlyListWrapper<V> selected;
	
	
	public VariabilityView() {
		this.model= new VariabilityModel();
		this.varSpec= new SimpleObjectProperty<>(null);
		this.colHeaderVariable= new SimpleStringProperty("Variable");
		this.colHeaderSelect= new SimpleStringProperty("Select");
		this.colHeaderValue= new SimpleStringProperty("Value");
		this.selected= new ReadOnlyListWrapper<>();
		
		tblVariabilityColVariable= new TableColumn<>();
		tblVariabilityColVariable.textProperty().bind(colHeaderVariable);
		
		this.idProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				tblVariabilityColVariable.setId(VariabilityView.this.getId()+"-col-variable");
				tblVariabilityColSelect.setId(VariabilityView.this.getId()+"-col-select");
				tblVariabilityColValue.setId(VariabilityView.this.getId()+"-col-value");
			}
		});
		tblVariabilityColVariable.setMaxWidth(Double.MAX_VALUE);
		tblVariabilityColSelect= new TableColumn<>();
		tblVariabilityColSelect.textProperty().bind(colHeaderSelect);
		tblVariabilityColValue= new TableColumn<>();
		tblVariabilityColValue.textProperty().bind(colHeaderValue);

        tblVariabilityColVariable.setSortable(false);
		tblVariabilityColSelect.setSortable(false);
		tblVariabilityColValue.setSortable(false);
		getColumns().add(tblVariabilityColVariable);
		getColumns().add(tblVariabilityColSelect);
		getColumns().add(tblVariabilityColValue);

		// distribute column width by percent:
		tblVariabilityColVariable.prefWidthProperty().bind(widthProperty().multiply(0.2));
		tblVariabilityColSelect.prefWidthProperty().bind(widthProperty().multiply(0.3));
		tblVariabilityColValue.prefWidthProperty().bind(widthProperty().subtract(tblVariabilityColVariable.widthProperty().add(tblVariabilityColSelect.widthProperty()).add(2)));
		tblVariabilityColValue.maxWidthProperty().bind(widthProperty().subtract(tblVariabilityColVariable.widthProperty().add(tblVariabilityColSelect.widthProperty()).add(2)));
		
		// cell values and factories:
		tblVariabilityColVariable.setCellValueFactory(new PropertyValueFactory<VariabilityRowModel,Vp>("vp"));
		tblVariabilityColVariable.setCellFactory( param -> new VariableCell() );

		tblVariabilityColSelect.setCellValueFactory(new PropertyValueFactory<VariabilityRowModel,V>("current"));
		tblVariabilityColSelect.setCellFactory( param -> new SelectCell() );
		
		tblVariabilityColValue.setCellValueFactory(new PropertyValueFactory<VariabilityRowModel,V>("label"));
        tblVariabilityColValue.setCellFactory( param -> new ValueCell() );
		
		// If the model is swapped, we need to listen to a different rowsProperty:
		itemsProperty().bindBidirectional(model.rowsProperty()); 
		selected.bind(model.selectedProperty()); // read selected values from model
		model.specProperty().bind(varSpec); // pass varSpec to model
		
		vpViews= new ArrayList<>();
	}

	public VarSpec getVarSpec() {
		return varSpec.get();
	}
	public void setVarSpec(VarSpec s) {
		this.varSpec.set(s);
	}
	public ObjectProperty<VarSpec> varSpecProperty() {
		return varSpec;
	}

	public String getColHeaderVariable() {
		return colHeaderVariable.get();
	}
	public void setColHeaderVariable(String s) {
		this.colHeaderVariable.set(s);
	}
	public  StringProperty colHeaderVariableProperty() {
		return colHeaderVariable;
	}

	public String getColHeaderSelect() {
		return colHeaderSelect.get();
	}
	public void setColHeaderSelect(String s) {
		this.colHeaderSelect.set(s);
	}
	public  StringProperty colHeaderSelectProperty() {
		return colHeaderSelect;
	}

	
	public String getColHeaderValue() {
		return colHeaderValue.get();
	}
	public void setColHeaderValue(String s) {
		this.colHeaderValue.set(s);
	}
	public  StringProperty colHeaderValueProperty() {
		return colHeaderValue;
	}

	/**
	 * @return the list of currently selected values. The order matches that
	 * of the variation points in {@link #getVarSpec()}.
	 */
	public ReadOnlyListProperty<V> selectedProperty() {
		return selected.getReadOnlyProperty();
	}
	public List<V> getSelected() {
		return selected.get();
	}
	
	
	private class VariableCell extends TableCell<VariabilityRowModel, Vp> {
		@Override public void updateItem(Vp item, boolean empty) {
			super.updateItem(item, empty);
        	setGraphic(null);
            if (empty || item == null  || model == null) {
            	setText(null);
            } else {
            	setText(item.getKey());
            }
		}
	}
	
	private class SelectCell extends TableCell<VariabilityRowModel, V> {
		{
			Log.debug("create SelectCell");
        	setPadding(new Insets(0,15,0,2));
		}
		@Override public void updateItem(V item, boolean empty) {
        	Log.debug(getClass()+".updateItem( row="+getIndex()+", item="+item+", empty="+empty+")");									
            super.updateItem(item, empty);
            if (empty || item == null  || model == null) {
            	setGraphic(null);
            } else {
            	int rowIndex= getIndex();
            	addVpViewsUpTo(rowIndex);
            	VpSelectView vpv= vpViews.get(rowIndex); 
            	vpv.updateBindingToRowModel();
            	vpv.setValue(item);
        		setGraphic(vpv);
            }
        }
	}
	
    private class ValueCell extends TableCell<VariabilityRowModel, V> {
        private ChangeListener<Number> widthChangeListener= null;
        @Override public void updateItem(V item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(null);
            if (empty || item == null  || model == null) {
                setText(null);
                setGraphic(null);
            } else {
                int rowIndex= getIndex();
                Vp vp= model.getRow(rowIndex).getVp();
                
                // source: https://stackoverflow.com/questions/42856471/correct-sizing-of-webview-embedded-in-tabelcell
                WebView wv= new WebView();
                wv.setBlendMode(BlendMode.DARKEN);

                setGraphic(wv);
                setText(null);
                WebEngine we= wv.getEngine();

                int maxFactor= 1, minFactor= 1;
                if (item.getValueType() == CVList.class) {
                    CVList list= (CVList)item.getValue();
                    List<CV> cvs= list.getElements();
                    if (cvs.size() > 0) {
                        maxFactor = cvs.size() * vp.getCVp().size();
                        if (maxFactor > 20) maxFactor= 20;
                        minFactor = 5;
                    }
                }
                int maxHeight= 15 * maxFactor;
                int minHeight= 15 * minFactor;

                Consumer<ValueCell> heightAdjuster= (c) -> {
                    wv.setPrefHeight(-1);
                    c.setPrefHeight(-1);
                    
                    String heightText = we.executeScript(  
//                            "window.getComputedStyle(document.documentElement, null).getPropertyValue('height')"
                            "var body = document.body,"
                            + "html = document.documentElement;"
                            + "Math.max(html.offsetHeight, body.offsetHeight, html.clientHeight, html.scrollHeight, body.scrollHeight);"
//                            + "Math.max( body.offsetHeight, "
//                            + "html.clientHeight, html.offsetHeight );"
                    ).toString();

                    double height = Double.parseDouble(heightText.replace("px", "")) + 8;
                    if (height > maxHeight) height= maxHeight;
                    if (height < minHeight) height= minHeight;
                    wv.setPrefHeight(height);
                    c.setPrefHeight(height);
                };
                we.documentProperty().addListener((obj, prev, newv) -> {
                    heightAdjuster.accept(this);
                });
                if (this.widthChangeListener != null) {
                    this.widthProperty().removeListener(this.widthChangeListener);
                }
                this.widthChangeListener= (obs, ov, nv) -> {
                    heightAdjuster.accept(this);
                };
                this.widthProperty().addListener(this.widthChangeListener);

                RenderHtml rh= new RenderHtml();
                String font= "font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', Oxygen, Ubuntu, Cantarell, 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif; font-size: 12px;"; 
                rh.addToClass(RenderHtml.CLASS_TABLE, font);
                rh.addToClass(RenderHtml.CLASS_CELL, font);
                String bg= "background-color: rgba(200, 200, 200, 0.3);";
                rh.addToClass(RenderHtml.CLASS_TH, bg);
                String border= "border-color: #ccc #bbb #ccc #bbb;";
                rh.addToClass(RenderHtml.CLASS_CELL, border);
                
                StringBuilder sb= new StringBuilder();
                sb.append("<body topmargin=0 leftmargin=0 style=\"background-color: transparent; ").append(font).append("\" id='id'>\n");
                sb.append(rh.getStyleElement("id"));
//                sb.append("<style>\n");
//                sb.append("table {\n");
//                sb.append("  margin: 2px;\n");
//                sb.append("}\n");
//                sb.append("table, th, td {\n");
//                sb.append("  font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', Oxygen, Ubuntu, Cantarell, 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif; font-size: 12px;\n");
//                sb.append("  border-style: solid;\n");
//                sb.append("  border-width: 1px;\n");
//                sb.append("  border-collapse: collapse;\n");
//                sb.append("}\n");
//                sb.append("td {\n");
//                sb.append("  vertical-align: top;\n");
//                sb.append("  text-align: left;\n");
//                sb.append("}\n");
//                sb.append("th {\n");
//                sb.append("  background-color: rgba(200, 200, 200, 0.3);\n");
//                sb.append("  vertical-align: middle;\n");
//                sb.append("  text-align: center;\n");
//                sb.append("}\n");
//                sb.append("th, td {\n");
//                sb.append("  border-color: #ccc #bbb #ccc #bbb;\n");
//                sb.append("  padding: 0px 2px 0px 2px;\n");
//                sb.append("}\n");
//                sb.append("</style>\n");
//              String s= (v == null ? V.nullToString() : v.toString().substring(2));
                sb.append(item == null ? "null" : RenderHtml.renderHtml(item, vp));
                sb.append("</body>\n");
                we.loadContent(sb.toString());
                
            }
        }
    }
    

	private void addVpViewsUpTo(int rowIndex) {
		while (vpViews.size() <= rowIndex) {
    		vpViews.add(new VpSelectView(vpViews.size(), model));
    	}
	}
}
