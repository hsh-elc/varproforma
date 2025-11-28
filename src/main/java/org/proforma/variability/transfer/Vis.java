package org.proforma.variability.transfer;

import java.lang.reflect.InvocationTargetException;
import javax.xml.bind.annotation.XmlTransient;

import org.proforma.variability.util.VisOrRange;

/**
 * A variant of an interval scaled type.
 */
@XmlTransient
public abstract class Vis extends V {

    @Override
    public abstract Vis clone();
    
    @Override
    public boolean isIntervalScaled() {
        return true;
    }
    
    public abstract boolean isZero();
    public abstract Vis abs();
    public abstract Vis plus(Vis other);
    public abstract Vis minus(Vis other);
    public abstract Vis pred();
    public abstract Vis succ();
    public abstract Vis times(long i);
    public abstract Vis divideBy(long i);
    public abstract long flooredDivideBy(Vis divisor);
    public abstract Vis modulo(long i);
    public abstract double toDouble();
    public abstract Long countStepsFromThisTo(Vis other);
    

    public Vis min(Vis other){
        if (other == null) throw new IllegalArgumentException(getClass()+".min: unexpected argument null");
        if (!other.getClass().equals(this.getClass())) throw new IllegalArgumentException(getClass()+".min: wrong argument type "+other.getClass());
        if (compareTo(other) <= 0) return this;
        return other;
    }
    public Vis max(Vis other) {
        if (other == null) throw new IllegalArgumentException(getClass()+".max: unexpected argument null");
        if (!other.getClass().equals(this.getClass())) throw new IllegalArgumentException(getClass()+".max: wrong argument type "+other.getClass());
        if (compareTo(other) >= 0) return this;
        return other;
    }

    public boolean hasDivisor(Vis query) {
        return query.times(this.flooredDivideBy(query)).minus(this).abs().isZero();
    }

    public static Vis fromDouble(double d, Vp vp) {
        try {
            return (Vis)vp.getRepresentedVSubclass().getDeclaredMethod("fromDouble", double.class, Vp.class).invoke(null, d, vp);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            throw new IllegalArgumentException("Cannot call "+vp.getRepresentedVSubclass()+".fromDouble", e);
        }
    }

    @Override
    public double distanceTo(V choice) {
        if (choice == null || !choice.getClass().equals(getClass())) {
            throw new IllegalArgumentException(getClass()+".distanceTo: unexpected argument type "+(choice == null ? null : choice.getClass()));
        }
        if (choice.equals(this)) return 0.0;
        return this.minus((Vis)choice).abs().toDouble();
    }
    
    public double distanceTo(VisOrRange choice) {
        return choice.distanceTo(new VisOrRange((Vis)this));
    }

    public static Vis zero(Vp vp) {
        return fromDouble(0.0, vp);
    }

}
