package service.sjkf;

import beans.SJKFTabManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:UnsaveAlertController
 * Package:service.sjkf
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/10/28 - 23:09
 */
public class UnsaveAlertController {
	private TabPane tabPane;
	private Tab tab;

	public void setTabPane(TabPane tabPane) {
		this.tabPane = tabPane;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}

	@FXML
	void closeButtonClick(ActionEvent event) throws SQLException, IOException, URISyntaxException, InterruptedException {
		//取消按钮逻辑
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void confirmButtonClick(ActionEvent event) throws SQLException, IOException, URISyntaxException, InterruptedException {
		//确认按钮逻辑
		tabPane.getTabs().remove(tab);
		LinkedHashMap<Tab, List<String>> tabsMapper = SJKFTabManager.getInstance().getTabPathMapper();
		tabsMapper.remove(tab);
		closeButtonClick(event);
	}

	public void createAlertBoxWindow(String alertText, TabPane tabPane, Tab tab) throws IOException{
		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("sjkf/UnsaveAlert.fxml"));
		VBox vBox = fxmlLoader.load();

		UnsaveAlertController loaderController = fxmlLoader.getController();
		loaderController.setTab(tab);
		loaderController.setTabPane(tabPane);

		Label label = new Label(alertText);
		label.setWrapText(true);
		vBox.getChildren().add(0, label);
		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("警告");
		stage.setScene(new Scene(vBox));
		stage.show();
	}
}