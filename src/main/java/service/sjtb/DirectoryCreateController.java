package service.sjtb;

import constants.SomeConstant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mapper.SJMXMapper;
import mapper.SJTBMapper;
import service.common.CommonAlertController;
import service.sjkf.SJKFController;
import service.sjmx.SJMXDetailController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * ClassName:DirectoryCreateController
 * Package:service.sjmx
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/10/10 - 22:36
 */
public class DirectoryCreateController {
	@FXML
	private TextField fileName;

	private static TreeItem<String> selectItem;
	private static String path;
	private static TreeView<String> treeView;
	private static String projectName;

	@FXML
	protected void closeButtonClick(ActionEvent event) {
		//关闭按钮逻辑
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void confirmButtonClick(ActionEvent event) throws SQLException, IOException, URISyntaxException, InterruptedException {
		//确认按钮逻辑
		try {
			//文件信息写入数据库
			List<Object> params = Arrays.asList(projectName, path+"."+fileName.getText(), SomeConstant.DIRECTORY, "", "", "", "", "", "");
			List<List<Object>> paramsList = Arrays.asList(null, params);
			SJTBMapper.insertDirectoryOrJob(paramsList);

			//刷新文件树
			if (selectItem != null){
				SJTBController.makeBranch(fileName.getText(), selectItem, SomeConstant.DIRECTORY);
			}else {
				SJTBController.makeBranch(fileName.getText(), treeView.getRoot(), SomeConstant.DIRECTORY);
			}
		}catch (SQLException e) {
			CommonAlertController.loadCommonAlertWindow("警告", "存在相同命名的文件(夹)");
		}finally {
			closeButtonClick(event);
		}
	}

	public void createConfirmWindow(String projectName, String path, TreeItem<String> selectedItem, TreeView<String> treeView) throws IOException {
		this.projectName = projectName;
		selectItem = selectedItem;
		this.path = path;
		this.treeView = treeView;

		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("sjtb/DirectoryCreate.fxml"));
		VBox vBox = fxmlLoader.load();
		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("新建文件夹");
		stage.setScene(new Scene(vBox));
		stage.show();
	}
}
