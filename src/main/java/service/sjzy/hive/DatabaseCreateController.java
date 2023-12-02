package service.sjzy.hive;

import constants.SqlConstant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.sjzy.SJZYController;
import utils.HiveUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

/**
 * ClassName:DatabaseCreateController
 * Package:service.sjzy
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/22 - 13:37
 */
public class DatabaseCreateController{
	@FXML
	private TextField databaseName;
	private static VBox hiveDataResourceContent;


	@FXML
	protected void closeButtonClick(ActionEvent event) {
		//关闭按钮逻辑
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void confirmButtonClick(ActionEvent event) throws SQLException, IOException, URISyntaxException, InterruptedException {
		//确认按钮逻辑
		//建库
		HiveUtil.processHql(String.format(SqlConstant.database_create, databaseName.getText()), null);

		//刷新hive库菜单
		HiveDataResourceController.setDatabaseMenu(hiveDataResourceContent);

		closeButtonClick(event);
	}

	public void createConfirmWindow(VBox content) throws IOException {
		this.hiveDataResourceContent = content;

		FXMLLoader fxmlLoader = new FXMLLoader(SJZYController.class.getClassLoader().getResource("sjzy/hive/DatabaseCreate.fxml"));
		VBox vBox = fxmlLoader.load();
		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("新建数据库");
		stage.setScene(new Scene(vBox));
		stage.show();
	}
}
