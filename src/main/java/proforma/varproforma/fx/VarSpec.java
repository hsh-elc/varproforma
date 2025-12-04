package proforma.varproforma.fx;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import proforma.varproforma.CV;
import proforma.varproforma.CVVp;
import proforma.varproforma.CVp;
import proforma.varproforma.V;
import proforma.varproforma.VarSpecRoot;
import proforma.varproforma.Vp;
import proforma.varproforma.util.Log;
import proforma.varproforma.util.VOptions;
import proforma.varproforma.util.VOptionsFactory;

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