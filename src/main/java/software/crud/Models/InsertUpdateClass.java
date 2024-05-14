
package software.crud.Models;

public class InsertUpdateClass {
    private String fieldName;
    private String dataType;
    private boolean isRequired;
    private String defaultValue;

    // Constructor
    public InsertUpdateClass(String dataType, String fieldName, boolean isRequired, String defaultValue) {
        this.dataType = dataType;
        this.fieldName = fieldName;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
    }

    // Getters
    public String getFieldName() {
        return fieldName;
    }

    public String getDataType() {
        return dataType;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    // Setters
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
