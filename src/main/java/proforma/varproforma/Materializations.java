package proforma.varproforma;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="materializations-type")
public class Materializations {
    
    @XmlElement(name="materialization")
    private List<Materialization> materialization;

    public Materializations() {
        
    }

    public Materializations(Materializations other) {
        if (other.materialization != null) {
            this.materialization= new ArrayList<>();
            for (Materialization c : other.getMaterialization()) {
                this.materialization.add(c.clone());
            }
        }
    }

    public List<Materialization> getMaterialization() {
        if (materialization == null) {
            materialization = new ArrayList<>();
        }
        return this.materialization;
    }


    public Materializations addMaterialization(Materialization r) {
        getMaterialization().add(r);
        return this;
    }

    @Override
    public String toString() {
        return "Materializations [materialization=" + materialization + "]";
    }
    
    

}
