package mapper;

import constants.SomeConstant;
import constants.SqlConstant;
import constants.SystemLibConstant;
import utils.HiveUtil;
import utils.MysqlUtil;
import utils.SystemMetastoreUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:SJZYMapper
 * Package:mapper
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/5 - 13:55
 */
public class SJZYMapper {
	private SJZYMapper(){
	}

	//查询数据库列表
	public static List<LinkedHashMap<Object, Object>> queryDatabasesAll(String dataSourceType) throws SQLException, IOException {
		if ("mysql".equals(dataSourceType)){
			String querySql = String.format(SqlConstant.database_show);
			return MysqlUtil.processSql(querySql, null);
		}else{
			String querySql = String.format(SqlConstant.database_show);
			return HiveUtil.processHql(querySql, null);
		}
	}

	//查询数据表列表
	public static List<LinkedHashMap<Object, Object>> queryDatatablesAll(String dataSourceType, String database) throws SQLException, IOException {
		if ("mysql".equals(dataSourceType)){
			String querySql = String.format(SqlConstant.table_show, database);
			return MysqlUtil.processSql(querySql, null);
		}else{
			String querySql = String.format(SqlConstant.table_show, database);
			return HiveUtil.processHql(querySql, null);
		}
	}

	//查询数据字段列表
	public static List<LinkedHashMap<Object, Object>> queryFieldsAll(String dataSourceType, List<List<Object>> paramsList) throws SQLException, IOException {
		if ("mysql".equals(dataSourceType)){
			String queryFieldSql = String.format(SqlConstant.database_change + "select * from `%s` where `%s` = ? and `%s` = ?;",
					SomeConstant.INFORMATION_SCHEMA, SomeConstant.INFORMATION_SCHEMA_TABLE,
					SomeConstant.INFORMATION_SCHEMA_COLUMNS_BASE,
					SomeConstant.INFORMATION_SCHEMA_COLUMNS_TABLE);
			return MysqlUtil.processSql(queryFieldSql, paramsList);
		}else{
			String queryFieldSql = String.format("use `%s`;select `%s`, `%s`, `%s`, `%s`, `%s` from `%s` join `%s` on `%s`.`%s` = `%s`.`%s` join `%s` on `%s`.`%s` = `%s`.`%s` where `%s`.`%s` = ? and `%s`.`%s` = ?;",
					SomeConstant.METASTORE,
					SomeConstant.METASTORE_COL_CDID, SomeConstant.METASTORE_COL_COMMENT, SomeConstant.METASTORE_COL_COLNAME, SomeConstant.METASTORE_COL_TYPE, SomeConstant.METASTORE_COL_INDEX,
					SomeConstant.METASTORE_COL,
					SomeConstant.METASTORE_TABLE,
					SomeConstant.METASTORE_TABLE, SomeConstant.METASTORE_TABLE_ID,
					SomeConstant.METASTORE_COL, SomeConstant.METASTORE_COL_CDID,
					SomeConstant.METASTORE_DB,
					SomeConstant.METASTORE_DB, SomeConstant.METASTORE_DB_ID,
					SomeConstant.METASTORE_TABLE, SomeConstant.METASTORE_TABLE_DBID,
					SomeConstant.METASTORE_DB, SomeConstant.METASTORE_DB_NAME,
					SomeConstant.METASTORE_TABLE, SomeConstant.METASTORE_TABLE_NAME);
			return MysqlUtil.processSql(queryFieldSql, paramsList);
		}
	}

	//查询hive表文件路径
	public static List<LinkedHashMap<Object, Object>> queryHdfsFilePath(List<List<Object>> paramsList) throws SQLException, IOException {
		String querySql = String.format(SqlConstant.database_change + "select `%s` from `%s` join `%s` on `%s`.`%s` = `%s`.`%s` join `%s` on `%s`.`%s` = `%s`.`%s` where `%s`.`%s` = ? and `%s`.`%s` = ?;",
				SomeConstant.METASTORE, SomeConstant.METASTORE_SDS_LOC, SomeConstant.METASTORE_SDS,
				SomeConstant.METASTORE_TABLE,
				SomeConstant.METASTORE_TABLE, SomeConstant.METASTORE_TABLE_SDID,
				SomeConstant.METASTORE_SDS, SomeConstant.METASTORE_SDS_SDID,
				SomeConstant.METASTORE_DB,
				SomeConstant.METASTORE_DB, SomeConstant.METASTORE_DB_ID,
				SomeConstant.METASTORE_TABLE, SomeConstant.METASTORE_TABLE_DBID,
				SomeConstant.METASTORE_DB, SomeConstant.METASTORE_DB_NAME,
				SomeConstant.METASTORE_TABLE, SomeConstant.METASTORE_TABLE_NAME);
		return MysqlUtil.processSql(querySql, paramsList);
	}
}
