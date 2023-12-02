package service.sjzy.hdfs;

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
import utils.HDFSUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

/**
 * ClassName:DirectoryCreateController
 * Package:SJKF
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/12 - 19:29
 */
public class DirectoryCreateController{

	@FXML
	private TextField fileName;
	private static VBox hdfsContent;


	@FXML
	protected void closeButtonClick(ActionEvent event) {
		//关闭按钮逻辑
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void confirmButtonClick(ActionEvent event) throws SQLException, IOException, URISyntaxException, InterruptedException {
		//确认按钮逻辑
		TextField pathQuery = (TextField) hdfsContent.lookup("#pathQuery");
		String createDirectoryPath = "";
		if (pathQuery.getText().endsWith("/")){
			createDirectoryPath = pathQuery.getText() + fileName.getText();
		}else {
			createDirectoryPath = pathQuery.getText() + "/" + fileName.getText();
		}
		HDFSUtil.mkdir(createDirectoryPath);
		HdfsDataResourceController.directoryDisplay(hdfsContent);

		closeButtonClick(event);
	}

	public void createConfirmWindow(VBox content) throws IOException {
		this.hdfsContent = content;
		FXMLLoader fxmlLoader = new FXMLLoader(SJZYController.class.getClassLoader().getResource("sjzy/hdfs/DirectoryCreate.fxml"));
		VBox vBox = fxmlLoader.load();
		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("新建文件夹");
		stage.setScene(new Scene(vBox));
		stage.show();
	}
}