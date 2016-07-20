package com.xpand.pdi.step.userAgent;


import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

/**
 * Created by dams on 09-07-2016.
 */
public class UserAgentStepMetaFunction implements Cloneable {

    private static Class<?> PKG = UserAgentStepMeta.class;

    public static final String XML_TAG = "UserAgentFields";

    private String fieldName;
    private String field;
    private String typeField;

    public UserAgentStepMetaFunction(String fieldName, String field, String typeField) {
        this.fieldName = fieldName;
        this.field = field;
        this.typeField = typeField;
    }

    public UserAgentStepMetaFunction() {

    }

    public UserAgentStepMetaFunction(Node userNode) {
        fieldName = XMLHandler.getTagValue(userNode, "fieldName");
        field = XMLHandler.getTagValue(userNode, "field");
        typeField = XMLHandler.getTagValue(userNode, "typeField");
    }

    public UserAgentStepMetaFunction(Repository rep, ObjectId id_step, int nr) throws KettleException {
        fieldName = rep.getStepAttributeString(id_step, nr, "fieldName");
        field = rep.getStepAttributeString(id_step, nr, "field");
        typeField = rep.getStepAttributeString(id_step, nr, "typeField");
    }

    public String getXML() {
        String xml = "";

        xml += "<" + XML_TAG + ">";
        xml += XMLHandler.addTagValue("fieldName", fieldName);
        xml += XMLHandler.addTagValue("field", field);
        xml += XMLHandler.addTagValue("typeField", typeField);

        xml += "</" + XML_TAG + ">";
        return xml;
    }

    public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step, int nr) throws KettleException {
        rep.saveStepAttribute(id_transformation, id_step, nr, "fieldName", fieldName);
        rep.saveStepAttribute(id_transformation, id_step, nr, "field", field);
        rep.saveStepAttribute(id_transformation, id_step, nr, "typeField", typeField);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && (obj.getClass().equals(this.getClass()))) {
            UserAgentStepMetaFunction mf = (UserAgentStepMetaFunction) obj;
            return (getXML().equals(mf.getXML()));
        }
        return false;
    }

    @Override
    public Object clone() {
        try {
            UserAgentStepMetaFunction retval = (UserAgentStepMetaFunction) super.clone();
            return retval;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setTypeField(String typeField) {
        this.typeField = typeField;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getField() {
        return field;
    }

    public String getTypeField() {
        return typeField;
    }

}
