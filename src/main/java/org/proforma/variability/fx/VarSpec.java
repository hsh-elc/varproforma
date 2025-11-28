package org.proforma.variability.fx;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.proforma.variability.transfer.CV;
import org.proforma.variability.transfer.CVVp;
import org.proforma.variability.transfer.CVp;
import org.proforma.variability.transfer.V;
import org.proforma.variability.transfer.VarSpecRoot;
import org.proforma.variability.transfer.Vp;
import org.proforma.variability.util.Log;
import org.proforma.variability.util.VOptions;
import org.proforma.variability.util.VOptionsFactory;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder= {"specRoot", "defaultValue"})
public class VarSpec {
	private VarSpecRoot specRoot;
	private CV defaultValue;
	
	public VarSpec(){
		
	}

	public VarSpec(VarSpecRoot cvs, CV defaultValue) {
		this.specRoot = cvs;
		this.defaultValue = defaultValue;
	}
	
	public VarSpec(VarSpec other) {
		this.specRoot= new VarSpecRoot(other.specRoot);
		this.defaultValue= new CV(other.defaultValue);
	}

	public VarSpecRoot getSpecRoot() {
		return specRoot;
	}
	public void setSpecRoot(VarSpecRoot specRoot) {
		this.specRoot = specRoot;
	}
	public CV getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(CV defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public boolean hasData() {
		return getSpecRoot() != null;
	}
	public CVp getCVp() {
		return getSpecRoot().getEffectiveCVp();
	}
	
	
	public VOptions calculateSelectableItemsForGivenPrefix(List<V> prefix) {
		CVp effCVp= getSpecRoot().getEffectiveCVp();
		CVVp fixture= CVVp.create(prefix);
		Vp vpKey= effCVp.get(prefix.size());
		Log.debug("prefix: "+prefix);
		Log.debug("fixture: "+fixture);
		Log.debug("dimKey: "+vpKey);
		VOptions options= VOptionsFactory.create(getSpecRoot(), fixture, vpKey.getKey());
		Log.debug("  selectable '"+vpKey+"':");
		Log.debug(options.toString());
		return options;
	}
}