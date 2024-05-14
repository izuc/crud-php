package software.crud.Models;

import java.util.Map;

public class CodeInput<T> {

	private Map<String, T> finalDataDic;
	private String destinationFolder;

	// Getters and Setters

	public Map<String, T> getFinalDataDic() {
		return finalDataDic;
	}

	public void setFinalDataDic(Map<String, T> finalDataDic) {
		this.finalDataDic = finalDataDic;
	}

	public String getDestinationFolder() {
		return destinationFolder;
	}

	public void setDestinationFolder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}
}
