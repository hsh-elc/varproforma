package proforma.varproforma.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

import proforma.varproforma.CV;
import proforma.varproforma.CVVp;
import proforma.varproforma.MatMethod;
import proforma.varproforma.V;
import proforma.varproforma.Vp;
import proforma.varproforma.VpT;

/**
 * <p>This materialization method supplies a variation point value that is written
 *  to the consumer of a MatArtifact.</p>
 * 
 * <p>The written value is resolved from the variation points. Usually this method is restricted
 * to a single variation point. If there are several variation points, any of the resolved variation
 * point values is written.</p>
 */
public class MatMethodProviderSetVpValue implements MatMethodProvider<Object>  {

    private VpT dataType;
    
    private CVVp cvvp;
    


    @Override
    public void init(MatMethod m, CV cv) {
        this.cvvp= CVVp.create(cv);
        if (!m.getRestrictVp().isEmpty()) {
            this.cvvp= this.cvvp.projectToCVp(m.getRestrictVp().toArray(new String[0]));
        }
        for (Vp vp : this.cvvp.getCVp()) {
            if (dataType == null) {
                dataType= vp.getType();
            } else if (! vp.getType().equals(dataType)) {
                throw new IllegalArgumentException("Variation point '"+vp.getKey()+"' does not match data-type '"+dataType+"'");
            }
        }
    }

    @Override
    public void execute(Supplier<Object> unusedSupplier, Consumer<Object> consumer, String hint) {
        V v= this.cvvp.getCV().get(0);
        Object result= v.getValue();
        consumer.accept(result);
    }

    @Override
    public Class<?> getValueType() {
        if (dataType == null) return null;
        return dataType.getAssociatedValueType();
    }
    

    @Override
    public String toString() {
        return "MatMethodSetVpValue [dataType=" + dataType + ", cvvp=" + cvvp + ", toString()=" + super.toString() + "]";
    }

    
}
