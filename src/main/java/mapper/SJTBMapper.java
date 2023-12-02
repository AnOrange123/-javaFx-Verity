package mapper;

import constants.SqlConstant;
import constants.SystemLibConstant;
import utils.SystemMetastoreUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:SJTBMapper
 * Package:mapper
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/5 - 13:56
 */
public class SJTBMapper {
	public SJTBMapper() {
	}

	//查询文件树信息
	public static List<LinkedHashMap<Object, Object>> queryFileTreeAll(List<List<Object>> paramsList) throws SQLException, IOException {
		String fileQuerySql = String.format("use `%s`;select * from `%s` where `%s` = ?;", SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.SYNCHRONIZEJOBINFOTABLE, SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_PROJECTNAME);

		return SystemMetastoreUtil.processSql(fileQuerySql, paramsList);
	}

	//查询任务基本信息
	public static List<LinkedHashMap<Object, Object>> queryJobBaseInfo(List<List<Object>> paramsList) throws SQLException, IOException {
		String fileQuerySql = String.format("use `%s`;select * from `%s` where `%s` = ? and `%s` = ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.SYNCHRONIZEJOBINFOTABLE,
				SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_PROJECTNAME, SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_JOBPATH, SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_PATHTYPE);

		return SystemMetastoreUtil.processSql(fileQuerySql, paramsList);
	}

	//查询任务详细信息
	public static List<LinkedHashMap<Object, Object>> queryJobDetailInfo(List<List<Object>> paramsList) throws SQLException, IOException {
		String fileQuerySql = String.format("use `%s`;select * from `%s` where `%s` = ? and `%s` = ? ;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.SYNCHRONIZEJOBDETAILTABLE,
				SystemLibConstant.SYNCHRONIZEJOBDETAILTABLE_PROJECTNAME, SystemLibConstant.SYNCHRONIZEJOBDETAILTABLE_JOBPATH);

		return SystemMetastoreUtil.processSql(fileQuerySql, paramsList);
	}

	//新建文件夹、同步任务
	public static void insertDirectoryOrJob(List<List<Object>> paramsList) throws SQLException, IOException {
		String insertSql = String.format("use `%s`;insert into `%s` values (?, ?, ?, '', ?, ?, ?,?, ?, ?);",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.SYNCHRONIZEJOBINFOTABLE);

		SystemMetastoreUtil.processSql(insertSql, paramsList);
	}

	//删除文件夹
	public static void deleteDirectory(List<List<Object>> paramsList, String path) throws SQLException, IOException {
		String deleteSql = String.format("use `%s`;delete from `%s` where (`%s` = ? and `%s` = ? and `%s` = ?) or (`%s` like '%s%%' and `%s` = ?);delete from `%s` where `%s` like '%s%%' and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE,
				SystemLibConstant.SYNCHRONIZEJOBINFOTABLE,
				SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_PROJECTNAME, SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_JOBPATH, SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_PATHTYPE,
				SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_JOBPATH, path + ".", SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_PROJECTNAME,
				SystemLibConstant.SYNCHRONIZEJOBDETAILTABLE,
				SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_JOBPATH, path + ".", SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_PROJECTNAME
		);

		SystemMetastoreUtil.processSql(deleteSql, paramsList);
	}

	//重命名同步作业
	public static void renameJob(List<List<Object>> paramsList) throws SQLException, IOException {
		String updateSql = String.format("use `%s`;update `%s` set `%s` = ? where  `%s` = ? and `%s` = ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE ,SystemLibConstant.SYNCHRONIZEJOBINFOTABLE,
				SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_JOBPATH,
				SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_PATHTYPE,
				SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_PROJECTNAME,
				SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_JOBPATH);
		SystemMetastoreUtil.processSql(updateSql, paramsList);
	}

	//删除文件
	public static void deleteJob(List<List<Object>> paramsList) throws SQLException, IOException {
		String deleteSql = String.format("use `%s`;delete from `%s` where `%s` = ? and `%s` = ? and `%s` != ?;delete from `%s` where `%s` = ? and `%s` = ?",
				SystemLibConstant.SYSTEMDATABASE,
				SystemLibConstant.SYNCHRONIZEJOBINFOTABLE,
				SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_PROJECTNAME, SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_JOBPATH, SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_PATHTYPE,
				SystemLibConstant.SYNCHRONIZEJOBDETAILTABLE,
				SystemLibConstant.SYNCHRONIZEJOBDETAILTABLE_PROJECTNAME, SystemLibConstant.SYNCHRONIZEJOBDETAILTABLE_JOBPATH
				);

		SystemMetastoreUtil.processSql(deleteSql, paramsList);
	}

	//保存任务详细信息
	public static void updateJobDetailInfo(List<List<Object>> paramsList, int rowSize) throws SQLException, IOException {
		String insertSql = String.format(SqlConstant.database_change + "delete from `%s` where `%s` = ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.SYNCHRONIZEJOBDETAILTABLE,
				SystemLibConstant.SYNCHRONIZEJOBDETAILTABLE_PROJECTNAME,
				SystemLibConstant.SYNCHRONIZEJOBDETAILTABLE_JOBPATH);

		for (int i = 0; i < rowSize; i++) {
			//逐行添加插入语句
			insertSql = String.format(insertSql + "insert into `%s` values (?, ?, ?, ?);", SystemLibConstant.SYNCHRONIZEJOBDETAILTABLE);
		}

		SystemMetastoreUtil.processSql(insertSql, paramsList);
	}

	//保存任务内容
	public static void updateJobBaseInfo(List<List<Object>> paramsList) throws SQLException, IOException {
		String updateSql = String.format("use `%s`;update `%s` set `%s` = ? where  `%s` = ? and `%s` = ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.SYNCHRONIZEJOBINFOTABLE,
				SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_CONTENT,
				SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_PROJECTNAME,
				SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_JOBPATH,
				SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_PATHTYPE);

		SystemMetastoreUtil.processSql(updateSql, paramsList);
	}
}