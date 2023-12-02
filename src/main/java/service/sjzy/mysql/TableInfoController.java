package service.sjzy.mysql;

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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import utils.CommonUtil;
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

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("sjzy/mysql/TableInfo.fxml"));
		VBox tableInfoContent = fxmlLoader.load();

		loadTabs(tableInfoContent);

		MysqlDataResourceController.replaceMainModule(event,tableInfoContent);
	}

	private void loadTabs(VBox tableInfoContent) throws SQLException, IOException {
		//获取表信息相关数据
		MysqlTableInfo mysqlTableInfo = getTableInfo();

		//设置表名和表注释
		labelSet(tableInfoContent, mysqlTableInfo);

		//加载表结构信息tab
		loadTableField(tableInfoContent, mysqlTableInfo);

		//加载表预览信息tab
		loadTableReview(tableInfoContent);

	}

	private void loadTableReview(VBox tableInfoContent) throws SQLException, IOException {
		VBox tableReviewVBox = (VBox) tableInfoContent.lookup("#tableReviewVBox");
		TableView<List<Object>> tableView =
				reviewTableViewGet();
		tableReviewVBox.getChildren().clear();
		tableReviewVBox.getChildren().add(tableView);
	}

	private TableView<List<Object>> reviewTableViewGet() throws SQLException, IOException {
		ObservableList<List<Object>> data = FXCollections.observableArrayList();
		List<LinkedHashMap<Object, Object>> queryResult = MysqlUtil.processSql(String.format(SqlConstant.select_all, databaseName, datatableName), null);

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

	private void labelSet(VBox tableInfoContent, MysqlTableInfo mysqlTableInfo) {
		Label tableNameLabel = (Label) tableInfoContent.lookup("#TableNameLabel");
		Label tableCommentLabal = (Label) tableInfoContent.lookup("#TableCommentLabel");

		tableNameLabel.setText(mysqlTableInfo.getTableName());
		if (!CommonUtil.isNullOrEmpty(mysqlTableInfo.getComment())){
			tableCommentLabal.setText(mysqlTableInfo.getComment());
		}
	}

	private void loadTableField(VBox tableInfoContent, MysqlTableInfo mysqlTableInfo) {
		TableView<MysqlFieldInfo> tableFieldView = (TableView<MysqlFieldInfo>) tableInfoContent.lookup("#tableFieldView");
		TableColumn<MysqlFieldInfo, String> fieldNameCol = (TableColumn<MysqlFieldInfo, String>) tableFieldView.getColumns().get(0);
		TableColumn<MysqlFieldInfo, String> commentCol = (TableColumn<MysqlFieldInfo, String>) tableFieldView.getColumns().get(1);
		TableColumn<MysqlFieldInfo, String> dataTypeCol = (TableColumn<MysqlFieldInfo, String>) tableFieldView.getColumns().get(2);
		TableColumn<MysqlFieldInfo, String> isPriCol = (TableColumn<MysqlFieldInfo, String>) tableFieldView.getColumns().get(3);

		fieldNameCol.setCellValueFactory(new PropertyValueFactory<>("fieldName"));
		commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
		dataTypeCol.setCellValueFactory(new PropertyValueFactory<>("dataType"));
		isPriCol.setCellValueFactory(new PropertyValueFactory<>("isKey"));

		tableFieldView.setItems(FXCollections.observableArrayList(mysqlTableInfo.getFieldInfos()));
	}

	private static MysqlTableInfo getTableInfo() throws SQLException, IOException {
		//获取建表语句
		String querySql = String.format(SqlConstant.table_describe, databaseName, datatableName);
		String queryFieldSql = String.format(SqlConstant.database_change + "select * from `%s` where `%s` = '%s' and `%s` = '%s';",
				SomeConstant.INFORMATION_SCHEMA, SomeConstant.INFORMATION_SCHEMA_TABLE,
				SomeConstant.INFORMATION_SCHEMA_COLUMNS_BASE, databaseName,
				SomeConstant.INFORMATION_SCHEMA_COLUMNS_TABLE, datatableName);

		//获取结果
		List<LinkedHashMap<Object, Object>> queryCreateSqlResult = MysqlUtil.processSql(querySql, null);
		List<LinkedHashMap<Object, Object>> queryFieldSqlResult = MysqlUtil.processSql(queryFieldSql, null);

		LinkedHashMap<Object, Object> mainRow = queryCreateSqlResult.get(0);
		String createSql = (String) mainRow.get(SomeConstant.DATATABLE_CREATE_SQL);

		String tableComment = "";
		if (createSql != null){
			//匹配出表注释
			Matcher tableCommentPattern = RegexUtil.match(createSql, RegexConstant.tableCommentPattern);
			//转换为字符串
			while (tableCommentPattern.find() ){
				tableComment = tableCommentPattern.group();
			}
		}

		//封装到对象
		MysqlTableInfo mysqlTableInfo = new MysqlTableInfo();
		mysqlTableInfo.setDataBase(databaseName);
		mysqlTableInfo.setComment(tableComment);
		mysqlTableInfo.setTableName(datatableName);

		List<MysqlFieldInfo> mysqlFieldInfos = new ArrayList<>();
		for (int i = 0; i < queryFieldSqlResult.size(); i++) {
			MysqlFieldInfo mysqlFieldInfo = new MysqlFieldInfo();
			mysqlFieldInfo.setFieldName((String) queryFieldSqlResult.get(i).get(SomeConstant.INFORMATION_SCHEMA_COLUMNS_COL));
			mysqlFieldInfo.setDataType((String) queryFieldSqlResult.get(i).get(SomeConstant.INFORMATION_SCHEMA_COLUMNS_TYPE));
			mysqlFieldInfo.setComment((String) queryFieldSqlResult.get(i).get(SomeConstant.INFORMATION_SCHEMA_COLUMNS_COMMENT));

			if ("PRI".equals((String) queryFieldSqlResult.get(i).get(SomeConstant.INFORMATION_SCHEMA_COLUMNS_KEY))){
				mysqlFieldInfo.setIsKey("是");
			}

			mysqlFieldInfos.add(mysqlFieldInfo);
		}
		mysqlTableInfo.setFieldInfos(mysqlFieldInfos);
		return mysqlTableInfo;
	}

	@FXML
	void goBackButtonClick(ActionEvent event) throws SQLException, IOException {
		new TableQueryController().createTableQueryWindow(oldEvent, databaseName);
	}
}
