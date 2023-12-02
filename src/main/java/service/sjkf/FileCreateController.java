package service.sjkf;

import beans.ProjectVariableManager;
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
public class FileCreateController {

	@FXML
	private TextField fileName;

	@FXML
	private ComboBox<String> typeChoice;

	private TreeItem<String> selectItem;
	private String parentDirect;
	private TreeView<String> rootItem;


	@FXML
	void closeButtonClick(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void confirmButtonClick(ActionEvent event) throws SQLException, IOException {
		try {
			//文件信息写入数据库
			List<Object> params = Arrays.asList(ProjectVariableManager.getInstance().getProjectName(), fileName.getText(), parentDirect, typeChoice.getValue(), "");
			List<List<Object>> paramsList = Arrays.asList(null, params);
			SJKFMapper.insertDirectoryOrFile(paramsList);

			//刷新文件树
			if (selectItem != null){
				SJKFController.makeBranch(fileName.getText(), selectItem, typeChoice.getValue());
			}else {
				SJKFController.makeBranch(fileName.getText(), rootItem.getRoot(), typeChoice.getValue());
			}

			Stage stage = (Stage) rootItem.getScene().getWindow();
			TabPane tabBox = (TabPane) stage.getScene().getRoot().lookup("#tabBox");

			Tab tab = new QueryTabController().loadQueryTabWindow(parentDirect + "." + fileName.getText(), "", typeChoice.getValue());
			tabBox.getTabs().add(tab);
		}catch (SQLException e) {
			CommonAlertController.loadCommonAlertWindow("警告", "存在相同命名的文件(夹)");
		}finally {
			closeButtonClick(event);
		}
	}

	public void createConfirmWindow(String finalSelectedValue, TreeItem<String> selectedItem, TreeView<String> sqlFileTree) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("sjkf/FileCreate.fxml"));
		VBox vBox = fxmlLoader.load();

		FileCreateController fileCreateController = fxmlLoader.getController();
		fileCreateController.setSelectItem(selectedItem);
		fileCreateController.setParentDirect(finalSelectedValue);
		fileCreateController.setRootItem(sqlFileTree);

		ComboBox<String> type = (ComboBox<String>) vBox.lookup("#typeChoice");
		type.getItems().addAll(SomeConstant.SQLFILE, SomeConstant.HQLFILE);
		type.setValue(SomeConstant.SQLFILE);
		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("新建文件");
		stage.setScene(new Scene(vBox));
		stage.show();
	}

	public void setSelectItem(TreeItem<String> selectItem) {
		this.selectItem = selectItem;
	}

	public void setParentDirect(String parentDirect) {
		this.parentDirect = parentDirect;
	}

	public void setRootItem(TreeView<String> rootItem) {
		this.rootItem = rootItem;
	}
}