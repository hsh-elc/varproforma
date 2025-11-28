package org.proforma.variability.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.proforma.variability.transfer.CV;
import org.proforma.variability.transfer.MatMethod;

public interface MatMethodProvider<T> {


    public abstract void init(MatMethod m, CV cv);
    
    /**
     * Must not be called before {@link #init(MatMethod, CV)}.
     */
    public abstract Class<?> getValueType();
    
    /**
     * Must not be called before {@link #init(MatMethod, CV)}.
     */
    public abstract void execute(Supplier<T> source, Consumer<T> target, String hint);



}
