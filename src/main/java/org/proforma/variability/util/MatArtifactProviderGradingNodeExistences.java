package org.proforma.variability.util;

import java.util.ArrayList;
import java.util.List;

import proforma.xml21.GradingHintsType;
import proforma.xml21.GradesNodeType;
import proforma.xml21.TaskType;
import org.proforma.variability.transfer.ChildRefRef;
import org.proforma.variability.transfer.MatArtifact;

public class MatArtifactProviderGradingNodeExistences implements MatArtifactProvider<Boolean> {
    
    private GradingHintsType gh;
    private List<ChildRefRef> refs;
    
    @Override
    public void init(MatArtifact a, Ribbon<TaskUnzipped> tu) {
        this.refs= a.getRef();
        TaskType task= tu.get().getTask();

        if (task == null) {
            throw new IllegalArgumentException("Task could not be read!");
        }
        
        gh = task.getGradingHints();
        if (gh == null) throw new IllegalArgumentException("proforma assignment has no grading hints");
    }

    
    @Override
    public Iterable<Ribbon<Boolean>> getItems() {
        ArrayList<Ribbon<Boolean>> result= new ArrayList<>();
        for (ChildRefRef ref : refs) {
            List<GradesNodeType> nodes= ProformaUtil.getNodesReferencing(gh, ref.isTestRef(), ref.getRef(), ref.getSubRef());
            final boolean existsPreviously= !nodes.isEmpty();
            result.add(new Ribbon<Boolean>(
                    () -> existsPreviously,
                    existsAfterwards -> {
                        if (!existsPreviously && existsAfterwards) {
                            throw new IllegalArgumentException("Cannot declare materialize artifact grading hints combine node existence 'true' for non-existing node '"+ref+"'.");
                        }
                        if (!existsAfterwards) {
                            dropRef(nodes, ref);
                        }
                    },
                    ""));
        }
        return result;
    }
    
    private void dropRef(List<GradesNodeType> referencingNodes, ChildRefRef nodeRef) {
        // drop all childrefs to the dropped node:
        boolean found= false;
        for (GradesNodeType node : referencingNodes) {
            if (ProformaUtil.removeChildRef(node, nodeRef.isTestRef(), nodeRef.getRef(),  nodeRef.getSubRef())) {
System.out.println("Dropping reference from '"+node.getId()+"' to '"+nodeRef+"'");                            
                found= true;
            }
        }
        if (!found) {
System.out.println("Warning: there is no node '"+nodeRef+"' to drop");                            
        } else if (!nodeRef.isTestRef()) {
            // We elimnate a complete combine node ...
            if (ProformaUtil.removeCombineNode(gh, nodeRef.getRef())) {
    System.out.println("Dropping combine node '"+nodeRef.getRef()+"'");                            
            }
        }
        
    }

    @Override
    public String toString() {
        return "MatArtifactProviderGradingHintsCombineNodesExistence [refs=" + refs + ", gh=" + gh + ", toString()="
                + super.toString() + "]";
    }
    
    
}
