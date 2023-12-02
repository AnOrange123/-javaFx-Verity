package service.sjkf;

import beans.ProjectVariableManager;
import beans.SJKFTabManager;
import constants.SomeConstant;
import constants.SqlConstant;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import mapper.SJKFMapper;
import netscape.javascript.JSObject;
import org.apache.commons.lang3.StringEscapeUtils;
import service.common.Loading;
import utils.HiveUtil;
import utils.MysqlUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * ClassName:QueryTabController
 * Package:service.sjkf
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/15 - 21:11
 */
public class QueryTabController {
	@FXML
	private WebView codeEditor;
	private String filePath;
	private String fileType;
	@FXML
	private VBox resultShowBox;
	@FXML
	private ComboBox<String> databaseChoice;
	@FXML
	private SplitPane splitPane;

	public QueryTabController() {
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	//加载tab标签页
	public Tab loadQueryTabWindow(String path, String content, String sqlType) throws IOException, SQLException {
		String fileName = path.substring(path.lastIndexOf(".")+1);
		Tab tab = new Tab(fileName);

		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("sjkf/QueryTab.fxml"));
		HBox hBox = fxmlLoader.load();

		QueryTabController queryTabController = fxmlLoader.getController();
		queryTabController.setFilePath(path);
		queryTabController.setFileType(sqlType);

		//数据库选择下拉框加载
		ComboBox databaseChoice = (ComboBox) hBox.lookup("#databaseChoice");
		List<LinkedHashMap<Object, Object>> queryResult = getQueryResult(SqlConstant.database_show, sqlType);
		if (SomeConstant.SQLFILE.equals(sqlType)){
			for (LinkedHashMap<Object, Object> database : queryResult) {
				databaseChoice.getItems().add(database.get(SomeConstant.DATABASE_SQL));
			}
		}else {
			for (LinkedHashMap<Object, Object> database : queryResult) {
				databaseChoice.getItems().add(database.get(SomeConstant.DATABASE_HQL));
			}
		}

		//加载编辑器
		SplitPane splitPane= (SplitPane) hBox.lookup("#splitPane");
		WebView codeEditor = (WebView)splitPane.getItems().get(0);
		WebEngine webEngine = codeEditor.getEngine();
		webEngine.load(getClass().getResource("/ace/editor_sql.html").toExternalForm());
		webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == Worker.State.SUCCEEDED) {
				// 在 WebView 引擎加载完成后设置内容
				String finalContent = content.replace("`", "\\`");
				webEngine.executeScript("editor.setValue(`" + finalContent + "`);");
			}
		});

		tab.setContent(hBox);
		LinkedHashMap<Tab, List<String>> tabsMapper = SJKFTabManager.getInstance().getTabPathMapper();
		tabsMapper.put(tab, Arrays.asList(path, fileType));
		return tab;
	}

	//根据sql类型执行对应sql
	private static List<LinkedHashMap<Object, Object>> getQueryResult(String sqlContent, String sqlType) throws SQLException, IOException {
		List<LinkedHashMap<Object, Object>> queryResult;
		if (sqlType.equals(SomeConstant.SQLFILE)){
			queryResult = MysqlUtil.processSql(sqlContent, null);
		} else {
			queryResult = HiveUtil.processHql(sqlContent, null);
		}
		return queryResult;
	}

	@FXML
	void executeButtonClick(ActionEvent event) throws SQLException {
		TabPane tabBox = (TabPane) ((Node) event.getSource()).getScene().lookup("#tabBox");
		Tab selectedTab = tabBox.getSelectionModel().getSelectedItem();
		HBox hBox = (HBox) selectedTab.getContent();
		SplitPane splitPane= (SplitPane) hBox.lookup("#splitPane");
		WebView webView = (WebView)splitPane.getItems().get(0);

		String sqlContent = (String) webView.getEngine().executeScript("editor.getSelectedText(); editor.session.getTextRange(editor.getSelectionRange());");
		String baseChangeSql = String.format(SqlConstant.database_change, databaseChoice.getValue());

		resultShowBox.getChildren().clear();

		//等待加载页
		Loading loading = new Loading((Stage) ((Node) event.getSource()).getScene().getWindow(), "查询执行中，请稍后...");
		loading.show();

		new Thread(() -> {
			try {
				//处理查询并更新结果
				try {
					List<LinkedHashMap<Object, Object>> queryResult = getQueryResult(baseChangeSql + sqlContent, SJKFTabManager.getInstance().getTabPathMapper().get(selectedTab).get(1));

					if (queryResult != null) {
						Platform.runLater(() -> {
							//执行结果不为空则直接打印结果
							TableView<List<Object>> tableView = loadTableView(queryResult);

							// 监听SplitPane的分界线位置变化
							splitPane.getDividers().get(0).positionProperty().addListener((obs, oldPos, newPos) -> {
								double tableViewHeight = resultShowBox.getHeight();
								tableView.setPrefHeight(tableViewHeight - 60);
							});

							resultShowBox.getChildren().clear();
							resultShowBox.getChildren().add(tableView);
						});
					} else {
						Platform.runLater(() -> {
							//执行结果为空则打印完成信息
							Label label = new Label("执行完成");
							label.setId("resultText");
							label.setWrapText(true);
							resultShowBox.getChildren().add(label);
						});
					}
				} catch (SQLException e) {
					Platform.runLater(() -> {
						String error = "";
						error = String.valueOf(e);
						//将错误信息打印
						Label label = new Label(error);
						label.setId("resultText");
						label.setWrapText(true);
						resultShowBox.getChildren().add(label);
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				loading.closeStage();
			}
		}).start();
	}

	@FXML
	void saveButtonClick(ActionEvent event) throws SQLException, IOException {
		TabPane tabBox = (TabPane) ((Node) event.getSource()).getScene().lookup("#tabBox");
		Tab selectedTab = tabBox.getSelectionModel().getSelectedItem();
		HBox hBox = (HBox) selectedTab.getContent();
		SplitPane splitPane= (SplitPane) hBox.lookup("#splitPane");
		WebView webView = (WebView)splitPane.getItems().get(0);

		String path = SJKFTabManager.getInstance().getTabPathMapper().get(selectedTab).get(0);

		String content = (String) webView.getEngine().executeScript("editor.getValue()");
		List<Object> params = Arrays.asList(content.replace("\\","\\\\"), path.substring(path.lastIndexOf(".")+1), path.substring(0, path.lastIndexOf(".")), ProjectVariableManager.getInstance().getProjectName());
		List<List<Object>> paramsList = Arrays.asList(null, params);

		SJKFMapper.updateFileContent(paramsList);
		selectedTab.setGraphic(null);
	}

	//创建查询结果表格
	private TableView<List<Object>> loadTableView(List<LinkedHashMap<Object, Object>> queryResult) {
		ObservableList<List<Object>> data = FXCollections.observableArrayList();

		for (LinkedHashMap<Object, Object> rowResult : queryResult) {
			data.add(new ArrayList<>(rowResult.values()));
		}

		TableView<List<Object>> tableView = new TableView<>();
		tableView.setEditable(true);

		// 加载数据到 TableView
		tableView.setItems(data);

		// 创建列
		int colIndex = 0;
		for (Object colName : queryResult.get(0).keySet()) {
			TableColumn<List<Object>, String> column = new TableColumn<>((String) colName);
			// 设置 CellFactory，使列可编辑
			column.setCellFactory(TextFieldTableCell.forTableColumn());
			// 设置列的数据源
			int finalColIndex = colIndex;
			column.setCellValueFactory(cellData -> new SimpleStringProperty( cellData.getValue().get(finalColIndex).toString()));
			// 将列添加到 TableView
			tableView.getColumns().add(column);
			colIndex++;
		}

		return tableView;
	}
}