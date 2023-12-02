package service.sjzy.hive;

import beans.sjzy.HiveFieldInfo;
import beans.sjzy.HiveTableInfo;
import beans.sjzy.MysqlFieldInfo;
import beans.sjzy.MysqlTableInfo;
import constants.RegexConstant;
import constants.SomeConstant;
import constants.SqlConstant;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import service.sjzy.mysql.MysqlDataResourceController;
import utils.HiveUtil;
import utils.MysqlUtil;
import utils.RegexUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;

/**
 * ClassName:TableInfoController
 * Package:service.sjzy.mysql
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/24 - 23:44
 */
public class TableInfoController {

	private static String databaseName;
	private static String datatableName;
	private static ActionEvent oldEvent;

	// TODO: 2023/9/27 1.查看建表语句功能按钮  2.下载字段信息功能按钮
	public void createTableInfoWindow(ActionEvent event, String database, String table) throws IOException, SQLException {
		databaseName = database;
		datatableName = table;
		oldEvent = event;

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("sjzy/hive/TableInfo.fxml"));
		VBox tableInfoContent = fxmlLoader.load();

		loadTabs(tableInfoContent);

		HiveDataResourceController.replaceMainModule(event,tableInfoContent);
	}

	private void loadTabs(VBox tableInfoContent) throws SQLException, IOException {
		//获取表信息相关数据
		HiveTableInfo hiveTableInfo = getTableInfo();

		//设置表名和表注释
		labelSet(tableInfoContent, hiveTableInfo);

		//加载表结构信息tab
		loadTableField(tableInfoContent, hiveTableInfo);

		//加载表预览信息tab
		loadTableReview(tableInfoContent);

	}

	private void loadTableReview(VBox tableInfoContent) throws SQLException, IOException {
		VBox tableReviewVBox = (VBox) tableInfoContent.lookup("#tableReviewVBox");
		TableView<List<Object>> tableView = reviewTableViewGet();
		tableReviewVBox.getChildren().clear();
		tableReviewVBox.getChildren().add(tableView);
	}

	private TableView<List<Object>> reviewTableViewGet() throws SQLException, IOException {
		ObservableList<List<Object>> data = FXCollections.observableArrayList();
		List<LinkedHashMap<Object, Object>> queryResult = HiveUtil.processHql(String.format(SqlConstant.select_limit, databaseName, datatableName), null);

		TableView<List<Object>> tableView = new TableView<>();
		tableView.setEditable(false);

		if (queryResult.size() != 0){
			for (LinkedHashMap<Object, Object> rowResult : queryResult) {
				ArrayList<Object> rowData = new ArrayList<>(rowResult.values());

				// 遍历rowData，检查是否为空，如果为空则替换为null
				for (int i = 0; i < rowData.size(); i++) {
					if (rowData.get(i) == null) {
						rowData.set(i, "null");
					}
				}

				data.add(rowData);
			}

			// 加载数据到 TableView
			tableView.setItems(data);

			// 创建列
			int colIndex = 0;
			for (Object colName : queryResult.get(0).keySet()) {
				TableColumn<List<Object>, String> column = new TableColumn<>((String) colName);
				// 设置列的数据源
				int finalColIndex = colIndex;
				column.setCellValueFactory(cellData -> new SimpleStringProperty( cellData.getValue().get(finalColIndex).toString()));
				// 将列添加到 TableView
				tableView.getColumns().add(column);
				colIndex++;
			}
		}

		return tableView;
	}

	private void labelSet(VBox tableInfoContent, HiveTableInfo hiveTableInfo) {
		Label tableNameLabel = (Label) tableInfoContent.lookup("#TableNameLabel");
		Label tableCommentLabal = (Label) tableInfoContent.lookup("#TableCommentLabel");

		tableNameLabel.setText(hiveTableInfo.getTableName());
		tableCommentLabal.setText(hiveTableInfo.getComment());
	}

	private void loadTableField(VBox tableInfoContent, HiveTableInfo hiveTableInfo) {
		TableView<HiveFieldInfo> tableFieldView = (TableView<HiveFieldInfo>) tableInfoContent.lookup("#tableFieldView");
		TableColumn<HiveFieldInfo, String> fieldNameCol = (TableColumn<HiveFieldInfo, String>) tableFieldView.getColumns().get(0);
		TableColumn<HiveFieldInfo, String> commentCol = (TableColumn<HiveFieldInfo, String>) tableFieldView.getColumns().get(1);
		TableColumn<HiveFieldInfo, String> dataTypeCol = (TableColumn<HiveFieldInfo, String>) tableFieldView.getColumns().get(2);
		TableColumn<HiveFieldInfo, String> isParCol = (TableColumn<HiveFieldInfo, String>) tableFieldView.getColumns().get(3);

		fieldNameCol.setCellValueFactory(new PropertyValueFactory<>("fieldName"));
		commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
		dataTypeCol.setCellValueFactory(new PropertyValueFactory<>("dataType"));
		isParCol.setCellValueFactory(new PropertyValueFactory<>("isPar"));

		tableFieldView.setItems(FXCollections.observableArrayList(hiveTableInfo.getHiveFieldInfos()));
	}

	private static HiveTableInfo getTableInfo() throws SQLException, IOException {
		//获取建表语句
		String queryTableCommentSql = String.format(SqlConstant.database_change + "select `%s` from `%s` join `%s` on `%s`.`%s` = `%s`.`%s` join `%s` on `%s`.`%s` = `%s`.`%s` where `%s` = '%s' and `%s` = 'comment' and `%s`.`%s` = '%s';",
				SomeConstant.METASTORE, SomeConstant.TABLEPARAMS_PARAMVALUE,
				SomeConstant.METASTORE_TABLE,SomeConstant.TABLEPARAMS,
				SomeConstant.METASTORE_TABLE,SomeConstant.METASTORE_TABLE_ID,
				SomeConstant.TABLEPARAMS,SomeConstant.TABLEPARAMS_ID,
				SomeConstant.METASTORE_DB,
				SomeConstant.METASTORE_DB, SomeConstant.METASTORE_DB_ID,
				SomeConstant.METASTORE_TABLE, SomeConstant.METASTORE_TABLE_DBID,
				SomeConstant.METASTORE_TABLE_NAME, datatableName,
				SomeConstant.TABLEPARAMS_PARAMKEY,
				SomeConstant.METASTORE_DB, SomeConstant.METASTORE_DB_NAME, databaseName);
		String queryFieldSql = String.format(SqlConstant.table_field_describe, databaseName, datatableName);
		String queryPartitionSql = String.format(SqlConstant.database_change + "select `%s` from `%s` join `%s` on `%s`.`%s` = `%s`.`%s` join `%s` on `%s`.`%s` = `%s`.`%s` where `%s` = '%s' and `%s`.`%s` = '%s';",
				SomeConstant.METASTORE, SomeConstant.METASTORE_PARTI_KEY, SomeConstant.METASTORE_TABLE, SomeConstant.METASTORE_PARTI,
				SomeConstant.METASTORE_TABLE, SomeConstant.METASTORE_TABLE_ID,
				SomeConstant.METASTORE_PARTI, SomeConstant.METASTORE_PARTI_TABLEID,
				SomeConstant.METASTORE_DB,
				SomeConstant.METASTORE_DB, SomeConstant.METASTORE_DB_ID,
				SomeConstant.METASTORE_TABLE, SomeConstant.METASTORE_TABLE_DBID,
				SomeConstant.METASTORE_TABLE_NAME, datatableName,
				SomeConstant.METASTORE_DB, SomeConstant.METASTORE_DB_NAME, databaseName
				);

		//获取结果
		List<LinkedHashMap<Object, Object>> queryTableCommentSqlResult = MysqlUtil.processSql(queryTableCommentSql, null);
		List<LinkedHashMap<Object, Object>> queryFieldSqlResult = HiveUtil.processHql(queryFieldSql, null);
		List<LinkedHashMap<Object, Object>> queryPartitionSqlResult = MysqlUtil.processSql(queryPartitionSql, null);

		//获取表注释
		String tableComment = "";
		if (queryTableCommentSqlResult.size() != 0){
			LinkedHashMap<Object, Object> mainRow = queryTableCommentSqlResult.get(0);
			tableComment = (String) mainRow.get(SomeConstant.TABLEPARAMS_PARAMVALUE);
		}

		//将分区字段封装成一个list
		List<String> parList = new ArrayList<>();
		for (LinkedHashMap<Object, Object> row : queryPartitionSqlResult) {
			parList.add((String) row.get(SomeConstant.METASTORE_PARTI_KEY));
		}

		//封装到对象
		HiveTableInfo hiveTableInfo = new HiveTableInfo();
		hiveTableInfo.setDataBase(databaseName);
		hiveTableInfo.setComment(tableComment);
		hiveTableInfo.setTableName(datatableName);

		List<HiveFieldInfo> hiveFieldInfos = new ArrayList<>();
		for (int i = 0; i < queryFieldSqlResult.size(); i++) {
			String fieldName = (String) queryFieldSqlResult.get(i).get(SomeConstant.DESC_RESULT_COLNAME);
			if (fieldName.startsWith("#")) {
				break;
			}

			HiveFieldInfo hiveFieldInfo = new HiveFieldInfo();
			hiveFieldInfo.setFieldName(fieldName);
			hiveFieldInfo.setDataType((String) queryFieldSqlResult.get(i).get(SomeConstant.DESC_RESULT_COLTYPE));
			hiveFieldInfo.setComment((String) queryFieldSqlResult.get(i).get(SomeConstant.DESC_RESULT_COLCOMMENT));

			if (parList.contains(fieldName)){
				hiveFieldInfo.setIsPar("是");
			}

			hiveFieldInfos.add(hiveFieldInfo);
		}
		hiveTableInfo.setHiveFieldInfos(hiveFieldInfos);
		return hiveTableInfo;
	}

	@FXML
	void goBackButtonClick(ActionEvent event) throws SQLException, IOException {
		new TableQueryController().createTableQueryWindow(oldEvent, databaseName);
	}
}
