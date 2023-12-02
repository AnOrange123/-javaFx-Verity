package service.sjzy;

import beans.sjzy.DataResource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.common.CommonAlertController;
import service.main.EmptyMainController;
import service.sjzy.hdfs.HdfsDataResourceController;
import service.sjzy.hive.HiveDataResourceController;
import service.sjzy.mysql.MysqlDataResourceController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

/**
 * ClassName:SJZYController
 * Package:SJKF
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/4 - 12:34
 */
public class SJZYController {
	@FXML
	private VBox dataResourceTable;

	public SJZYController() {
	}

	public void createSjzyWindow(ActionEvent event) throws IOException, SQLException {
		//获取数据资源界面窗口
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("sjzy/SJZY.fxml"));
		VBox sjzyContent = fxmlLoader.load();

		//获取数据资源列表
		setDataResource(sjzyContent);

		EmptyMainController.replaceMainModule(event,sjzyContent);
	}

	//加载数据资源列表
	private void setDataResource(VBox sjzyContent){
		ScrollPane scroll = (ScrollPane) sjzyContent.lookup("#scroll");
		VBox dataResourceTable = (VBox) scroll.getContent();

		//逐行添加数据资源信息
		for (String type: new String[]{"mysql", "hive", "hdfs"}){
			HBox dataResourceRow = addDataResource(new DataResource("", "", type));
			dataResourceTable.getChildren().add(dataResourceRow);
		}
	}

	//添加一行数据资源信息
	private HBox addDataResource(DataResource dataResource){
		HBox dataResourceRow = new HBox();
		dataResourceRow.setId("dataResourceRow");

		Label label = new Label(dataResource.getResourceName());
		Label label2 = new Label(dataResource.getResourceUrl());
		Label label3 = new Label(dataResource.getResourceType());

		HBox buttonBox = new HBox();
		Button button = new Button("编辑");
		Button button2 = new Button("删除");
		Button button3 = new Button("进入资源");
		button3.setOnAction(event -> {
			//根据资源类型进入对应的资源页面
			if ("mysql".equals(dataResource.getResourceType())){
				try {
					new MysqlDataResourceController().createMysqlDataResourceWindow(event);
				} catch (IOException | SQLException e) {
					throw new RuntimeException(e);
				}
			} else if ("hive".equals(dataResource.getResourceType())) {
				try {
					new HiveDataResourceController().createHiveDataResourceWindow(event);
				} catch (IOException | SQLException e) {
					throw new RuntimeException(e);
				}
			} else {
				try {
					new HdfsDataResourceController().createHdfsDataResourceWindow(event);
				} catch (IOException | URISyntaxException | InterruptedException | SQLException e) {
					try {
						CommonAlertController.loadCommonAlertWindow("警告", "连接失败\n1.请检查HDFS连接配置是否正确\n2.请检查HDFS服务是否启动");
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
					throw new RuntimeException(e);
				}
			}
		});

		buttonBox.getChildren().addAll(button, button2, button3);
		buttonBox.setId("buttonBox");

		dataResourceRow.getChildren().addAll(label, label2, label3, buttonBox);

		return dataResourceRow;
	}
}