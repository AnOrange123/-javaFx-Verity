package service.sjzy.mysql;

import beans.sjzy.MysqlFieldInfo;
import beans.sjzy.MysqlTableInfo;
import constants.SqlConstant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.sjzy.SJZYController;
import utils.MysqlUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ClassName:TableCreateController
 * Package:service.sjzy
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/23 - 18:57
 */
public class TableCreateController {
	@FXML
	private VBox Main;

	@FXML
	private VBox tableConstruct;

	@FXML
	private TextField tableName;

	@FXML
	private TextField tableComment;

	private static String databaseName;

	@FXML
	void cancelButtonClick(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void confirmButtonClick(ActionEvent event) throws SQLException, IOException {
		MysqlTableInfo mysqlTableInfo = new MysqlTableInfo();
		mysqlTableInfo.setDataBase(databaseName);
		mysqlTableInfo.setTableName(tableName.getText());
		mysqlTableInfo.setComment(tableComment.getText());

		ArrayList<MysqlFieldInfo> mysqlFieldInfos = new ArrayList<>();
		for (Node child : tableConstruct.getChildren()) {
			TextField fieldName = (TextField) child.lookup("#fieldName");
			TextField fieldComment = (TextField) child.lookup("#fieldComment");
			TextField dataType = (TextField) child.lookup("#dataType");
			ComboBox<String> isPriKey = (ComboBox<String>) child.lookup("#isPriKey");
			ComboBox<String> isUnique = (ComboBox<String>) child.lookup("#isUnique");
			ComboBox<String> isNotNull = (ComboBox<String>) child.lookup("#isNotNull");
			ComboBox<String> isAutoIncrement = (ComboBox<String>) child.lookup("#isAutoIncrement");

			MysqlFieldInfo mysqlFieldInfo = new MysqlFieldInfo();
			mysqlFieldInfo.setFieldName(fieldName.getText());
			mysqlFieldInfo.setComment(fieldComment.getText());
			mysqlFieldInfo.setDataType(dataType.getText());
			mysqlFieldInfo.setIsKey(isPriKey.getSelectionModel().toString());
			mysqlFieldInfo.setIsUnique(isUnique.getSelectionModel().toString());
			mysqlFieldInfo.setIsNotNull(isNotNull.getSelectionModel().toString());
			mysqlFieldInfo.setIsAutoIncrement(isAutoIncrement.getSelectionModel().toString());

			mysqlFieldInfos.add(mysqlFieldInfo);
		}
		mysqlTableInfo.setFieldInfos(mysqlFieldInfos);

		MysqlUtil.processSql(String.format(SqlConstant.database_change+"%s", databaseName, generateCreateTableSql(mysqlTableInfo)), null);
		cancelButtonClick(event);
	}

	@FXML
	void addRowButtonClick(ActionEvent event) {
		HBox tableConstructRow = new HBox();
		tableConstructRow.setId("tableConstructRow");

		TextField fieldName = new TextField();
		fieldName.setId("fieldName");
		TextField fieldComment = new TextField();
		fieldComment.setId("fieldComment");
		TextField dataType = new TextField();
		dataType.setId("dataType");

		ComboBox<String> isPriKey = new ComboBox<>();
		isPriKey.getItems().setAll("是", "否");
		isPriKey.setId("isPriKey");
		ComboBox<String> isUnique = new ComboBox<>();
		isUnique.getItems().setAll("是", "否");
		isUnique.setId("isUnique");
		ComboBox<String> isNotNull = new ComboBox<>();
		isNotNull.getItems().setAll("是", "否");
		isNotNull.setId("isNotNull");
		ComboBox<String> isAutoIncrement = new ComboBox<>();
		isAutoIncrement.getItems().setAll("是", "否");
		isAutoIncrement.setId("isAutoIncrement");

		tableConstructRow.getChildren().addAll(fieldName,fieldComment,dataType,isPriKey,isUnique,isNotNull,isAutoIncrement);

		tableConstruct.getChildren().add(tableConstructRow);
	}

	@FXML
	void reduceRowButtonClick(ActionEvent event) {
		tableConstruct.getChildren().remove(tableConstruct.getChildren().size()-1);
	}

	public void createTableCreateWindow(ActionEvent event, String database) throws IOException {
		databaseName = database;
		FXMLLoader fxmlLoader = new FXMLLoader(SJZYController.class.getClassLoader().getResource("sjzy/mysql/TableCreate.fxml"));
		VBox vBox = fxmlLoader.load();
		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("新建数据表");
		stage.setScene(new Scene(vBox));
		stage.show();
	}

	private static String generateCreateTableSql(MysqlTableInfo mysqlTableInfo) {
		// 创建一个StringBuilder对象，用来拼接sql字符串
		StringBuilder sb = new StringBuilder();
		// 拼接create table语句
		sb.append("create table ");
		// 如果有数据库名，就加上数据库名和点号
		if (mysqlTableInfo.getDataBase() != null && !mysqlTableInfo.getDataBase().isEmpty()) {
			sb.append(mysqlTableInfo.getDataBase()).append(".");
		}
		// 加上表名和左括号
		sb.append(mysqlTableInfo.getTableName()).append(" (\n");
		// 获取字段列表
		List<MysqlFieldInfo> mysqlFieldInfos = mysqlTableInfo.getFieldInfos();
		// 遍历字段列表
		for (int i = 0; i < mysqlFieldInfos.size(); i++) {
			// 获取当前字段对象
			MysqlFieldInfo mysqlFieldInfo = mysqlFieldInfos.get(i);
			// 拼接字段名和数据类型
			sb.append("  ").append(mysqlFieldInfo.getFieldName()).append(" ").append(mysqlFieldInfo.getDataType());
			// 如果是主键，就加上primary key
			if (Objects.equals(mysqlFieldInfo.getIsKey(), "是")) {
				sb.append(" primary key");
			}
			// 如果是唯一，就加上unique
			if (Objects.equals(mysqlFieldInfo.getIsUnique(), "是")) {
				sb.append(" unique");
			}
			// 如果是非空，就加上not null
			if (Objects.equals(mysqlFieldInfo.getIsNotNull(), "是")) {
				sb.append(" not null");
			}
			// 如果是自增，就加上auto_increment
			if (Objects.equals(mysqlFieldInfo.getIsAutoIncrement(), "是")) {
				sb.append(" auto_increment");
			}
			// 如果有注释，就加上注释
			if (mysqlFieldInfo.getComment() != null && !mysqlFieldInfo.getComment().isEmpty()) {
				sb.append(" comment '").append(mysqlFieldInfo.getComment()).append("'");
			}
			// 如果不是最后一个字段，就加上逗号和换行符
			if (i < mysqlFieldInfos.size() - 1) {
				sb.append(",\n");
			}
		}
		// 拼接右括号
		sb.append("\n)");
		// 如果有注释，就加上comment和注释内容
		if (mysqlTableInfo.getComment() != null && !mysqlTableInfo.getComment().isEmpty()) {
			sb.append(" comment '").append(mysqlTableInfo.getComment()).append("'");
		}
		sb.append(";");
		// 返回sql字符串
		return sb.toString();
	}
}
