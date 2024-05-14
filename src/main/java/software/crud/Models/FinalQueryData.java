package software.crud.Models;

import java.util.List;

public class FinalQueryData {

    private String tableName;
    private String tableModuleName;
    private SelectQueryData selectQueryData;
    private InsertUpdateQueryData insertUpdateQueryData;
    private List<PrimaryKeyClass> primaryKeys;
    private String primaryKeyString;
    private String primaryKeyCommaString;
    private String selectAllQuery;
    private List<ExtraQuery> selectByFKQuery;
    private String selectOneQuery;
    private String deleteQuery;
    private String searchQuery;
    private String searchCountQuery;
    private String searchQueryByColumn;
    private String searchCountQueryByColumn;
    private String selectAllRecordCountQuery;
    private String searchRecordCountQuery;
    private String propertyListString;
    private String createPropertyListString;
    private String primaryKeyControllerString;
    private String insertQuery;
    private String updateQuery;
    private String updatePatchQuery;
    private String updateParam;
    private String updatePatchParam;
    private String insertParam;
    private String requiredFieldString_CreateUpdate;

    // Getters and setters

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableModuleName() {
        return tableModuleName;
    }

    public void setTableModuleName(String tableModuleName) {
        this.tableModuleName = tableModuleName;
    }

    public SelectQueryData getSelectQueryData() {
        return selectQueryData;
    }

    public void setSelectQueryData(SelectQueryData selectQueryData) {
        this.selectQueryData = selectQueryData;
    }

    public InsertUpdateQueryData getInsertUpdateQueryData() {
        return insertUpdateQueryData;
    }

    public void setInsertUpdateQueryData(InsertUpdateQueryData insertUpdateQueryData) {
        this.insertUpdateQueryData = insertUpdateQueryData;
    }

    public List<PrimaryKeyClass> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<PrimaryKeyClass> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public String getPrimaryKeyString() {
        return primaryKeyString;
    }

    public void setPrimaryKeyString(String primaryKeyString) {
        this.primaryKeyString = primaryKeyString;
    }

    public String getPrimaryKeyCommaString() {
        return primaryKeyCommaString;
    }

    public void setPrimaryKeyCommaString(String primaryKeyCommaString) {
        this.primaryKeyCommaString = primaryKeyCommaString;
    }

    public String getSelectAllQuery() {
        return selectAllQuery;
    }

    public void setSelectAllQuery(String selectAllQuery) {
        this.selectAllQuery = selectAllQuery;
    }

    public List<ExtraQuery> getSelectByFKQuery() {
        return selectByFKQuery;
    }

    public void setSelectByFKQuery(List<ExtraQuery> selectByFKQuery) {
        this.selectByFKQuery = selectByFKQuery;
    }

    public String getSelectOneQuery() {
        return selectOneQuery;
    }

    public void setSelectOneQuery(String selectOneQuery) {
        this.selectOneQuery = selectOneQuery;
    }

    public String getDeleteQuery() {
        return deleteQuery;
    }

    public void setDeleteQuery(String deleteQuery) {
        this.deleteQuery = deleteQuery;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getSearchCountQuery() {
        return searchCountQuery;
    }

    public void setSearchCountQuery(String searchCountQuery) {
        this.searchCountQuery = searchCountQuery;
    }

    public String getSearchQueryByColumn() {
        return searchQueryByColumn;
    }

    public void setSearchQueryByColumn(String searchQueryByColumn) {
        this.searchQueryByColumn = searchQueryByColumn;
    }

    public String getSearchCountQueryByColumn() {
        return searchCountQueryByColumn;
    }

    public void setSearchCountQueryByColumn(String searchCountQueryByColumn) {
        this.searchCountQueryByColumn = searchCountQueryByColumn;
    }

    public String getSelectAllRecordCountQuery() {
        return selectAllRecordCountQuery;
    }

    public void setSelectAllRecordCountQuery(String selectAllRecordCountQuery) {
        this.selectAllRecordCountQuery = selectAllRecordCountQuery;
    }

    public String getSearchRecordCountQuery() {
        return searchRecordCountQuery;
    }

    public void setSearchRecordCountQuery(String searchRecordCountQuery) {
        this.searchRecordCountQuery = searchRecordCountQuery;
    }

    public String getPropertyListString() {
        return propertyListString;
    }

    public void setPropertyListString(String propertyListString) {
        this.propertyListString = propertyListString;
    }

    public String getCreatePropertyListString() {
        return createPropertyListString;
    }

    public void setCreatePropertyListString(String createPropertyListString) {
        this.createPropertyListString = createPropertyListString;
    }

    public String getPrimaryKeyControllerString() {
        return primaryKeyControllerString;
    }

    public void setPrimaryKeyControllerString(String primaryKeyControllerString) {
        this.primaryKeyControllerString = primaryKeyControllerString;
    }

    public String getInsertQuery() {
        return insertQuery;
    }

    public void setInsertQuery(String insertQuery) {
        this.insertQuery = insertQuery;
    }

    public String getUpdateQuery() {
        return updateQuery;
    }

    public void setUpdateQuery(String updateQuery) {
        this.updateQuery = updateQuery;
    }

    public String getUpdatePatchQuery() {
        return updatePatchQuery;
    }

    public void setUpdatePatchQuery(String updatePatchQuery) {
        this.updatePatchQuery = updatePatchQuery;
    }

    public String getUpdateParam() {
        return updateParam;
    }

    public void setUpdateParam(String updateParam) {
        this.updateParam = updateParam;
    }

    public String getUpdatePatchParam() {
        return updatePatchParam;
    }

    public void setUpdatePatchParam(String updatePatchParam) {
        this.updatePatchParam = updatePatchParam;
    }

    public String getInsertParam() {
        return insertParam;
    }

    public void setInsertParam(String insertParam) {
        this.insertParam = insertParam;
    }

    public String getRequiredFieldString_CreateUpdate() {
        return requiredFieldString_CreateUpdate;
    }

    public void setRequiredFieldString_CreateUpdate(String requiredFieldString_CreateUpdate) {
        this.requiredFieldString_CreateUpdate = requiredFieldString_CreateUpdate;
    }

}
