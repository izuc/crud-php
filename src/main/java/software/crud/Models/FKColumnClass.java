package software.crud.Models;

public class FKColumnClass {

    private String localField;

    private String dataTypeLocal;

    private String tableName1;

    private String fieldName1;

    private String tableChar2;

    private String tableName2;

    private String fieldName2;

    private String dataType2;

    public FKColumnClass(String localField, String dataTypeLocal, String tableName2, String fieldName2,
            String tableName1, String tableChar2, String fieldName1, String dataType2) {
        this.localField = localField;
        this.dataTypeLocal = dataTypeLocal;
        this.tableName2 = tableName2;
        this.fieldName2 = fieldName2;
        this.tableName1 = tableName1;
        this.tableChar2 = tableChar2;
        this.fieldName1 = fieldName1;
        this.dataType2 = dataType2;
    }

    // Getters and Setters

    public String getLocalField() {
        return localField;
    }

    public void setLocalField(String localField) {
        this.localField = localField;
    }

    public String getDataTypeLocal() {
        return dataTypeLocal;
    }

    public void setDataTypeLocal(String dataTypeLocal) {
        this.dataTypeLocal = dataTypeLocal;
    }

    public String getTableName1() {
        return tableName1;
    }

    public void setTableName1(String tableName1) {
        this.tableName1 = tableName1;
    }

    public String getFieldName1() {
        return fieldName1;
    }

    public void setFieldName1(String fieldName1) {
        this.fieldName1 = fieldName1;
    }

    public String getTableChar2() {
        return tableChar2;
    }

    public void setTableChar2(String tableChar2) {
        this.tableChar2 = tableChar2;
    }

    public String getTableName2() {
        return tableName2;
    }

    public void setTableName2(String tableName2) {
        this.tableName2 = tableName2;
    }

    public String getFieldName2() {
        return fieldName2;
    }

    public void setFieldName2(String fieldName2) {
        this.fieldName2 = fieldName2;
    }

    public String getDataType2() {
        return dataType2;
    }

    public void setDataType2(String dataType2) {
        this.dataType2 = dataType2;
    }
}
