package proforma.varproforma.util;

import java.nio.charset.StandardCharsets;
import java.util.ListIterator;

import proforma.xml21.TaskFileType;
import proforma.xml21.TaskFilesType;
import proforma.xml21.GradesBaseRefChildType;
import proforma.xml21.GradesCombineRefChildType;
import proforma.xml21.GradesTestRefChildType;

public class ProformaUtil {
    
    public static interface FileChoiceGroup {
        boolean isTxt();
        boolean isBin();
        boolean isAttached();
        boolean isEmbedded();
        String getPath();
        String getEncoding();
        String getNaturalLang();
        byte[] getBytesOfEmbeddedFile();
    }
    
    public static TaskFileChoiceGroup getFile(TaskFileType file) {
        return TaskFileChoiceGroup.from(file);
    }
    
    public static class TaskFileChoiceGroup implements FileChoiceGroup {
        TaskFileType file;
        public static TaskFileChoiceGroup from(TaskFileType file) {
            if (file.getEmbeddedBinFile() == null &&
                file.getEmbeddedTxtFile() == null &&
                file.getAttachedBinFile() == null &&
                file.getAttachedTxtFile() == null) {
                return null;
            }
            return new TaskFileChoiceGroup(file);
        }
        @Override public String toString() {
            if (file.getEmbeddedBinFile() != null) return "embedded-bin-file";
            if (file.getEmbeddedTxtFile() != null) return "embedded-txt-file";
            if (file.getAttachedBinFile() != null) return "attached-bin-file";
            if (file.getAttachedTxtFile() != null) return "attached-txt-file";
            return "unknown-file";
        }
        private TaskFileChoiceGroup(TaskFileType file) {
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
        public byte[] getBytesOfEmbeddedFile() {
            if (!isEmbedded()) return null;
            if (isTxt()) {
                return file.getEmbeddedTxtFile().getValue().getBytes(StandardCharsets.UTF_8);
            } else {
                return file.getEmbeddedBinFile().getValue();
            }
        }

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


}
