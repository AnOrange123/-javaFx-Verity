package beans.sjtb.dataxJsonBeans;

import java.util.List;

/**
 * ClassName:Connection
 * Package:beans.sjtb.dataxJsonBeans
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/27 - 15:02
 */
public class Connection {
	private List<String> table;
	private String jdbcUrl;
	public void setTable(List<String> table) {
		this.table = table;
	}
	public List<String> getTable() {
		return table;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	public String getJdbcUrl() {
		return jdbcUrl;
	}
}
