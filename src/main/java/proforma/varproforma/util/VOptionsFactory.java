package proforma.varproforma.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

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

public class VOptionsFactory {
    
    public static VOptions create(VarSpecRoot root, CVVp fixture, String vpKey) {
        VOrRangeTree vort= new VOrRangeTree(root.getEffectiveVp(vpKey));
        addDistinctValuesForVpForGivenFixture(root.getFirstNonDefineChild(), fixture, vpKey, vort);
        Log.debug("vort: "+vort);
        VOptions options= new VOptions(vort);
        return options;
        
    }

    
    private static void addDistinctValuesForVpForGivenFixture(VarSpecNode node, CVVp fixture, String vpKey, VOrRangeTree toTree) {
        if (node instanceof VarSpecLeafVal) {
            leafValAddDistinctValuesForVpForGivenFixture((VarSpecLeafVal)node, fixture, vpKey, toTree);
        } else if (node instanceof VarSpecLeafCollect) {
            leafCollectAddDistinctValuesForVpForGivenFixture((VarSpecLeafCollect)node, fixture, vpKey, toTree);
        } else if (node instanceof VarSpecLeafCombine) {
            leafCombineAddDistinctValuesForVpForGivenFixture((VarSpecLeafCombine)node, fixture, vpKey, toTree);
        } else if (node instanceof VarSpecLeafRange) {
            leafRangeAddDistinctValuesForVpForGivenFixture((VarSpecLeafRange)node, fixture, vpKey, toTree);
        } else if (node instanceof VarSpecLeafRef) {
            leafRefAddDistinctValuesForVpForGivenFixture((VarSpecLeafRef)node, fixture, vpKey, toTree);
        } else if (node instanceof VarSpecNodeDef) {
            nodeDefAddDistinctValuesForVpForGivenFixture((VarSpecNodeDef)node, fixture, vpKey, toTree);
        } else if (node instanceof VarSpecNodeTable) {
            nodeTableAddDistinctValuesForVpForGivenFixture((VarSpecNodeTable)node, fixture, vpKey, toTree);
        } else if (node instanceof VarSpecNodeCollectGroup) {
            nodeCollectGroupAddDistinctValuesForVpForGivenFixture((VarSpecNodeCollectGroup)node, fixture, vpKey, toTree);
        } else if (node instanceof VarSpecNodeCombineGroup) {
            nodeCombineGroupAddDistinctValuesForVpForGivenFixture((VarSpecNodeCombineGroup)node, fixture, vpKey, toTree);
        } else if (node instanceof VarSpecRoot) {
            rootAddDistinctValuesForVpForGivenFixture((VarSpecRoot)node, fixture, vpKey, toTree);
        } else {
            throw new IllegalArgumentException("Unexpected node type "+(node == null ? null : node.getClass()));
        } 
    }


    private static void leafValAddDistinctValuesForVpForGivenFixture(VarSpecLeafVal leaf, CVVp fixture, String vpKey, VOrRangeTree toTree) {
        if (fixture.getCVp().size() == 0 && leaf.getEffectiveVp(vpKey) != null) {
            toTree.addElement(VOrRange.fromS(leaf.getValue()));
        }
    }

    
    private static void leafCollectAddDistinctValuesForVpForGivenFixture(VarSpecLeafCollect leaf, CVVp fixture, String vpKey, VOrRangeTree toTree) {
        if (fixture.getCVp().size() == 0 && leaf.getEffectiveVp(vpKey) != null) {
            for (V s : leaf.getChoices()) {
                toTree.addElement(VOrRange.fromS(s));
            }
        }
    }
    
    private static void leafCombineAddDistinctValuesForVpForGivenFixture(VarSpecLeafCombine leaf, CVVp fixture, String vpKey, VOrRangeTree toTree) {
        CVp myCVp= leaf.getEffectiveCVp();
        int index= leaf.getEffectiveVpIndex(vpKey);
        if (index >= 0) {
            for (int i=0; i<myCVp.size(); i++) {
                String key= myCVp.get(i).getKey();
                if (fixture.getVp(key) != null) {
                    V v= leaf.getValue().get(i);
                    if (!Objects.equals(fixture.get(key), v)) return; // empty
                }
            }
            toTree.addElement(VOrRange.fromS(leaf.getValue().get(index)));
        }
    }

    
    private static void leafRangeAddDistinctValuesForVpForGivenFixture(VarSpecLeafRange leaf, CVVp fixture, String vpKey, VOrRangeTree toTree) {
        Vp vp= leaf.getEffectiveVp(vpKey);
        if (fixture.getCVp().size() == 0 && vp != null) {
            VisOrRange r= new VisOrRange(leaf.getFirst(), leaf.getLast(), leaf.getCount());
            toTree.addElement(new VOrRange(r));
        }
    }

    private static void leafRefAddDistinctValuesForVpForGivenFixture(VarSpecLeafRef leaf, CVVp fixture, String vpKey, VOrRangeTree toTree) {
        VarSpecNode node= leaf.defNode();
        addDistinctValuesForVpForGivenFixture(node, fixture, vpKey, toTree);
    }

    private static void nodeDefAddDistinctValuesForVpForGivenFixture(VarSpecNodeDef node, CVVp fixture, String vpKey, VOrRangeTree toTree) {
        VarSpecNode definedNode= node.getDefinedNode();
        if (node != null) 
            addDistinctValuesForVpForGivenFixture(definedNode, fixture, vpKey, toTree);
    }
    
    private static void nodeTableAddDistinctValuesForVpForGivenFixture(VarSpecNodeTable node, CVVp fixture, String vpKey, VOrRangeTree toTree) {
        addDistinctValuesForVpForGivenFixture(node.getFirstNonDefineChild(), fixture, vpKey, toTree);
    }

    private static void nodeCollectGroupAddDistinctValuesForVpForGivenFixture(VarSpecNodeCollectGroup node, CVVp fixture, String vpKey, VOrRangeTree toTree) {
        Log.debug(VOptionsFactory.class+".nodeCollectGroupAddDistinctValuesForVpForGivenFixture("+vpKey+")");      
        if (node.getChildren() != null) {
            for (VarSpecNode c : node.getNonDefineChildren()) {
                addDistinctValuesForVpForGivenFixture(c, fixture, vpKey, toTree);
            }
        }
    }
    
    
    
    private static void nodeCombineGroupAddDistinctValuesForVpForGivenFixture(VarSpecNodeCombineGroup node, CVVp fixture, String vpKey, VOrRangeTree toTree) {
//      Log.log(getClass()+".getDistinctValuesForDim("+dimKey+")");     
        if (node.getChildren() != null) {
            
            VarSpecNode childWithVpKey= null;
            List<Vp> vpsOfChildWithDimKey= null;
            HashSet<Vp> fixtureVpsLeft= new HashSet<>(fixture.getCVp().getVariationPoints());

            List<String> keysSeenSoFar= new ArrayList<>();
            for (int i=0; i<node.getChildren().size(); i++) {
                VarSpecNode c= node.getChildren().get(i);
                if (! (c instanceof VarSpecNodeDef)) {
                    if (c instanceof VarSpecLeafRef && ((VarSpecLeafRef) c).getReferencedNode() instanceof VarSpecLeafDerive) {
                        c= ((VarSpecLeafRef)c).getReferencedNode();
                    }
                    CVp cd= c.getEffectiveCVp();
                    Vp cVd= c.getEffectiveVp(vpKey);
                    if (cVd != null) {
                        childWithVpKey= c;
                        vpsOfChildWithDimKey= cd.getVariationPoints();
                    } else {
                        if (fixture.isAtLeastOneVpMatching(cd)) {
                            boolean contained;
                            if (c instanceof VarSpecLeafDerive) {
                                // special handling of derive child
                                if (cd.size() != 1) throw new IllegalStateException("derive leaf should have dimension 1");
                                Map<String,Object> map= fixture.getAsKeyOrderedMap(keysSeenSoFar);
                                contained= Derive.deriveValuesFrom(map, ((VarSpecLeafDerive)c)).contains(fixture.get(cd.get(0).getKey()));
                            } else {
                                CVVp cvvp= fixture.projectToCVpLeavingNonMatchingVpsNull(cd);
                                contained= SpecContainsHelper.contains(c, cvvp);
                            }
                            if (!contained) {
                                break;
                            }
                            fixture.createIntersectionWithCVp(cd).forEach(it -> keysSeenSoFar.add(it.getKey()));
                            fixtureVpsLeft.removeAll(cd.getVariationPoints());
                            if (vpsOfChildWithDimKey != null && vpsOfChildWithDimKey.containsAll(fixtureVpsLeft)) break;
                        }
                    }
                }
            }
            
            
            if (vpsOfChildWithDimKey != null && vpsOfChildWithDimKey.containsAll(fixtureVpsLeft)) {
                
                CVVp cf= fixture.projectToCVpLeavingNonMatchingVpsNull(fixture.createIntersectionWithCVp(new CVp(vpsOfChildWithDimKey)));
                
                if (childWithVpKey instanceof VarSpecLeafDerive) {
                    // special handling of derive child
                    VarSpecLeafDerive dl= (VarSpecLeafDerive)childWithVpKey;
                    if (vpsOfChildWithDimKey.size() != 1) {
                        throw new IllegalStateException("derive leaf should have dimension 1");
                    }
                    TreeMap<String,Object> map= new TreeMap<>();
                    for (String dssf : keysSeenSoFar) map.put(dssf, fixture.get(dssf).getValue());
                    for (V v : Derive.deriveValuesFrom(map, dl)) {
                        toTree.addElement(VOrRange.fromS(v));
                    }
                    return;
                } else {
                    addDistinctValuesForVpForGivenFixture(childWithVpKey, cf, vpKey, toTree);
                    return;
                }
            }
        }
    }
    

    private static void rootAddDistinctValuesForVpForGivenFixture(VarSpecRoot root, CVVp fixture, String vpKey,
            VOrRangeTree toTree) {
        VarSpecNode node= root.getFirstNonDefineChild();
        if (node != null) {
            addDistinctValuesForVpForGivenFixture(node, fixture, vpKey, toTree);
        }
    }



//    private static void nodeCombineGroupAddDistinctValuesForVpForGivenFixture(SpecNodeCombineGroup node, CSVp fixture, String vpKey, SOrRangeTree toTree) {
//
//        if (node.getChildren() != null) {
//            
//            SpecNode childWithVpKey= null;
//            List<Vp> vpsOfChildWithDimKey= null;
//            HashSet<Vp> fixtureVpsLeft= new HashSet<>(fixed.getVariationPoints());
//
//            List<String> keysSeenSoFar= new ArrayList<>();
//            for (int i=0; i<node.getChildren().size(); i++) {
//                SpecNode c= node.getChildren().get(i);
//                if (! (c instanceof SpecNodeDef)) {
//                    if (c instanceof SpecLeafRef && ((SpecLeafRef) c).getReferencedNode() instanceof SpecLeafDerivation) {
//                        c= ((SpecLeafRef)c).getReferencedNode();
//                    }
//                    CVp cd= c.getEffectiveCVp();
//                    Vp cVd= c.getEffectiveVp(vpKey);
//                    if (cVd != null) {
//                        childWithVpKey= c;
//                        vpsOfChildWithDimKey= cd.getVariationPoints();
//                    } else {
//                        if (fixed.isAtLeastOneVpMatching(cd)) {
//                            boolean contained;
//                            if (c instanceof SpecLeafDerivation) {
//                                // special handling of derive child
//                                if (cd.size() != 1) throw new IllegalStateException("derive leaf should have dimension 1");
//                                Map<String,Object> map= fixture.getAsKeyOrderedMap(keysSeenSoFar);
//                                contained= ((SpecLeafDerivation)c).deriveFrom(map).contains(fixture.get(cd.get(0).getKey()));
//                            } else {
//                                CVr cvr= fixture.projectToCVpLeavingNonMatchingVpsNull(cd);
//                                contained= c.contains(cvr);
//                            }
//                            if (!contained) {
//                                break;
//                            }
//                            fixture.createIntersectionWithCVp(cd).forEach(it -> keysSeenSoFar.add(it.getKey()));
//                            fixtureVpsLeft.removeAll(cd.getVariationPoints());
//                            if (vpsOfChildWithDimKey != null && vpsOfChildWithDimKey.containsAll(fixtureVpsLeft)) break;
//                        }
//                    }
//                }
//            }
//            
//            
//            if (vpsOfChildWithDimKey != null && vpsOfChildWithDimKey.containsAll(fixtureVpsLeft)) {
//                
//                CVr cf= fixture.projectToCVpLeavingNonMatchingVpsNull(fixture.createIntersectionWithCVp(new CVp(vpsOfChildWithDimKey)));
//                
//                if (childWithVpKey instanceof Derivation) {
//                    // special handling of derive child
//                    Derivation dl= (Derivation)childWithVpKey;
//                    if (vpsOfChildWithDimKey.size() != 1) {
//                        throw new IllegalStateException("derive leaf should have dimension 1");
//                    }
//                    TreeMap<String,Object> map= new TreeMap<>();
//                    for (String dssf : keysSeenSoFar) map.put(dssf, fixture.get(dssf).getValue());
//                    for (V vp : dl.deriveFrom(map)) {
//                        toTree.addElement(vp);
//                    }
//                    return;
//                } else {
//                    childWithVpKey.addDistinctValuesForVpForGivenFixture(cf, vpKey, toTree);
//                    return;
//                }
//            }
//        }
//    }
    
//    /**
//     * @return a mapping from vp keys to values. The value type is determined by the 
//     * Vp type. E.g. for a {@link Vi} the returned value is Integer.  
//     * For a {@link Vt} the returned value is a nested <code>List&lt;Map&lt;String,Object&gt;&gt;</code>. 
//     */
//    private static LinkedHashMap<String, Object> getAsKeyOrderedMap(CS cs, CVp cvp, List<String> restrictToKeys) {
//        LinkedHashMap<String, Object> map= new LinkedHashMap<>();
//        for (Vp vp : cvp) {
//            String key= vp.getKey();
//            if (restrictToKeys == null || restrictToKeys.contains(key)) {
//                S s= cs.get(cvp.indexOf(key));
//                Object val= s.getSpec();
//                if (s instanceof St) { // table?
//                    List<Map<String, Object>> list= new ArrayList<>();
//                    List<?> cvs= (List<?>)val;
//                    for (int i=0; i<cvs.size(); i++) {
//                        CVr record= CVr.createValue((CV)cvs.get(i));
//                        list.add(record.getAsKeyOrderedMap());
//                    }
//                    val= list;
//                } else {
//                    val= s.getSpec();
//                }
//                map.put(key, val);
//            }
//        }
//        return map;
//    }


    
    
}
