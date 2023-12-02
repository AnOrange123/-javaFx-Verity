package beans.sjtb.dataxJsonBeans;

import java.util.List;

/**
 * ClassName:MysqlReaderParameter
 * Package:beans.sjtb.dataxJsonBeans
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/27 - 15:20
 */
public class MysqlWriterParameter extends Parameter{

	private String writeMode;
	private String username;
	private String password;
	private List<String> column;
	private List<String> session;
	private List<String> preSql;
	private List<Connection> connection;
	public void setWriteMode(String writeMode) {
		this.writeMode = writeMode;
	}
	public String getWriteMode() {
		return writeMode;
	}

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

	public void setSession(List<String> session) {
		this.session = session;
	}
	public List<String> getSession() {
		return session;
	}

	public void setPreSql(List<String> preSql) {
		this.preSql = preSql;
	}
	public List<String> getPreSql() {
		return preSql;
	}

	public void setConnection(List<Connection> connection) {
		this.connection = connection;
	}

	public List<Connection> getConnection() {
		return connection;
	}

}