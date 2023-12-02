package service.sjtb;

import constants.SomeConstant;
import constants.SystemLibConstant;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mapper.SJTBMapper;
import mapper.SJZYMapper;
import service.common.CommonAlertController;
import service.sjkf.SJKFController;
import utils.CommonUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:FileCreateAndRenameController
 * Package:service.sjtb
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/10/10 - 23:25
 */
public class FileCreateAndRenameController {
	@FXML
	private TextField fileName;
	@FXML
	private ComboBox<String> sourceDatabaseComboBox;

	@FXML
	private ComboBox<String> sourceDatatableComboBox;

	@FXML
	private ComboBox<String> sourceDatatypeComboBox;

	@FXML
	private ComboBox<String> targetDatabaseComboBox;

	@FXML
	private ComboBox<String> targetDatatableComboBox;

	@FXML
	private ComboBox<String> targetDatatypeComboBox;

	private static TreeItem<String> selectItem;
	private static String path;
	private static TreeView<String> treeView;
	private static String projectName;

	@FXML
	void closeButtonClick(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void confirmButtonClick(ActionEvent event) throws SQLException, IOException {
		try {
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			List<List<Object>> paramsList;

			if ("新建".equals(stage.getTitle())){
				if (!CommonUtil.isNullOrEmpty(fileName.getText()) && !CommonUtil.isNullOrEmpty(sourceDatatypeComboBox.getValue()) && !CommonUtil.isNullOrEmpty(targetDatatypeComboBox.getValue()) && !CommonUtil.isNullOrEmpty(sourceDatabaseComboBox.getValue()) && !CommonUtil.isNullOrEmpty(targetDatabaseComboBox.getValue()) && !CommonUtil.isNullOrEmpty(sourceDatatableComboBox.getValue()) && !CommonUtil.isNullOrEmpty(targetDatatableComboBox.getValue())){
					//文件信息写入数据库
					List<Object> params = Arrays.asList(projectName, path+"."+fileName.getText(),SomeConstant.JOB,sourceDatatypeComboBox.getValue(),targetDatatypeComboBox.getValue(), sourceDatabaseComboBox.getValue(),targetDatabaseComboBox.getValue(), sourceDatatableComboBox.getValue(),targetDatatableComboBox.getValue() );
					paramsList = Arrays.asList(null, params);
					SJTBMapper.insertDirectoryOrJob(paramsList);
					//刷新文件树
					if (selectItem != null){
						SJTBController.makeBranch(fileName.getText(), selectItem, SomeConstant.MODULE);
					}else {
						SJTBController.makeBranch(fileName.getText(), treeView.getRoot(), SomeConstant.MODULE);
					}
				}
			}else {
				if (!CommonUtil.isNullOrEmpty(fileName.getText())){
					//文件信息写入数据库
					List<Object> params = Arrays.asList(path.substring(0, path.lastIndexOf("."))+"."+fileName.getText(),SomeConstant.JOB,projectName,path);
					paramsList = Arrays.asList(null, params);
					SJTBMapper.renameJob(paramsList);
					selectItem.setValue(fileName.getText());
				}
			}
		}catch (SQLException e) {
			CommonAlertController.loadCommonAlertWindow("警告", "存在相同命名的文件(夹)");
		}finally {
			closeButtonClick(event);
		}
	}

	public void createNewFileWindow(String projectName, String path, TreeItem<String> selectedItem, TreeView<String> fileTree) throws IOException {
		this.projectName = projectName;
		selectItem = selectedItem;
		this.path = path;
		treeView = fileTree;

		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("sjtb/FileCreate.fxml"));
		VBox vBox = fxmlLoader.load();

		//获取下拉框
		ComboBox<String> sourceDatatypeComboBox = (ComboBox<String>) vBox.lookup("#sourceDatatypeComboBox");
		ComboBox<String> targetDatatypeComboBox = (ComboBox<String>) vBox.lookup("#targetDatatypeComboBox");
		ComboBox<String> sourceDatabaseComboBox = (ComboBox<String>) vBox.lookup("#sourceDatabaseComboBox");
		ComboBox<String> targetDatabaseComboBox = (ComboBox<String>) vBox.lookup("#targetDatabaseComboBox");
		ComboBox<String> sourceDatatableComboBox = (ComboBox<String>) vBox.lookup("#sourceDatatableComboBox");
		ComboBox<String> targetDatatableComboBox = (ComboBox<String>) vBox.lookup("#targetDatatableComboBox");

		//设置下拉框列表
		sourceDatatypeComboBox.getItems().addAll("hive", "mysql");
		targetDatatypeComboBox.getItems().addAll("hive", "mysql");
		sourceDatatypeComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
				try {
					sourceDatabaseComboBox.getItems().clear();
					sourceDatatableComboBox.getItems().clear();
					sourceDatabaseComboBox.setItems(getDatabaseList(t1));
				} catch (SQLException | IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
		targetDatatypeComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
				try {
					targetDatabaseComboBox.getItems().clear();
					targetDatatableComboBox.getItems().clear();
					targetDatabaseComboBox.setItems(getDatabaseList(t1));
				} catch (SQLException | IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
		sourceDatabaseComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
				try {
					sourceDatatableComboBox.getItems().clear();
					sourceDatatableComboBox.setItems(getDatatableList(sourceDatatypeComboBox.getValue(), t1));
				} catch (SQLException | IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
		targetDatabaseComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
				try {
					targetDatatableComboBox.getItems().clear();
					targetDatatableComboBox.setItems(getDatatableList(targetDatatypeComboBox.getValue(), t1));
				} catch (SQLException | IOException e) {
					throw new RuntimeException(e);
				}
			}
		});

		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("新建");
		stage.setScene(new Scene(vBox));
		stage.show();
	}

	private ObservableList<String> getDatatableList(String dataSourceType, String database) throws SQLException, IOException {
		ObservableList<String> datatableList = FXCollections.observableArrayList();

		//根据数据源类型获取数据库列表
		List<LinkedHashMap<Object, Object>> queryResult = SJZYMapper.queryDatatablesAll(dataSourceType, database);
		for (LinkedHashMap<Object, Object> row : queryResult) {
			String datatable = (String) row.values().iterator().next();
			datatableList.add(datatable);
		}

		return datatableList;
	}

	private ObservableList<String> getDatabaseList(String dataSourceType) throws SQLException, IOException {
		ObservableList<String> databaseList = FXCollections.observableArrayList();

		//根据数据源类型获取数据库列表
		List<LinkedHashMap<Object, Object>> queryResult = SJZYMapper.queryDatabasesAll(dataSourceType);
		for (LinkedHashMap<Object, Object> row : queryResult) {
			String database = (String) row.values().iterator().next();
			databaseList.add(database);
		}

		return databaseList;
	}

	public void createEditFileWindow(String projectName, String path, TreeItem<String> selectedItem, TreeView<String> fileTree) throws IOException, SQLException {
		this.projectName = projectName;
		selectItem = selectedItem;
		this.path = path;
		treeView = fileTree;

		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("sjtb/FileCreate.fxml"));
		VBox vBox = fxmlLoader.load();

		//获取下拉框
		ComboBox<String> sourceDatatypeComboBox = (ComboBox<String>) vBox.lookup("#sourceDatatypeComboBox");
		ComboBox<String> targetDatatypeComboBox = (ComboBox<String>) vBox.lookup("#targetDatatypeComboBox");
		ComboBox<String> sourceDatabaseComboBox = (ComboBox<String>) vBox.lookup("#sourceDatabaseComboBox");
		ComboBox<String> targetDatabaseComboBox = (ComboBox<String>) vBox.lookup("#targetDatabaseComboBox");
		ComboBox<String> sourceDatatableComboBox = (ComboBox<String>) vBox.lookup("#sourceDatatableComboBox");
		ComboBox<String> targetDatatableComboBox = (ComboBox<String>) vBox.lookup("#targetDatatableComboBox");

		//设置下拉框不可更改并设置值
		List<Object> params = Arrays.asList(projectName, path, SomeConstant.JOB);
		List<List<Object>> paramsList = Arrays.asList(null, params);
		List<LinkedHashMap<Object, Object>> queryResult = SJTBMapper.queryJobBaseInfo(paramsList);
		sourceDatatypeComboBox.setDisable(true);
		sourceDatatypeComboBox.setValue((String) queryResult.get(0).get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_SOURCEDATATYPE));
		targetDatatypeComboBox.setDisable(true);
		targetDatatypeComboBox.setValue((String) queryResult.get(0).get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_TARGETDATATYPE));
		sourceDatabaseComboBox.setDisable(true);
		sourceDatabaseComboBox.setValue((String) queryResult.get(0).get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_SOURCEDATABASE));
		targetDatabaseComboBox.setDisable(true);
		targetDatabaseComboBox.setValue((String) queryResult.get(0).get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_TARGETDATABASE));
		sourceDatatableComboBox.setDisable(true);
		sourceDatatableComboBox.setValue((String) queryResult.get(0).get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_SOURCEDATATABLE));
		targetDatatableComboBox.setDisable(true);
		targetDatatableComboBox.setValue((String) queryResult.get(0).get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_TARGETDATATABLE));

		TextField fileName = (TextField) vBox.lookup("#fileName");
		fileName.setText(selectedItem.getValue());

		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("重命名");
		stage.setScene(new Scene(vBox));
		stage.show();
	}
}
