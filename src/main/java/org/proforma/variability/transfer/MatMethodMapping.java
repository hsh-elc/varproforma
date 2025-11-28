package org.proforma.variability.transfer;

//import javax.xml.bind.annotation.XmlAccessType;
//import javax.xml.bind.annotation.XmlAccessorType;
//import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlElements;
//import javax.xml.bind.annotation.XmlType;
//
//@XmlAccessorType(XmlAccessType.FIELD)
//@XmlType(name="mat-method-mapping", propOrder = {
//        "from", "to"
//    })
//public class MatMethodMapping {
//
//    @XmlElements({
//        @XmlElement(name="from-integer", type=Vi.class),
//        @XmlElement(name="from-double", type=Vd.class),
//        @XmlElement(name="from-string", type=Vs.class),
//        @XmlElement(name="from-boolean", type=Vb.class),
//        @XmlElement(name="from-character", type=Vc.class),
//        @XmlElement(name="from-table", type=Vt.class)
//    })
//    private V from;
//    
//    @XmlElements({
//        @XmlElement(name="to-integer", type=Vi.class),
//        @XmlElement(name="to-double", type=Vd.class),
//        @XmlElement(name="to-string", type=Vs.class),
//        @XmlElement(name="to-boolean", type=Vb.class),
//        @XmlElement(name="to-character", type=Vc.class),
//        @XmlElement(name="to-table", type=Vt.class)
//    })
//    private V to;
//  
//    public MatMethodMapping() {
//        
//    }
//    
//    public MatMethodMapping(V from, V to) {
//        this.from = from;
//        this.to = to;
//    }
//
//    public MatMethodMapping(MatMethodMapping other) {
//        this.from= other.from;
//        this.to= other.to;
//    }
//    
//    public MatMethodMapping clone() {
//        return new MatMethodMapping(this);
//    }
//
//    public V getFrom() {
//        return from;
//    }
//
//    public void setFrom(V from) {
//        this.from = from;
//    }
//
//    public V getTo() {
//        return to;
//    }
//
//    public void setTo(V to) {
//        this.to = to;
//    }
//
//    @Override
//    public String toString() {
//        return "MatMethodMapping [from=" + from + ", to=" + to + "]";
//    }
//
//    
//}
