package service.main;

import beans.ProjectVariableManager;
import constants.SystemLibConstant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import mapper.MainMapper;
import service.common.CommonAlertController;
import utils.PassUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:WelcomeMainController
 * Package:Main
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/2 - 16:14
 */
public class WelcomeMainController {
	@FXML
	private SVGPath MinimizeSvg;

	@FXML
	private SVGPath CloseSvg;
	@FXML
	private PasswordField passwordLabel;

	@FXML
	private TextField userNameLabel;
	@FXML
	private Button startButton;
	@FXML
	private HBox contentHBox;

	@FXML
	public void Start(ActionEvent event) throws IOException, SQLException {
		//判断用户和密码是否匹配
		String loginStatusPromptText = getLoginStatus(event);

		//根据匹配出来的登录状态执行不同业务
		if (loginStatusPromptText != null){
			CommonAlertController.loadCommonAlertWindow("警告", loginStatusPromptText);
		}else{
			ProjectVariableManager.getInstance().setUser(userNameLabel.getText());

			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("EmptyMain/EmptyMain.fxml"));
			Parent scene = fxmlLoader.load();

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

			FXMLLoader projectSelectLoader = new FXMLLoader(getClass().getClassLoader().getResource("EmptyMain/SJMX.fxml"));
			VBox sjmxContent = projectSelectLoader.load();
			ProjectSelectController projectSelectController = projectSelectLoader.getController();

			//获取项目列表
			projectSelectController.setProjectTable(sjmxContent);
			VBox moduleContent = (VBox) scene.lookup("#moduleContent");
			moduleContent.getChildren().clear();
			moduleContent.getChildren().add(sjmxContent);

			stage.setMaximized(true);
			stage.setScene(new Scene(scene));
			stage.setTitle("Verity");
			stage.show();
		}
	}

	//判断用户和密码是否匹配
	private String getLoginStatus(ActionEvent event) throws SQLException, IOException {
		String userName = userNameLabel.getText();
		String pass = passwordLabel.getText();

		List<LinkedHashMap<Object, Object>> queryResult = MainMapper.queryUserAll();

		//是否存在该用户
		boolean isExistUser = false;
		//密码是否正确
		boolean isCorrectPass = false;
		for (LinkedHashMap<Object, Object> row : queryResult) {
			String legalUser = (String) row.get(SystemLibConstant.USERINFOTABLE_USERNAME);
			String legalPass = (String) row.get(SystemLibConstant.USERINFOTABLE_PASSWORD);
			//存在用户并判断密码
			if (legalUser.equals(userName)){
				isExistUser = true;
				if (PassUtil.checkPass(pass, legalPass)){
					isCorrectPass = true;

					//更新最近登录时间
					List<Object> params = Arrays.asList(userName);
					List<List<Object>> paramsList = Arrays.asList(null, params);
					MainMapper.updateUserLastLoginTime(paramsList);

					break;
				}
			}
		}

		//根据两个flag返回对应的值
		if (isExistUser){
			if (isCorrectPass){
				return null;
			}else{
				return "用户名或密码错误";
			}
		}else{
			return "不存在该用户";
		}
	}

	@FXML
	void CloseWindows(MouseEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void MinimizeWindows(MouseEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setIconified(true);
	}

	@FXML
	void CloseHover(MouseEvent event) {
		CloseSvg.setStroke(Color.valueOf("#FF8303"));
	}

	@FXML
	void CloseExit(MouseEvent event) {
		CloseSvg.setStroke(Color.WHITE);
	}
	@FXML
	void MinimizeHover(MouseEvent event) {
		MinimizeSvg.setStroke(Color.valueOf("#FF8303"));
	}

	@FXML
	void MinimizeExit(MouseEvent event) {
		MinimizeSvg.setStroke(Color.WHITE);
	}

	//忘记密码
	@FXML
	void forgottenButtonClick(MouseEvent event) throws IOException {
		startButton.setDisable(true);
		ForgottenPassController.loadForgottenPass(contentHBox);
	}

	//注册账号
	@FXML
	void registerButtonClick(MouseEvent event) throws IOException {
		startButton.setDisable(true);
		RegisterUserController.loadRegisterUser(contentHBox);
	}
}
