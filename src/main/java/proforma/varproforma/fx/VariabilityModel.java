package proforma.varproforma.fx;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import proforma.varproforma.CV;
import proforma.varproforma.V;
import proforma.varproforma.Vp;
import proforma.varproforma.util.Log;
import proforma.varproforma.util.VOptions;

public class VariabilityModel {
	
	private ListProperty<VariabilityRowModel> rows;
	private StringProperty comboBoxCssClass;
	private ObjectProperty<VarSpec> spec;
    private List<V> privateSelected;  // this list might get changed during a single user interaction multiple times at multiple indices
    private ListProperty<V> selected; // this property will only change once after all changes in "selected" took place.
	
	
	/**
	 * initialize default values
	 */
	public VariabilityModel() {
		this(new VarSpec(null,null) );
	}


	public VariabilityModel(VarSpec source) {
		this.rows= new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		this.comboBoxCssClass= new SimpleStringProperty(null);
		
        this.privateSelected= new ArrayList<>();
        this.selected= new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));

		this.spec= new SimpleObjectProperty<>(null);
		this.spec.addListener((obs, ov, nv) -> update(ov, nv));
		this.spec.set(source);
	}

	private List<VariabilityRowModel> getRows() {
		return rows.get();
	}
//	public void setRows(List<VariabilityRowModel> rows) {
//		this.rows.setAll(rows);
//	}
	ListProperty<VariabilityRowModel> rowsProperty() {
		return rows;
	}

	public VariabilityRowModel getRow(int index) {
		return getRows().get(index);
	}
	
  	public VarSpec getSpec() {
		return spec.get();
	}
	public void setSpec(VarSpec value) {
		this.spec.set(value);
	}
	public ObjectProperty<VarSpec> specProperty() {
		return spec;
	}
	
	
	public String getComboBoxCssClass() {
		return comboBoxCssClass.get();
	}
	public void setComboBoxCssClass(String comboBoxCssClass) {
		this.comboBoxCssClass.set(comboBoxCssClass);
	}
	public StringProperty combobBoxCssClassProperty() {
		return comboBoxCssClass;
	}

	/**
	 * @return a list of values currently selected by the user. The order matches the order
	 * of variation points in {@link #getSpec()}.
	 */
//	private List<V> getSelected() {
//		return selected.get();
//	}
//	private void setSelected(List<V> selected) {
//		this.selected.setAll(selected);
//	}
	public ListProperty<V> selectedProperty() {
		return selected;
	}

	public CV createCurrentChoice() {
		ArrayList<V> items= new ArrayList<>();
		for (VariabilityRowModel row : rows){
			items.add(row.getCurrent());
		}
		CV cv= new CV();
		cv.setVariants(items);
		return cv;
	}
	
	
    
	/**
	 * Each row should listen to selection changes in previous rows, in order to update
	 * it's own combobox's items and maybe it's own selection.
	 * In every row we create an instance of this class that will be added as a listener 
	 * to every previous row's {@link VariabilityRowModel#currentProperty()}.
	 */
	private class UpdateOptionsOnPreviousSelectionChanges implements ChangeListener<V> {
		private int rowIndex;
		private boolean atLeastOnePrecedingRowValueChanged;
		UpdateOptionsOnPreviousSelectionChanges(int rowIndex) {
			this.rowIndex = rowIndex;
			this.atLeastOnePrecedingRowValueChanged= false;
		}
		@Override public void changed(ObservableValue<? extends V> observable, V oldValue, V newValue) {
			try {
				Log.debug(getClass()+".changed( row="+rowIndex+", ov="+oldValue+", nv="+newValue+")");
				if (newValue == null) return; // Selection changes to null are temporary and should be ignored. Otherwise subsequent comboboxes selections get invalidated.
				VOptions newOptions= calculateSelectableItems();
				VariabilityRowModel myRow= rows.get(rowIndex); 
				V oldCurrent= myRow.getCurrent();
				Log.debug("Going to replace options in row "+rowIndex+" with "+newOptions+" (ID="+System.identityHashCode(newOptions)+") - previous current="+oldCurrent);
				V newCurrent= oldCurrent;
				boolean thisRowValueChanged= false;
				if (!newOptions.containsOption(oldCurrent) && !newOptions.isEmpty()) {
					newCurrent= newOptions.getOptionOrNeighbour(oldCurrent);
					thisRowValueChanged= !newCurrent.equals(oldCurrent);
				}
	
				boolean prevRowValueChanged= !newValue.equals(oldValue);
				
				this.atLeastOnePrecedingRowValueChanged= thisRowValueChanged || prevRowValueChanged;
				if (rowIndex > 0) this.atLeastOnePrecedingRowValueChanged |= updateOptionsListenersPerRow.get(rowIndex-1).atLeastOnePrecedingRowValueChanged;
				if (rowIndex > 1) this.atLeastOnePrecedingRowValueChanged |= updateOptionsListenersPerRow.get(rowIndex-2).atLeastOnePrecedingRowValueChanged;
				
				if (thisRowValueChanged) {
					Log.debug(getClass()+": row "+rowIndex+": setCurrent = "+newCurrent);
				} else {
					Log.debug(getClass()+": row "+rowIndex+": current unchanged = "+oldCurrent);
				}
				myRow.setOptionsAndCurrent(newOptions, newCurrent); // sets options and (maybe) current
				// ^ this will trigger change events in next row automatically, if current is new
				
				Log.debug(getClass()+": replaced options in row "+rowIndex+".");

				boolean thereAreSubsequentRows= rows.size() > rowIndex+1;
				if (!thisRowValueChanged && atLeastOnePrecedingRowValueChanged && thereAreSubsequentRows) {
					// the following row depends on this row's changes and also on all previous rows.
					// We analyze, if there were changes in previous rows since the last update ...
					
					// call subsequent rows, since these may be affected, even if this row didn't change it's value.
					Log.debug("calling next row changed: next row="+(rowIndex+1));
					updateOptionsListenersPerRow.get(rowIndex+1).changed(myRow.currentProperty(), oldCurrent, newCurrent);
				} else if (!thereAreSubsequentRows) {
				    // last row. We need to publish any changes to selected values:
                    publishSelected();
				}
			} finally {
				// prepare for next change event
				this.atLeastOnePrecedingRowValueChanged= false;
			}
		}
		VOptions calculateSelectableItems() {
			ArrayList<V> selectedItemsInPrevRows= new ArrayList<>();
			for (int prevRow= 0; prevRow < rowIndex; prevRow++) {
				selectedItemsInPrevRows.add(rows.get(prevRow).getCurrent());
			}		
			
			VOptions optionsToEnable= spec.get().calculateSelectableItemsForGivenPrefix(selectedItemsInPrevRows);
			return optionsToEnable;
		}
		
	}
	
    private class UpdatePrivateSelectedOnSelectionChanges implements ChangeListener<V> {
        private int rowIndex;
        UpdatePrivateSelectedOnSelectionChanges(int rowIndex) {
            this.rowIndex = rowIndex;
        }
        @Override
        public void changed(ObservableValue<? extends V> observable, V oldValue, V newValue) {
            if (newValue == null) return; // Selection changes to null are temporary and should be ignored. 
            //System.out.println("Setting new value '"+newValue+"' in row '"+rowIndex+"'");         
            VariabilityModel.this.privateSelected.set(rowIndex, newValue);
        }
        
    }
    
    private class PublishPrivateSelectedOnSelectionRowPush implements ChangeListener<Boolean> {
        //private int rowIndex;
        PublishPrivateSelectedOnSelectionRowPush(int rowIndex) {
            //this.rowIndex = rowIndex;
        }
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (oldValue == false && newValue == true) {
                publishSelected();
            }
        }
        
    }
    
	/**
	 * This holds the listener for every row.
	 * Even on loading a new varSpec, all old listeners will be reused for the new varSpec.
	 * This way we can reuse old rows and the current state of old rows even on varSpec changes.
	 */
	private List<UpdateOptionsOnPreviousSelectionChanges> updateOptionsListenersPerRow= new ArrayList<>();
	private List<UpdatePrivateSelectedOnSelectionChanges> updatePrivateSelectedPerRow= new ArrayList<>();
	private List<PublishPrivateSelectedOnSelectionRowPush> publishPrivateSelectedOnSelectionRowPush= new ArrayList<>();
	
	
	private void update(VarSpec oldSpec, VarSpec newSpec) {

		if (newSpec != null && newSpec.hasData()) {
		
			// unbind rows
			for (VariabilityRowModel r : rows.get()) {
				for (ChangeListener<V> listener : updateOptionsListenersPerRow) {
					r.currentProperty().removeListener(listener);
				}
                for (ChangeListener<V> listener : updatePrivateSelectedPerRow) {
                    r.currentProperty().removeListener(listener);
                }
                for (ChangeListener<Boolean> listener : publishPrivateSelectedOnSelectionRowPush) {
                    r.pushedProperty().removeListener(listener);
                }
			}
			
			rows.clear();
			List<V> selected= new ArrayList<>();
	
			for (int rowIndex= 0; rowIndex < newSpec.getCVp().size(); rowIndex++) {
				Vp vp= newSpec.getCVp().get(rowIndex);
				
				// There is a singleton listener for each row.
	    		// If there are missing listeners: add them
				while (updateOptionsListenersPerRow.size() <= rowIndex) {
					updateOptionsListenersPerRow.add(new UpdateOptionsOnPreviousSelectionChanges(updateOptionsListenersPerRow.size()));
	    		}
				// calculate once:
				VOptions options= updateOptionsListenersPerRow.get(rowIndex).calculateSelectableItems();
	    		
	    		
				Log.debug("Creating new row "+rowIndex);
	    		V newCurrent;
	    		if (options.isEmpty()) {
	    			newCurrent= null; 
	    		} else if (newSpec.getDefaultValue() == null) {
	    			newCurrent= options.getVariantAtRank(0);
	    		} else {
	    			V variant= newSpec.getDefaultValue().getVariants().get(rowIndex);
	    			if (vp.represents(variant.getClass())) {
		    			// if the new model source does not match the current selection, we use nearest neighbours:
						variant= options.getOptionOrNeighbour(variant);
	    			} else {
	    				variant= options.getVariantAtRank(0);
	    			}
					newCurrent= variant;
	    		}
	    		VariabilityRowModel newRow= new VariabilityRowModel(vp, options, newCurrent);
				selected.add(newCurrent);
	
				// this new row wants a call, when any selection changes in previous rows happen:
				if (rowIndex >= 1) {
		        	rows.get(rowIndex-1).currentProperty().addListener(updateOptionsListenersPerRow.get(rowIndex));
		        }
	
				// There is a singleton listener for each row.
	    		// If there are missing listeners: add them
				while (updatePrivateSelectedPerRow.size() <= rowIndex) {
					updatePrivateSelectedPerRow.add(new UpdatePrivateSelectedOnSelectionChanges(updatePrivateSelectedPerRow.size()));
	    		}
				newRow.currentProperty().addListener(updatePrivateSelectedPerRow.get(rowIndex));
				
				// The last row needs additional handling. When the slider is releases, the locked in current
				// value is copied over from privateSelected to selected:
				boolean isLastRow= rowIndex == newSpec.getCVp().size() - 1;
				if (isLastRow) {
	                while (publishPrivateSelectedOnSelectionRowPush.size() <= rowIndex) {
	                    publishPrivateSelectedOnSelectionRowPush.add(new PublishPrivateSelectedOnSelectionRowPush(updatePrivateSelectedPerRow.size()));
	                }
				    newRow.pushedProperty().addListener(publishPrivateSelectedOnSelectionRowPush.get(rowIndex));
				}
				
		        rows.add(newRow);
			}
			
			this.privateSelected.clear();
			this.privateSelected.addAll(selected);
			publishSelected();
		}
		
	}
	
	private void publishSelected() {
	    this.selected.setAll(privateSelected);
	}
}
