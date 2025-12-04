package proforma.varproforma.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

import proforma.varproforma.CV;
import proforma.varproforma.CVVp;
import proforma.varproforma.MatMethod;
import proforma.varproforma.MatMethodOperator;
import proforma.varproforma.V;
import proforma.varproforma.Vis;

/**
 * <p>This materialization method performs an arithmetic operation on the value supplied by a MatArtifact.</p>
 * 
 * <p>The supplied value stands on the left of the following expression:</p>
 * <pre> suppliedValue &lt;op> &lt;v1> &lt;op> ... &lt;op> &lt;vn></pre>
 * 
 * <p>The operands &lt;vi&gt; are resolved from the variation points. Usually there is 
 * only one operand. Supported operations are add, sub, mul, div.</p>
 */
public class MatMethodProviderArithmeticOperation implements MatMethodProvider<Double>  {

    private MatMethodOperator operator;
    
    private CVVp cvvp;
    

    @Override
    public void init(MatMethod m, CV cv) {
        this.operator= m.getOperator();
        this.cvvp= CVVp.create(cv);
        if (!m.getRestrictVp().isEmpty()) {
            this.cvvp= this.cvvp.projectToCVp(m.getRestrictVp().toArray(new String[0]));
        }
    }

    @Override
    public Class<?> getValueType() {
        return Double.class;
    }

    @Override
    public void execute(Supplier<Double> supplier, Consumer<Double> consumer, String hint) {
        double operand;
        switch (this.operator) {
            case ADD: case SUB: operand= 0.0; break;
            case MUL: case DIV: operand= 1.0; break;
            default: throw new UnsupportedOperationException("Unexpected operation '"+this.operator+"'");
        }
        for (V v : this.cvvp.getCV()) {
            if (! (v instanceof Vis)) {
                throw new UnsupportedOperationException("Unexpected arithmetic operation for variation point type '"+v.getClass()+"'");
            }
            double d= ((Vis)v).toDouble();
            switch (this.operator) {
                case ADD: case SUB: operand += d; break;
                case MUL: case DIV: operand *= d; break;
                default: throw new UnsupportedOperationException("Unexpected operation '"+this.operator+"'");
            }
        }
        Double supplied= supplier.get();
        double result;
        switch (this.operator) {
            case ADD: result = supplied + operand; break;
            case SUB: result = supplied - operand; break;
            case MUL: result = supplied * operand; break;
            case DIV: result = supplied / operand; break;
            default: throw new UnsupportedOperationException("Unexpected operation '"+this.operator+"'");
        }
        consumer.accept(result);
    }


    @Override
    public String toString() {
        return "MatMethodArithmeticOperation [operator=" + operator + ", cvvp=" + cvvp + ", toString()="
                + super.toString() + "]";
    }

    
    
}
