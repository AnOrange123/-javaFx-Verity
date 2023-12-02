package service.sjtb;

import beans.sjtb.JobFieldMapperBean;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import service.sjkf.SJKFController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

/**
 * ClassName:MapperAddController
 * Package:service.sjtb
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/26 - 21:39
 */
public class MapperAddController {
	@FXML
	private ListView<String> selectListView;
	private static TableView<JobFieldMapperBean> fieldMapperTableView;

	@FXML
	void addMapperButtonClick(ActionEvent event) {
		ObservableList<String> selectedItems = selectListView.getSelectionModel().getSelectedItems();
		for (String selectedItem : selectedItems) {
			JobFieldMapperBean jobFieldMapperBean = new JobFieldMapperBean();
			jobFieldMapperBean.setSourceField(selectedItem);
			fieldMapperTableView.getItems().add(jobFieldMapperBean);
		}

		fieldMapperTableView.refresh();
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void cancelButtonClick(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	public void createMapperAddWindow(String datatype, String database, String datatable, TableView<JobFieldMapperBean> fieldMapperTableView) throws IOException, SQLException {
		this.fieldMapperTableView = fieldMapperTableView;

		FXMLLoader fxmlLoader = new FXMLLoader(SJTBController.class.getClassLoader().getResource("sjtb/MapperAdd.fxml"));
		AnchorPane anchorPane = fxmlLoader.load();

		//获取全部字段
		Set<String> fieldSet = JobEditController.getFieldSet(datatype, database, datatable);
		//去除已添加的字段
		for (JobFieldMapperBean tableViewItem : fieldMapperTableView.getItems()) {
			fieldSet.remove(tableViewItem.getSourceField());
		}
		//设置可添加字段到控件
		ListView<String> selectListView = (ListView<String>) anchorPane.lookup("#selectListView");
		selectListView.getItems().setAll(fieldSet);
		selectListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("添加映射");
		stage.setScene(new Scene(anchorPane));
		stage.show();
	}
}
