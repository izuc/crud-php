package software.crud.Models;

public class CrudMessage {

    private String message;
    private boolean isSuccess;

    public CrudMessage(String message) {
        this.message = message;
        this.isSuccess = true;
    }

    public CrudMessage(String message, boolean isSuccess) {
        this.message = message;
        this.isSuccess = isSuccess;
    }

    // Getters and Setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
