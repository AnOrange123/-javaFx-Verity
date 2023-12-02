package service.common;

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
import service.sjkf.SJKFController;

import java.io.IOException;

/**
 * ClassName:CommonAlertController
 * Package:service.common
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/6 - 9:20
 */
public class CommonAlertController {
	@FXML
	void confirmButtonClick(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	public static void loadCommonAlertWindow(String titleText, String alertText) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("common/CommonAlert.fxml"));
		VBox vBox = fxmlLoader.load();
		Label label = new Label(alertText);
		label.setWrapText(true);
		vBox.getChildren().add(0, label);
		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle(titleText);
		stage.setScene(new Scene(vBox));
		stage.show();
	}
}
