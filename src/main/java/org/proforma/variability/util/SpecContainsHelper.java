package org.proforma.variability.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.proforma.variability.transfer.CVVp;
import org.proforma.variability.transfer.CVp;
import org.proforma.variability.transfer.V;
import org.proforma.variability.transfer.Vis;
import org.proforma.variability.transfer.VarSpecLeafCollect;
import org.proforma.variability.transfer.VarSpecLeafCombine;
import org.proforma.variability.transfer.VarSpecLeafDerive;
import org.proforma.variability.transfer.VarSpecLeafRange;
import org.proforma.variability.transfer.VarSpecLeafRef;
import org.proforma.variability.transfer.VarSpecLeafVal;
import org.proforma.variability.transfer.VarSpecNode;
import org.proforma.variability.transfer.VarSpecNodeCollectGroup;
import org.proforma.variability.transfer.VarSpecNodeCombineGroup;
import org.proforma.variability.transfer.VarSpecNodeDef;
import org.proforma.variability.transfer.VarSpecNodeTable;
import org.proforma.variability.transfer.VarSpecRoot;
import org.proforma.variability.transfer.Vp;

public class SpecContainsHelper {


    public static boolean contains(VarSpecNode node, CVVp cvvp) {
        if (node instanceof VarSpecLeafVal) {
            return leafValContains((VarSpecLeafVal)node, cvvp);
        } else if (node instanceof VarSpecLeafCollect) {
            return leafCollectContains((VarSpecLeafCollect)node, cvvp);
        } else if (node instanceof VarSpecLeafCombine) {
            return leafCombineContains((VarSpecLeafCombine)node, cvvp);
        } else if (node instanceof VarSpecLeafRange) {
            return leafRangeContains((VarSpecLeafRange)node, cvvp);
        } else if (node instanceof VarSpecLeafRef) {
            return leafRefContains((VarSpecLeafRef)node, cvvp);
        } else if (node instanceof VarSpecNodeDef) {
            return nodeDefContains((VarSpecNodeDef)node, cvvp);
        } else if (node instanceof VarSpecNodeTable) {
            return nodeTableContains((VarSpecNodeTable)node, cvvp);
        } else if (node instanceof VarSpecNodeCollectGroup) {
            return nodeCollectGroupContains((VarSpecNodeCollectGroup)node, cvvp);
        } else if (node instanceof VarSpecNodeCombineGroup) {
            return nodeCombineGroupContains((VarSpecNodeCombineGroup)node, cvvp);
        } else if (node instanceof VarSpecRoot) {
            return rootContains((VarSpecRoot)node, cvvp);
        } else {
            throw new IllegalArgumentException("Unexpected node type "+(node == null ? null : node.getClass()));
        }
    }
    



    


    private static boolean leafValContains(VarSpecLeafVal leaf, CVVp cvvp) {
        if (!cvvp.getCVp().equals(leaf.getEffectiveCVp())) return false;
        V v= cvvp.getCV().get(0);
        if (v == null) return true; // wildcard
        return v.equals(leaf.getValue());
    }


    private static boolean leafCollectContains(VarSpecLeafCollect leaf, CVVp cvvp) {
        if (!cvvp.getCVp().equals(leaf.getEffectiveCVp())) return false;
        V v= cvvp.getCV().get(0);
        if (v == null) return true; // wildcard
        return leaf.getChoices().contains(v);
    }
    
    private static boolean leafCombineContains(VarSpecLeafCombine node, CVVp cvvp) {
        CVp myCVp= node.getEffectiveCVp();
        if (myCVp.size() != cvvp.getCVp().size()) return false;
        for (Vp vp : cvvp.getCVp()) {
            int index= myCVp.getVariationPoints().indexOf(vp);
            if (index < 0) return false;
            V v= cvvp.get(vp.getKey());
            if (v != null // wildcard 
                && !v.equals(node.getValue().get(index))) return false;
        }
        return true;
    }



    private static boolean leafRangeContains(VarSpecLeafRange leaf, CVVp cvvp) {
        if (!cvvp.getCVp().equals(leaf.getEffectiveCVp())) return false;
        Vis v= (Vis)cvvp.getCV().getVariants().get(0);
        if (v == null) return true; // wildcard
        return new VisOrRange(leaf.getFirst(), leaf.getLast(), leaf.getCount()).contains(new VisOrRange(v));
    }


    private static boolean leafRefContains(VarSpecLeafRef leaf, CVVp cvvp) {
        return contains(leaf.defNode(), cvvp);
    }


    private static boolean nodeDefContains(VarSpecNodeDef node, CVVp cvvp) {
        VarSpecNode definedNode= node.getDefinedNode();
        if (definedNode != null) 
            return contains(definedNode, cvvp);
        return false;
    }


    private static boolean nodeTableContains(VarSpecNodeTable node, CVVp cvvp) {
        VarSpecNode definedNode= node.getFirstNonDefineChild();
        if (definedNode != null) 
            return contains(definedNode, cvvp);
        return false;
    }


    private static boolean nodeCollectGroupContains(VarSpecNodeCollectGroup node, CVVp cvvp) {
        for (VarSpecNode c : node.getNonDefineChildren()) {
            if (contains(c, cvvp)) return true;
        }
        return false;
    }


    private static boolean nodeCombineGroupContains(VarSpecNodeCombineGroup node, CVVp cvvp) {
        if (node.getChildren() != null) {
            
            List<String> keysSeenSoFar= new ArrayList<>();
            for (int i=0; i<node.getChildren().size(); i++) {
                VarSpecNode c= node.getChildren().get(i);
                if (! (c instanceof VarSpecNodeDef)) {
                    if (c instanceof VarSpecLeafRef && ((VarSpecLeafRef) c).getReferencedNode() instanceof VarSpecLeafDerive) {
                        c= ((VarSpecLeafRef)c).getReferencedNode();
                    }
                    CVp cd= c.getEffectiveCVp();
                    boolean contained;
                    if (c instanceof VarSpecLeafDerive) {
                        // special handling of derive child
                        if (cd.size() != 1) throw new IllegalStateException("derive leaf should have dimension 1");
                        Map<String,Object> map= cvvp.getAsKeyOrderedMap(keysSeenSoFar);
                        contained= Derive.deriveValuesFrom(map, ((VarSpecLeafDerive)c)).contains(cvvp.get(cd.get(0).getKey()));
                    } else {
                        CVVp childCvvp= cvvp.projectToCVpLeavingNonMatchingVpsNull(cd);
                        contained= contains(c, childCvvp);
                    }
                    if (!contained) {
                        return false;
                    }
                    cd.forEach( it -> keysSeenSoFar.add(it.getKey()) );
                }
            }
        }
        return true;
    }

    private static boolean rootContains(VarSpecRoot node, CVVp cvvp) {
        VarSpecNode definedNode= node.getFirstNonDefineChild();
        if (definedNode != null) 
            return contains(definedNode, cvvp);
        return false;
    }


}
