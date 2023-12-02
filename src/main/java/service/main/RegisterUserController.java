package service.main;

import constants.SystemLibConstant;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;
import mapper.MainMapper;
import service.common.CommonAlertController;
import utils.CommonUtil;
import utils.PassUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;

/**
 * ClassName:RegisterUserController
 * Package:service.main
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/6 - 14:23
 */
public class RegisterUserController {
	@FXML
	private PasswordField SecPasswordLabel;

	@FXML
	private PasswordField passwordLabel;

	@FXML
	private TextField phoneLabel;

	@FXML
	private SVGPath registerStepFirstSVG;

	@FXML
	private SVGPath registerStepSecondSVG;

	@FXML
	private TextField userNameLabel;
	@FXML
	private ProgressBar stepProgressBar;
	@FXML
	private VBox tipVBox;

	@FXML
	void nextButtonClick(ActionEvent event) throws IOException, SQLException, InterruptedException {
		//判断用户是否存在
		//是否存在该用户
		boolean isExistUser = false;
		List<LinkedHashMap<Object, Object>> queryResult = MainMapper.queryUserAll();
		for (LinkedHashMap<Object, Object> row : queryResult) {
			String legalUser = (String) row.get(SystemLibConstant.USERINFOTABLE_USERNAME);
			//存在用户
			if (legalUser.equals(userNameLabel.getText())){
				isExistUser = true;
				break;
			}
		}

		if (isExistUser){
			CommonAlertController.loadCommonAlertWindow("警告", "该用户已存在");
		}else if (CommonUtil.isNullOrEmpty(userNameLabel.getText())){
			CommonAlertController.loadCommonAlertWindow("警告", "用户名不能为空");
		}else if (!Objects.equals(passwordLabel.getText(), SecPasswordLabel.getText())){
			CommonAlertController.loadCommonAlertWindow("警告", "两次输入的密码不一致");
			passwordLabel.clear();
			SecPasswordLabel.clear();
		}else if (phoneLabel.getText().length() != 11){
			CommonAlertController.loadCommonAlertWindow("警告", "非法手机号");
		}else {
			stepProgressBar.setProgress(100);
			registerStepSecondSVG.setStroke(Color.valueOf("#00FF00"));

			//插入新的用户
			List<Object> params = Arrays.asList(userNameLabel.getText(), PassUtil.passEncode(passwordLabel.getText()),  phoneLabel.getText());
			List<List<Object>> paramsList = Arrays.asList(null, params);
			MainMapper.insertUser(paramsList);

			//更新页面并返回登录页面
			tipVBox.getChildren().clear();
			Label label = new Label("注册成功，即将返回登录界面");
			tipVBox.getChildren().add(label);
			tipVBox.setPadding(new Insets(80,0,0,0));

			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("WelcomeMain/WelcomeMain.fxml"));
			Parent scene = fxmlLoader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			VBox welcomeMain = (VBox) stage.getScene().getRoot().lookup("#WelcomeMain");

			//创建三秒延迟
			PauseTransition delay = new PauseTransition(Duration.seconds(3));
			delay.setOnFinished(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					welcomeMain.getChildren().clear();
					welcomeMain.getChildren().add(scene);
				}
			});
			delay.play();
		}
	}

	public static void loadRegisterUser(HBox contentHBox) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(RegisterUserController.class.getClassLoader().getResource("WelcomeMain/RegisterUser.fxml"));
		HBox registerUserContent = fxmlLoader.load();

		contentHBox.getChildren().clear();
		contentHBox.getChildren().add(registerUserContent);
	}
}
