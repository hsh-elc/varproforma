package org.proforma.variability.transfer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.proforma.variability.util.Log;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="collect-group-type")
public class VarSpecNodeCollectGroup extends VarSpecReorderingNode {

	public VarSpecNodeCollectGroup() {
		
	}
	
	public VarSpecNodeCollectGroup(String ... keys) {
	    super(keys);
	}
	
	public VarSpecNodeCollectGroup(VarSpecNodeCollectGroup other) {
		super(other);
		if (other.inheritedCVp != null) {
			inheritedCVp= new CVp(other.inheritedCVp);
		}
	}
	
    @Override
    public VarSpecNodeCollectGroup clone() {
        return new VarSpecNodeCollectGroup(this);
    }

	
	@XmlTransient
	private CVp inheritedCVp;
	
	@Override
	protected void pushInheritedCVpToChildren(CVp inheritedCVp) {
		completeOwnVpsFromParent(inheritedCVp);
		this.inheritedCVp= inheritedCVp;
		Log.debug(getClass()+".pushInherited:");
		Log.debug("   inherited: "+inheritedCVp);
		Log.debug("   effective: "+getEffectiveCVp());
		if (getChildren() != null) {
			for (VarSpecNode c : getChildren()) {
				if (c instanceof VarSpecNodeDef) {
					c.pushInheritedCVpToChildren(null);
				} else {
					CVp effectiveCVp;
					if (getCVp() != null) {
						effectiveCVp= getCVp();
					} else {
						effectiveCVp= inheritedCVp;
					}
					c.pushInheritedCVpToChildren(effectiveCVp);
				}
			}
		}
		// truncate dimensions to maximum dimension
		this.inheritedCVp.setVariationPoints(new ArrayList<>( this.inheritedCVp.getVariationPoints().subList(0, dim())) );
        // subList returns a non-serializable list, so we need to copy it to a new ArrayList:
	}
	
	
	@Override
	public long sizeLowerBound() {
		long sum= 0;
		if (getChildren() != null) {
			for (VarSpecNode c : getChildren()) {
				if (! (c instanceof VarSpecNodeDef)) {
					sum += c.sizeLowerBound();
				}
			}
		}
		return sum;
	}

	@Override
	public int dim() {
		if (getChildren() != null) {
			for (VarSpecNode c : getChildren()) {
				if (! (c instanceof VarSpecNodeDef)) {
					return c.dim();
				}
			}
		}
		return 0;
	}
	
	@Override
	public CVp getEffectiveCVp() {
		if (getCVp() != null) return getCVp();
		return inheritedCVp;
	}
	
	
	@Override
	public VarSpecNode endCollectGroup() {
		getParent().validateNewChild(this);
		return getParent();
	}


	@Override
	protected void validateNewChild(VarSpecNode child) throws IllegalArgumentException {
		if (child instanceof VarSpecRoot || child instanceof VarSpecLeafDerive) {
			throw new IllegalArgumentException("Cannot use '"+child.getClass()+"' inside '"+this.getClass()+"'");
		}

		
	}


	@Override
	protected void prettyPrint(PrintStream out, String prefix) {
		String keySpec= null;
		if (getEffectiveCVp() != null) {
			List<String> keySpecList= new ArrayList<>();
			for (Vp vp : getEffectiveCVp()) keySpecList.add(vp.toString());
			keySpec= String.join(", ", keySpecList);
		}
		if (getCVp() == null) keySpec= "["+keySpec+"]";
		out.format("%scollectGroup(%s) /%s/\n", prefix, keySpec, getDebugId());
		for (VarSpecNode c : getChildren()) {
			c.prettyPrint(out, prefix+"    ");
		}
		out.format("%sendCollectGroup\n", prefix);
	}


}
