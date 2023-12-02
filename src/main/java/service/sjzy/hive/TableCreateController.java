package service.sjzy.hive;

import beans.sjzy.HiveFieldInfo;
import beans.sjzy.HiveTableInfo;
import beans.sjzy.MysqlFieldInfo;
import beans.sjzy.MysqlTableInfo;
import constants.SqlConstant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.sjzy.SJZYController;
import utils.HiveUtil;
import utils.MysqlUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

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
	@FXML
	private TextField tableLocation;

	private static String databaseName;

	@FXML
	void cancelButtonClick(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void confirmButtonClick(ActionEvent event) throws SQLException, IOException {
		HiveTableInfo hiveTableInfo = new HiveTableInfo();
		hiveTableInfo.setDataBase(databaseName);
		hiveTableInfo.setTableName(tableName.getText());
		hiveTableInfo.setComment(tableComment.getText());
		if (!Objects.equals(tableLocation.getText(), "")){
			hiveTableInfo.setLocation(tableLocation.getText());
		}

		ArrayList<HiveFieldInfo> hiveFieldInfos = new ArrayList<>();
		HashMap<String, String> partitions = new HashMap<>();
		for (Node child : tableConstruct.getChildren()) {
			TextField fieldName = (TextField) child.lookup("#fieldName");
			TextField fieldComment = (TextField) child.lookup("#fieldComment");
			TextField dataType = (TextField) child.lookup("#dataType");
			ComboBox<String> isPar = (ComboBox<String>) child.lookup("#isPar");

			if ("是".equals(isPar.getSelectionModel().getSelectedItem())){
				partitions.put(fieldName.getText(), dataType.getText());
			}else {
				HiveFieldInfo hiveFieldInfo = new HiveFieldInfo();
				hiveFieldInfo.setFieldName(fieldName.getText());
				hiveFieldInfo.setComment(fieldComment.getText());
				hiveFieldInfo.setDataType(dataType.getText());
				hiveFieldInfos.add(hiveFieldInfo);
			}

		}
		hiveTableInfo.setHiveFieldInfos(hiveFieldInfos);
		hiveTableInfo.setPartition(partitions);

		HiveUtil.processHql(String.format(SqlConstant.database_change+"%s", databaseName, generateCreateTableHql(hiveTableInfo)), null);
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

		ComboBox<String> isPar = new ComboBox<>();
		isPar.getItems().setAll("是", "否");
		isPar.setId("isPar");

		tableConstructRow.getChildren().addAll(fieldName,fieldComment,dataType,isPar);

		tableConstruct.getChildren().add(tableConstructRow);
	}

	@FXML
	void reduceRowButtonClick(ActionEvent event) {
		tableConstruct.getChildren().remove(tableConstruct.getChildren().size()-1);
	}

	public void createTableCreateWindow(ActionEvent event, String database) throws IOException {
		databaseName = database;
		FXMLLoader fxmlLoader = new FXMLLoader(SJZYController.class.getClassLoader().getResource("sjzy/hive/TableCreate.fxml"));
		VBox vBox = fxmlLoader.load();
		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("新建数据表");
		stage.setScene(new Scene(vBox));
		stage.show();
	}

	private static String generateCreateTableHql(HiveTableInfo hiveTableInfo) {
		// 创建一个StringBuilder对象，用来拼接sql字符串
		StringBuilder sb = new StringBuilder();
		// 拼接create table语句
		sb.append("create table ");
		sb.append(hiveTableInfo.getDataBase()).append(".");
		// 加上表名和左括号
		sb.append(hiveTableInfo.getTableName()).append(" (\n");

		// 获取字段列表
		List<HiveFieldInfo> hiveFieldInfos = hiveTableInfo.getHiveFieldInfos();
		// 遍历字段列表
		for (int i = 0; i < hiveFieldInfos.size(); i++) {
			// 获取当前字段对象
			HiveFieldInfo hiveFieldInfo = hiveFieldInfos.get(i);
			// 拼接字段名和数据类型
			sb.append("  ").append(hiveFieldInfo.getFieldName());
			sb.append(" ").append(hiveFieldInfo.getDataType());
			// 如果有注释，就加上注释
			if (hiveFieldInfo.getComment() != null && !hiveFieldInfo.getComment().isEmpty()) {
				sb.append(" comment '").append(hiveFieldInfo.getComment()).append("'");
			}
			// 如果不是最后一个字段，就加上逗号和换行符
			if (i < hiveFieldInfos.size() - 1) {
				sb.append(",\n");
			}
		}
		// 拼接右括号
		sb.append("\n)");

		// 如果有注释，就加上comment和注释内容
		if (hiveTableInfo.getComment() != null && !hiveTableInfo.getComment().isEmpty()) {
			sb.append(" comment '").append(hiveTableInfo.getComment()).append("'\n");
		}
		// 如果有分区，就加上分区
		if (hiveTableInfo.getPartition() != null && !hiveTableInfo.getPartition().isEmpty()) {
			int pos = 1;
			sb.append("partitioned by (");
			Set<String> keySet = hiveTableInfo.getPartition().keySet();
			for (String partitionField : keySet) {
				String dataType = hiveTableInfo.getPartition().get(partitionField);
				sb.append(partitionField).append(" ").append(dataType);
				// 如果不是最后一个分区字段，就加上逗号
				if (pos < hiveTableInfo.getPartition().size()) {
					sb.append(", ");
				}
				pos += 1;
			}
			sb.append(")\n");
		}
		// 如果有location，就加上location
		if (hiveTableInfo.getLocation() != null && !hiveTableInfo.getLocation().isEmpty()) {
			sb.append("location '").append(hiveTableInfo.getLocation()).append("'\n");
		}

		sb.append(";");
		return sb.toString();
	}
}