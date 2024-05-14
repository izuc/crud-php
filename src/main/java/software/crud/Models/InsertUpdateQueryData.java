
package software.crud.Models;

import java.util.List;

public class InsertUpdateQueryData {

    private List<ColumnModel> columnList;
    private List<PrimaryKeyClass> primaryKeys;
    private List<InsertUpdateClass> insertColumnList;
    private List<InsertUpdateClass> updateColumnList;

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

    public List<InsertUpdateClass> getInsertColumnList() {
        return insertColumnList;
    }

    public void setInsertColumnList(List<InsertUpdateClass> insertColumnList) {
        this.insertColumnList = insertColumnList;
    }

    public List<InsertUpdateClass> getUpdateColumnList() {
        return updateColumnList;
    }

    public void setUpdateColumnList(List<InsertUpdateClass> updateColumnList) {
        this.updateColumnList = updateColumnList;
    }
}