package org.proforma.variability.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.proforma.variability.transfer.CV;
import org.proforma.variability.transfer.CVVp;
import org.proforma.variability.transfer.MatMethod;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class MatMethodProviderMustache implements MatMethodProvider<String>  {

    private String prefix;
    
    private String suffix;

    private Map<String,Object> map;

    private MustacheFactory mf;
    

    

    
    @Override
    public void init(MatMethod m, CV cv) {
        this.prefix= m.getPrefix();
        this.suffix= m.getSuffix();
        
        CVVp cvvp= CVVp.create(cv);
        if (!m.getRestrictVp().isEmpty()) {
            cvvp= cvvp.projectToCVp(m.getRestrictVp().toArray(new String[0]));
        }
        this.map= cvvp.getAsKeyOrderedMap();
//        expandAllCVSpecValues(this.map);
        this.mf = new DefaultMustacheFactory();
        if (prefix != null || suffix != null) {
            if (prefix == null) prefix= "{{";
            if (suffix == null) suffix= "}}";
        }
    }
    

    @Override
    public Class<?> getValueType() {
        return String.class;
    }

    
//    private static void expandAllCVSpecValues(Map<String,Object> map) {
//        List<Map.Entry<String, Object>> expanded= new ArrayList<>();
//        for (Map.Entry<String, Object> entry : map.entrySet()) {
//            String key= entry.getKey();
//            Object val= entry.getValue();
//            if (val instanceof VtSpec) {
//                CVSet set= ((VtSpec)val).expand();
//                ArrayList<Map<String, Object>> list= new ArrayList<>();
//                for (CV subCv : set.getCVs()) {
//                    CVr subCvr= CVr.createValue(subCv);
//                    Map<String,Object> subMap= subCvr.getAsKeyOrderedMap();
//                    expandAllCVSpecValues(subMap);
//                    list.add(subMap);
//                }
//                expanded.add(new AbstractMap.SimpleEntry<>(key, list));
//            }
//        }
//        for (Map.Entry<String, Object> entry : expanded) {
//            map.put(entry.getKey(), entry.getValue());
//        }
//    }
    
    private String materializeString(String template, String hint) {
        StringWriter writer = new StringWriter();
        if (prefix != null || suffix != null) {
            StringBuilder sb= new StringBuilder();
            sb.append("{{=").append(prefix).append(" ").append(suffix).append("=}}");
            sb.append(template);
            template= sb.toString();
        }
        Mustache mustache = mf.compile(new StringReader(template), "1");
        mustache.execute(writer, this.map);
        writer.flush();
        return writer.toString();
    }

    @Override
    public void execute(Supplier<String> supplier, Consumer<String> consumer, String hint) {
        consumer.accept(materializeString(supplier.get(), hint));
    }

    @Override
    public String toString() {
        return "MatMethodMustache [map=" + map + ", toString()=" + super.toString() + "]";
    }
    
}
