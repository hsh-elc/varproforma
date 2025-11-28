package org.proforma.variability.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import org.proforma.variability.transfer.MatArtifact;


public class MatArtifactProviderTaskXml implements MatArtifactProvider<String> {
    
    private Path pathToTaskXml;
    
    
    @Override
    public void init(MatArtifact a, Ribbon<TaskUnzipped> tu) {
        this.pathToTaskXml= tu.get().getPathToTaskXml();

        if (this.pathToTaskXml == null) {
            throw new IllegalArgumentException("pathToTaskXml should not be null!");
        }
        if (!this.pathToTaskXml.toFile().exists()) {
            throw new IllegalArgumentException("Cannot find file '"+this.pathToTaskXml+"'");
        }
    }
    
    
    @Override
    public Iterable<Ribbon<String>> getItems() {
        return Arrays.asList(new Ribbon<>(
                () -> {
                    try {
                        return new String(java.nio.file.Files.readAllBytes(pathToTaskXml), "UTF-8");
                    } catch (IOException e) {
                        throw new IllegalArgumentException("Cannot read file '"+pathToTaskXml+"'", e);
                    }
                },
                newContent -> {
                    try {
                        java.nio.file.Files.write(pathToTaskXml, newContent.getBytes("UTF-8"));
                    } catch (IOException e) {
                        throw new IllegalArgumentException("Cannot write file '"+pathToTaskXml+"'", e);
                    }
                },
                ""));
    }

    @Override
    public String toString() {
        return "MatArtifactProviderTaskXml [pathToTaskXml=" + pathToTaskXml + ", toString()=" + super.toString() + "]";
    }
}
