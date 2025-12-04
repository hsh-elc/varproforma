package proforma.varproforma;

import javax.xml.bind.annotation.XmlTransient;

/**
 * A variant of a nominal scaled type.
 */
@XmlTransient
public abstract class Vns extends V {

    private static final long serialVersionUID = 1L;

    @Override
    public abstract Vns clone();

    @Override
    public boolean isIntervalScaled() {
        return false;
    }
    

}
