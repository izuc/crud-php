
package software.crud.Models;

public class ColumnModel {
    private String field;
    private String typeName;
    private String isNull;
    private String key;
    private String defaultValue;
    private String extra;
    private FKDetails fkDetails;

    public ColumnModel(String field, String typeName, String isNull, String key, String defaultValue, String extra) {
        this.field = field;
        this.typeName = typeName;
        this.isNull = isNull;
        this.key = key;
        this.defaultValue = defaultValue;
        this.extra = extra;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getIsNull() {
        return isNull;
    }

    public void setIsNull(String isNull) {
        this.isNull = isNull;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public FKDetails getFkDetails() {
        return fkDetails;
    }

    public void setFkDetails(FKDetails fkDetails) {
        this.fkDetails = fkDetails;
    }

    public ColumnModel() {
        this.fkDetails = null;
    }
}
