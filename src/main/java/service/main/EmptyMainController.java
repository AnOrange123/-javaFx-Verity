package service.main;

import beans.ProjectVariableManager;
import beans.SJMXManager;
import javafx.scene.control.TabPane;
import service.sjkf.SJKFController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import service.sjmx.SJMXDetailController;
import service.sjtb.SJTBController;
import service.sjzy.SJZYController;
import utils.CommonUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * ClassName:EmptyMainController
 * Package:Main
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/2 - 19:04
 */
public class EmptyMainController {
	@FXML
	private ToggleButton MXSJButton;

	@FXML
	private ToggleButton SJZYButton;

	@FXML
	private ToggleButton SJKFButton;

	@FXML
	private ToggleButton SJTBButton;
	@FXML
	private SVGPath MXSJ_Svg;

	@FXML
	private SVGPath SJKF_Svg;

	@FXML
	private SVGPath SJTB_Svg;

	@FXML
	private SVGPath SJZY_Svg;
	@FXML
	private SVGPath MinimizeSvg;

	@FXML
	private SVGPath CloseSvg;
	private static String currentModule;

	//鼠标悬停关闭页面按钮触发
	@FXML
	void CloseHover(MouseEvent event) {
		CloseSvg.setStroke(Color.valueOf("#FF8303"));
	}

	//鼠标移出关闭页面按钮触发
	@FXML
	void CloseExit(MouseEvent event) {
		CloseSvg.setStroke(Color.WHITE);
	}

	//鼠标悬停最小化按钮触发
	@FXML
	void MinimizeHover(MouseEvent event) {
		MinimizeSvg.setStroke(Color.valueOf("#FF8303"));
	}

	//鼠标移出最小化按钮触发
	@FXML
	void MinimizeExit(MouseEvent event) {
		MinimizeSvg.setStroke(Color.WHITE);
	}

	//鼠标点击关闭按钮触发
	@FXML
	void CloseWindows(MouseEvent event) throws SQLException, IOException {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	//鼠标点击最小化按钮触发
	@FXML
	void MinimizeWindows(MouseEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setIconified(true);
	}

	//点击模型设计按钮触发
	@FXML
	void MXSJButton(ActionEvent event) throws SQLException, IOException {
		if (!CommonUtil.isNullOrEmpty(ProjectVariableManager.getInstance().getProjectName())) {
			//颜色变化
			if (MXSJButton.isSelected()) {
				colorSet(MXSJ_Svg);
			}
			SJMXDetailController.createProjectWindow(event, ProjectVariableManager.getInstance().getProjectName());
			currentModule = "模型设计";
		}
	}

	//点击数据同步按钮触发
	@FXML
	void SJTBButton(ActionEvent event) throws SQLException, IOException {
		if (!CommonUtil.isNullOrEmpty(ProjectVariableManager.getInstance().getProjectName())) {
			//颜色变化
			if (SJTBButton.isSelected()) {
				colorSet(SJTB_Svg);
			}
			SJTBController.createSJTBWindow(event, ProjectVariableManager.getInstance().getProjectName());
			currentModule = "数据同步";
		}
	}

	//点击数据开发按钮触发
	@FXML
	void SJKFButton(ActionEvent event) throws IOException, SQLException {
		if (!CommonUtil.isNullOrEmpty(ProjectVariableManager.getInstance().getProjectName())) {
			//颜色变化
			if (SJKFButton.isSelected()) {
				colorSet(SJKF_Svg);
			}
			//加载数据开发模块界面
			SJKFController.createSjkfWindow(event);
			currentModule = "数据开发";
		}
	}

	//点击数据资源按钮触发
	@FXML
	void SJZYButton(ActionEvent event) throws SQLException, IOException {
		if (!CommonUtil.isNullOrEmpty(ProjectVariableManager.getInstance().getProjectName())){
			//颜色变化
			if (SJZYButton.isSelected()){
				colorSet(SJZY_Svg);
			}
			//加载数据资源模块界面
			new SJZYController().createSjzyWindow(event);
			currentModule = "数据资源";
		}
	}

	/**
	 * 替换页面中的内容
	 * @param event
	 * @param content  替换后的内容
	 */
	public static void replaceMainModule(ActionEvent event, Node content) throws IOException, SQLException {
		if (!CommonUtil.isNullOrEmpty(ProjectVariableManager.getInstance().getProjectName())){
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			VBox moduleContent = (VBox) stage.getScene().getRoot().lookup("#moduleContent");

			if ("模型设计".equals(currentModule) && SJMXManager.getInstance().getStatus() == 1){
				SJMXManager.getInstance().dataModuleSave();
			}

			moduleContent.getChildren().clear();
			moduleContent.getChildren().add(content);
		}
	}

	/**
	 * 当选择模块后，变化svg的颜色
	 * @param svg 要变化颜色的svg图
	 */
	private void colorSet(SVGPath svg) {
		SJKF_Svg.setStroke(Color.valueOf("#8C8D90"));
		SJTB_Svg.setStroke(Color.valueOf("#8C8D90"));
		SJZY_Svg.setStroke(Color.valueOf("#8C8D90"));
		MXSJ_Svg.setStroke(Color.valueOf("#8C8D90"));
		svg.setStroke(Color.valueOf("#FF8303"));
	}
}