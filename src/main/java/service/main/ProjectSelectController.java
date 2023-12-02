package service.main;

import beans.ProjectVariableManager;
import constants.SystemLibConstant;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mapper.MainMapper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:ProjectSelectController
 * Package:service.sjmx
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/10/4 - 13:14
 */
public class ProjectSelectController {

	public ProjectSelectController() {
	}

	@FXML
	void projectCreateButtonClick(ActionEvent event) throws IOException, SQLException {
		new ProjectCreateController().createNewProjectWindow(event);
	}

	public void createSjmxWindow(ActionEvent event) throws IOException, SQLException {
		//获取数据模型界面窗口
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("EmptyMain/SJMX.fxml"));
		VBox sjmxContent = fxmlLoader.load();

		//获取项目列表
		setProjectTable(sjmxContent);

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		VBox moduleContent = (VBox) stage.getScene().getRoot().lookup("#moduleContent");
		moduleContent.getChildren().clear();
		moduleContent.getChildren().add(sjmxContent);
	}

	public void setProjectTable(VBox sjmxContent) throws SQLException, IOException {
		//查询项目列表
		List<LinkedHashMap<Object, Object>> queryResult = MainMapper.queryProjectAll();

		ScrollPane scroll = (ScrollPane) sjmxContent.lookup("#scroll");
		VBox projectTable = (VBox) scroll.getContent();

		for (LinkedHashMap<Object, Object> resultRow : queryResult) {
			HBox projectRow = addProjectInfo(resultRow);
			projectTable.getChildren().add(projectRow);
		}
	}

	private HBox addProjectInfo(LinkedHashMap<Object, Object> resultRow) throws SQLException, IOException {
		HBox projectInfoRow = new HBox();
		projectInfoRow.setId("projectRow");

		Label nameLabel = new Label((String) resultRow.get(SystemLibConstant.DATAMODULEINFO_MODULENAME));
		nameLabel.setId("nameLabel");

		Label postscriptLabel = new Label((String) resultRow.get(SystemLibConstant.DATAMODULEINFO_POSTSCRIPT));
		postscriptLabel.setId("postscriptLabel");

		HBox buttonBox = new HBox();

		//编辑按钮逻辑
		Button editButton = new Button("编辑");
		editButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					new ProjectCreateController().createNewProjectWindow(event, nameLabel.getText(), postscriptLabel.getText());
				} catch (IOException | SQLException e) {
					throw new RuntimeException(e);
				}
			}
		});

		//删除按钮逻辑
		Button deleteButton = new Button("删除");
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				List<Object> params1 = Arrays.asList(nameLabel.getText());
				List<Object> params2 = Arrays.asList( nameLabel.getText());
				List<Object> params3 = Arrays.asList( nameLabel.getText());
				List<List<Object>> paramsList = Arrays.asList(null, params1, params2, params3);

				try {
					new ProjectDeleteAlertController().createAlertBoxWindow("此操作会删除项目下所有数据，是否继续？", paramsList, event);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});

		Button gotoButton = new Button("进入项目");
		gotoButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ProjectVariableManager.getInstance().setProjectName(nameLabel.getText());
				try {
					EmptyMainController.replaceMainModule(event, null);
				} catch (IOException | SQLException e) {
					throw new RuntimeException(e);
				}
			}
		});

		//权限设置
		editButton.setDisable(true);
		deleteButton.setDisable(true);
		gotoButton.setDisable(true);

		//如果是受权人，则开放权限
		List<Object> projectPermissionParams = Arrays.asList(nameLabel.getText());
		List<LinkedHashMap<Object, Object>> projectPermissionQueryResult = MainMapper.queryUserPermissionByProject(Arrays.asList(null, projectPermissionParams));
		for (LinkedHashMap<Object, Object> row : projectPermissionQueryResult) {
			if (row.get(SystemLibConstant.USEPROJECTPERMISSIONTABLE_USER).equals(ProjectVariableManager.getInstance().getUser())){
				gotoButton.setDisable(false);
			}
		}

		//如果是所属人，则开放权限
		List<Object> projectOwnerParams = Arrays.asList(nameLabel.getText());
		List<LinkedHashMap<Object, Object>> projectOwnerQueryResult = MainMapper.queryOwnerByProject(Arrays.asList(null, projectOwnerParams));
		if (ProjectVariableManager.getInstance().getUser().equals(projectOwnerQueryResult.get(0).get(SystemLibConstant.DATAMODULEINFO_OWNER))){
			editButton.setDisable(false);
			deleteButton.setDisable(false);
			gotoButton.setDisable(false);
		}

		buttonBox.getChildren().addAll(editButton, deleteButton, gotoButton);
		buttonBox.setId("buttonBox");

		projectInfoRow.getChildren().addAll(nameLabel, postscriptLabel, buttonBox);

		return projectInfoRow;
	}
}
