package proforma.varproforma;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "template-spec-type", propOrder = {"varSpec", "defaultValue", "matSpec"})
@XmlRootElement(name="template-spec")
public class TemplateSpec {

    @XmlElement(name="var-spec", required=true)
    private VarSpecRoot varSpec;

    @XmlElement(name="default-value", required=true)
    private CV defaultValue;
    
    @XmlElement(name="mat-spec", required=true)
    private MatSpec matSpec;

    public TemplateSpec() {
        
    }

    public TemplateSpec(VarSpecRoot cvs, CV defaultValue, MatSpec matSpec) {
        this.varSpec = cvs;
        this.defaultValue = defaultValue;
        this.matSpec= matSpec;
    }
    
    public TemplateSpec(TemplateSpec other) {
        this.varSpec= new VarSpecRoot(other.varSpec);
        this.defaultValue= new CV(other.defaultValue);
        this.matSpec= new MatSpec(other.matSpec);
    }

    /**
     * Postprocessing 
     */
    public void afterUnmarshal(Unmarshaller unused1, Object unused2) {
        defaultValue.pushCVp(varSpec.getCVp());
    }


    public VarSpecRoot getVarSpec() {
        return varSpec;
    }
    public void setVarSpec(VarSpecRoot varSpec) {
        this.varSpec = varSpec;
    }
    public CV getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * This stores a copy of the parameter after being converted to value representation (no specs).
     * @param defaultValue  The parameter must be fully equipped with associated Vps. This can be accomplished 
     * by calling {@link CV#pushCVp(CVp)} immediately before. Alternately call {@link #setVarSpec(CVSpec, CV)}.
     */
    public void setDefaultValue(CV defaultValue) {
        this.defaultValue = defaultValue.switchToValue();
    }

    /**
     * This stores both variability data. The default value will automatically be equipped with the Vps from 
     * the cvSpec parameter and automatically converted to value representation (no specs).
     * @param cvSpec
     * @param defaultValue
     */
    public void setVarSpec(VarSpecRoot cvSpec, CV defaultValue) {
        this.varSpec= cvSpec;
        defaultValue.pushCVp(cvSpec.getCVp());
        this.defaultValue= defaultValue.switchToValue();
    }
    
    public MatSpec getMatSpec() {
        return matSpec;
    }

    public void setMatSpec(MatSpec matSpec) {
        this.matSpec = matSpec;
    }
    

    public CVp getCVp() {
        return varSpec.getCVp();
    }

    @Override
    public String toString() {
        return "TemplateSpec [varSpec=" + varSpec + ", defaultValue=" + defaultValue + ", matSpec=" + matSpec
                + "]";
    }

    
	
}
