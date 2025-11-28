package org.proforma.variability.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import proforma.util.div.XmlUtils;

/**
 * This class replaces a possibly wrong implementation 
 * in {@link XmlUtils#unmarshalToObject(InputStream, Class)}
 */
class XmlUtil {
    
    public static <T> T unmarshalToObject(InputStream is, Class<T> clazz) throws Exception {
        JAXBContext c = JAXBContext.newInstance(clazz);
        Unmarshaller u = c.createUnmarshaller();
        Object o = u.unmarshal(is);
        return clazz.cast(o);
    }
    public static <T> T unmarshalToObject(byte[] byteArray, Class<T> clazz) throws Exception {
        try (ByteArrayInputStream baos = new ByteArrayInputStream(byteArray)) {
            return unmarshalToObject(baos, clazz);
        }
    }
}
