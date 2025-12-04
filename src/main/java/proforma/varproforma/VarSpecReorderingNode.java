package proforma.varproforma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({
    VarSpecNodeCollectGroup.class,
    VarSpecNodeCombineGroup.class,
    VarSpecNodeDef.class,
    VarSpecNodeTable.class})
@XmlType(name="reordering-node-type", propOrder = {"keys"})
public abstract class VarSpecReorderingNode extends VarSpecInnerNode {
	
    public VarSpecReorderingNode() {
        
    }
    
	public VarSpecReorderingNode(String ... keys) {
	    if (keys.length > 0) {
	        setCVp(createBasicChildCVpFrom(keys));
	        this.keys= new ArrayList<>();
	        this.keys.addAll(Arrays.asList(keys));
	    }
	}
	public VarSpecReorderingNode(VarSpecReorderingNode other) {
	    super(other);
		if (other.keys != null) {
		    keys= new ArrayList<>(other.keys);
		}

	}
	
	@XmlElement(name="key")
	private List<String> keys;
    


	/**
     * This is called by sub classes in the push downwards phase to complete variation points
     * at the child level.
     */
    protected void completeOwnVpsFromParent(CVp inheritedCVp) {
        
        if (keys == null || keys.isEmpty()) return;

        // If this is called after unmarshalling, then the constructor has been bypassed. So we 
        // prepare cvp now:
        if (getCVp() == null) {
            setCVp(createBasicChildCVpFrom(keys.toArray(new String[keys.size()])));
        }

        HashMap<String, Vp> relevantVps;
        
        if (this instanceof VarSpecNodeCollectGroup  || this instanceof VarSpecNodeCombineGroup) {
            // for combineGroup and collectGroup we simply inherit the parent's CVp, possibly
            // reordered:
            relevantVps= new HashMap<>();
            for (Vp vp : getParent().getEffectiveCVp()) {
                relevantVps.put(vp.getKey(), vp);
            }
        } else if (this instanceof VarSpecNodeDef) {
            // A define node might reference any vp of the whole hierarchy. So we
            // walk to the root and take that CVp as the base.
            // From the root CVp a define node might define some of the top level 
            // vps or some of the nested tables.
            CVp rootCVp= getRoot().getEffectiveCVp();
            StringBuilder msg= new StringBuilder();
            relevantVps= findVps(rootCVp, msg);
            if (relevantVps == null) {
                throw new IllegalArgumentException(msg.toString());
            }
        } else if (this instanceof VarSpecNodeTable) {
            // VtSpec gets inherited CVp, possibly reordered:
            relevantVps= new HashMap<>();
            for (Vp vp : inheritedCVp) {
                relevantVps.put(vp.getKey(), vp);
            }
        } else {
            throw new UnsupportedOperationException("Unexpected class '"+getClass()+"'");
        }

        for (String key : keys) {
            Vp parentVp= relevantVps.get(key);
            if (parentVp == null) throw new IllegalArgumentException("Unknown vp key '"+key+"'");
            int index= getCVp().indexOf(key);
            Vp myVp= getCVp().get(index);
            myVp.setType(parentVp.getType());
            myVp.setAccuracy(parentVp.getAccuracy());
            myVp.setCVp(parentVp.getCVp());
        }
        
    }
    

    private HashMap<String, Vp> findVps(CVp cvp, StringBuilder msg) {
        HashMap<String, Vp> result= new HashMap<>();
        boolean allFound= true;
        for (String key : keys) {
            boolean keyFound= false;
            for (Vp vp : cvp) {
                if (vp.getKey().equals(key)) {
                    keyFound= true;
                    result.put(key, vp);
                    break;
                }
            }
            if (!keyFound) {
                allFound= false;
                break;
            }
        }
        if (allFound) return result;
        
        msg.append("The keys ").append(keys).append(" are not a subset of ").append(cvp.keySet()).append(". ");
        
        // The keys might refer to an embedded map
        for (Vp vp : cvp) {
            if (vp.isTableType()) {
                result= findVps(vp.getCVp(), msg);
                if (result != null) return result;
            }
        }
        
        return null;
    }
    
    private static CVp createBasicChildCVpFrom(String[] keys) {
        List<Vp> vps= new ArrayList<>();
        for (String key : keys) {
            Vp vp= new Vp(key, null, null); // type and accuracy are set when receiving push...-call
            vps.add(vp);
        }
        return new CVp(vps);
    }
    
    

}
