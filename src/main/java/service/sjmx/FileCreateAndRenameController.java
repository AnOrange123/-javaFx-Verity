package service.sjmx;

import constants.SomeConstant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mapper.SJMXMapper;
import service.common.CommonAlertController;
import service.sjkf.SJKFController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * ClassName:FileCreateAndRenameController
 * Package:service.sjmx
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/10/10 - 23:25
 */
public class FileCreateAndRenameController {
	@FXML
	private TextField fileName;
	private static TreeItem<String> selectItem;
	private static String path;
	private static TreeView<String> treeView;
	private static String projectName;

	@FXML
	void closeButtonClick(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void confirmButtonClick(ActionEvent event) throws SQLException, IOException {
		try {
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			List<List<Object>> paramsList;

			if ("新建数据模型".equals(stage.getTitle())){
				//文件信息写入数据库
				List<Object> params = Arrays.asList(projectName, path+"."+fileName.getText(),SomeConstant.MODULE );
				paramsList = Arrays.asList(null, params);
				SJMXMapper.insertDirectoryOrFile(paramsList);
				//刷新文件树
				if (selectItem != null){
					SJMXDetailController.makeBranch(fileName.getText(), selectItem, SomeConstant.MODULE);
				}else {
					SJMXDetailController.makeBranch(fileName.getText(), treeView.getRoot(), SomeConstant.MODULE);
				}
			}else {
				//文件信息写入数据库
				List<Object> params = Arrays.asList(path.substring(0, path.lastIndexOf("."))+"."+fileName.getText(),SomeConstant.MODULE,projectName,path);
				paramsList = Arrays.asList(null, params);
				SJMXMapper.renameFile(paramsList);
				selectItem.setValue(fileName.getText());
			}
		}catch (SQLException e) {
			CommonAlertController.loadCommonAlertWindow("警告", "存在相同命名的文件(夹)");
		}finally {
			closeButtonClick(event);
		}
	}

	public void createNewModuleWindow(String projectName, String path, TreeItem<String> selectedItem, TreeView<String> fileTree) throws IOException {
		this.projectName = projectName;
		selectItem = selectedItem;
		this.path = path;
		treeView = fileTree;
		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("sjmx/FileCreate.fxml"));
		VBox vBox = fxmlLoader.load();
		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("新建数据模型");
		stage.setScene(new Scene(vBox));
		stage.show();
	}

	public void createEditModuleWindow(String projectName, String path, TreeItem<String> selectedItem, TreeView<String> fileTree) throws IOException {
		this.projectName = projectName;
		selectItem = selectedItem;
		this.path = path;
		treeView = fileTree;
		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("sjmx/FileCreate.fxml"));
		VBox vBox = fxmlLoader.load();
		TextField fileName = (TextField) vBox.lookup("#fileName");
		fileName.setText(selectedItem.getValue());
		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("编辑数据模型");
		stage.setScene(new Scene(vBox));
		stage.show();
	}
}
