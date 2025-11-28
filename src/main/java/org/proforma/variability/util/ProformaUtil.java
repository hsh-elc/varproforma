package org.proforma.variability.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import proforma.xml21.TaskFileType;
import proforma.xml21.TaskFilesType;
import proforma.xml21.EmbeddedBinFileType;
import proforma.xml21.EmbeddedTxtFileType;
import proforma.xml21.AttachedTxtFileType;

import proforma.xml21.GradesBaseRefChildType;
import proforma.xml21.GradesCombineRefChildType;
import proforma.xml21.GradesTestRefChildType;

import proforma.xml21.GradesNodeType;
import proforma.xml21.GradingHintsType;

public class ProformaUtil {
    
    public static AbstractFileChoiceGroup getFile(TaskFileType file) {
        return AbstractFileChoiceGroup.from(file);
    }
    
    public static class AbstractFileChoiceGroup {
        TaskFileType file;
        public static AbstractFileChoiceGroup from(TaskFileType file) {
            if (file.getEmbeddedBinFile() == null &&
                file.getEmbeddedTxtFile() == null &&
                file.getAttachedBinFile() == null &&
                file.getAttachedTxtFile() == null) {
                return null;
            }
            return new AbstractFileChoiceGroup(file);
        }
        @Override public String toString() {
            if (file.getEmbeddedBinFile() != null) return "embedded-bin-file";
            if (file.getEmbeddedTxtFile() != null) return "embedded-txt-file";
            if (file.getAttachedBinFile() != null) return "attached-bin-file";
            if (file.getAttachedTxtFile() != null) return "attached-txt-file";
            return "unknown-file";
        }
        private AbstractFileChoiceGroup(TaskFileType file) {
            this.file= file;
        }
        
        public boolean isAttached() {
            return file.getAttachedBinFile() != null || file.getAttachedTxtFile() != null;
        }
        public boolean isEmbedded() { 
            return file.getEmbeddedBinFile() != null || file.getEmbeddedTxtFile() != null;
        }
        public boolean isBin() {
            return file.getEmbeddedBinFile() != null || file.getAttachedBinFile() != null;
        }
        public boolean isTxt() { 
            return file.getEmbeddedTxtFile() != null || file.getAttachedTxtFile() != null;
        }

        public String getPath() {
            if (file.getEmbeddedBinFile() != null) return file.getEmbeddedBinFile().getFilename();
            if (file.getEmbeddedTxtFile() != null) return file.getEmbeddedTxtFile().getFilename();
            if (file.getAttachedBinFile() != null) return file.getAttachedBinFile();
            if (file.getAttachedTxtFile() != null) return file.getAttachedTxtFile().getValue();
            return null;
        }
        public void setPath(String path) {
            if (file.getEmbeddedBinFile() != null) file.getEmbeddedBinFile().setFilename(path);
            if (file.getEmbeddedTxtFile() != null) file.getEmbeddedTxtFile().setFilename(path);
            if (file.getAttachedBinFile() != null) file.setAttachedBinFile(path);
            if (file.getAttachedTxtFile() != null) file.getAttachedTxtFile().setValue(path);
        }
        
        
        public String getEncoding() {
            if (file.getEmbeddedBinFile() != null) return null;
            if (file.getEmbeddedTxtFile() != null) return "UTF-8";
            if (file.getAttachedBinFile() != null) return null;
            if (file.getAttachedTxtFile() != null) return file.getAttachedTxtFile().getEncoding();
            return null;
            
        }

        public String getNaturalLang() {
            if (file.getEmbeddedBinFile() != null) return null;
            if (file.getEmbeddedTxtFile() != null) return null;
            if (file.getAttachedBinFile() != null) return null;
            if (file.getAttachedTxtFile() != null) return file.getAttachedTxtFile().getNaturalLang();
            return null;
            
        }
        
        public void replacePath(String path) {
            if (file.getEmbeddedBinFile() == null &&
                file.getEmbeddedTxtFile() == null &&
                file.getAttachedBinFile() == null &&
                file.getAttachedTxtFile() == null) {
                throw new AssertionError("Unexpected file type");
            } else {
                setPath(path);
            }
        }

    }
    
    
    public static List<GradesNodeType> getNodesReferencing(GradingHintsType gh, boolean isTestRef, String ref, String subRef) {
        ArrayList<GradesNodeType> result= new ArrayList<>();
        for (GradesNodeType node : getRootAndCombineNodes(gh)) {
            if (getChildRefByRefAndSubRef(node, isTestRef, ref, subRef) != null) {
                result.add(node);
            };
        }
        return result;
        
    }
    
    public static boolean removeCombineNode(GradingHintsType gh, String combineId) {
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
    
    
    public static Iterable<GradesNodeType> getRootAndCombineNodes(GradingHintsType gh) {
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
    
    public static boolean removeChildRef(GradesNodeType gn, boolean isTestRef, String ref, String subRef) {
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
    

    public static TaskFileType findByPath(TaskFilesType files, String ppath) {
        for (TaskFileType f : files.getFile()) {
            String path= getFile(f).getPath();
            if (path.trim().equals(ppath)) {
                return f;
            }
        }
        return null;
    }

    public static TaskFileType findById(TaskFilesType files, String id) {
        for (TaskFileType f : files.getFile()) {
            if (f.getId().trim().equals(id)) {
                return f;
            }
        }
        return null;
    }
    

    public static boolean removeById(TaskFilesType files, String id) {
        for (ListIterator<TaskFileType> itr= files.getFile().listIterator(); itr.hasNext(); ) {
            TaskFileType f= itr.next();
            if (f.getId().equals(id)) {
                itr.remove();
                return true;
            }
        }
        return false;
    }

    public static String getRef(GradesBaseRefChildType cr) {
        if (cr instanceof GradesCombineRefChildType) {
            return ((GradesCombineRefChildType)cr).getRef();
        } if (cr instanceof GradesTestRefChildType) {
            return ((GradesTestRefChildType)cr).getRef();
        } else {
            return null;
        }
    }

    public static String getSubRef(GradesBaseRefChildType cr) {
        if (cr instanceof GradesCombineRefChildType) {
            return null;
        } if (cr instanceof GradesTestRefChildType) {
            return ((GradesTestRefChildType)cr).getSubRef();
        } else {
            return null;
        }
    }
    
    public static GradesBaseRefChildType getChildRefByRefAndSubRef(GradesNodeType gn, boolean isTestRef, String ref, String subRef) {
        for (GradesBaseRefChildType cr : gn.getTestRefOrCombineRef()) {
            if ((cr instanceof GradesTestRefChildType && isTestRef || cr instanceof GradesCombineRefChildType && !isTestRef) 
                    && ProformaUtil.getRef(cr).equals(ref) 
                    && Objects.equals(ProformaUtil.getSubRef(cr), subRef)) {
                return cr;
            }
        }
        return null;
    }
    


}
