package proforma.varproforma.util;
//
//import java.util.List;
//import java.util.Objects;
//import java.util.function.Consumer;
//import java.util.function.Supplier;
//
//import proforma.varproforma.CV;
//import proforma.varproforma.CVVp;
//import proforma.varproforma.MatMethod;
//import proforma.varproforma.MatMethodMapping;
//import proforma.varproforma.V;
//import proforma.varproforma.Vp;
//import proforma.varproforma.VpT;
//
///**
// * <p>This materialization method supplies a mapping of a variation point value and writes the mapped value
// *  to the consumer of a MatArtifact.</p>
// * 
// * <p>The written value is resolved from the variation points. Usually this method is restricted
// * to a single variation point. If there are several variation points, any of the resolved variation
// * point values is written.</p>
// */
//public class MatMethodProviderMapVpValue implements MatMethodProvider<Object>  {
//
//    private V defaultValue;
//    
//    private List<MatMethodMapping> mappings;
//    
//    private VpT sourceDataType;
//    private VpT targetDataType;
//    
//    private CVVp cvvp;
//    
//
//
//    @Override
//    public void init(MatMethod m, CV cv) {
//        this.defaultValue= m.getDefaultValue();
//        this.mappings= m.getMappings();
//        
//        this.cvvp= CVVp.create(cv);
//        if (!m.getRestrictVp().isEmpty()) {
//            this.cvvp= this.cvvp.projectToCVp(m.getRestrictVp().toArray(new String[0]));
//        }
//        for (Vp vp : this.cvvp.getCVp()) {
//            if (sourceDataType == null) {
//                sourceDataType= vp.getType();
//            } else if (! vp.getType().equals(sourceDataType)) {
//                throw new IllegalArgumentException("Variation point '"+vp.getKey()+"' does not match data-type '"+sourceDataType+"'");
//            }
//        }
//        
//        for (MatMethodMapping mapping : this.mappings) {
//            V from= mapping.getFrom();
//            V to= mapping.getTo();
//            if (sourceDataType != null && from != null) {
//                if (!sourceDataType.getAssociatedValueType().equals(from.getValueType())) {
//                    throw new IllegalArgumentException("Mapping ('"+from+"', '"+to+"') does not match vp type '"+sourceDataType+"'");
//                }
//            }
//            if (targetDataType == null) {
//                targetDataType= VpT.fromV(to);
//            } else if (! targetDataType.equals(VpT.fromV(to))) {
//                throw new IllegalArgumentException("Mapping ('"+from+"', '"+to+"') does not match target data-type '"+targetDataType+"' of a previous mapping");
//            }
//        }
//    }
//
//    @Override
//    public void execute(Supplier<Object> unusedSupplier, Consumer<Object> consumer, String hint) {
//        V from= this.cvvp.getCV().get(0);
//        V to= this.defaultValue;
//        for (MatMethodMapping mapping : this.mappings) {
//            if (Objects.equals(from, mapping.getFrom())) {
//                to= mapping.getTo();
//                break;
//            }
//        }
//        Object result= to.getValue();
//        consumer.accept(result);
//    }
//
//    @Override
//    public Class<?> getValueType() {
//        if (targetDataType == null) return null;
//        return targetDataType.getAssociatedValueType();
//    }
//
//    @Override
//    public String toString() {
//        return "MatMethodProviderMapVpValue [defaultValue=" + defaultValue + ", mappings=" + mappings
//                + ", sourceDataType=" + sourceDataType + ", targetDataType=" + targetDataType + ", cvvp=" + cvvp + "]";
//    }
//    
//
//    
//
//    
//}
