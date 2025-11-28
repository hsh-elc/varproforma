package org.proforma.variability.transfer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;




@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="root-type", propOrder = {"cvp"})
@XmlRootElement(name="root")
public class VarSpecRoot extends VarSpecInnerNode {
	
    @XmlElement(name = "cvp")
    private CVp cvp;


	public VarSpecRoot(){
		
	}
    
	public VarSpecRoot(VarSpecRoot other) {
		super(other);
        if (other.cvp != null) {
            cvp= other.cvp.clone();
        }
	}
	
	   
    @Override
    public VarSpecRoot clone() {
        return new VarSpecRoot(this);
    }
        
    @Override
    protected void pushInheritedCVpToChildren(CVp dummy) {
        for (VarSpecNode c : getChildren()) {
            if (c instanceof VarSpecNodeDef) {
                c.pushInheritedCVpToChildren(null);
            } else {
                c.pushInheritedCVpToChildren(cvp);
                break;
            }
        }
    }
    
        

	/**
     * @return list of variation points.
     * @exception UnsupportedOperationException if this is a {@link VarSpecLeafRef} or
     * {@link VarSpecLeafCombine}.
     */
	@Override
    public CVp getCVp() {
        return cvp;
    }

    /**
     * @param cvp list of variation points
     * @exception UnsupportedOperationException if this is a {@link VarSpecLeafRef} or
     * {@link VarSpecLeafCombine}.
     */
    @Override
    public void setCVp(CVp cvp) {
        super.setCVp(cvp);
        this.cvp = cvp;
    }


    @Override
    public void setChildren(List<VarSpecNode> children) {
        if (children != null) {
            int cnt= 0;
            for (VarSpecNode c : children) {
                if (! (c instanceof VarSpecNodeDef)) {
                    cnt++;
                    if (cnt > 1) {
                        throw new IllegalArgumentException(this.getClass()+" must have exactly one non-define child");
                    }
                }
            }
        }
        super.setChildren(children);
    }

    
    @Override
    public long sizeLowerBound() {
        for (VarSpecNode c : getChildren()) {
            if (! (c instanceof VarSpecNodeDef)) {
                return c.sizeLowerBound();
            }
        }
        return 0;
    }
    @Override
    public int dim() {
        for (VarSpecNode c : getChildren()) {
            if (! (c instanceof VarSpecNodeDef)) {
                return c.dim();
            }
        }
        return 0;
    }


    @Override
    public CVp getEffectiveCVp() {
        return getCVp();
    }

    
	public static VarSpecRoot build(Vp ... vps) {
		VarSpecRoot cvs= new VarSpecRoot();
		cvs.setCVp(new CVp(Arrays.asList(vps)));
		cvs.setChildren(new ArrayList<>());
		return cvs;
	}
	
	public VarSpecRoot endBuild() {
	    collectAndValidateAllDefsAndRefs();
		pushInheritedCVpToChildren(null);
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
        if (getCVp() != null) {
            List<String> keySpecList= new ArrayList<>();
            for (Vp vp : getCVp()) keySpecList.add(vp.toString());
            keySpec= String.join(", ", keySpecList);
        }
        out.format("%sbuild(%s) /%s/\n", prefix, keySpec, getDebugId());
        for (VarSpecNode c : getChildren()) {
            c.prettyPrint(out, prefix+"    ");
        }
        out.format("%sendBuild%n", prefix);
    }

    public String prettyPrintToString() {
        ByteArrayOutputStream baos= null;
        PrintStream ps= null;
        try {
            baos= new ByteArrayOutputStream();
            ps= new PrintStream(baos, true, StandardCharsets.UTF_8.name());
            prettyPrint(ps);
            ps.flush();
            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        } catch (UnsupportedEncodingException e) {
            // should never occur
        } finally {
            if (ps!= null) ps.close();
            if (baos!= null)
                try {
                    baos.close();
                } catch (IOException e) {
                }
        }
        return null;
    }


}
