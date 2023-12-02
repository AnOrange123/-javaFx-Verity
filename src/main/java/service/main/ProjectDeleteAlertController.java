package service.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mapper.MainMapper;
import service.sjkf.SJKFController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

/**
 * ClassName:UnsaveAlertController
 * Package:service.sjkf
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/10/28 - 23:09
 */
public class ProjectDeleteAlertController {
	private static List<List<Object>> paramsList;
	private static ActionEvent event;

	@FXML
	void closeButtonClick(ActionEvent event) throws SQLException, IOException, URISyntaxException, InterruptedException {
		//取消按钮逻辑
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void confirmButtonClick(ActionEvent event) throws SQLException, IOException, URISyntaxException, InterruptedException {
		//确认按钮逻辑
		MainMapper.deleteProject(paramsList);
		new ProjectSelectController().createSjmxWindow(ProjectDeleteAlertController.event);
		closeButtonClick(event);
	}

	public void createAlertBoxWindow(String alertText, List<List<Object>> paramsList, ActionEvent event) throws IOException{
		this.paramsList = paramsList;
		this.event = event;

		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("EmptyMain/ProjectDeleteAlert.fxml"));
		VBox vBox = fxmlLoader.load();
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