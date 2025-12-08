package proforma.varproforma.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import proforma.xml21.GradingHintsType;
import proforma.varproforma.ChildRefRef;
import proforma.varproforma.MatArtifact;
import proforma.xml21.GradesBaseRefChildType;
import proforma.xml21.GradesCombineRefChildType;
import proforma.xml21.GradesNodeType;
import proforma.xml21.GradesTestRefChildType;
import proforma.xml21.TaskType;

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
            List<GradesNodeType> nodes= getNodesReferencing(gh, ref.isTestRef(), ref.getRef(), ref.getSubRef());
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

    private static List<GradesNodeType> getNodesReferencing(GradingHintsType gh, boolean isTestRef, String ref, String subRef) {
        ArrayList<GradesNodeType> result= new ArrayList<>();
        for (GradesNodeType node : getRootAndCombineNodes(gh)) {
            if (getChildRefByRefAndSubRef(node, isTestRef, ref, subRef) != null) {
                result.add(node);
            };
        }
        return result;
        
    }
    
    
    private static GradesBaseRefChildType getChildRefByRefAndSubRef(GradesNodeType gn, boolean isTestRef, String ref, String subRef) {
        for (GradesBaseRefChildType cr : gn.getTestRefOrCombineRef()) {
            if ((cr instanceof GradesTestRefChildType && isTestRef || cr instanceof GradesCombineRefChildType && !isTestRef) 
                    && ProformaUtil.getRef(cr).equals(ref) 
                    && Objects.equals(ProformaUtil.getSubRef(cr), subRef)) {
                return cr;
            }
        }
        return null;
    }
    

    
    private static Iterable<GradesNodeType> getRootAndCombineNodes(GradingHintsType gh) {
        return new Iterable<GradesNodeType>() {
            @Override public Iterator<GradesNodeType> iterator() {
                return new Iterator<GradesNodeType>() {
                    private boolean noneReturnedYet= true;
                    private Iterator<GradesNodeType> itr= null;
                    @Override public boolean hasNext() {
                        if (noneReturnedYet) return true;
                        if (itr == null) itr= gh.getCombine().iterator();
                        return itr.hasNext();
                    }
                    @Override public GradesNodeType next() {
                        if (!hasNext()) throw new NoSuchElementException();
                        if (noneReturnedYet) {
                            noneReturnedYet= false;
                            return gh.getRoot();
                        } else {
                            return itr.next();
                        }
                    }
                };
            }
        };
    }
    

    private static boolean removeChildRef(GradesNodeType gn, boolean isTestRef, String ref, String subRef) {
        boolean found= false;
        for (int i= gn.getTestRefOrCombineRef().size()-1; i >= 0; i--) {
            GradesBaseRefChildType chr= gn.getTestRefOrCombineRef().get(i);
            if (chr instanceof GradesCombineRefChildType && !isTestRef) {
                GradesCombineRefChildType cr= (GradesCombineRefChildType)chr;
                if (cr.getRef().equals(ref)) {
                    gn.getTestRefOrCombineRef().remove(i);
                    found= true;
                }
            } else if (chr instanceof GradesTestRefChildType && isTestRef) {
                GradesTestRefChildType tr= (GradesTestRefChildType)chr;
                if (tr.getRef().equals(ref) && Objects.equals(tr.getSubRef(), subRef)) {
                    gn.getTestRefOrCombineRef().remove(i);
                    found= true;
                }
            }
        }
        return found;
    }
    

    private static boolean removeCombineNode(GradingHintsType gh, String combineId) {
        boolean found= false;
        for (int k= gh.getCombine().size()-1; k >= 0; k--) {
            GradesNodeType node= gh.getCombine().get(k);
            if (node.getId().equals(combineId)) {
                gh.getCombine().remove(k);
                found= true;
            }
        }
        return found;
    }
    
    
    private void dropRef(List<GradesNodeType> referencingNodes, ChildRefRef nodeRef) {
        // drop all childrefs to the dropped node:
        boolean found= false;
        for (GradesNodeType node : referencingNodes) {
            if (removeChildRef(node, nodeRef.isTestRef(), nodeRef.getRef(),  nodeRef.getSubRef())) {
System.out.println("Dropping reference from '"+node.getId()+"' to '"+nodeRef+"'");                            
                found= true;
            }
        }
        if (!found) {
System.out.println("Warning: there is no node '"+nodeRef+"' to drop");                            
        } else if (!nodeRef.isTestRef()) {
            // We elimnate a complete combine node ...
            if (removeCombineNode(gh, nodeRef.getRef())) {
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
