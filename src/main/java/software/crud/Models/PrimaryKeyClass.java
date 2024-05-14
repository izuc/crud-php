
package software.crud.Models;

public class PrimaryKeyClass {
    private String fieldName;
    private String dataType;

    public PrimaryKeyClass() {
    }

    public PrimaryKeyClass(String dataType, String fieldName) {
        this.dataType = dataType;
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
