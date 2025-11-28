package org.proforma.variability.fx;

import java.util.Objects;

import javax.xml.bind.Unmarshaller;

import org.proforma.variability.transfer.V;
import org.proforma.variability.transfer.Vp;
import org.proforma.variability.util.Log;
import org.proforma.variability.util.VOptions;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;


public class VariabilityRowModel {
	
	private Vp vp;
	private VOptions options;   
	private ObjectProperty<V> current;
	private ObjectProperty<V> label; // this may be different from current while moving the slider
	private IntegerProperty state;
	private BooleanProperty pushed; // This signals, that a slider has been "locked in", so the current value can be published
	
	
	/**
	 * initialize default values
	 */
	public VariabilityRowModel() {
		this(null, null, null);
	}

	/**
     * Postprocessing sets parent relationships
     */
    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        this.label.set(current.get());
    }
    
	public VariabilityRowModel(Vp vp, VOptions options, V current) {
		this.vp= vp;
		this.options= options;
		this.current= new SimpleObjectProperty<>(current);
		this.label= new SimpleObjectProperty<>(current);
		this.state= new SimpleIntegerProperty(0);
		this.pushed= new SimpleBooleanProperty(false);
		updateState();
	}
	
	private void updateState() {
		int nv= Objects.hash(vp, options, current.get());
		Log.debug("updateState: old="+state.get()+" -> "+nv);
		this.state.set(nv);
	}


	public Vp getVp() {
		return vp;
	}
	public void setVp(Vp vp) {
		this.vp= vp;
		updateState();
	}
	


	public VOptions getOptions() {
		return options;
	}
	
	
	

	public V getCurrent() {
		return current.get();
	}
	public void setCurrent(V current) {
		this.current.set(current);
		this.label.set(current);
		updateState();
	}
	public ObjectProperty<V> currentProperty() {
		return current;
	}

	
	public void setOptionsAndCurrent(VOptions options, V current) {
		this.options= options;
		this.current.set(current);
        this.label.set(current);
		updateState();
	}
	
	public IntegerProperty stateProperty() {
		return state;
	}
	
	public ObjectProperty<V> labelProperty() {
		return label;
	}
	
	public BooleanProperty pushedProperty() {
	    return pushed;
	}
	
	/**
	 * This only changes the label but not the current value. This method can be used when moving the slider.
	 * @param rank
	 */
	public void setLabelToRank(long rank) {
		if (rank < 0 || this.options == null) {
			this.label.set(null);
		} else {
			V s= this.options.getVariantAtRank(rank);
			this.label.set(s);
		}
	}
	
	public void pushCurrent() {
        pushed.set(true);
        pushed.set(false);
	}
	
	@Override
	public String toString() {
		return "VariabilityRow [vp=" + vp + ", options=" + options + ", current=" + current + "]";
	}
}
