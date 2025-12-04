package proforma.varproforma.util;

import proforma.varproforma.MatArtifact;

public interface MatArtifactProvider<T> {
    
    /**
     * <p>This method supplies task data and the path, where unzipped files are located.
     * An object of this class usually does read only tu.get() in order to get references to those
     * parts of the task that belong to the artifact. A MatMethod might modify these parts in place.</p>
     * 
     * <p>Taken to the extreme, a MatArtifact may provide tu itself to a MatMethod via getItems.
     * So a MatMethod might replace the whole task and even the path of files, that are referenced by
     * the task.</p>
     * 
     * @param tu
     */
    public abstract void init(MatArtifact a, Ribbon<TaskUnzipped> tu);

    public abstract Iterable<Ribbon<T>> getItems();
    
    
}
