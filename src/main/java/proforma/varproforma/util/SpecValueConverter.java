package proforma.varproforma.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import proforma.varproforma.CV;
import proforma.varproforma.CVList;
import proforma.varproforma.CVListVp;
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


public class SpecValueConverter {
    
    
    public static VarSpecNodeTable toTableSpec(CVListVp list) {
        if (list != null) {
            CVp cvp= list.getCVp();
            VarSpecNode n= VarSpecNodeTable.buildTable(cvp.keySet().toArray(new String[0])).collectGroup();
            for (CV cs : list.getList()) {
                n= appendCombineGroup(n, cvp, cs);
            }
            VarSpecNodeTable cvs= n.endCollectGroup().endBuildTable();
            return cvs;
        }
        return null;
    }

    public static VarSpecNodeTable toTableSpec(CVVp cvvp) {
        if (cvvp != null) {
            CVp cvp= cvvp.getCVp();
            CV cv= cvvp.getCV();
            VarSpecNode n= VarSpecNodeTable.buildTable(cvp.keySet().toArray(new String[0]));
            n= appendCombineGroup(n, cvp, cv);
            VarSpecNodeTable cvs= n.endBuildTable();
            return cvs;
        }
        return null;
    }
    
    public static VarSpecRoot toSpecRoot(CVVp cvvp) {
        if (cvvp != null) {
            CVp cvp= cvvp.getCVp();
            CV cv= cvvp.getCV();
            VarSpecNode n= VarSpecRoot.build(cvp.getVariationPoints().toArray(new Vp[0]));
            n= appendCombineGroup(n, cvp, cv);
            VarSpecRoot cvs= n.endBuild();
            return cvs;
        }
        return null;
    }
    
    private static VarSpecNode appendCombineGroup(VarSpecNode n, CVp cvp, CV cv) {
        V[] nv= new V[cv.size()];
        n= n.combineGroup();
            for (int i=0; i<cv.size(); i++) {
                V v= cv.get(i);
                Vp vp= cvp.get(i);
                Object spec= v.getSpec();
                n= n.val(spec);
                nv[i]= V.fromValue(v.getValue(), vp); // we need to create new values,
                // because reusing the existing values could lead to conflicts between
                // cached data in the reused values and the new data from the task.
            }
        n= n.endCombineGroup();
        return n;
    }
    
    
    public static CVListVp expandNode(VarSpecNode node) {
        return new CVListVp(node.getEffectiveCVp(), expandNodeToList(node).getElements());
    }

    public static CVList expandNodeToList(VarSpecNode node) {
        if (node instanceof VarSpecLeafVal) {
            return expandValToList((VarSpecLeafVal)node);
        } else if (node instanceof VarSpecLeafCollect) {
            return expandCollectToList((VarSpecLeafCollect)node);
        } else if (node instanceof VarSpecLeafCombine) {
            return expandCombineToList((VarSpecLeafCombine)node);
        } else if (node instanceof VarSpecLeafRange) {
            return expandRangeToList((VarSpecLeafRange)node);
        } else if (node instanceof VarSpecLeafRef) {
            return expandRefToList((VarSpecLeafRef)node);
        } else if (node instanceof VarSpecNodeDef) {
            return expandDefToList((VarSpecNodeDef)node);
        } else if (node instanceof VarSpecNodeTable) {
            return expandTableToList((VarSpecNodeTable)node);
        } else if (node instanceof VarSpecNodeCollectGroup) {
            return expandCollectGroupToList((VarSpecNodeCollectGroup)node);
        } else if (node instanceof VarSpecNodeCombineGroup) {
            return expandCombineGroupToList((VarSpecNodeCombineGroup)node);
        } else if (node instanceof VarSpecRoot) {
            return expandRootToList((VarSpecRoot)node);
        } else {
            throw new IllegalArgumentException("Unexpected node of type "+ (node == null ? null : node.getClass()));
        }
    }
    
    private static CVList expandValToList(VarSpecLeafVal node) {
        CVList result= new CVList();
        Vp vp= node.getEffectiveCVp().get(0);
        V s= node.getValue();
        Object value= s.clone().switchToValue();
        V newS= V.fromValue(value, vp);
        CV cs= new CV(newS);
        result.getElements().add(cs);
        return result;
    }

    private static CVList expandCollectToList(VarSpecLeafCollect node) {
        CVList result= new CVList();
        Vp vp= node.getEffectiveCVp().get(0);
        for (V s : node.getChoices()) {
            Object value= s.clone().switchToValue();
            V newS= V.fromValue(value, vp);
            CV cs= new CV(newS);
            result.getElements().add(cs);
        }
        return result;
    }
    
    private static CVList expandCombineToList(VarSpecLeafCombine node) {
        List<V> list= node.getValue();
        ArrayList<V> newList= new ArrayList<>();
        for (int i=0; i< list.size(); i++) {
            Vp vp= node.getEffectiveCVp().get(i);
            V s= list.get(i);
            Object value= s.clone().switchToValue();
            V newS= V.fromValue(value, vp);
            newList.add(newS);
        }
        CVList result= new CVList();
        CV cs= new CV(newList);
        result.getElements().add(cs);
        return result;
    }
    
    
    private static CVList expandRangeToList(VarSpecLeafRange node) {
        CVList result= new CVList();
        Vp vp= node.getEffectiveCVp().get(0);
        VisOrRange range= new VisOrRange(node.getFirst(),  node.getLast(),  node.getCount());
        for (int i=0; i<node.getCount(); i++) {
            V s= range.getStep(i);
            Object value= s.clone().switchToValue();
            V newS= V.fromValue(value, vp);
            CV cs= new CV(newS);
            result.getElements().add(cs);
        }
        return result;
    }

    private static CVList expandRefToList(VarSpecLeafRef node) {
        return expandNodeToList(node.defNode());
    }

    private static CVList expandDefToList(VarSpecNodeDef node) {
        VarSpecNode n= node.getDefinedNode();
        if (n == null) {
            throw new IllegalArgumentException("Expected defined node");
        }
        CVp myCVp= node.getEffectiveCVp();
        CVp otherCVp= n.getEffectiveCVp();
        CVList list= expandNodeToList(n);
        if (!otherCVp.equals(myCVp)) {
            // reordered keys
            list= list.duplicateWithReorderedCVp(otherCVp, myCVp);
        }
        return list;
    }

    private static CVList expandCollectGroupToList(VarSpecNodeCollectGroup node) {
        CVp myCVp= node.getEffectiveCVp();
        CVList result= new CVList();
        for (VarSpecNode c : node.getNonDefineChildren()) {
            CVList list= expandNodeToList(c);
            CVp otherCVp= c.getEffectiveCVp();
            if (!otherCVp.equals(myCVp)) {
                // reordered keys
                list= list.duplicateWithReorderedCVp(otherCVp, myCVp);
            }
            result.getElements().addAll(list.getElements());
        }
        return result;
    }

//    private static class CVSet {
//        CVp cvp;
//        CSList list;
//        static CVSet createEmptySet(CVp cvp) {
//            CVSet result= new CVSet();
//            result.cvp= cvp;
//            result.list= new CSList();
//            result.list.getElements();
//            return result;
//        }
//    }

    private static CVList expandCombineGroupToList(VarSpecNodeCombineGroup node) {
        CVp myCVp= node.getEffectiveCVp();
        List<CVListVp> factors= new ArrayList<>();
        if (node.getChildren() != null) {
            List<Vp> vpsSeenSoFar= new ArrayList<>();
            int d=0;
            for (int i=0; i<node.getChildren().size(); i++) {
                VarSpecNode c= node.getChildren().get(i);
                if (! (c instanceof VarSpecNodeDef)) {
                    CVp cd= c.getEffectiveCVp();
                    CVListVp cs;
                    int dc;
                    if (c instanceof VarSpecLeafRef && ((VarSpecLeafRef) c).getReferencedNode() instanceof VarSpecLeafDerive) {
                        c= ((VarSpecLeafRef)c).getReferencedNode();
                    }
                    if (c instanceof VarSpecLeafDerive) {
                        // special handling of derive child
                        if (cd.size() != 1) throw new IllegalStateException("derive leaf should have dimension 1");
                        // collapse all factors collected so far into one ...
                        CVListVp allFactorsCollapsed;
                        if (factors.isEmpty()) {
                            allFactorsCollapsed= CVListVp.createEmptyList(new CVp(vpsSeenSoFar));
                        } else {
                            allFactorsCollapsed= expand(factors);
                        }
                        List<Vp> childResultVps= new ArrayList<>();
                        childResultVps.addAll(vpsSeenSoFar);
                        childResultVps.addAll(cd.getVariationPoints());
                        CVListVp childResult= CVListVp.createEmptyList(new CVp(childResultVps));
                        for (CV cv : allFactorsCollapsed.getList()) {
                            Map<String,Object> map= new LinkedHashMap<>();
                            int k= 0;
                            for (Vp vp : allFactorsCollapsed.getCVp()) {
                                map.put(vp.getKey(), cv.getVariants().get(k++).getValue());
                            }
                            List<V> leafResultValues= Derive.deriveValuesFrom(map, (VarSpecLeafDerive)c);
                            for (V leafResultVp : leafResultValues) {
                                CV childResultTuple= new CV(new ArrayList<>());
                                childResultTuple.getVariants().addAll(cv.getVariants());
                                childResultTuple.getVariants().add(leafResultVp);
                                childResult.getList().add(childResultTuple);
                            }
                        }
                        cs= childResult;
                        factors.clear();
                        dc= d+1;
                        d= 0;
                    } else {
                        cs= expandNode(c);
                        dc= cs.getCVp().size();
                    }

                    CVp myCVpForC= new CVp(myCVp.getVariationPoints().subList(d, d+dc));
                    if (! cs.getCVp().equals(myCVpForC)) {
                        // reordered keys
                        cs= cs.duplicateWithReorderedCVp(myCVpForC);
                    }
                    if (! cs.getCVp().equals(myCVpForC)) {
                        throw new AssertionError();
                    }
                    if (c instanceof VarSpecLeafDerive) {
                        vpsSeenSoFar.addAll(cd.getVariationPoints());
                    } else {
                        // since the effective vp might have been reordered, we instead add the
                        // reordered vps.
                        vpsSeenSoFar.addAll(myCVpForC.getVariationPoints());
                    }
                    
                    factors.add(cs);
                    d+=dc;
                }
            }
        }
        if (factors.isEmpty()) return new CVList(new ArrayList<>());
        return new CVList(expand(factors).getList());
    }

    
    
    private static CVListVp expand(List<CVListVp> factors) {
        CVListVp head= factors.get(0);
        if (factors.size() == 1) return head;
        CVListVp tail= expand(factors.subList(1, factors.size()));
        
        CVListVp result= new CVListVp();
        List<Vp> vps= new ArrayList<>(head.getCVp().getVariationPoints());
        vps.addAll(tail.getCVp().getVariationPoints());
        result.setCVp(new CVp(vps));
        List<CV> tuples= new ArrayList<>();
        for (CV h : head.getList()) {
            for (CV t : tail.getList()) {
                CV ht= new CV();
                List<V> items= new ArrayList<>(h.getVariants());
                items.addAll(t.getVariants());
                ht.setVariants(items);
                tuples.add(ht);
            }
        }
        result.setList(tuples);
        return result;
    }

    
    
    private static CVList expandTableToList(VarSpecNodeTable spec) {
        VarSpecNode n= spec.getFirstNonDefineChild();
        if (n == null) {
            throw new IllegalArgumentException("Expected child node");
        }
        CVp myCVp= spec.getEffectiveCVp();
        CVp childCVp= n.getEffectiveCVp();
        CVList list= expandNodeToList(n);
        if (!childCVp.equals(myCVp)) {
            // reordered keys
            list= list.duplicateWithReorderedCVp(childCVp, myCVp);
        }
        return list;
    }


    
    
    
    private static CVList expandRootToList(VarSpecRoot node) {
        VarSpecNode n= node.getFirstNonDefineChild();
        if (n == null) {
            throw new IllegalArgumentException("Expected child node");
        }
        CVp myCVp= node.getEffectiveCVp();
        CVp childCVp= n.getEffectiveCVp();
        CVList list= expandNodeToList(n);
        if (!childCVp.equals(myCVp)) {
            // reordered keys
            list= list.duplicateWithReorderedCVp(childCVp, myCVp);
        }
        return list;
    }


    

//    private static CSList duplicateWithReorderedCVp(CSList list, CVp originalCVp, CVp reorderedCVp) {
//        Map<String,Integer> map= new HashMap<>();
//        for (Vp vpReordered : reorderedCVp) {
//            for (int i=0; i<originalCVp.size(); i++) {
//                Vp dimOwn= originalCVp.get(i);
//                if (dimOwn.equals(vpReordered)) {
//                    map.put(vpReordered.getKey(), i);
//                    break;
//                }
//            }
//            if (!map.containsKey(vpReordered.getKey())) {
//                String keySpec= null;
//                if (originalCVp != null) {
//                    List<String> keySpecList= new ArrayList<>();
//                    for (Vp vd : originalCVp) keySpecList.add(vd.toString());
//                    keySpec= String.join(", ", keySpecList);
//                }
//                String keySpecReordered= null;
//                if (reorderedCVp != null) {
//                    List<String> keySpecList= new ArrayList<>();
//                    for (Vp vd : reorderedCVp) keySpecList.add(vd.toString());
//                    keySpecReordered= String.join(", ", keySpecList);
//                }
//
//                throw new IllegalArgumentException("Cannot duplicate "+CSList.class+"("+keySpec+") with unexpected reorderedCVp=("+keySpecReordered+")");
//            }
//        }
//
//        CSList result= new CSList();
//        for (CS cvOwn : list.getElements()) {
//            CS cvReordered= new CS();
//            ArrayList<S> itemsReordered= new ArrayList<>();
//            for (Vp dimReordered : reorderedCVp) {
//                int indexOwn= map.get(dimReordered.getKey());
//                S v= cvOwn.getVariants().get(indexOwn);
//                itemsReordered.add(v);
//            }
//            cvReordered.setVariants(itemsReordered);
//            result.getElements().add(cvReordered);
//        }
//        
//        return result;
//        
//    }
    
}
