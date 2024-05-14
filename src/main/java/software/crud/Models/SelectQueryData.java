package software.crud.Models;

import java.util.List;

public class SelectQueryData {

    private List<ColumnModel> columnList;
    private List<PrimaryKeyClass> primaryKeys;
    private List<JoinColumnClass> joinQueryData;
    private List<FKColumnClass> fkColumnData;
    private List<String> selectColumnList;

    // Getters and Setters for all properties

    public List<ColumnModel> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<ColumnModel> columnList) {
        this.columnList = columnList;
    }

    public List<PrimaryKeyClass> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<PrimaryKeyClass> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public List<JoinColumnClass> getJoinQueryData() {
        return joinQueryData;
    }

    public void setJoinQueryData(List<JoinColumnClass> joinQueryData) {
        this.joinQueryData = joinQueryData;
    }

    public List<FKColumnClass> getFKColumnData() {
        return fkColumnData;
    }

    public void setFkColumnData(List<FKColumnClass> fkColumnData) {
        this.fkColumnData = fkColumnData;
    }

    public List<String> getSelectColumnList() {
        return selectColumnList;
    }

    public void setSelectColumnList(List<String> selectColumnList) {
        this.selectColumnList = selectColumnList;
    }
}
