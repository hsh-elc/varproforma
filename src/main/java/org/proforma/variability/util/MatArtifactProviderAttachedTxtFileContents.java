package org.proforma.variability.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import proforma.xml21.AttachedTxtFileType;
import proforma.xml21.TaskType;
import proforma.xml21.TaskFileType;
import org.proforma.variability.transfer.MatArtifact;
import org.proforma.variability.util.ProformaUtil.AbstractFileChoiceGroup;


public class MatArtifactProviderAttachedTxtFileContents implements MatArtifactProvider<String>  {
    
    private List<String> fileId;

    private List<String> path;

    private TaskUnzipped tu;


    @Override
    public void init(MatArtifact a, Ribbon<TaskUnzipped> tu) {
        this.fileId= a.getFileId();
        this.path= a.getPath();
        this.tu= tu.get();
        if (fileId != null) {
            for (String fid : fileId) {
                getRelevantDataFromTu(fid, null); // call once for validation
            }
        } 
        if (path != null) {
            for (String p : path) {
                getRelevantDataFromTu(null, p); // call once for validation
            }
        }
    }
    
    private static class Data {
        Path srcFile;
        String encoding;
        public Data(Path srcFile, String encoding) {
            this.srcFile = srcFile;
            this.encoding = encoding;
        }
    }

    private Data getRelevantDataFromTu(String fid, String p) {
        TaskType task= tu.getTask();
        if (task == null) {
            throw new IllegalArgumentException("Task could not be read!");
        }
    
        if (fid != null) {
            TaskFileType file= ProformaUtil.findById(task.getFiles(), fid);
            if (file == null) throw new IllegalArgumentException("Unexpected file id '"+fid+"'");
            AbstractFileChoiceGroup fcg = ProformaUtil.getFile(file); 
            if (fcg == null) {
                throw new IllegalArgumentException("Unexpected file choice group 'null' in matArtifact of type attachedTxtFileContent for fileId '"+fid+"'");
            }
    
            if (! (fcg.isAttached() && fcg.isTxt())) {
                throw new IllegalArgumentException("Unexpected file choice group of type '"+fcg+"' in matArtifact of type attachedTxtFileContent for fileId '"+fid+"'");
            }
                            
            Path pth= tu.getPathToUnzippedContents().resolve(fcg.getPath());
            if (!pth.toFile().exists()) {
                throw new IllegalArgumentException("Cannot find file '"+pth+"'");
            }

            String e= fcg.getEncoding();
            if (e == null) e= StandardCharsets.UTF_8.name();
            return new Data(pth,e);
        }
        
        
        if (p != null) {
            Path srcFile= tu.getPathToUnzippedContents().resolve(p);
            String e= StandardCharsets.UTF_8.name();
            return new Data(srcFile, e);
        }
        
        return null;

    }
    
    
    @Override
    public Iterable<Ribbon<String>> getItems() {
        ArrayList<Ribbon<String>> result= new ArrayList<>();
        if (fileId != null) {
            for (String fid : fileId) {
                result.add(new Ribbon<>(
                        () -> {
                            Data data= getRelevantDataFromTu(fid, null); // refresh
                            try {
                                return new String(java.nio.file.Files.readAllBytes(data.srcFile), data.encoding);
                            } catch (IOException e) {
                                throw new IllegalArgumentException("Cannot read file '"+data.srcFile+"'", e);
                            }
                        },
                        newContent -> {
                            Data data= getRelevantDataFromTu(fid, null); // refresh
                            try {
                                java.nio.file.Files.write(data.srcFile, newContent.getBytes(data.encoding));
                            } catch (IOException e) {
                                throw new IllegalArgumentException("Cannot write file '"+data.srcFile+"'", e);
                            }
                        },
                        ""));
            }
        }
        if (path != null) {
            for (String p : path) {
                result.add(new Ribbon<>(
                        () -> {
                            Data data= getRelevantDataFromTu(null, p); // refresh
                            try {
                                return new String(java.nio.file.Files.readAllBytes(data.srcFile), data.encoding);
                            } catch (IOException e) {
                                throw new IllegalArgumentException("Cannot read file '"+data.srcFile+"'", e);
                            }
                        },
                        newContent -> {
                            Data data= getRelevantDataFromTu(null, p); // refresh
                            try {
                                java.nio.file.Files.write(data.srcFile, newContent.getBytes(data.encoding));
                            } catch (IOException e) {
                                throw new IllegalArgumentException("Cannot write file '"+data.srcFile+"'", e);
                            }
                        },
                        ""));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "MatArtifactProviderAttachedTxtFileContent [fileId=" + fileId + ", path="+path+"]";
    }
    
    
}
