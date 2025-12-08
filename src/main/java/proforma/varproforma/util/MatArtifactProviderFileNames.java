package proforma.varproforma.util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import proforma.xml21.TaskType;
import proforma.varproforma.MatArtifact;
import proforma.varproforma.util.ProformaUtil.TaskFileChoiceGroup;
import proforma.xml21.TaskFileType;

public class MatArtifactProviderFileNames implements MatArtifactProvider<String> {
    
    private List<String> fileId;

    private List<String> path;

    private TaskUnzipped tu;

    private Path pathToUnzippedContents;

    
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
        TaskFileType file;
        Path srcFile;
        public Data(TaskFileType file, Path srcFile) {
            this.file = file;
            this.srcFile = srcFile;
        }
    }
    
    
    private Data getRelevantDataFromTu(String fid, String p) {
        TaskType task= tu.getTask();
        if (task == null) {
            throw new IllegalArgumentException("Task could not be read!");
        }
    
        this.pathToUnzippedContents= tu.getPathToUnzippedContents();

        if (fid != null) {
            TaskFileType file= ProformaUtil.findById(task.getFiles(), fid);
            if (file == null) throw new IllegalArgumentException("Unexpected file id '"+fid+"'");
    
            
            // We rename attached files in the zip:
            TaskFileChoiceGroup fcg= ProformaUtil.getFile(file);
            Path srcFile;
            if (fcg.isAttached()) {
                srcFile= pathToUnzippedContents.resolve(fcg.getPath());
            } else {
                srcFile= null;
            }
        
            return new Data(file, srcFile);
        }
        if (p != null) {
            Path srcFile;
            srcFile= pathToUnzippedContents.resolve(p);

            // search matching task file element, if any: 
            TaskFileType file= null;
            
            for (TaskFileType f : task.getFiles().getFile()) {
                TaskFileChoiceGroup fcg= ProformaUtil.getFile(f);
                if (fcg.isAttached()) {
                    if (p.equals(fcg.getPath())) {
                        file= f;
                    }
                }
            }
            
            return new Data(file, srcFile);
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
                            return ProformaUtil.getFile(data.file).getPath();
                        },
                        newFileName -> {
                            Data data= getRelevantDataFromTu(fid, null); // refresh
                            Path destFile= pathToUnzippedContents.resolve(newFileName);
                            if (data.srcFile != null) {
                                data.srcFile.toFile().renameTo(destFile.toFile());
                            }
                            ProformaUtil.getFile(data.file).replacePath(newFileName);
                        },
                        ""));
                     
            }
        }
        if (path != null) {
            for (String p : path) {
                result.add(new Ribbon<>(
                        () -> {
                            Data data= getRelevantDataFromTu(null, p); // refresh
                            return pathToUnzippedContents.relativize(data.srcFile).toString().replace('\\', '/');
                        },
                        newFileName -> {
                            Data data= getRelevantDataFromTu(null, p); // refresh
                            Path destFile= pathToUnzippedContents.resolve(newFileName);
                            if (data.srcFile != null) {
                                data.srcFile.toFile().renameTo(destFile.toFile());
                            }
                            if (data.file != null) {
                                ProformaUtil.getFile(data.file).replacePath(newFileName);
                            }
                        },
                        ""));
            }
        }
        
        return result;
    }

    @Override
    public String toString() {
        return "MatArtifactProviderFileName [fileId=" + fileId + ", path=" + path + ", pathToUnzippedContents=" + pathToUnzippedContents + ", toString()=" + super.toString() + "]";
    }
    
}
