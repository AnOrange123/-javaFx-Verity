package mapper;

import constants.SqlConstant;
import constants.SystemLibConstant;
import javafx.event.ActionEvent;
import utils.SystemMetastoreUtil;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:MainMapper
 * Package:mapper
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/5 - 13:51
 */
public class MainMapper {
	private MainMapper() {
	}

	//更新用户的项目权限表
	public static void updateUserPermission(List<List<Object>> paramsList, int insertCount) throws SQLException, IOException {
		String updateSql = String.format(SqlConstant.database_change + "delete from `%s` where `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.USEPROJECTPERMISSIONTABLE,
				SystemLibConstant.USEPROJECTPERMISSIONTABLE_PROJECTNAME);

		for (int i = 0; i < insertCount; i++) {
			updateSql += String.format("insert into `%s` values (?,?);",
					SystemLibConstant.USEPROJECTPERMISSIONTABLE);
		}

		SystemMetastoreUtil.processSql(updateSql, paramsList);
	}

	//根据项目查询用户的项目权限表
	public static List<LinkedHashMap<Object, Object>> queryUserPermissionByProject(List<List<Object>> paramsList) throws SQLException, IOException {
		String querySql = String.format("use `%s`;select * from `%s` where `%s` = ?;", SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.USEPROJECTPERMISSIONTABLE, SystemLibConstant.USEPROJECTPERMISSIONTABLE_PROJECTNAME);
		return SystemMetastoreUtil.processSql(querySql, paramsList);
	}

	//查询所有用户信息
	public static List<LinkedHashMap<Object, Object>> queryUserAll() throws SQLException, IOException {
		String querySql = String.format("use `%s`;select * from `%s`;", SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.USERINFOTABLE);
		return SystemMetastoreUtil.processSql(querySql, null);
	}


	//更新某用户最近登录时间
	public static void updateUserLastLoginTime(List<List<Object>> paramsList) throws SQLException, IOException {
		String alterSql = String.format(SqlConstant.database_change + "update `%s` set `%s` = CURRENT_TIME where `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.USERINFOTABLE,
				SystemLibConstant.USERINFOTABLE_LASTLOGINTIME,
				SystemLibConstant.USERINFOTABLE_USERNAME );
		SystemMetastoreUtil.processSql(alterSql,paramsList);
	}

	//插入新的用户
	public static void insertUser(List<List<Object>> paramsList) throws SQLException, IOException {
		String insertSql = String.format(SqlConstant.database_change + "insert into `%s` values (?,?,?,CURRENT_TIME,null);",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.USERINFOTABLE);
		SystemMetastoreUtil.processSql(insertSql,paramsList);
	}

	//更新某用户密码
	public static void updateUserPass(List<List<Object>> paramsList) throws SQLException, IOException {
		String alterSql = String.format(SqlConstant.database_change + "update `%s` set `%s` = ? where `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.USERINFOTABLE,
				SystemLibConstant.USERINFOTABLE_PASSWORD,
				SystemLibConstant.USERINFOTABLE_USERNAME );
		SystemMetastoreUtil.processSql(alterSql,paramsList);
	}

	//新建项目
	public static void insertProject(List<List<Object>> paramsList) throws SQLException, IOException {
		String insertSql = String.format(SqlConstant.database_change + "insert into `%s` values (?, ?, ?);",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.DATAMODULEINFOTABLE);
		SystemMetastoreUtil.processSql(insertSql,paramsList);
	}

	//更新项目
	public static void updateProject(List<List<Object>> paramsList) throws SQLException, IOException {
		String alterSql = String.format(SqlConstant.database_change + "update `%s` set `%s` = ? where `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.DATAMODULEINFOTABLE,
				SystemLibConstant.DATAMODULEINFO_POSTSCRIPT,
				SystemLibConstant.DATAMODULEINFO_MODULENAME );
		SystemMetastoreUtil.processSql(alterSql,paramsList);
	}

	//根据项目查询项目所属人
	public static List<LinkedHashMap<Object, Object>> queryOwnerByProject(List<List<Object>> paramsList) throws SQLException, IOException {
		String querySql = String.format(SqlConstant.database_change + "select * from `%s` where `%s` = ?;", SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.DATAMODULEINFOTABLE, SystemLibConstant.DATAMODULEINFO_MODULENAME);
		return SystemMetastoreUtil.processSql(querySql, paramsList);
	}

	//查询项目列表
	public static List<LinkedHashMap<Object, Object>> queryProjectAll() throws SQLException, IOException {
		String querySql = String.format(SqlConstant.select_all, SystemLibConstant.SYSTEMDATABASE, SystemLibConstant.DATAMODULEINFOTABLE);
		return SystemMetastoreUtil.processSql(querySql, null);
	}

	//删除项目
	public static void deleteProject(List<List<Object>> paramsList) throws SQLException, IOException {
		//删除所有表中与该表相关的数据
		String deleteSql = String.format(SqlConstant.database_change + "delete from `%s` where `%s` = ?;delete from `%s` where `%s` = ?;delete from `%s` where `%s` = ?;",
				SystemLibConstant.SYSTEMDATABASE,
				SystemLibConstant.DATAMODULEINFOTABLE, SystemLibConstant.DATAMODULEINFO_MODULENAME,
				SystemLibConstant.DATAMODULEFILETREETABLE, SystemLibConstant.DATAMODULEFILETREE_MODULENAME,
				SystemLibConstant.DATAMODULEDETAILTABLE, SystemLibConstant.DATAMODULEDETAIL_MODULENAME);
		SystemMetastoreUtil.processSql(deleteSql,paramsList);
	}

	//初始化系统表
	public static void initSystemTable() throws SQLException, IOException {
		String initSql = generateSql();
		SystemMetastoreUtil.processSql(initSql, null);
	}

	private static String generateSql() {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(String.format("create database if not exists `%s` character set utf8;", SystemLibConstant.SYSTEMDATABASE));
		stringBuilder.append(String.format("use `%s`;", SystemLibConstant.SYSTEMDATABASE));

		stringBuilder.append(String.format("create table if not exists `%s` (" ,SystemLibConstant.SQLFILEINFOTABLE));
		stringBuilder.append(String.format("`%s` VARCHAR(100) NOT NULL COMMENT '项目名称'," ,SystemLibConstant.SQLFILEINFO_PROJECTNAME));
		stringBuilder.append(String.format("`%s` VARCHAR(100) NOT NULL COMMENT '文件名'," ,SystemLibConstant.SQLFILEINFO_FILENAME));
		stringBuilder.append(String.format("`%s` VARCHAR(100) NOT NULL COMMENT '父级文件名'," ,SystemLibConstant.SQLFILEINFO_PARENTFILENAME));
		stringBuilder.append(String.format("`%s` VARCHAR(50) COMMENT '文件类别'," ,SystemLibConstant.SQLFILEINFO_FILETYPE));
		stringBuilder.append(String.format("`%s` TEXT COMMENT 'sql内容'," ,SystemLibConstant.SQLFILEINFO_CONTENT));
		stringBuilder.append(String.format("PRIMARY KEY (`%s`,`%s`, `%s`, `%s`)) COMMENT = 'sql文件树信息';",SystemLibConstant.SQLFILEINFO_PROJECTNAME,SystemLibConstant.SQLFILEINFO_FILENAME, SystemLibConstant.SQLFILEINFO_PARENTFILENAME,SystemLibConstant.SQLFILEINFO_FILETYPE));

		stringBuilder.append(String.format("create table if not exists `%s` (" ,SystemLibConstant.DATAMODULEINFOTABLE));
		stringBuilder.append(String.format("`%s` VARCHAR(100) NOT NULL COMMENT '项目名称'," ,SystemLibConstant.DATAMODULEINFO_MODULENAME));
		stringBuilder.append(String.format("`%s` TEXT COMMENT '数据模型备注'," ,SystemLibConstant.DATAMODULEINFO_POSTSCRIPT));
		stringBuilder.append(String.format("`%s` VARCHAR(150) COMMENT '项目所属人'," ,SystemLibConstant.DATAMODULEINFO_OWNER));
		stringBuilder.append(String.format("PRIMARY KEY (`%s`)) COMMENT = '数据模型信息';",SystemLibConstant.DATAMODULEINFO_MODULENAME));

		stringBuilder.append(String.format("create table if not exists `%s` (" ,SystemLibConstant.DATAMODULEFILETREETABLE));
		stringBuilder.append(String.format("`%s` VARCHAR(100) NOT NULL COMMENT '项目名称'," ,SystemLibConstant.DATAMODULEFILETREE_MODULENAME));
		stringBuilder.append(String.format("`%s` VARCHAR(150) NOT NULL COMMENT '全路径'," ,SystemLibConstant.DATAMODULEFILETREE_PATH));
		stringBuilder.append(String.format("`%s` VARCHAR(30) COMMENT '路径类型'," ,SystemLibConstant.DATAMODULEFILETREE_TYPE));
		stringBuilder.append(String.format("PRIMARY KEY (`%s`, `%s`)) COMMENT = '数据模型文件树信息';",SystemLibConstant.DATAMODULEFILETREE_MODULENAME ,SystemLibConstant.DATAMODULEFILETREE_PATH));

		stringBuilder.append(String.format("create table if not exists `%s` (" ,SystemLibConstant.DATAMODULEDETAILTABLE));
		stringBuilder.append(String.format("`%s` VARCHAR(100) NOT NULL COMMENT '项目名称'," ,SystemLibConstant.DATAMODULEDETAIL_MODULENAME));
		stringBuilder.append(String.format("`%s` VARCHAR(150) NOT NULL COMMENT '全路径'," ,SystemLibConstant.DATAMODULEDETAIL_PATH));
		stringBuilder.append(String.format("`%s` VARCHAR(150) COMMENT '中文字段名'," ,SystemLibConstant.DATAMODULEDETAIL_CHIFIELDNAME));
		stringBuilder.append(String.format("`%s` VARCHAR(150) NOT NULL COMMENT '英文字段名'," ,SystemLibConstant.DATAMODULEDETAIL_ENGFIELDNAME));
		stringBuilder.append(String.format("`%s` VARCHAR(50) NOT NULL COMMENT '字段数据类型'," ,SystemLibConstant.DATAMODULEDETAIL_FIELDDATATYPE));
		stringBuilder.append(String.format("`%s` VARCHAR(150) COMMENT '来源表名'," ,SystemLibConstant.DATAMODULEDETAIL_RESOURCETABLENAME));
		stringBuilder.append(String.format("`%s` VARCHAR(150) COMMENT '中文来源字段名'," ,SystemLibConstant.DATAMODULEDETAIL_CHIRESOURCEFIELDNAME));
		stringBuilder.append(String.format("`%s` VARCHAR(150) COMMENT '英文来源字段名'," ,SystemLibConstant.DATAMODULEDETAIL_ENGRESOURCEFIELDNAME));
		stringBuilder.append(String.format("PRIMARY KEY (`%s`, `%s`, `%s`)) COMMENT = '数据模型详细信息';",SystemLibConstant.DATAMODULEDETAIL_MODULENAME ,SystemLibConstant.DATAMODULEDETAIL_PATH,SystemLibConstant.DATAMODULEDETAIL_ENGFIELDNAME));

		stringBuilder.append(String.format("create table if not exists `%s` (" ,SystemLibConstant.USERINFOTABLE));
		stringBuilder.append(String.format("`%s` VARCHAR(100) NOT NULL COMMENT '用户名'," ,SystemLibConstant.USERINFOTABLE_USERNAME));
		stringBuilder.append(String.format("`%s` BLOB NOT NULL COMMENT '密码'," ,SystemLibConstant.USERINFOTABLE_PASSWORD));
		stringBuilder.append(String.format("`%s` BLOB NOT NULL COMMENT '手机号码'," ,SystemLibConstant.USERINFOTABLE_PHONENUM));
		stringBuilder.append(String.format("`%s` DATETIME NOT NULL COMMENT '创建时间'," ,SystemLibConstant.USERINFOTABLE_CREATETIME));
		stringBuilder.append(String.format("`%s` DATETIME COMMENT '最近登录时间'," ,SystemLibConstant.USERINFOTABLE_LASTLOGINTIME));
		stringBuilder.append(String.format("PRIMARY KEY (`%s`)) COMMENT = '用户信息表';",SystemLibConstant.USERINFOTABLE_USERNAME));

		stringBuilder.append(String.format("create table if not exists `%s` (" ,SystemLibConstant.USEPROJECTPERMISSIONTABLE));
		stringBuilder.append(String.format("`%s` VARCHAR(255) NOT NULL COMMENT '用户名'," ,SystemLibConstant.USEPROJECTPERMISSIONTABLE_USER));
		stringBuilder.append(String.format("`%s` VARCHAR(255) NOT NULL COMMENT '项目'," ,SystemLibConstant.USEPROJECTPERMISSIONTABLE_PROJECTNAME));
		stringBuilder.append(String.format("PRIMARY KEY (`%s`, `%s`)) COMMENT = '项目权限表';",SystemLibConstant.USEPROJECTPERMISSIONTABLE_USER ,SystemLibConstant.USEPROJECTPERMISSIONTABLE_PROJECTNAME));

		return stringBuilder.toString();
	}
}