package beans.sjtb.dataxJsonBeans;

import java.util.List;

/**
 * ClassName:HdfsWriterParameter
 * Package:beans.sjtb.dataxJsonBeans
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/27 - 15:20
 */
public class HdfsWriterParameter extends Parameter {
	private String defaultFS;
	private String fileType;
	private String path;
	private String fileName;
	private List<Column> column;
	private String writeMode;
	private String fieldDelimiter;
	private String compress;
	public void setDefaultFS(String defaultFS) {
		this.defaultFS = defaultFS;
	}
	public String getDefaultFS() {
		return defaultFS;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileType() {
		return fileType;
	}

	public void setPath(String path) {
		this.path = path;
	}
	public String getPath() {
		return path;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileName() {
		return fileName;
	}

	public void setColumn(List<Column> column) {
		this.column = column;
	}
	public List<Column> getColumn() {
		return column;
	}

	public void setWriteMode(String writeMode) {
		this.writeMode = writeMode;
	}
	public String getWriteMode() {
		return writeMode;
	}

	public void setFieldDelimiter(String fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}
	public String getFieldDelimiter() {
		return fieldDelimiter;
	}

	public void setCompress(String compress) {
		this.compress = compress;
	}
	public String getCompress() {
		return compress;
	}

}
