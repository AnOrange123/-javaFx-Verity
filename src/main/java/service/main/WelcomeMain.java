package service.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mapper.MainMapper;
import utils.ConfigLoader;
import utils.PropertiesUtil;

import java.util.Properties;

/**
 * ClassName:WelcomeMain
 * Package:Main
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/2 - 16:11
 */
public class WelcomeMain extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("WelcomeMain/WelcomeMain.fxml"));
		Parent scene = fxmlLoader.load();

		//读取配置信息
		Properties loadedProperties = ConfigLoader.loadConfig();
		PropertiesUtil.getInstance().setProps(loadedProperties);

		primaryStage.getIcons().add(new Image("VerityIcon.png"));
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setScene(new Scene(scene));
		primaryStage.setTitle("Verity");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
