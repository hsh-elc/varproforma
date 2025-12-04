package proforma.varproforma.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import proforma.varproforma.CV;
import proforma.varproforma.CVVp;
import proforma.varproforma.CVp;
import proforma.varproforma.V;
import proforma.varproforma.VarSpecLeafCollect;
import proforma.varproforma.VarSpecLeafCombine;
import proforma.varproforma.VarSpecLeafDerive;
import proforma.varproforma.VarSpecLeafRange;
import proforma.varproforma.VarSpecLeafRef;
import proforma.varproforma.VarSpecLeafVal;
import proforma.varproforma.VarSpecNode;
import proforma.varproforma.VarSpecNodeCollectGroup;
import proforma.varproforma.VarSpecNodeCombineGroup;
import proforma.varproforma.VarSpecNodeDef;
import proforma.varproforma.VarSpecNodeTable;
import proforma.varproforma.VarSpecRoot;
import proforma.varproforma.Vp;

public class SpecRandomHelper {

    private static Random random= new Random();
    
    public static CVVp random(VarSpecNode node) {
        if (node instanceof VarSpecLeafVal) {
            return leafValRandom((VarSpecLeafVal)node);
        } else if (node instanceof VarSpecLeafCollect) {
            return leafCollectRandom((VarSpecLeafCollect)node);
        } else if (node instanceof VarSpecLeafCombine) {
            return leafCombineRandom((VarSpecLeafCombine)node);
        } else if (node instanceof VarSpecLeafRange) {
            return leafRangeRandom((VarSpecLeafRange)node);
        } else if (node instanceof VarSpecLeafRef) {
            return leafRefRandom((VarSpecLeafRef)node);
        } else if (node instanceof VarSpecNodeDef) {
            return nodeDefRandom((VarSpecNodeDef)node);
        } else if (node instanceof VarSpecNodeTable) {
            return nodeTableRandom((VarSpecNodeTable)node);
        } else if (node instanceof VarSpecNodeCollectGroup) {
            return nodeCollectGroupRandom((VarSpecNodeCollectGroup)node);
        } else if (node instanceof VarSpecNodeCombineGroup) {
            return nodeCombineGroupRandom((VarSpecNodeCombineGroup)node);
        } else if (node instanceof VarSpecRoot) {
            return rootRandom((VarSpecRoot)node);
        } else {
            throw new IllegalArgumentException("Unexpected node type "+(node == null ? null : node.getClass()));
        }
    }

    

    private static CVVp leafValRandom(VarSpecLeafVal node) {
        return CVVp.create(node.getValue().clone().switchToValue());
    }

    private static CVVp leafCollectRandom(VarSpecLeafCollect node) {
        int i= random.nextInt(node.getChoices().size());
        V v= node.getChoices().get(i).clone().switchToValue();
        return CVVp.create(v);
    }

    private static CVVp leafCombineRandom(VarSpecLeafCombine node) {
        List<V> values= node.getValue().stream().map(v -> v.clone().switchToValue()).collect(Collectors.toList());
        return CVVp.create(values);
    }

    private static CVVp leafRangeRandom(VarSpecLeafRange node) {
        V v= new VisOrRange(node.getFirst(), node.getLast(), node.getCount()).getRandomElement().clone().switchToValue();
        return CVVp.create(v);
    }

    private static CVVp leafRefRandom(VarSpecLeafRef node) {
        return random(node.defNode());
    }

    private static CVVp nodeDefRandom(VarSpecNodeDef node) {
        VarSpecNode definedNode= node.getDefinedNode();
        if (definedNode != null) 
            return random(definedNode).projectToCVpLeavingNonMatchingVpsNull(node.getEffectiveCVp());
        return null;
    }

    private static CVVp nodeTableRandom(VarSpecNodeTable node) {
        VarSpecNode c= node.getFirstNonDefineChild();
        if (c != null) {
            return random(c).projectToCVpLeavingNonMatchingVpsNull(node.getEffectiveCVp());
        }
        return null;
    }

    private static CVVp nodeCollectGroupRandom(VarSpecNodeCollectGroup node) {
        if (node.getChildren() != null) {
            long index= Math.abs(random.nextLong()) % node.sizeLowerBound();
            long sz= 0;
            for (VarSpecNode c : node.getNonDefineChildren()) {
                sz += c.sizeLowerBound();
                if (index < sz) {
                    return random(c).projectToCVpLeavingNonMatchingVpsNull(node.getEffectiveCVp());
                }
            }
            // default should never occur, But...
            return random(node.getChildren().get(node.getChildren().size()-1)).projectToCVpLeavingNonMatchingVpsNull(node.getEffectiveCVp());
        }
        return null;
    }

    private static CVVp nodeCombineGroupRandom(VarSpecNodeCombineGroup node) {
        if (node.getChildren() != null) {
            List<Vp> vps= new ArrayList<>();
            List<V> items= new ArrayList<>();
            
            for (VarSpecNode c : node.getNonDefineChildren()) {
                if (c instanceof VarSpecLeafRef && ((VarSpecLeafRef) c).getReferencedNode() instanceof VarSpecLeafDerive) {
                    c= ((VarSpecLeafRef)c).getReferencedNode();
                }
                if (c instanceof VarSpecLeafDerive) {
                    // special handling of derive child
                    VarSpecLeafDerive dl= (VarSpecLeafDerive)c;
                    Map<String,Object> map= Derive.getAsKeyOrderedMap(new CVp(vps), new CV(items)); 
                    List<V> vpc= Derive.deriveValuesFrom(map, dl);
                    vps.addAll(dl.getEffectiveCVp().getVariationPoints()); // should be exactly one
                    int index= random.nextInt(vpc.size());
                    items.add(vpc.get(index));
                } else {
                    CVVp cCvvp= random(c);
                    for (Vp vp : cCvvp.getCVp()) {
                        vps.add(vp);
                        items.add(cCvvp.get(vp.getKey()));
                    }
                }
            }
            return CVVp.create(items);
        }
        return null;
    }
    

    private static CVVp rootRandom(VarSpecRoot node) {
        VarSpecNode c= node.getFirstNonDefineChild();
        if (c != null) {
            return random(c).projectToCVpLeavingNonMatchingVpsNull(node.getEffectiveCVp());
        }
        return null;

    }

}
