package mapper;

import beans.ProjectVariableManager;
import constants.SystemLibConstant;
import utils.SystemMetastoreUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:SJKFMapper
 * Package:mapper
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/5 - 13:41
 */
public class SJKFMapper {
	private SJKFMapper() {
	}

	//根据项目名称查询文件树表所有信息
	public static List<LinkedHashMap<Object, Object>> queryFileInfoByProject(List<List<Object>> paramsList) throws SQLException, IOException {
		String fileQuerySql = String.format("use `%s`;select * from `%s` where `%s` = ?;", SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.SQLFILEINFOTABLE, SystemLibConstant.SQLFILEINFO_PROJECTNAME);
		return SystemMetastoreUtil.processSql(fileQuerySql, paramsList);
	}

	//新建文件夹、文件
	public static void insertDirectoryOrFile(List<List<Object>> paramsList) throws SQLException, IOException {
		String insertSql = String.format("use `%s`;insert into `%s` values (?, ?, ?, ?, ?);",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.SQLFILEINFOTABLE);
		SystemMetastoreUtil.processSql(insertSql,paramsList);
	}

	//重命名文件夹
	public static void updateDirectory(List<List<Object>> paramsList, String parentDirect) throws SQLException, IOException {
		String updateSql = String.format("use `%s`;update `%s` set `%s` = ? where `%s` = ? and `%s` = ? and `%s` = ? and `%s` = ?;update `%s` set `%s` = concat(?, substring(`%s`, length(?) + 1)) where `%s` like '%s%%' and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE,
				SystemLibConstant.SQLFILEINFOTABLE,
				SystemLibConstant.SQLFILEINFO_FILENAME,  SystemLibConstant.SQLFILEINFO_FILENAME,  SystemLibConstant.SQLFILEINFO_PARENTFILENAME,  SystemLibConstant.SQLFILEINFO_FILETYPE, SystemLibConstant.SQLFILEINFO_PROJECTNAME,
				SystemLibConstant.SQLFILEINFOTABLE,
				SystemLibConstant.SQLFILEINFO_PARENTFILENAME,  SystemLibConstant.SQLFILEINFO_PARENTFILENAME,  SystemLibConstant.SQLFILEINFO_PARENTFILENAME,parentDirect,SystemLibConstant.SQLFILEINFO_PROJECTNAME
		);
		SystemMetastoreUtil.processSql(updateSql,paramsList);
	}

	//删除文件夹
	public static void deleteDirectory(List<List<Object>> paramsList, String finalSelectedValue) throws SQLException, IOException {
		String deleteSql = String.format("use `%s`;delete from `%s` where (`%s` = ? and `%s` = ? and `%s` = ? and `%s` = ?) or (`%s` like '%s%%' and `%s` = ?) or (`%s` = '%s' and `%s` = ?);",
				SystemLibConstant.SYSTEMDATABASE,
				SystemLibConstant.SQLFILEINFOTABLE,
				SystemLibConstant.SQLFILEINFO_FILENAME,  SystemLibConstant.SQLFILEINFO_PARENTFILENAME, SystemLibConstant.SQLFILEINFO_FILETYPE,SystemLibConstant.SQLFILEINFO_PROJECTNAME,
				SystemLibConstant.SQLFILEINFO_PARENTFILENAME, finalSelectedValue + '.',SystemLibConstant.SQLFILEINFO_PROJECTNAME,
				SystemLibConstant.SQLFILEINFO_PARENTFILENAME, finalSelectedValue, SystemLibConstant.SQLFILEINFO_PROJECTNAME
		);
		SystemMetastoreUtil.processSql(deleteSql,paramsList);
	}

	//重命名文件
	public static void updateFileName(List<List<Object>> paramsList) throws SQLException, IOException {
		String updateSql = String.format("use `%s`;update `%s` set `%s` = ? where `%s` = ? and `%s` != ? and `%s` = ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE,
				SystemLibConstant.SQLFILEINFOTABLE,
				SystemLibConstant.SQLFILEINFO_FILENAME,  SystemLibConstant.SQLFILEINFO_PARENTFILENAME,  SystemLibConstant.SQLFILEINFO_FILETYPE, SystemLibConstant.SQLFILEINFO_PROJECTNAME, SystemLibConstant.SQLFILEINFO_FILENAME
		);
		SystemMetastoreUtil.processSql(updateSql,paramsList);
	}

	//保存文件
	public static void updateFileContent(List<List<Object>> paramsList) throws SQLException, IOException {
		String updateSql = String.format("use `%s`;update `%s` set `%s` = ? where `%s` = ? and `%s` = ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.SQLFILEINFOTABLE,
				SystemLibConstant.SQLFILEINFO_CONTENT, SystemLibConstant.SQLFILEINFO_FILENAME, SystemLibConstant.SQLFILEINFO_PARENTFILENAME, SystemLibConstant.SQLFILEINFO_PROJECTNAME);
		SystemMetastoreUtil.processSql(updateSql,paramsList);
	}

	//删除文件
	public static void deleteFile(List<List<Object>> paramsList) throws SQLException, IOException {
		String deleteSql =  String.format("use `%s`;delete from `%s` where `%s` = ? and `%s` = ? and `%s` != ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.SQLFILEINFOTABLE,
				SystemLibConstant.SQLFILEINFO_FILENAME,  SystemLibConstant.SQLFILEINFO_PARENTFILENAME, SystemLibConstant.SQLFILEINFO_FILETYPE, SystemLibConstant.SQLFILEINFO_PROJECTNAME
		);
		SystemMetastoreUtil.processSql(deleteSql,paramsList);
	}

	//查询文件内容
	public static List<LinkedHashMap<Object, Object>> queryFileContent(List<List<Object>> paramsList) throws SQLException, IOException {
		String querySql = String.format("use `%s`;select * from `%s` where `%s` = ? and `%s` = ? and `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.SQLFILEINFOTABLE,
				SystemLibConstant.SQLFILEINFO_FILENAME,
				SystemLibConstant.SQLFILEINFO_PARENTFILENAME, SystemLibConstant.SQLFILEINFO_PROJECTNAME);
		return SystemMetastoreUtil.processSql(querySql,paramsList);
	}
}