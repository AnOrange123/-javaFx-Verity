package mapper;

import constants.SqlConstant;
import constants.SystemLibConstant;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import utils.SystemMetastoreUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:SJMXMapper
 * Package:mapper
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/5 - 13:51
 */
public class SJMXMapper {
	private SJMXMapper() {
	}

	//保存数据模型
	public static void updateDataModuleInfo(List<List<Object>> paramsList, int rowSize) throws SQLException, IOException {
		String insertSql = String.format(SqlConstant.database_change + "delete from `%s` where `%s` = ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.DATAMODULEDETAILTABLE,
				SystemLibConstant.DATAMODULEDETAIL_MODULENAME,
				SystemLibConstant.DATAMODULEDETAIL_PATH);

		for (int i = 0; i < rowSize; i++) {
			//逐行添加插入语句
			insertSql = String.format(insertSql + "insert into `%s` values (?, ?, ?, ?, ?, ?, ?, ?, ?);", SystemLibConstant.DATAMODULEDETAILTABLE);
		}

		SystemMetastoreUtil.processSql(insertSql, paramsList);
	}

	//血缘查询（上游）
	public static List<LinkedHashMap<Object, Object>> queryParentBlood(List<List<Object>> paramsList) throws SQLException, IOException {
		String querySql = String.format(SqlConstant.database_change + "select `%s` from `%s` where `%s` = ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.DATAMODULEDETAIL_RESOURCETABLENAME, SystemLibConstant.DATAMODULEDETAILTABLE,
				SystemLibConstant.DATAMODULEDETAIL_MODULENAME,
				SystemLibConstant.DATAMODULEDETAIL_PATH);

		return SystemMetastoreUtil.processSql(querySql, paramsList);
	}

	//血缘查询（下游）
	public static List<LinkedHashMap<Object, Object>> queryChildBlood(List<List<Object>> paramsList) throws SQLException, IOException {
		String querySql = String.format(SqlConstant.database_change + "select `%s` from `%s` where `%s` = ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.DATAMODULEDETAIL_PATH, SystemLibConstant.DATAMODULEDETAILTABLE,
				SystemLibConstant.DATAMODULEDETAIL_MODULENAME,
				SystemLibConstant.DATAMODULEDETAIL_RESOURCETABLENAME);

		return SystemMetastoreUtil.processSql(querySql, paramsList);
	}

	//查询所有数据模型
	public static List<LinkedHashMap<Object, Object>> queryDataModuleAll(List<List<Object>> paramsList) throws SQLException, IOException {
		String querySql = String.format(SqlConstant.database_change + "select * from `%s` where `%s` = ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.DATAMODULEFILETREETABLE,
				SystemLibConstant.DATAMODULEFILETREE_MODULENAME, SystemLibConstant.DATAMODULEFILETREE_TYPE);

		return SystemMetastoreUtil.processSql(querySql, paramsList);
	}

	//查询某个数据模型下某个字段注释
	public static List<LinkedHashMap<Object, Object>> queryFieldComment(List<List<Object>> paramsList) throws SQLException, IOException {
		String querySql = String.format(SqlConstant.database_change + "select * from `%s` where `%s` = ? and `%s` = ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.DATAMODULEDETAILTABLE,
				SystemLibConstant.DATAMODULEDETAIL_MODULENAME,
				SystemLibConstant.DATAMODULEDETAIL_PATH,
				SystemLibConstant.DATAMODULEDETAIL_ENGFIELDNAME
		);

		return SystemMetastoreUtil.processSql(querySql, paramsList);
	}

	//查询某个数据模型下所有字段
	public static List<LinkedHashMap<Object, Object>> queryFieldAll(List<List<Object>> paramsList) throws SQLException, IOException {
		String querySql = String.format(SqlConstant.database_change + "select * from `%s` where `%s` = ? and `%s` = ? order by `%s`;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.DATAMODULEDETAILTABLE,
				SystemLibConstant.DATAMODULEDETAIL_MODULENAME,
				SystemLibConstant.DATAMODULEDETAIL_PATH,
				SystemLibConstant.DATAMODULEDETAIL_ROWNUM
		);

		return SystemMetastoreUtil.processSql(querySql, paramsList);
	}

	//新建文件夹、数据模型
	public static void insertDirectoryOrFile(List<List<Object>> paramsList) throws SQLException, IOException {
		String insertSql = String.format("use `%s`;insert into `%s` values (?, ?, ?);",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.DATAMODULEFILETREETABLE);

		SystemMetastoreUtil.processSql(insertSql, paramsList);
	}

	//重命名数据模型
	public static void renameFile(List<List<Object>> paramsList) throws SQLException, IOException {
		String updateSql = String.format("use `%s`;update `%s` set `%s` = ? where  `%s` = ? and `%s` = ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE ,SystemLibConstant.DATAMODULEFILETREETABLE,
				SystemLibConstant.DATAMODULEFILETREE_PATH,
				SystemLibConstant.DATAMODULEFILETREE_TYPE,
				SystemLibConstant.DATAMODULEFILETREE_MODULENAME,
				SystemLibConstant.DATAMODULEFILETREE_PATH);
		SystemMetastoreUtil.processSql(updateSql, paramsList);
	}

	//删除文件夹
	public static void deleteDirectory(List<List<Object>> paramsList, String path) throws SQLException, IOException {
		String deleteSql = String.format("use `%s`;delete from `%s` where (`%s` = ? and `%s` = ? and `%s` = ? and `%s` = ?) or (`%s` like '%s%%' and `%s` = ?) or (`%s` = '%s' and `%s` = ?);delete from `%s` where `%s` = ? and (`%s` like '%s%%' or `%s` = '%s');",
				SystemLibConstant.SYSTEMDATABASE,
				SystemLibConstant.DATAMODULEFILETREETABLE,
				SystemLibConstant.DATAMODULEFILETREE_MODULENAME, SystemLibConstant.DATAMODULEFILETREE_PATH, SystemLibConstant.DATAMODULEFILETREE_TYPE,SystemLibConstant.DATAMODULEFILETREE_MODULENAME,
				SystemLibConstant.DATAMODULEFILETREE_PATH, path + ".", SystemLibConstant.DATAMODULEFILETREE_MODULENAME,
				SystemLibConstant.DATAMODULEFILETREE_PATH, path, SystemLibConstant.DATAMODULEFILETREE_MODULENAME,
				SystemLibConstant.DATAMODULEDETAILTABLE,
				SystemLibConstant.DATAMODULEDETAIL_MODULENAME,  SystemLibConstant.DATAMODULEDETAIL_PATH, path + ".", SystemLibConstant.DATAMODULEDETAIL_PATH, path
		);

		SystemMetastoreUtil.processSql(deleteSql, paramsList);
	}

	//删除文件
	public static void deleteFile(List<List<Object>> paramsList) throws SQLException, IOException {
		String deleteSql = String.format("use `%s`;delete from `%s` where `%s` = ? and `%s` = ? and `%s` != ?;delete from `%s` where `%s` = ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE,
				SystemLibConstant.DATAMODULEFILETREETABLE,
				SystemLibConstant.DATAMODULEFILETREE_MODULENAME, SystemLibConstant.DATAMODULEFILETREE_PATH, SystemLibConstant.DATAMODULEFILETREE_TYPE,
				SystemLibConstant.DATAMODULEDETAILTABLE,
				SystemLibConstant.DATAMODULEDETAIL_MODULENAME, SystemLibConstant.DATAMODULEDETAIL_PATH
		);

		SystemMetastoreUtil.processSql(deleteSql, paramsList);
	}

	//查询文件树信息
	public static List<LinkedHashMap<Object, Object>> queryFileTreeAll(List<List<Object>> paramsList) throws SQLException, IOException {
		String fileQuerySql = String.format("use `%s`;select * from `%s` where `%s` = ?;", SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.DATAMODULEFILETREETABLE, SystemLibConstant.DATAMODULEFILETREE_MODULENAME);

		return SystemMetastoreUtil.processSql(fileQuerySql, paramsList);
	}

	public static List<LinkedHashMap<Object, Object>> queryDataModuleByName(List<List<Object>> paramsList) throws SQLException, IOException {
		String querySql = String.format(SqlConstant.database_change + "select * from `%s` where `%s` = ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.DATAMODULEDETAILTABLE,
				SystemLibConstant.DATAMODULEDETAIL_MODULENAME,
				SystemLibConstant.DATAMODULEDETAIL_PATH);

		return SystemMetastoreUtil.processSql(querySql, paramsList);
	}
}
