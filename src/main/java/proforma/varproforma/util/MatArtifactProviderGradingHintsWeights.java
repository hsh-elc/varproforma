package proforma.varproforma.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import proforma.varproforma.ChildRefRef;
import proforma.varproforma.MatArtifact;
import proforma.xml21.GradesBaseRefChildType;
import proforma.xml21.GradesCombineRefChildType;
import proforma.xml21.GradingHintsType;
import proforma.xml21.GradesNodeType;
import proforma.xml21.TaskType;
import proforma.xml21.GradesTestRefChildType;

public class MatArtifactProviderGradingHintsWeights implements MatArtifactProvider<Double> {
    
    private Map<ChildRefRef,GradesBaseRefChildType> childRefObjects;

    @Override
    public void init(MatArtifact a, Ribbon<TaskUnzipped> tu) {
        TaskType task= tu.get().getTask();

        if (task == null) {
            throw new IllegalArgumentException("Task could not be read!");
        }
        
        GradingHintsType gh = task.getGradingHints();
        if (gh == null) throw new IllegalArgumentException("proforma assignment has no grading hints");
        
        this.childRefObjects= new HashMap<>();
        
        if (a.getRef() == null || a.getRef().isEmpty()) {
            throw new IllegalArgumentException("matArtifact of type gradingHintsWeights expects at least one child-ref identifier");
        } else {
            
            ArrayList<GradesNodeType> nodes= new ArrayList<>();
            nodes.add(gh.getRoot());
            nodes.addAll(gh.getCombine());
            for (GradesNodeType gn : nodes) {
                for (GradesBaseRefChildType cr : gn.getTestRefOrCombineRef()) {
                    ChildRefRef ref= new ChildRefRef(cr instanceof GradesTestRefChildType, ProformaUtil.getRef(cr), ProformaUtil.getSubRef(cr));
                    if (a.getRef().contains(ref)) {
                        this.childRefObjects.put(ref, cr);
                    }
                }
            }
            Set<ChildRefRef> remaining= new HashSet<>(a.getRef());
            remaining.removeAll(this.childRefObjects.keySet());
            if (!remaining.isEmpty()) {
                throw new IllegalArgumentException("matArtifact of type gradingHintsWeights includes childRefs that are not contained in the task: "+remaining);
            }
        }
    }

    
    @Override
    public Iterable<Ribbon<Double>> getItems() {
        ArrayList<Ribbon<Double>> result= new ArrayList<>();
        for (ChildRefRef ref : childRefObjects.keySet()) {
            GradesBaseRefChildType chr= childRefObjects.get(ref);
            
            GradesTestRefChildType tr= (chr instanceof GradesTestRefChildType ? (GradesTestRefChildType)chr : null);
            if (tr != null) {
                result.add(new Ribbon<Double>(
                        () -> tr.getWeight().doubleValue(),
                        w -> tr.setWeight(w),
                        ""));
            }
            GradesCombineRefChildType cr= (chr instanceof GradesCombineRefChildType ? (GradesCombineRefChildType)chr : null);
            if (cr != null) {
                result.add(new Ribbon<Double>(
                        () -> cr.getWeight().doubleValue(),
                        w -> cr.setWeight(w),
                        ""));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "MatArtifactProviderGradingHintsWeights [childRefObjects=" + childRefObjects
                + ", toString()=" + super.toString() + "]";
    }
    
    
    
}
