package org.proforma.variability.transfer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.proforma.variability.util.Log;




@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="derive-type", propOrder= {"aggregateType", "jsSource"})
public class VarSpecLeafDerive extends VarSpecLeaf {
	
	@XmlAttribute(required = true, name="aggregate-type")
	private DerivativeAggregateType aggregateType;
	
	@XmlElement(required = true, name="js-source")
	private String jsSource;
	


	@XmlTransient
	private CVp inheritedCVp;

	
	public DerivativeAggregateType getAggregateType() {
		return aggregateType;
	}


	public void setAggregateType(DerivativeAggregateType aggregateType) {
		this.aggregateType = aggregateType;
	}


	public String getJsSource() {
		return jsSource;
	}

	public void setJsSource(String jsSource) {
		this.jsSource = jsSource;
	}

	public VarSpecLeafDerive() {
		
	}

	public VarSpecLeafDerive(DerivativeAggregateType aggregateType, String jsSource) {
		setAggregateType(aggregateType);
		setJsSource(jsSource);
	}
	
	public VarSpecLeafDerive(VarSpecLeafDerive other) {
		super(other);
		if (other.inheritedCVp != null) {
			inheritedCVp= other.inheritedCVp.clone();
		}
		aggregateType= other.aggregateType;
		jsSource= other.jsSource;
	}
	
    @Override
    public VarSpecLeafDerive clone() {
        return new VarSpecLeafDerive(this);
    }
	
    @Override
	protected void pushInheritedCVpToChildren(CVp inheritedCVp) {
        // subList returns a non-serializable list, so we need to copy it to a new ArrayList:
		this.inheritedCVp= new CVp(new ArrayList<>(inheritedCVp.getVariationPoints().subList(0, dim())));
		Log.debug(getClass()+".pushInherited:");
		Log.debug("   inherited: "+inheritedCVp);
		Log.debug("   effective: "+getEffectiveCVp());
	}
	
	@Override
	public long sizeLowerBound() {
		if (aggregateType.equals(DerivativeAggregateType.VALUE)) return 1;
		return 0;  // empty list or range is possible.
	}
	
	@Override
	public int dim() {
		return 1;
	}
	
	
	@Override
	public CVp getEffectiveCVp() {
		return inheritedCVp;
	}

    
	
	@Override
	protected void validateNewChild(VarSpecNode child) throws IllegalArgumentException {
		throw new IllegalArgumentException("Cannot use '"+child.getClass()+"' inside '"+this.getClass()+"'");
	}
	
	@Override
	protected void prettyPrint(PrintStream out, String prefix) {
		out.format("%sderive([%s] /%s/ -> %n", prefix, getEffectiveCVp() == null ? null : getEffectiveCVp().get(0), getDebugId());
		out.format("%s    aggregateType=%s, javascript=%n",  prefix, aggregateType);
		try (Scanner sc= new Scanner(jsSource)) {
    		for (; sc.hasNextLine(); ) {
    			out.format("%s        %s%n", prefix, sc.nextLine());
    		}
		}
		out.format("%sendDerive\n", prefix);
	}
	
	
}
