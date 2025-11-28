package org.proforma.variability.transfer;

/**
 * <p>
 * This class stores a list of all classes that
 * are needed as class context when marshalling and unmarshalling 
 * objects using JAXB.
 * </p>
 */
public class Domain {

	private static final Class<?>[] DOMAIN = { 
			VarSpecRoot.class,
			CVListVp.class,
            CVp.class,
            CVVp.class,
			Vs.class,
			Vb.class,
			Vi.class,
			Vd.class,
            Vc.class,
            Vt.class,
			TemplateSpec.class
		};

	public static Class<?>[] domain() {
		return DOMAIN;
	}
}