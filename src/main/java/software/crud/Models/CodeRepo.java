package software.crud.Models;

import java.util.ArrayList;
import java.util.List;

public class CodeRepo {

    private String functionType;
    private String functionName;
    private String finalURL;
    private String methodName;
    private List<String> getList;
    private List<String> pathParams;

    public CodeRepo() {
        this.getList = new ArrayList<>();
        this.pathParams = new ArrayList<>();
    }

    // Getter and Setter methods for all fields

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFinalURL() {
        return finalURL;
    }

    public void setFinalURL(String finalURL) {
        this.finalURL = finalURL;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getGetList() {
        return getList;
    }

    public void setGetList(List<String> getList) {
        this.getList = getList;
    }

    public List<String> getPathParams() {
        return pathParams;
    }

    public void setPathParams(List<String> pathParams) {
        this.pathParams = pathParams;
    }

    // add a method to add a single path parameter
    public void addPathParam(String param) {
        this.pathParams.add(param);
    }
}
