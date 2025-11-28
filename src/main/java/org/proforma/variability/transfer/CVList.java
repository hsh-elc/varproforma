package org.proforma.variability.transfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cv-list-type")
public class CVList implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @XmlElement(name="element")
    private List<CV> elements;
    
    public CVList() {
        
    }
    
    public CVList(List<CV> initialElements) {
        this.elements= initialElements;
    }
    
    public List<CV> getElements() {
        if (elements == null) elements= new ArrayList<>();
        return elements;
    }
    
    
    public CVList duplicateWithReorderedCVp(CVp originalCVp, CVp reorderedCVp) {
        Map<String,Integer> map= new HashMap<>();
        for (Vp vpReordered : reorderedCVp) {
            for (int i=0; i<originalCVp.size(); i++) {
                Vp dimOwn= originalCVp.get(i);
                if (dimOwn.equals(vpReordered)) {
                    map.put(vpReordered.getKey(), i);
                    break;
                }
            }
            if (!map.containsKey(vpReordered.getKey())) {
                String keySpec= null;
                if (originalCVp != null) {
                    List<String> keySpecList= new ArrayList<>();
                    for (Vp vd : originalCVp) keySpecList.add(vd.toString());
                    keySpec= String.join(", ", keySpecList);
                }
                String keySpecReordered= null;
                if (reorderedCVp != null) {
                    List<String> keySpecList= new ArrayList<>();
                    for (Vp vd : reorderedCVp) keySpecList.add(vd.toString());
                    keySpecReordered= String.join(", ", keySpecList);
                }

                throw new IllegalArgumentException("Cannot duplicate "+CVList.class+"("+keySpec+") with unexpected reorderedCVp=("+keySpecReordered+")");
            }
        }

        CVList result= new CVList();
        for (CV cvOwn : getElements()) {
            CV cvReordered= new CV();
            ArrayList<V> itemsReordered= new ArrayList<>();
            for (Vp dimReordered : reorderedCVp) {
                int indexOwn= map.get(dimReordered.getKey());
                V v= cvOwn.getVariants().get(indexOwn);
                itemsReordered.add(v);
            }
            cvReordered.setVariants(itemsReordered);
            result.getElements().add(cvReordered);
        }
        
        return result;
        
    }

    

}
