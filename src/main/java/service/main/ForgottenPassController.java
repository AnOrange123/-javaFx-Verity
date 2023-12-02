package service.main;

import constants.SystemLibConstant;
import javafx.animation.PauseTransition;
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
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;
import mapper.MainMapper;
import service.common.CommonAlertController;
import utils.PassUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * ClassName:ForgottenPassController
 * Package:service.main
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/6 - 15:50
 */
public class ForgottenPassController {
	@FXML
	private PasswordField secPasswordLabel;

	@FXML
	private Label passLabel1;

	@FXML
	private Label passLabel2;

	@FXML
	private PasswordField passwordLabel;

	@FXML
	private TextField phoneLabel;

	@FXML
	private SVGPath registerStepSecondSVG;

	@FXML
	private SVGPath registerStepThirdSVG;

	@FXML
	private ProgressBar stepFirstProgressBar;

	@FXML
	private ProgressBar stepSecondProgressBar;

	@FXML
	private Label tipLabel;

	@FXML
	private VBox tipVBox;

	@FXML
	private TextField userNameLabel;

	private int stepCount = 1;

	@FXML
	void nextButtonClick(ActionEvent event) throws SQLException, IOException {
		//用户输入
		String phone = phoneLabel.getText();
		String userName = userNameLabel.getText();
		String pass = passwordLabel.getText();
		String secPass = secPasswordLabel.getText();

		//获取用户表中数据
		boolean isLegal = false;
		List<LinkedHashMap<Object, Object>> queryResult = MainMapper.queryUserAll();
		for (LinkedHashMap<Object, Object> row : queryResult) {
			String legalUser = (String) row.get(SystemLibConstant.USERINFOTABLE_USERNAME);
			String legalPhone = (String) row.get(SystemLibConstant.USERINFOTABLE_PHONENUM);
			//检查用户和手机号绑定情况
			if (Objects.equals(legalUser, userName) && Objects.equals(legalPhone, phone)){
				isLegal = true;
				break;
			}
		}

		switch (stepCount){
			//验证手机号和用户名的绑定是否合法
			case 1:
				if (isLegal){
					stepCount ++;
					passLabel1.setVisible(true);
					passLabel2.setVisible(true);
					passwordLabel.setVisible(true);
					secPasswordLabel.setVisible(true);
					stepFirstProgressBar.setProgress(100);
					registerStepSecondSVG.setStroke(Color.valueOf("#00FF00"));
					phoneLabel.setDisable(true);
					userNameLabel.setDisable(true);
					tipLabel.setText("请输入新的密码");
				}else {
					CommonAlertController.loadCommonAlertWindow("警告", "用户名或手机号错误");
				}
				break;
			//重置密码
			default:
				if (!Objects.equals(pass, secPass)){
					CommonAlertController.loadCommonAlertWindow("警告", "两次输入的密码不一致");
					passwordLabel.clear();
					secPasswordLabel.clear();
				}else{
					stepSecondProgressBar.setProgress(100);
					registerStepThirdSVG.setStroke(Color.valueOf("#00FF00"));

					//插入新的用户
					List<Object> params = Arrays.asList(PassUtil.passEncode(pass), userName);
					List<List<Object>> paramsList = Arrays.asList(null, params);
					MainMapper.updateUserPass(paramsList);

					//更新页面并返回登录页面
					tipVBox.getChildren().clear();
					tipLabel.setText("");
					Label label = new Label("重置成功，即将返回登录界面");
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
	}

	public static void loadForgottenPass(HBox contentHBox) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(ForgottenPassController.class.getClassLoader().getResource("WelcomeMain/ForgottenPass.fxml"));
		HBox forgottenPassContent = fxmlLoader.load();

		contentHBox.getChildren().clear();
		contentHBox.getChildren().add(forgottenPassContent);
	}
}