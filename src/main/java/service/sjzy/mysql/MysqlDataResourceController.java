package service.sjzy.mysql;

import constants.SomeConstant;
import constants.SqlConstant;
import constants.SystemLibConstant;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.main.EmptyMainController;
import service.sjzy.SJZYController;
import utils.MysqlUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:MysqlDataResourceController
 * Package:service.sjzy
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/20 - 0:24
 */
public class MysqlDataResourceController {

	@FXML
	private VBox databaseMenuBox;

	@FXML
	private VBox tableQuery;

	@FXML
	void databaseCreateButtonClick(ActionEvent event) throws IOException {
		new DatabaseCreateController().createConfirmWindow(databaseMenuBox);
	}

	@FXML
	void returnMainMenuButton(ActionEvent event) throws SQLException, IOException {
		new SJZYController().createSjzyWindow(event);
	}

	public void createMysqlDataResourceWindow(ActionEvent event) throws IOException, SQLException {
		//获取数据资源界面窗口
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("sjzy/mysql/MysqlDataResource.fxml"));
		VBox mysqlDataResourceContent = fxmlLoader.load();

		//设置数据库菜单
		setDatabaseMenu(mysqlDataResourceContent);

		EmptyMainController.replaceMainModule(event,mysqlDataResourceContent);
	}

	//设置数据库菜单
	public static void setDatabaseMenu(VBox mysqlDataResourceContent) throws SQLException, IOException {
		ScrollPane scrollPane = (ScrollPane) mysqlDataResourceContent.lookup("#scroll");
		VBox databaseMenu = (VBox) scrollPane.getContent();

		String querySql = String.format(SqlConstant.database_show);
		List<LinkedHashMap<Object, Object>> queryResult = MysqlUtil.processSql(querySql, null);

		//遍历数据库名查询结果，逐行添加到文件树
		databaseMenu.getChildren().clear();
		ToggleGroup group = new ToggleGroup();
		for (LinkedHashMap<Object, Object> row : queryResult) {
			HBox hBox = addRow(row, mysqlDataResourceContent, group);
			databaseMenu.getChildren().add(hBox);
		}
	}

	//添加一行信息
	private static HBox addRow(LinkedHashMap<Object, Object> row, VBox mysqlDataResourceContent, ToggleGroup group) {
		HBox hBox = new HBox();

		//库名选择按钮设置
		String database = (String) row.values().iterator().next();
		String finalDatabase = database.replaceAll("_", "__");
		ToggleButton databaseNameButton = new ToggleButton(finalDatabase);
		databaseNameButton.setId("databaseNameButton");
		databaseNameButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//对应数据库表的查询页
				try {
					new TableQueryController().createTableQueryWindow(event, (String) row.values().iterator().next());
				} catch (IOException | SQLException e) {
					throw new RuntimeException(e);
				}
			}
		});
		databaseNameButton.setToggleGroup(group);

		HBox buttonBox = new HBox();
		buttonBox.setId("buttonBox");

		//建表按钮逻辑
		Button tableCreateButton = new Button("建表");
		tableCreateButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					new TableCreateController().createTableCreateWindow(event, (String) row.values().iterator().next());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});

		buttonBox.getChildren().addAll( tableCreateButton);
		hBox.getChildren().addAll(databaseNameButton, buttonBox);
		return hBox;
	}

	//替换数据库表查询页
	public static void replaceMainModule(ActionEvent event, Node content){
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		VBox moduleContent = (VBox) stage.getScene().getRoot().lookup("#tableQuery");
		moduleContent.getChildren().clear();
		moduleContent.getChildren().add(content);
	}
}