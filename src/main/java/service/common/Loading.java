package service.common;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 加载等待页案例
 *
 * @author Miaoqx
 */
public class Loading {

	protected Stage stage;
	protected StackPane root;
	protected Label messageLb;
	protected TextArea textArea;
	protected ImageView loadingView;

	public Loading(Stage owner, String alertText) {
		loadingView = new ImageView(new Image("loading.gif"));

		messageLb = new Label(alertText);
		messageLb.setWrapText(true);
		messageLb.setFont(Font.font(20));
		messageLb.setTextFill(Color.WHITE);

		textArea = new TextArea("");
		textArea.setWrapText(true);
		textArea.setStyle("-fx-font-size:16;-fx-background-color: rgb(0, 0, 0, 0.7); -fx-control-inner-background: rgb(0, 0, 0, 0.7);");

		root = new StackPane();
		root.setMouseTransparent(true);
		root.setPrefSize(owner.getWidth(), owner.getHeight());
		root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.5), null, null)));
		root.getChildren().addAll(textArea, loadingView, messageLb);

		Scene scene = new Scene(root);
		scene.setFill(Color.TRANSPARENT);

		stage = new Stage();

		stage.setScene(scene);
		stage.setResizable(false);
		stage.initOwner(owner);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.getIcons().addAll(owner.getIcons());
		stage.setX(owner.getX());
		stage.setY(owner.getY());
		stage.setHeight(owner.getHeight());
		stage.setWidth(owner.getWidth());
	}

	// 更改信息
	public void showMessage(String message) {
		Platform.runLater(() -> textArea.setText(textArea.getText() + message));
	}

	//关闭加载球，允许操作
	public void closeLoadingView() {
		Platform.runLater(() -> root.setMouseTransparent(false));
		Platform.runLater(() -> loadingView.setImage(null));
		Platform.runLater(() -> messageLb.setText(null));
	}

	//添加双击关闭监听
	public void setCloseEvent() {
		// 添加鼠标双击事件监听器
		stage.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
				closeStage();
			}
		});
	}

	// 显示
	public void show() {
		Platform.runLater(() -> stage.show());
	}

	// 关闭
	public void closeStage() {
		Platform.runLater(() -> stage.close());
	}
}
