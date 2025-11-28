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
@XmlType(name="table-type")
public class VarSpecNodeTable extends VarSpecReorderingNode {
	

    public VarSpecNodeTable(){
        
    }
    public VarSpecNodeTable(String ... keys) {
        super(keys);
    }
    
    public VarSpecNodeTable(VarSpecNodeTable other) {
        super(other);
        if (other.inheritedCVp != null) {
            inheritedCVp= new CVp(other.inheritedCVp);
        }
    }
    
    @Override
    public VarSpecNodeTable clone() {
        return new VarSpecNodeTable(this);
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
        this.inheritedCVp.setVariationPoints(new ArrayList<>( this.inheritedCVp.getVariationPoints().subList(0, dim())));
        // subList returns a non-serializable list, so we need to copy it to a new ArrayList:
    }
    
    @Override
    public long sizeLowerBound() {
        if (getChildren() != null) {
            for (VarSpecNode c : getChildren()) {
                if (! (c instanceof VarSpecNodeDef)) {
                    return c.sizeLowerBound();
                }
            }
        }
        return 0;
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
    

    
    

    public static VarSpecNodeTable buildTable(String ... keys) {
        VarSpecNodeTable cvs= new VarSpecNodeTable(keys);
        cvs.setChildren(new ArrayList<>());
        return cvs;
    }
    
    @Override
    public VarSpecNodeTable endBuildTable() {
        return this;
    }

    @Override
    protected void validateNewChild(VarSpecNode child) throws IllegalArgumentException {
        if (child instanceof VarSpecRoot || child instanceof VarSpecLeafDerive) {
            throw new IllegalArgumentException("Cannot use '"+child.getClass()+"' inside '"+this.getClass()+"'");
        } else if (numNonDefineChildren() > 1) {
            throw new IllegalArgumentException("Cannot use more than one nested construct (except define) inside '"+this.getClass()+"'");
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
        out.format("%sbuildTable(%s) /%s/\n", prefix, keySpec, getDebugId());
        for (VarSpecNode c : getChildren()) {
            c.prettyPrint(out, prefix+"    ");
        }
        out.format("%sendBuildTable\n", prefix);
    }


}
