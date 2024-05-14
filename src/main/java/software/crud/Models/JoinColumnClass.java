package software.crud.Models;

public class JoinColumnClass {

    private String tableName1;

    private String fieldName1;

    private String tableChar2;

    private String tableName2;

    private String fieldName2;

    private ColumnModel column2Data;

    private ColumnModel column1Data;

    public JoinColumnClass(String tableName2, String fieldName2, String tableName1, String tableChar2,
            String fieldName1, ColumnModel column2Data, ColumnModel column1Data) {
        this.tableName2 = tableName2;
        this.fieldName2 = fieldName2;
        this.tableName1 = tableName1;
        this.tableChar2 = tableChar2;
        this.fieldName1 = fieldName1;
        this.column2Data = column2Data;
        this.column1Data = column1Data;
    }

    // Getters and Setters

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

    public ColumnModel getColumn2Data() {
        return column2Data;
    }

    public void setColumn2Data(ColumnModel column2Data) {
        this.column2Data = column2Data;
    }

    public ColumnModel getColumn1Data() {
        return column1Data;
    }

    public void setColumn1Data(ColumnModel column1Data) {
        this.column1Data = column1Data;
    }
}
