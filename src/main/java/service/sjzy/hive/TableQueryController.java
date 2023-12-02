package service.sjzy.hive;

import constants.SqlConstant;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.sjzy.mysql.MysqlDataResourceController;
import utils.HiveUtil;
import utils.MysqlUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * ClassName:TableQueryController
 * Package:service.sjzy
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/22 - 14:28
 */
public class TableQueryController {
	@FXML
	private VBox Main;
	@FXML
	private TextField tableQueryTextField;

	@FXML
	private VBox tableQueryResult;

	private static String databaseName;


	public void createTableQueryWindow(ActionEvent event, String databaseName) throws IOException, SQLException {
		TableQueryController.databaseName = databaseName;

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("sjzy/hive/TableQuery.fxml"));
		VBox tableQueryContent = fxmlLoader.load();

		//显示所有库表
		tableQueryResultDisplay(event, tableQueryContent);

		MysqlDataResourceController.replaceMainModule(event,tableQueryContent);
	}

	//显示库表
	public static void tableQueryResultDisplay(ActionEvent event, VBox tableQueryContent) throws SQLException, IOException {
		TextField tableQueryTextField = (TextField) tableQueryContent.lookup("#tableQueryTextField");
		ScrollPane scroll = (ScrollPane) tableQueryContent.lookup("#scroll");
		VBox content = (VBox) scroll.getContent();
		content.getChildren().clear();

		//查表sql执行
		String sql = "";
		if (!Objects.equals(tableQueryTextField.getText(), "")){
			//查询框内容非空时，执行搜索sql
			sql = String.format("use `%s`;show tables like '*%s*';", databaseName, tableQueryTextField.getText());
		}else {
			//查询框内容为空时，执行show tables
			sql = String.format(SqlConstant.table_show, databaseName);
		}
		List<LinkedHashMap<Object, Object>> queryResult = HiveUtil.processHql(sql, null);

		//遍历查询结果，逐行添加表名按钮
		HBox hBox = null;
		for (int i = 0; i < queryResult.size(); i++) {
			if (i%2 == 0){
				hBox = new HBox();
				hBox.setId("row");
				content.getChildren().add(hBox);
			}

			Label tableNameLabel = new Label((String) queryResult.get(i).values().iterator().next());
			tableNameLabel.setId("tableNameLabel");
			tableNameLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					try {
						new TableInfoController().createTableInfoWindow(event, databaseName, tableNameLabel.getText());
					} catch (IOException | SQLException e) {
						throw new RuntimeException(e);
					}
				}
			});
			hBox.getChildren().add(tableNameLabel);
		}
	}

	//查询按钮事件
	@FXML
	void tableQueryButtonClick(ActionEvent event) throws SQLException, IOException {
		tableQueryResultDisplay(event, Main);
	}
}