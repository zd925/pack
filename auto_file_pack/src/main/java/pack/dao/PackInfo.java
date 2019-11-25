package pack.dao;

public class PackInfo {
	/**
	 * 项目名
	 */
	private String jarName;
	/**
	 *  上传的多文件路径 以,分开
	 */
	private String uploadFilePath;

	/**
	 * 打包 新版本和旧版本
	 */
	private String newVersion;
	private String oldVersion;

	public String getNewVersion() {
		return newVersion;
	}

	public void setNewVersion(String newVersion) {
		this.newVersion = newVersion;
	}

	public String getOldVersion() {
		return oldVersion;
	}

	public void setOldVersion(String oldVersion) {
		this.oldVersion = oldVersion;
	}

	public void setJarName(String jarName) {
		this.jarName = jarName;
	}

	public String getJarName() {
		return jarName;
	}

	public String getUploadFilePath() {
		return uploadFilePath;
	}

	public void setUploadFilePath(String uploadFilePath) {
		this.uploadFilePath = uploadFilePath;
	}
}
