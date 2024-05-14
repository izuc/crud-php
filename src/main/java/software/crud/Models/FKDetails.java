package software.crud.Models;

public class FKDetails {

    private String COLUMN_NAME;
    private String CONSTRAINT_NAME;
    private String REFERENCED_COLUMN_NAME;
    private String REFERENCED_TABLE_NAME;

    // Getters and Setters for COLUMN_NAME
    public String getCOLUMN_NAME() {
        return COLUMN_NAME;
    }

    public void setCOLUMN_NAME(String COLUMN_NAME) {
        this.COLUMN_NAME = COLUMN_NAME;
    }

    // Getters and Setters for CONSTRAINT_NAME
    public String getCONSTRAINT_NAME() {
        return CONSTRAINT_NAME;
    }

    public void setCONSTRAINT_NAME(String CONSTRAINT_NAME) {
        this.CONSTRAINT_NAME = CONSTRAINT_NAME;
    }

    // Getters and Setters for REFERENCED_COLUMN_NAME
    public String getREFERENCED_COLUMN_NAME() {
        return REFERENCED_COLUMN_NAME;
    }

    public void setREFERENCED_COLUMN_NAME(String REFERENCED_COLUMN_NAME) {
        this.REFERENCED_COLUMN_NAME = REFERENCED_COLUMN_NAME;
    }

    // Getters and Setters for REFERENCED_TABLE_NAME
    public String getREFERENCED_TABLE_NAME() {
        return REFERENCED_TABLE_NAME;
    }

    public void setREFERENCED_TABLE_NAME(String REFERENCED_TABLE_NAME) {
        this.REFERENCED_TABLE_NAME = REFERENCED_TABLE_NAME;
    }
}
