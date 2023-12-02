package service.sjkf;

import beans.ProjectVariableManager;
import constants.SomeConstant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mapper.SJKFMapper;
import service.common.CommonAlertController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * ClassName:DirectoryCreateController
 * Package:SJKF
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/12 - 19:29
 */
public class FileRenameController {

	@FXML
	private TextField fileName;

	private TreeItem<String> selectItem;
	private String parentDirect;
	private String renameType;

	@FXML
	void closeButtonClick(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void confirmButtonClick(ActionEvent event) throws SQLException, IOException {
		try{
			List<List<Object>> paramsList;

			if (SomeConstant.DIRECTORY.equals(renameType)){
				//文件信息写入数据库
				List<Object> params1 = Arrays.asList(fileName.getText(),selectItem.getValue(),parentDirect.substring(0, parentDirect.lastIndexOf(".")),SomeConstant.DIRECTORY, ProjectVariableManager.getInstance().getProjectName());
				List<Object> params2 = Arrays.asList(parentDirect.substring(0, parentDirect.lastIndexOf("."))+"."+fileName.getText(),parentDirect, ProjectVariableManager.getInstance().getProjectName());
				paramsList = Arrays.asList(null, params1, params2);
				SJKFMapper.updateDirectory(paramsList, parentDirect);
			}else {
				List<Object> params = Arrays.asList(fileName.getText(),parentDirect.substring(0, parentDirect.lastIndexOf(".")),SomeConstant.DIRECTORY, ProjectVariableManager.getInstance().getProjectName(), selectItem.getValue());
				paramsList = Arrays.asList(null, params);
				SJKFMapper.updateFileName(paramsList);
			}
			//刷新文件树
			selectItem.setValue(fileName.getText());
		}catch (SQLException e) {
			CommonAlertController.loadCommonAlertWindow("警告", "存在相同命名的文件(夹)");
		}finally {
			closeButtonClick(event);
		}
	}

	public void renameConfirmWindow(String finalSelectedValue, TreeItem<String> selectedItem, String type) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("sjkf/FileRename.fxml"));
		VBox vBox = fxmlLoader.load();

		FileRenameController fileRenameController = fxmlLoader.getController();
		fileRenameController.setSelectItem(selectedItem);
		fileRenameController.setParentDirect(finalSelectedValue);
		fileRenameController.setRenameType(type);

		TextField fileName = (TextField) vBox.lookup("#fileName");
		fileName.setText(selectedItem.getValue());
		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("重命名");
		stage.setScene(new Scene(vBox));
		stage.show();
	}

	public void setSelectItem(TreeItem<String> selectItem) {
		this.selectItem = selectItem;
	}

	public void setParentDirect(String parentDirect) {
		this.parentDirect = parentDirect;
	}

	public void setRenameType(String renameType) {
		this.renameType = renameType;
	}
}