package proforma.varproforma.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>A Ribbon connects a data object of type T with a supplier and a consumer.
 * A client of the ribbon can read the data object via the supplier and write or
 * replace the data object via the supplier.</p>
 * 
 * <p>Example:</p>
 * <pre>
 *   java.awt.Point p= new java.awt.Point(4,5);
 *   Ribbon&lt;Integer&gt; rx= new Ribbon&lt;&gt;(() -&gt; p.x, newx -&gt; p.x= newx);
 * </pre>
 * <p>The previous example allows a client to read and replace the x coordinate of p,
 * without knowing about the type of the whole object p.</p>
 * 
 * <p>The name "ribbon" comes from looking at it as a thing that is start and ending in itself,
 * just like a printer ribbon.</p>
 * 
 * @param <T>
 */
public class Ribbon<T> implements Supplier<T>, Consumer<T> {
    private Supplier<T> source;
    private Consumer<T> target;
    private String hint;
    public Ribbon(Supplier<T> source, Consumer<T> target) {
        this(source, target, "");
    }
    public Ribbon(Supplier<T> source, Consumer<T> target, String hint) {
        super();
        this.source = source;
        this.target = target;
        this.hint = hint;
    }
    public Supplier<T> getSource() {
        return source;
    }
    public void setSource(Supplier<T> source) {
        this.source = source;
    }
    
    public Consumer<T> getTarget() {
        return target;
    }
    public void setTarget(Consumer<T> target) {
        this.target = target;
    }
    public String getHint() {
        return hint;
    }
    public void setHint(String hint) {
        this.hint = hint;
    }
    @Override
    public void accept(T t) {
        target.accept(t);
    }
    @Override
    public T get() {
        return source.get();
    }
    
}  
    