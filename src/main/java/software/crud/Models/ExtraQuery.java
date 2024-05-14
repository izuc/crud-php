
package software.crud.Models;

public class ExtraQuery {
    private String selectQuery;
    private String selectCountQuery;
    private String insertQuery;
    private String updateQuery;

    public String getSelectQuery() {
        return selectQuery;
    }

    public void setSelectQuery(String selectQuery) {
        this.selectQuery = selectQuery;
    }

    public String getSelectCountQuery() {
        return selectCountQuery;
    }

    public void setSelectCountQuery(String selectCountQuery) {
        this.selectCountQuery = selectCountQuery;
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
}
