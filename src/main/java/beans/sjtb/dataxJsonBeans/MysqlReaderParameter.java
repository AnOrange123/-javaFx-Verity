package beans.sjtb.dataxJsonBeans;

import java.util.List;

/**
 * ClassName:MysqlReaderParameter
 * Package:beans.sjtb.dataxJsonBeans
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/27 - 16:06
 */
public class MysqlReaderParameter extends Parameter{
	private String username;
	private String password;
	private List<String> column;
	private String splitPk;
	private List<Connection> connection;
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}

	public void setColumn(List<String> column) {
		this.column = column;
	}
	public List<String> getColumn() {
		return column;
	}

	public void setSplitPk(String splitPk) {
		this.splitPk = splitPk;
	}
	public String getSplitPk() {
		return splitPk;
	}

	public void setConnection(List<Connection> connection) {
		this.connection = connection;
	}
	public List<Connection> getConnection() {
		return connection;
	}
}
