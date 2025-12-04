package proforma.varproforma;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import proforma.varproforma.util.MatArtifactProvider;
import proforma.varproforma.util.MatArtifactProviderAttachedTxtFileContents;
import proforma.varproforma.util.MatArtifactProviderFileExistences;
import proforma.varproforma.util.MatArtifactProviderFileNames;
import proforma.varproforma.util.MatArtifactProviderGradingHintsWeights;
import proforma.varproforma.util.MatArtifactProviderGradingNodeExistences;
import proforma.varproforma.util.MatArtifactProviderTaskXml;

@XmlType(name = "mat-artifact-type-type")
@XmlEnum
public enum MatArtifactType {

    @XmlEnumValue("task-xml")
    TASK_XML("task-xml", VpT.STRING, true, MatArtifactProviderTaskXml.class),
    @XmlEnumValue("file-names")
    FILE_NAMES("file-names", VpT.STRING, false, MatArtifactProviderFileNames.class),
    @XmlEnumValue("attached-txt-file-contents")
    ATTACHED_TXT_FILE_CONTENTS("attached-txt-file-contents", VpT.STRING, false, MatArtifactProviderAttachedTxtFileContents.class),
    @XmlEnumValue("file-existences")
    FILE_EXISTENCES("file-existences", VpT.BOOLEAN, false, MatArtifactProviderFileExistences.class),
    @XmlEnumValue("grading-node-existences")
    GRADING_NODE_EXISTENCES("grading-node-existences", VpT.BOOLEAN, false, MatArtifactProviderGradingNodeExistences.class),
    @XmlEnumValue("grading-hints-weights")
    GRADING_HINTS_WEIGHTS("grading-hints-weights", VpT.DOUBLE, false, MatArtifactProviderGradingHintsWeights.class),
    @XmlEnumValue("other")
    OTHER("other", null, null, null);
    
    
    private final String value;
    private final VpT dataType;
    private final Boolean operatesOnTaskXml;
    private Class<? extends MatArtifactProvider<?>> providerClass;
    
    MatArtifactType(String v, VpT dataType, Boolean operatesOnTaskXml, Class<? extends MatArtifactProvider<?>> providerClass) {
        value = v;
        this.dataType= dataType;
        this.operatesOnTaskXml= operatesOnTaskXml;
        this.providerClass= providerClass;
    }

    public String value() {
        return value;
    }

    public static MatArtifactType fromValue(String v) {
        for (MatArtifactType c: MatArtifactType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public VpT getDataType() {
        return dataType;
    }
    
    public Boolean operatesOnTaskXml() {
        return operatesOnTaskXml;
    }
    
    public Class<? extends MatArtifactProvider<?>> getProviderClass() {
        return providerClass;
    }
}
