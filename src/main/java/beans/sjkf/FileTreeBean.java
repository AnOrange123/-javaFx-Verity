package beans.sjkf;

/**
 * ClassName:FileTreeBean
 * Package:beans.sjkf
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/15 - 0:08
 */
public class FileTreeBean {
	private String fileName;
	private String parentFileName;
	private String fileType;
	private String content;

	public FileTreeBean() {
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getParentFileName() {
		return parentFileName;
	}

	public void setParentFileName(String parentFileName) {
		this.parentFileName = parentFileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
