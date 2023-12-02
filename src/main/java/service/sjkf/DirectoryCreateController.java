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
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mapper.SJKFMapper;
import service.common.CommonAlertController;

import java.io.IOException;
import java.net.URISyntaxException;
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
public class DirectoryCreateController{

	@FXML
	private TextField fileName;

	private TreeItem<String> selectItem;
	private String parentDirect;
	private TreeView<String> rootItem;

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
			List<Object> params = Arrays.asList(ProjectVariableManager.getInstance().getProjectName(), fileName.getText(), parentDirect, SomeConstant.DIRECTORY, "");
			List<List<Object>> paramsList = Arrays.asList(null, params);
			SJKFMapper.insertDirectoryOrFile(paramsList);

			//刷新文件树
			if (selectItem != null){
				SJKFController.makeBranch(fileName.getText(), selectItem, SomeConstant.DIRECTORY);
			}else {
				SJKFController.makeBranch(fileName.getText(), rootItem.getRoot(), SomeConstant.DIRECTORY);
			}
		}catch (SQLException e) {
			CommonAlertController.loadCommonAlertWindow("警告", "存在相同命名的文件(夹)");
		}finally {
			closeButtonClick(event);
		}
	}

	public void createConfirmWindow(String finalSelectedValue, TreeItem<String> selectedItem, TreeView<String> sqlFileTree) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("sjkf/DirectoryCreate.fxml"));
		VBox vBox = fxmlLoader.load();

		DirectoryCreateController directoryCreateController = fxmlLoader.getController();
		directoryCreateController.setSelectItem(selectedItem);
		directoryCreateController.setParentDirect(finalSelectedValue);
		directoryCreateController.setRootItem(sqlFileTree);

		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("新建文件夹");
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