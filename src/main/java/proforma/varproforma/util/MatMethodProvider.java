package proforma.varproforma.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

import proforma.varproforma.CV;
import proforma.varproforma.MatMethod;

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
