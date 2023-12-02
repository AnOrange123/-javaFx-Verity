package service.main;

import beans.ProjectVariableManager;
import constants.SystemLibConstant;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mapper.MainMapper;
import service.sjkf.SJKFController;
import service.sjzy.mysql.TableInfoController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * ClassName:ProjectCreateController
 * Package:service.sjmx
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/10/4 - 20:14
 */
public class ProjectCreateController {
	@FXML
	private TextField projectName;

	@FXML
	private TextField projectPostscript;
	private ActionEvent mainEvent;
	@FXML
	private VBox userNameTableVBox;

	public void setMainEvent(ActionEvent mainEvent) {
		this.mainEvent = mainEvent;
	}

	@FXML
	void closeButtonClick(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void confirmButtonClick(ActionEvent event) throws SQLException, IOException {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		if ("新建项目".equals(stage.getTitle())){
			//新建项目处理逻辑
			//将新项目数据添加到数据库
			List<Object> params = Arrays.asList(projectName.getText(), projectPostscript.getText(), ProjectVariableManager.getInstance().getUser());
			List<List<Object>> paramsList = Arrays.asList(null, params);
			MainMapper.insertProject(paramsList);
		}else{
			//编辑项目处理逻辑
			List<Object> params = Arrays.asList(projectPostscript.getText(), projectName.getText());
			List<List<Object>> paramsList = Arrays.asList(null, params);
			MainMapper.updateProject(paramsList);
		}

		//更新用户项目权限表
		List<Object> params = Arrays.asList(projectName.getText());
		List<List<Object>> paramsList =  new ArrayList<>();
		paramsList.add(null);
		paramsList.add(params);
		int insertCount = 0;
		for (Node child : userNameTableVBox.getChildren()) {
			HBox hBox = (HBox) child;
			for (Node originUserCheckBox : hBox.getChildren()) {
				CheckBox userCheckBox = (CheckBox) originUserCheckBox;
				if (userCheckBox.isSelected()){
					paramsList.add(Arrays.asList(userCheckBox.getText(), projectName.getText()));
					insertCount ++;
				}
			}
		}
		MainMapper.updateUserPermission(paramsList, insertCount);

		//更新项目列表
		new ProjectSelectController().createSjmxWindow(mainEvent);

		closeButtonClick(event);
	}

	//新建项目
	public void createNewProjectWindow(ActionEvent event) throws IOException, SQLException {
		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("EmptyMain/ProjectCreate.fxml"));
		VBox vBox = fxmlLoader.load();
		ProjectCreateController projectCreateController = fxmlLoader.getController();
		projectCreateController.setMainEvent(event);

		//权限列表设置
		permissionListSet(vBox);

		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("新建项目");
		stage.setScene(new Scene(vBox));
		stage.show();
	}

	//编辑项目
	public void createNewProjectWindow(ActionEvent event, String name, String postscript) throws IOException, SQLException {
		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("EmptyMain/ProjectCreate.fxml"));
		VBox vBox = fxmlLoader.load();
		ProjectCreateController projectCreateController = fxmlLoader.getController();
		projectCreateController.setMainEvent(event);

		//设置文本内容
		TextField projectNameTextField = (TextField) vBox.lookup("#projectNameTextField");
		TextField projectPostscriptTextField = (TextField) vBox.lookup("#projectPostscriptTextField");

		projectNameTextField.setText(name);
		//禁止重命名项目
		projectNameTextField.setDisable(true);
		projectPostscriptTextField.setText(postscript);

		//权限列表设置
		permissionListSet(vBox, name);

		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("编辑项目");
		stage.setScene(new Scene(vBox));
		stage.show();
	}

	//新建项目：权限列表
	private void permissionListSet(VBox vBox) throws SQLException, IOException {
		ScrollPane scroll = (ScrollPane) vBox.lookup("#scroll");
		VBox userVBox = (VBox) scroll.getContent();

		//获取所有用户
		List<LinkedHashMap<Object, Object>> userQueryResult = MainMapper.queryUserAll();
		ArrayList<String> allUserList = new ArrayList<>();
		for (LinkedHashMap<Object, Object> row : userQueryResult) {
			allUserList.add((String) row.get(SystemLibConstant.USERINFOTABLE_USERNAME));
		}

		//添加checkbox
		HBox hBox = null;
		for (int i = 0; i < allUserList.size(); i++) {
			if (i%2 == 0){
				hBox = new HBox();
				hBox.setId("row");
				userVBox.getChildren().add(hBox);
			}

			CheckBox userCheckBox = new CheckBox(allUserList.get(i));
			//设置当前用户的权限框为不可设置
			if (ProjectVariableManager.getInstance().getUser().equals(allUserList.get(i))){
				userCheckBox.setSelected(true);
				userCheckBox.setDisable(true);
			}
			userCheckBox.setId("userCheckBox");
			hBox.getChildren().add(userCheckBox);
		}
	}

	//编辑项目：权限列表
	private void permissionListSet(VBox vBox, String name) throws SQLException, IOException {
		ScrollPane scroll = (ScrollPane) vBox.lookup("#scroll");
		VBox userVBox = (VBox) scroll.getContent();

		//获取所有用户
		List<LinkedHashMap<Object, Object>> userQueryResult = MainMapper.queryUserAll();
		ArrayList<String> allUserList = new ArrayList<>();
		for (LinkedHashMap<Object, Object> row : userQueryResult) {
			allUserList.add((String) row.get(SystemLibConstant.USERINFOTABLE_USERNAME));
		}

		//获取用户对应权限表
		List<Object> params = Collections.singletonList(name);
		List<List<Object>> paramsList = Arrays.asList(null, params);
		List<LinkedHashMap<Object, Object>> permissionQueryResult = MainMapper.queryUserPermissionByProject(paramsList);
		ArrayList<String> legalUserList = new ArrayList<>();
		for (LinkedHashMap<Object, Object> row : permissionQueryResult) {
			legalUserList.add((String) row.get(SystemLibConstant.USEPROJECTPERMISSIONTABLE_USER));
		}

		//添加checkbox
		HBox hBox = null;
		for (int i = 0; i < allUserList.size(); i++) {
			if (i%2 == 0){
				hBox = new HBox();
				hBox.setId("row");
				userVBox.getChildren().add(hBox);
			}

			CheckBox userCheckBox = new CheckBox(allUserList.get(i));
			//设置已有权限的用户
			if (legalUserList.contains(allUserList.get(i))){
				userCheckBox.setSelected(true);
			}
			//设置当前用户的权限框为不可设置
			if (ProjectVariableManager.getInstance().getUser().equals(allUserList.get(i))){
				userCheckBox.setSelected(true);
				userCheckBox.setDisable(true);
			}
			userCheckBox.setId("userCheckBox");
			hBox.getChildren().add(userCheckBox);
		}
	}
}