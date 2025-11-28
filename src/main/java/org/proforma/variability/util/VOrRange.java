package org.proforma.variability.util;

import org.proforma.variability.transfer.V;
import org.proforma.variability.transfer.Vis;
import org.proforma.variability.transfer.Vns;

public class VOrRange {
    
    private VisOrRange visOrRange;
    private Vns sns;
    
    static VOrRange fromS(V s) {
        if (s.isIntervalScaled()) {
            return new VOrRange(new VisOrRange((Vis)s));
        } else {
            return new VOrRange((Vns)s);
        }
    }
    
    public VOrRange(VisOrRange sisOrRange) {
        this.visOrRange= sisOrRange;
    }
    
    public VOrRange(Vns sns) {
        this.sns= sns;
    }
    
    public boolean isIntervalScaled() {
        return visOrRange != null;
    }
    
    public boolean isNominalScaled() {
        return sns != null;
    }
    
    public boolean isSingle() {
        return isNominalScaled() || visOrRange.isSingle();
    }
    
    public VisOrRange getSisOrRange() {
        return visOrRange;
    }
    
    public Vns getSns() {
        return sns;
    }

    public V getSingle() {
        if (isNominalScaled()) {
            return sns;
        }
        return visOrRange.getSingle();
    }
    
    public V getHullMin() {
        if (isNominalScaled()) {
            return sns;
        }
        return visOrRange.getHullMin();
    }
    
    public V getHullMax() {
        if (isNominalScaled()) {
            return sns;
        }
        return visOrRange.getHullMax();
    }
    
    
    public String toString(String prefix)  {
        if (isIntervalScaled()) {
            return prefix + "SOrRange [sisOrRange=" + visOrRange.toString() + "]";
        } else {
            return prefix + "SOrRange [sns=" + sns.toString() + "]";
        }
    }


}
