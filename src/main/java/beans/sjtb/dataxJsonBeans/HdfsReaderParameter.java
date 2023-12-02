package beans.sjtb.dataxJsonBeans;

import java.util.List;

/**
 * ClassName:HdfsReaderParameter
 * Package:beans.sjtb.dataxJsonBeans
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/27 - 16:11
 */
public class HdfsReaderParameter extends Parameter{
	private String path;
	private String defaultFS;
	private List<Column> column;
	private String fileType;
	private String encoding;
	private String fieldDelimiter;
	public void setPath(String path) {
		this.path = path;
	}
	public String getPath() {
		return path;
	}

	public void setDefaultFS(String defaultFS) {
		this.defaultFS = defaultFS;
	}
	public String getDefaultFS() {
		return defaultFS;
	}

	public void setColumn(List<Column> column) {
		this.column = column;
	}
	public List<Column> getColumn() {
		return column;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileType() {
		return fileType;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getEncoding() {
		return encoding;
	}

	public void setFieldDelimiter(String fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}
	public String getFieldDelimiter() {
		return fieldDelimiter;
	}

}
