package proforma.varproforma.util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

import proforma.varproforma.CV;
import proforma.varproforma.CVList;
import proforma.varproforma.CVp;
import proforma.varproforma.DerivativeAggregateType;
import proforma.varproforma.V;
import proforma.varproforma.VarSpecLeafDerive;
import proforma.varproforma.Vp;
import proforma.varproforma.VpT;
import proforma.varproforma.VpType;
import proforma.varproforma.Vt;

public class Derive {

    public static List<V> deriveValuesFrom(Map<String,Object> map, VarSpecLeafDerive leaf) {
        Vp vp= leaf.getEffectiveCVp().get(0);
        DerivativeAggregateType aggregateType= leaf.getAggregateType();
        String jsSource= leaf.getJsSource();
        return deriveFrom(map, vp, aggregateType, jsSource);
    }
    
    
	public static V deriveValueFrom(Map<String,Object> map, Vp vp, String jsSource) {
		List<V> result= deriveFrom(map, vp, DerivativeAggregateType.VALUE, jsSource);
		if (result.size() != 1) throw new IllegalArgumentException("javascript in derivative shluld return a single value, but returned "+result.size()+" values");
		return result.get(0);
	}
	
	public static List<V> deriveValuesFrom(Map<String,Object> map, Vp vp, String jsSource) {
		List<V> result= deriveFrom(map, vp, DerivativeAggregateType.COLLECTION, jsSource);
		return result;
	}
	
	public static synchronized List<V> deriveFrom(Map<String,Object> map, Vp vp, DerivativeAggregateType aggregateType, String jsSource) {
		Log.debug("deriveFrom("+map+","+vp+","+aggregateType+","+jsSource);
		if (jsSource == null) return null;
		List<V> result= new ArrayList<>();
		try {
			String jsConsole= "console = { debug: print, log: print, warn: print, error: print };\n\n";

			// prepare javascript object as parameter of the apply function:
			String param= convertMapToJavaScriptParameter(map);

			Object received = getContext().eval("js", jsConsole+jsSource+"\n\napply("+param+");");
			
			Log.debug("javascript result of type "+received.getClass());
	        switch (aggregateType) {
		        case VALUE: {
					// handle javascript to java type differences:
		        	Object converted= castToClass(received, vp);
		        	result.add(V.fromValue(converted, vp));
		        	break;
		        }
		        case COLLECTION: {
		            for (Object v : convertJavascriptReturnToList(received)) {
                        // handle javascript to java type differences:
                        Object converted= castToClass(v, vp);
                        result.add(V.fromValue(converted, vp));
		            }
		        	break;
		        }
		        case RANGE: {
		        	throw new UnsupportedOperationException();
		        	//break;
		        }
	        }
		} catch (PolyglotException | IllegalArgumentException | IllegalStateException e) {
		    throw new IllegalArgumentException("Cannot evaluate javascript source in derivative", e);
		}
		return result;
	}
	
	private static Map<Class<?>, Class<?>> toWrapperClass;
	static {
		toWrapperClass= new HashMap<>();
		toWrapperClass.put(byte.class, Byte.class);
		toWrapperClass.put(short.class, Short.class);
		toWrapperClass.put(int.class, Integer.class);
		toWrapperClass.put(long.class, Long.class);
		toWrapperClass.put(char.class, Character.class);
		toWrapperClass.put(float.class, Float.class);
		toWrapperClass.put(double.class, Double.class);
		toWrapperClass.put(boolean.class, Boolean.class);
	}
	
	private static boolean isNumberType(Class<?> c) {
		return c.equals(Double.class) || c.equals(Float.class) || c.equals(Integer.class) || c.equals(Long.class) || c.equals(Short.class) || c.equals(Byte.class);
	}
	
    private static Object castToClass(Object o, VpType dataType) {
        if (o == null) {
            throw new IllegalArgumentException("Expected result of type Value, but javascript source in derivative returned "+o);
        }
        if (! (o instanceof Value)) {
            throw new IllegalArgumentException("Expected result of type Value, but javascript source in derivative returned '"+o+"' of type "+o.getClass());
        }
        Value val = (Value)o;
        Class<?> c= dataType.getAssociatedValueType();
        if (c.isPrimitive()) c= toWrapperClass.get(c);
        if (o.getClass().equals(c)) return o;
        IllegalArgumentException t= new IllegalArgumentException("Expected result of type "+c+", but javascript source in derivative returned '"+val+"'");
        if (dataType.isNumberType()) {
            if (!(val.isNumber())) throw t;
            
            if (c.equals(Long.class) && val.fitsInLong()) return val.asLong();
            else if (c.equals(Integer.class) && val.fitsInInt()) return val.asInt();
            else if (c.equals(Short.class) && val.fitsInShort()) return val.asShort();
            else if (c.equals(Byte.class) && val.fitsInByte()) return val.asByte();
            else if (c.equals(Double.class) && val.fitsInDouble()) return val.asDouble();
            else if (c.equals(Float.class) && val.fitsInFloat()) return val.asFloat();
            
            throw t;
        }
        if (dataType.getType().equals(VpT.BOOLEAN)) {
            if (! val.isBoolean() ) throw t;
            return val.asBoolean();
        }
        if (dataType.getType().equals(VpT.STRING)) {
            if (! val.isString() ) throw t;
            return val.asString();
        }
        if (dataType.getType().equals(VpT.CHARACTER)) {
            if (! val.isString() ) throw t;
            String s = val.asString();
            if (s.length() != 1) throw t;
            return s.charAt(0);
        }
        if (dataType.getType().equals(VpT.TABLE)) {

            // The following variant creates a List of values
            List<CV> result= new ArrayList<>();
            for (Object arrayElem : convertJavascriptReturnToList(val)) {
                Map<?,?> arrayElemAsMap= convertJavaScriptReturnToMap(arrayElem);
                Map<String,Object> kv= new HashMap<>();
                for (Object oKey : arrayElemAsMap.keySet()) {
                    Object oValue= arrayElemAsMap.get(oKey);
                    String key= (String)oKey;
                    Object value= castToClass(oValue, dataType.getVp(key));
                    kv.put(key, value);
                }
                CV cv= new CV(new ArrayList<>());
                for (Vp subvp : dataType.getCVp().getVariationPoints()) {
                    cv.getVariants().add(V.fromValue(kv.get(subvp.getKey()), subvp));
                }
                result.add(cv);
            }
            return new CVList(result);
        }
        throw t;
    }

	
	private static Context context;
	
	private static Context getContext() {
	    if (context != null) return context;

	    // see https://stackoverflow.com/a/73497977
	    Engine engine = Engine.newBuilder()
                .option("engine.WarnInterpreterOnly", "false")
                .build();
        context = Context.newBuilder("js").engine(engine)
                .allowExperimentalOptions(true).option("js.nashorn-compat", "true").build();
	    return context;
    }

    private static List<Object> convertJavascriptReturnToList(Object received) {
        if (received == null) {
            throw new IllegalArgumentException("Expected result of type Value, but javascript source in derivative returned "+received);
        }
        if (! (received instanceof Value)) {
            throw new IllegalArgumentException("Expected result of type Value, but javascript source in derivative returned '"+received+"' of type "+received.getClass());
        }
        Value val = (Value)received;
        if (!val.hasArrayElements()) {
            throw new IllegalArgumentException("javascript in derivative should return an array, but returned '"+val+"'");
        }
        List<Object> result= new ArrayList<>();

        for (long i = 0; i < val.getArraySize(); i++) {
            result.add(val.getArrayElement(i));
        }
        return result;
    }
    
    private static Map<?,?> convertJavaScriptReturnToMap(Object received) {
        if (received == null) {
            throw new IllegalArgumentException("Expected result of type Value, but javascript source in derivative returned "+received);
        }
        if (! (received instanceof Value)) {
            throw new IllegalArgumentException("Expected result of type Value, but javascript source in derivative returned '"+received+"' of type "+received.getClass());
        }
        Value val = (Value)received;
        
        if (!val.hasMembers()) {
            throw new IllegalArgumentException("javascript in derivative should return a map - observed '"+val+"'");
        }
        Map<String,Value> receivedAsMap= new HashMap<>();
        Set<String> keys= val.getMemberKeys();
        
        for (String key : keys) {
            Value elem = val.getMember(key);
            receivedAsMap.put(key, elem);
        }
        return receivedAsMap;
    }
    
	private static String convertMapToJavaScriptParameter(Map<String,Object> map) {
        // prepare javascript object as parameter of the apply function:
        ArrayList<String> list= new ArrayList<>();
        for (Object key : map.keySet()) {
            Object val= map.get(key);
            String valAsString;
            if (val instanceof V) {
                // typical error
                throw new IllegalArgumentException("Expecting a map of keys to object values (found '"+val.getClass()+"' instance as value)");
            }
            if (val == null || isNumberType(val.getClass()) || val.getClass().equals(Boolean.class)) {
                valAsString= String.valueOf(val);
            } else if (val instanceof String || val instanceof Character) {
                // string values could contain special characters, that need to be encoded here and decoded on the javascript side:
                valAsString= "decodeURIComponent(\""+JavascriptString.encodeURIComponent(val.toString())+"\")";
            } else if (val instanceof List) {
                ArrayList<String> elems= new ArrayList<>();
                for (Object o : (List<?>)val) {
                    Map<String,Object> subMap;
                    if (o == null) {
                        throw new UnsupportedOperationException("Unsupported: javascript in derivative cannot accept a list parameter with element 'null'");
                    } else if (o instanceof Map) {
                        @SuppressWarnings("unchecked") Map<String,Object> subMapTmp= (Map<String,Object>)o;
                        subMap= subMapTmp;
//                    } else if (o instanceof CS) {
//                        CS cv= (CS)o;
//                        subMap= CSVp.create(cv).getAsKeyOrderedMap();
                    } else {
                        throw new UnsupportedOperationException("Unsupported: javascript in derivative cannot accept a list parameter with elements of type '"+o.getClass()+"'");
                    }
                    String s= convertMapToJavaScriptParameter(subMap);
                    elems.add(s);
                }
                valAsString= "[" + String.join(",", elems) + "]";
            } else if (val instanceof CVList) {
                ArrayList<String> elems= new ArrayList<>();
                for (CV cv : ((CVList)val).getElements()) {
                    Map<String,Object> subMap= getAsKeyOrderedMap(cv);
                    String s= convertMapToJavaScriptParameter(subMap);
                    elems.add(s);
                }
                valAsString= "[" + String.join(",", elems) + "]";
            } else {
                throw new UnsupportedOperationException("Unsupported: javascript in derivative cannot accept parameter of type '"+val.getClass()+"'");
            }
            list.add("\""+key+"\":"+valAsString);
        }   
        String param= "{" + String.join(",", list)+"}";
        return param;
	}
	
    public static LinkedHashMap<String, Object> getAsKeyOrderedMap(CV cv) {
        return getAsKeyOrderedMap(cv, null);
    }

    public static LinkedHashMap<String, Object> getAsKeyOrderedMap(CV cv, List<String> restrictToKeys) {
        return getAsKeyOrderedMap(cv.getCVp(), cv, restrictToKeys);
    }
    
    public static LinkedHashMap<String, Object> getAsKeyOrderedMap(CVp cvp, CV cv) {
        return getAsKeyOrderedMap(cvp, cv, null);
    }
    
    public static LinkedHashMap<String, Object> getAsKeyOrderedMap(CVp cvp, CV cv, List<String> restrictToKeys) {
        LinkedHashMap<String, Object> map= new LinkedHashMap<>();
        for (Vp vp : cvp) {
            String key= vp.getKey();
            if (restrictToKeys == null || restrictToKeys.contains(key)) {
                int index= cvp.indexOf(key);
                V s= cv.get(index);
                Object val;
                if (s instanceof Vt) { // table?
                    List<Map<String, Object>> list= new ArrayList<>();
                    List<CV> cvs= ((Vt)s).getValue().getElements();
                    for (CV cs : cvs) {
                        list.add(getAsKeyOrderedMap(cs));
                    }
                    val= list;
                } else {
                    val= s.getValue();
                }
                map.put(key, val);
            }
        }
        return map;
    }
}
