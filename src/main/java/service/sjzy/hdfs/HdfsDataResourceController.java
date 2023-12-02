package service.sjzy.hdfs;

import beans.sjzy.HdfsFileObject;
import constants.SomeConstant;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.common.Loading;
import service.main.EmptyMainController;
import service.sjzy.SJZYController;
import service.sjzy.hdfs.DirectoryCreateController;
import utils.HDFSUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * ClassName:HdfsDataResourceController
 * Package:service.sjzy
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/20 - 0:25
 */
public class HdfsDataResourceController {
	@FXML
	private TextField pathQuery;
	@FXML
	private VBox mainVBox;

	public void createHdfsDataResourceWindow(ActionEvent event) throws IOException, URISyntaxException, InterruptedException, SQLException {
		//获取hdfs数据资源界面窗口
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("sjzy/hdfs/HdfsDataResource.fxml"));
		VBox hdfsDataResourceContent = fxmlLoader.load();

		//将查询目录的TextField默认设置为"/"
		TextField pathQuery = (TextField) hdfsDataResourceContent.lookup("#pathQuery");
		pathQuery.setText("/");
		//显示目录信息
		directoryDisplay(hdfsDataResourceContent);

		EmptyMainController.replaceMainModule(event,hdfsDataResourceContent);
	}

	//显示目录信息
	public static void directoryDisplay(VBox hdfsContent) throws URISyntaxException, IOException, InterruptedException {
		ScrollPane scroll = (ScrollPane) hdfsContent.lookup("#scroll");
		TextField pathQuery = (TextField) hdfsContent.lookup("#pathQuery");
		VBox hdfsFileInfoTable = (VBox) scroll.getContent();
		hdfsFileInfoTable.getChildren().clear();

		//查询TextField里的路径下，hdfs的文件信息
		List<LinkedHashMap<String, Object>> queryResult = HDFSUtil.queryDirectory(pathQuery.getText());

		//遍历文件信息，逐行添加
		for (LinkedHashMap<String, Object> row : queryResult) {
			HBox rowHBox = addRowInfo(row, hdfsContent);
			hdfsFileInfoTable.getChildren().add(rowHBox);
		}
	}

	/**
	 * 添加一行文件信息
	 * @param row  一行文件信息
	 * @param hdfsContent
	 * @return
	 */
	private static HBox addRowInfo(LinkedHashMap<String, Object> row, VBox hdfsContent) {
		TextField pathQuery = (TextField) hdfsContent.lookup("#pathQuery");
		HBox rowHBox = new HBox();
		rowHBox.setId("row");

		Label permissionLabel = new Label((String) row.get(SomeConstant.PERMISSION));
		permissionLabel.setId("permissionLabel");
		Label ownerLabel = new Label((String) row.get(SomeConstant.OWNER));
		ownerLabel.setId("ownerLabel");
		Label groupLabel = new Label((String) row.get(SomeConstant.GROUP));
		groupLabel.setId("groupLabel");
		Label sizeLabel = new Label(String.valueOf(row.get(SomeConstant.SIZE)) );
		sizeLabel.setId("sizeLabel");
		Label blockSizeLabel = new Label(String.valueOf(row.get(SomeConstant.BLOCKSIZE)));
		blockSizeLabel.setId("blockSizeLabel");

		//文件名按钮，点击可以进行跳转或者打开文件操作
		Button pathButton = new Button(((String) row.get(SomeConstant.PATH)).replace("_", "__"));
		pathButton.setId("pathButton");
		pathButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if ((Boolean) row.get(SomeConstant.ISDirectory)){
					//如果是文件夹，则拼接路径到TextField，并刷新目录信息表
					String newPath = "";
					if (pathQuery.getText().endsWith("/")){
						newPath = pathQuery.getText() + row.get(SomeConstant.PATH);
					}else {
						newPath = pathQuery.getText() + "/" + row.get(SomeConstant.PATH);
					}
					pathQuery.setText(newPath);
					try {
						directoryDisplay(hdfsContent);
					} catch (URISyntaxException | IOException | InterruptedException e) {
						throw new RuntimeException(e);
					}
				}else {
					// TODO: 2023/9/22 文件预览功能
					System.out.println("是文件");
				}
			}
		});

		HBox buttonBox = new HBox();
		Button deleteButton = new Button("删除");
		//删除文件（夹）
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String deletePath = "";
				if (pathQuery.getText().endsWith("/")){
					deletePath = pathQuery.getText() + row.get(SomeConstant.PATH);
				}else {
					deletePath = pathQuery.getText() + "/" + row.get(SomeConstant.PATH);
				}
				try {
					HDFSUtil.delete(deletePath);
					directoryDisplay(hdfsContent);
				} catch (URISyntaxException | IOException | InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});
		Button downloadButton = new Button("下载");
		//下载文件（夹）
		downloadButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setTitle("选择文件夹"); // 设置对话框标题
				Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
				File selectedDirectory = directoryChooser.showDialog(window); // 打开文件夹选择对话框

				if (selectedDirectory != null) {
					String downloadFilePath = "";
					if (pathQuery.getText().endsWith("/")){
						downloadFilePath = pathQuery.getText() + row.get(SomeConstant.PATH);
					}else {
						downloadFilePath = pathQuery.getText() + "/" + row.get(SomeConstant.PATH);
					}
					HdfsFileObject hdfsFileObject = new HdfsFileObject();
					hdfsFileObject.setSourcePosition(downloadFilePath);
					hdfsFileObject.setTargetPosition(selectedDirectory.getAbsolutePath());

					try {
						HDFSUtil.downloadFile(hdfsFileObject);
						directoryDisplay(hdfsContent);
					} catch (URISyntaxException | IOException | InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		});

		buttonBox.getChildren().addAll(deleteButton, downloadButton);
		buttonBox.setId("buttonBox");

		rowHBox.getChildren().addAll(permissionLabel, ownerLabel, groupLabel, sizeLabel, blockSizeLabel, pathButton, buttonBox);

		return rowHBox;
	}

	//用户输入textField后点击转到按钮，刷新目录信息表
	@FXML
	void gotoButton(ActionEvent event) throws URISyntaxException, IOException, InterruptedException {
		if (Objects.equals(pathQuery.getText(), "")){
			pathQuery.setText("/");
		}
		directoryDisplay(mainVBox);
	}

	//点击创建文件夹按钮触发
	@FXML
	void createDirectoryButton(ActionEvent event) throws IOException {
		new DirectoryCreateController().createConfirmWindow(mainVBox);
	}

	//上传文件
	@FXML
	void uploadButton(ActionEvent event) throws URISyntaxException, IOException, InterruptedException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("选择文件"); // 设置对话框标题
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File selectedFile = fileChooser.showOpenDialog(window); // 打开文件选择对话框

		//等待加载页
		Loading loading = new Loading((Stage) ((Node) event.getSource()).getScene().getWindow(), "文件正在上传...");
		loading.show();

		new Thread(()->{
			try {
				if (selectedFile != null) {
					HdfsFileObject hdfsFileObject = new HdfsFileObject();
					hdfsFileObject.setSourcePosition(selectedFile.getAbsolutePath());
					hdfsFileObject.setTargetPosition(pathQuery.getText());

					HDFSUtil.uploadFile(hdfsFileObject);
					Platform.runLater(()->{
						try {
							directoryDisplay(mainVBox);
						} catch (URISyntaxException | IOException | InterruptedException e) {
							throw new RuntimeException(e);
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				loading.closeStage();
			}
		}).start();
	}

	@FXML
	void returnMainMenuButton(ActionEvent event) throws SQLException, IOException {
		new SJZYController().createSjzyWindow(event);
	}
}