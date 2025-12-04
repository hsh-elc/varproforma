package proforma.varproforma.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import proforma.xml21.TaskFilesType;
import proforma.xml21.TaskType;
import proforma.varproforma.MatArtifact;
import proforma.varproforma.util.ProformaUtil.AbstractFileChoiceGroup;
import proforma.xml21.TaskFileType;

public class MatArtifactProviderFileExistences implements MatArtifactProvider<Boolean> {
    

    private List<String> fileId;

    private List<String> path;

    private TaskUnzipped tu;

    private TaskFilesType files;

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
        private Path srcFileToBeDeleted;
        private List<String> fileIdsToBeDeleted;
        public Data(Path srcFileToBeDeleted, List<String> fileIdsToBeDeleted) {
            this.srcFileToBeDeleted = srcFileToBeDeleted;
            this.fileIdsToBeDeleted = fileIdsToBeDeleted;
        }
    }
    
    private Data getRelevantDataFromTu(String fid, String p) {
        TaskType task= tu.getTask();
        if (task == null) {
            throw new IllegalArgumentException("Task could not be read!");
        }
    
        this.files= task.getFiles();
        
        Path pathToUnzippedContents= tu.getPathToUnzippedContents();

        List<String> fileIdsToBeDeleted= new ArrayList<>();
        Path srcFileToBeDeleted= null;
        if (fid != null) {
            TaskFileType file= ProformaUtil.findById(files, fid);
            if (file == null) throw  new IllegalArgumentException("Unexpected file id '"+fid+"'");

            fileIdsToBeDeleted.add(fid);

            // We delete attached files from the zip:
            AbstractFileChoiceGroup fcg= ProformaUtil.getFile(file);
            if (fcg.isAttached()) {
                srcFileToBeDeleted= pathToUnzippedContents.resolve(fcg.getPath());
            }
        } 
        if (p != null) {
            for (TaskFileType file : files.getFile()) {
                AbstractFileChoiceGroup fcg= ProformaUtil.getFile(file);
                if (fcg.isAttached()) {
                    if (fcg.getPath().startsWith(p)) {
                        fileIdsToBeDeleted.add(file.getId());
                    }
                }
            }
            srcFileToBeDeleted= pathToUnzippedContents.resolve(p);
        }
        return new Data(srcFileToBeDeleted, fileIdsToBeDeleted);
    }
    
    private void act(Data data) {
        if (data.srcFileToBeDeleted != null) {
            File f= data.srcFileToBeDeleted.toFile();
            boolean deleted;
            if (f.isDirectory()) {
                try {
                    deleted= java.nio.file.Files.walk(data.srcFileToBeDeleted)
                        .sorted(Comparator.reverseOrder())
                        .allMatch( p -> p.toFile().delete() );
                } catch (IOException e) {
                    throw new IllegalArgumentException("Cannot walk dir '"+data.srcFileToBeDeleted+"'.", e);
                }
            } else {
                deleted= f.delete();
            }
            if (!deleted) {
                throw new IllegalArgumentException("Cannot delete file '"+data.srcFileToBeDeleted+"'.");
            }
        }
        for (String fileId : data.fileIdsToBeDeleted) {
            boolean removed= ProformaUtil.removeById(this.files, fileId);
            if (!removed) {
                throw new IllegalArgumentException("Cannot remove file element '"+fileId+"'.");
            }
        }
    }
    
    
    @Override
    public Iterable<Ribbon<Boolean>> getItems() {
        ArrayList<Ribbon<Boolean>> result= new ArrayList<>();
        
        if (fileId != null) {
            for (String fid : fileId) {
                result.add(new Ribbon<>(
                        () -> true,
                        existsAfterwards -> {
                            if (!existsAfterwards) {
                                Data data= getRelevantDataFromTu(fid, null); // refresh
                                act(data);
                            }
                        },
                        ""));
            }
        } 
        if (path != null) {
            for (String p : path) {
                result.add(new Ribbon<>(
                        () -> true,
                        existsAfterwards -> {
                            if (!existsAfterwards) {
                                Data data= getRelevantDataFromTu(null, p); // refresh
                                act(data);
                            }
                        },
                        ""));
            }
        }
        
        return result;
    }
    @Override
    public String toString() {
        return "MatArtifactProviderFileExistence [fileId=" + fileId + ", path=" + path 
                + "]";
    }
    
}
