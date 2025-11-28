package org.proforma.variability.fx;


import org.proforma.variability.transfer.V;
import org.proforma.variability.util.Log;
import org.proforma.variability.util.VOptions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * <p>This class realizes a slider. While moving the slider, the current row model's labelStringProperty is
 * continuosly updated. When releasing the knob, also the current row model's currentProperty is set.</p>
 * 
 * <p>This control connects itself to the row model's currentProperty and the row model's
 * options size. The size of the slider (number of ticks) is adjusted to the current number of options. If there
 * is only one option, the slider is disabled.
 * The knob is bound to the currentProperty of the row model.</p>
 */
public class VpSelectView extends HBox {

	private Slider slider;
	
	private ChangeListener<Number> rowModelChangedListener;
	private VariabilityRowModel currentRowModel;
	private int rowIndex;
	private String sliderCssClass;
	private VariabilityModel model;
	
	private Long firstOldValue= null;

	private Long toLong(Number v) {
        return v == null? null : (long)Math.round(v.doubleValue());
	}

	private void lockSliderKnobTo(Long sliderValue) {
		if (sliderValue != null && sliderValue != firstOldValue) {
	    	firstOldValue= null;
	        // push user selection to row model's current selection property:
			V s= getCurrentRowModel().getOptions().getVariantAtRank(sliderValue);
	        getCurrentRowModel().setCurrent(s);		
		}
	}
	
	VpSelectView(int rowIndex, VariabilityModel model) {
		this.rowIndex= rowIndex;
		this.sliderCssClass= model.getComboBoxCssClass();
		this.model= model;
		
		Log.debug("VpView: row="+rowIndex+": init first time");		
    	createSlider();
    	
    	this.getChildren().add(slider);
    	this.setPadding(Insets.EMPTY);
    	this.setSpacing(0.0);
    	this.setMaxHeight(USE_COMPUTED_SIZE);
    	this.setMaxWidth(Double.MAX_VALUE);
    	
    	slider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue != null) {
					if (newValue) {
						if (firstOldValue == null) firstOldValue= toLong(slider.getValue());
					} else {
						lockSliderKnobTo(toLong(slider.getValue()));
					}
                    if (oldValue == true && newValue == false) {
                        // push selection when changing phase ends:
                        getCurrentRowModel().pushCurrent();
                    }
				}
			}
		});
    	
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			private boolean alreadyCalled = false;
            @Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(alreadyCalled) return;
                try {
                    Long nv= toLong(newValue);
                	// while changing, push the current slider position to the row model in order to trigger label updates...
                	getCurrentRowModel().setLabelToRank(nv);

                	slider.setValue(nv.doubleValue());

                	// when receiving many change events, we must compare the newValue with the value before the first of these events
                	// That value is stored in firstOldValue:
                	if (firstOldValue == null) firstOldValue= toLong(oldValue);

                    if (nv != null && !nv.equals(firstOldValue) && !slider.isValueChanging()) {
                        alreadyCalled = true;
                    	lockSliderKnobTo(nv);
                    }
                    
                    if (!slider.valueChangingProperty().get()) {
                        // push selection only, if not currently changing:
                        getCurrentRowModel().pushCurrent();
                    }
                } finally {
                	alreadyCalled = false; 
                }
            }
		});

		rowModelChangedListener= new ChangeListener<Number>() {
            private boolean alreadyCalled = false;
            @Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
	            if(alreadyCalled) return;
	            try {
	                alreadyCalled = true;
	            	V newCurrent= getCurrentRowModel().getCurrent();
	            	double max= getCurrentRowModel().getOptions().getSize() -1;
                   	Log.debug(getClass()+".rowModelChangedListener: row="+getCurrentRowModel().getVp()+": Setting slider max to "+max+", setting slider value to "+newCurrent+", rank="+getCurrentRowModel().getOptions().getRankAtVariant(newCurrent));									
	    			setMax(max);
	    			setValue(newCurrent);
	            }
	            finally {alreadyCalled = false; }
	        }
        };

	}
	
	private void createSlider() {
		slider= new Slider();
		Log.debug("row="+rowIndex+": created currentSlider with id="+System.identityHashCode(slider));		
    	slider.setShowTickMarks(true);
    	slider.setShowTickLabels(false);
    	slider.setMinorTickCount(0);
    	slider.setMajorTickUnit(1);
    	slider.setMin(0.0);
    	slider.setSnapToTicks(true);
    	slider.setBlockIncrement(1);
		if (sliderCssClass != null) {
			slider.getStyleClass().add(sliderCssClass);
		}
		slider.setMaxWidth(Double.MAX_VALUE);
    	setHgrow(slider, Priority.ALWAYS);
	}
	
	void setMax(double max) {
		slider.setMax(max);
		slider.setDisable(max == 0.0);
	}
	void setValue(V newValue) {
		VOptions options= getCurrentRowModel().getOptions();
        long rank= options.getRankAtVariant(newValue);
        Log.debug("row="+getCurrentRowModel().getVp()+": rankAtVariant: "+rank);									
        slider.setValue(rank);
	}

	private VariabilityRowModel getCurrentRowModel() {
		return model.getRow(rowIndex);
	}
	
	/**
	 * If the row model was replaced, we need to update listeners ...
	 */
	void updateBindingToRowModel() {
    	VariabilityRowModel newRowModel= model.getRow(rowIndex);
    	if (currentRowModel != newRowModel) {
			if (currentRowModel != null) {
				// remove old bindings
				currentRowModel.stateProperty().removeListener(rowModelChangedListener);
			}
			Log.debug("row="+rowIndex+": updateBindingToRowModel -> neu row model");		
			currentRowModel= newRowModel;
			currentRowModel.stateProperty().addListener(rowModelChangedListener);
			setMax(currentRowModel.getOptions().getSize() - 1);
    	}
	}
}
