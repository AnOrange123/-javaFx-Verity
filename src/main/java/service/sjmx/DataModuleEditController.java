package service.sjmx;

import beans.SJMXManager;
import beans.sjmx.DataModuleInfoBean;
import beans.sjzy.HiveFieldInfo;
import beans.sjzy.HiveTableInfo;
import constants.SomeConstant;
import constants.SystemLibConstant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import mapper.SJMXMapper;
import service.sjkf.SJKFController;
import utils.CommonUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ClassName:DataModuleEditController
 * Package:service.sjmx
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/10/11 - 12:55
 */
public class DataModuleEditController {
	@FXML
	private VBox dataModuleInfoTable;

	private static String projectName;
	private static String modulePath;

	@FXML
	void dataModuleSaveButtonClick(ActionEvent event) throws SQLException, IOException {
		//基础sql：切换数据库+清空原有数据
		List<Object> params = Arrays.asList(projectName, modulePath);
		List<List<Object>> paramsList = new ArrayList<>();
		paramsList.add(null);
		paramsList.add(params);
		TableView<DataModuleInfoBean> moduleInfoTableView = (TableView<DataModuleInfoBean>) dataModuleInfoTable.getChildren().get(0);
		ObservableList<DataModuleInfoBean> moduleInfoBeans = moduleInfoTableView.getItems();
		int rowSize = 0;
		for (DataModuleInfoBean dataModuleInfoBean : moduleInfoBeans) {
			//逐行添加插入语句
			if (!CommonUtil.isNullOrEmpty(dataModuleInfoBean.getEngFieldName())){
				rowSize += 1;
				List<Object> rowSqlParams = Arrays.asList(projectName, modulePath,
						dataModuleInfoBean.getChiFieldName() == null ? "":dataModuleInfoBean.getChiFieldName(), dataModuleInfoBean.getEngFieldName(), dataModuleInfoBean.getFieldDataType(),
						dataModuleInfoBean.getResourceTableName()== null || Objects.equals(dataModuleInfoBean.getResourceTableName(), "") ? "":"root."+dataModuleInfoBean.getResourceTableName(),
						dataModuleInfoBean.getChiResourceFieldName()== null || Objects.equals(dataModuleInfoBean.getChiResourceFieldName(), "") ? "":dataModuleInfoBean.getChiResourceFieldName(),
						dataModuleInfoBean.getEngResourceFieldName()== null|| Objects.equals(dataModuleInfoBean.getEngResourceFieldName(), "") ? "":dataModuleInfoBean.getEngResourceFieldName(),
						rowSize);
				paramsList.add(rowSqlParams);
			}
		}
		SJMXMapper.updateDataModuleInfo(paramsList, rowSize);

		//设置当前模型的保存状态
		SJMXManager.getInstance().setSavedStatus();

		SJMXDetailController.replaceMainModule(event, createDataModuleWindow(projectName, modulePath));
	}

	@FXML
	void ddlGenerateButtonClick(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("sjmx/DDLGenerateTemplate.fxml"));
		VBox vBox = fxmlLoader.load();

		//设置模型名
		Label dataModuleNameLabel = (Label) vBox.lookup("#DataModuleNameLabel");
		dataModuleNameLabel.setText(modulePath.split("\\.")[modulePath.split("\\.").length-1]);

		//设置编辑器
		WebView codeEditor = (WebView) vBox.lookup("#codeEditor");
		WebEngine webEngine = codeEditor.getEngine();
		webEngine.load(getClass().getResource("/ace/editor_sql.html").toExternalForm());
		webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == Worker.State.SUCCEEDED) {
				//封装hive表对象
				HiveTableInfo hiveTableInfo = new HiveTableInfo();
				hiveTableInfo.setTableName(dataModuleNameLabel.getText());
				List<HiveFieldInfo> hiveFieldInfos = new ArrayList<>();
				TableView<DataModuleInfoBean> moduleInfoTableView = (TableView<DataModuleInfoBean>) dataModuleInfoTable.getChildren().get(0);
				ObservableList<DataModuleInfoBean> moduleInfoBeans = moduleInfoTableView.getItems();
				for (DataModuleInfoBean dataModuleInfoBean : moduleInfoBeans) {
					//封装字段
					HiveFieldInfo hiveFieldInfo = new HiveFieldInfo();
					hiveFieldInfo.setFieldName(dataModuleInfoBean.getEngFieldName());
					hiveFieldInfo.setDataType(dataModuleInfoBean.getFieldDataType());
					hiveFieldInfo.setComment(dataModuleInfoBean.getChiFieldName());
					hiveFieldInfos.add(hiveFieldInfo);
				}
				hiveTableInfo.setHiveFieldInfos(hiveFieldInfos);

				// 在 WebView 引擎加载完成后设置内容
				String content = generateCreateTableHql(hiveTableInfo);
				webEngine.executeScript("editor.setValue(\"" + content + "\");");
			}
		});

		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("ddl生成");
		stage.setScene(new Scene(vBox));
		stage.show();
	}

	@FXML
	void addRowButtonClick(ActionEvent event) throws SQLException, IOException {
		//设置保存状态
		SJMXManager.getInstance().setUnSavedStatus();
		TableView<DataModuleInfoBean> moduleInfoTableView = (TableView<DataModuleInfoBean>) dataModuleInfoTable.getChildren().get(0);
		DataModuleInfoBean dataModuleInfoBean = getRowInstance(moduleInfoTableView, null);

		moduleInfoTableView.getItems().add(dataModuleInfoBean);
	}

	@FXML
	void reduceRowButtonClick(ActionEvent event) {
		//设置保存状态
		SJMXManager.getInstance().setUnSavedStatus();
		TableView<DataModuleInfoBean> moduleInfoTableView = (TableView<DataModuleInfoBean>) dataModuleInfoTable.getChildren().get(0);
		ObservableList<DataModuleInfoBean> selectedItems = moduleInfoTableView.getSelectionModel().getSelectedItems();
		moduleInfoTableView.getItems().removeAll(selectedItems);
	}

	//打开数据模型查看编辑界面
	public VBox createDataModuleWindow(String projectName, String modulePath) throws IOException, SQLException {
		//保存之前的数据模型的相关信息
		if (SJMXManager.getInstance().getStatus() == 1){
			SJMXManager.getInstance().dataModuleSave();
		}

		DataModuleEditController.modulePath = modulePath;
		DataModuleEditController.projectName = projectName;

		//获取数据模型界面窗口
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("sjmx/DataModuleEdit.fxml"));
		VBox emptyVBox = fxmlLoader.load();

		//加载数据模型标题
		Label  dataModuleNameLabel = (Label) emptyVBox.lookup("#DataModuleNameLabel");
		dataModuleNameLabel.setText(modulePath.split("\\.")[modulePath.split("\\.").length-1]);

		//加载数据模型信息
		VBox dataModuleInfoVBox = loadDataModuleInfo(emptyVBox);

		//加载血缘信息
		VBox allInfoVBox = loadBloodInfo(dataModuleInfoVBox);
		Label bloodLabel = (Label) allInfoVBox.lookup("#BloodLabel");
		bloodLabel.setText(modulePath.split("\\.")[modulePath.split("\\.").length-1]);

		//设置当前数据模型的相关信息
		VBox dataModuleInfoTable = (VBox) allInfoVBox.lookup("#dataModuleInfoTable");
		SJMXManager.getInstance().setModulePath(modulePath);
		SJMXManager.getInstance().setProjectName(projectName);
		SJMXManager.getInstance().setDataModuleInfoTable(dataModuleInfoTable);

		return allInfoVBox;
	}

	private VBox loadBloodInfo(VBox dataModuleInfoVBox) throws SQLException, IOException {
		//得到血缘信息vbox
		ScrollPane parentBloodScroll = (ScrollPane) dataModuleInfoVBox.lookup("#ParentBloodScroll");
		ScrollPane childBloodScroll = (ScrollPane) dataModuleInfoVBox.lookup("#ChildBloodScroll");

		VBox parentBloodList = (VBox) parentBloodScroll.getContent();
		VBox childBloodList = (VBox) childBloodScroll.getContent();

		//加载上游列表
		List<Object> params = Arrays.asList(projectName, modulePath);
		List<List<Object>> paramsList = Arrays.asList(null, params);
		List<LinkedHashMap<Object, Object>> queryResult = SJMXMapper.queryParentBlood(paramsList);

		ArrayList<String> tableNames = new ArrayList<>();
		for (LinkedHashMap<Object, Object> row : queryResult) {
			String tableName = (String) row.get(SystemLibConstant.DATAMODULEDETAIL_RESOURCETABLENAME);
			if (!tableNames.contains(tableName) && !CommonUtil.isNullOrEmpty(tableName)){
				tableNames.add(tableName);
			}
		}
		for (String tableName : tableNames) {
			Button button = new Button(tableName);
			button.setId("BloodNodeButton");
			button.setOnAction(event -> {
				try {
					SJMXDetailController.replaceMainModule(event, createDataModuleWindow(projectName, tableName));
				} catch (IOException | SQLException e) {
					throw new RuntimeException(e);
				}
			});
			parentBloodList.getChildren().add(button);
		}

		//加载下游列表
		List<Object> childParams = Arrays.asList(projectName, modulePath);
		List<List<Object>> childParamsList = Arrays.asList(null, childParams);
		List<LinkedHashMap<Object, Object>> queryChildResult = SJMXMapper.queryChildBlood(childParamsList);

		ArrayList<String> childTableNames = new ArrayList<>();
		for (LinkedHashMap<Object, Object> row : queryChildResult) {
			String tableName = (String) row.get(SystemLibConstant.DATAMODULEDETAIL_PATH);
			if (!childTableNames.contains(tableName)){
				childTableNames.add(tableName);
			}
		}
		for (String tableName : childTableNames) {
			Button button = new Button(tableName);
			button.setId("BloodNodeButton");
			button.setOnAction(event -> {
				try {
					SJMXDetailController.replaceMainModule(event, createDataModuleWindow(projectName, tableName));
				} catch (IOException | SQLException e) {
					throw new RuntimeException(e);
				}
			});
			childBloodList.getChildren().add(button);
		}

		return dataModuleInfoVBox;
	}

	private VBox loadDataModuleInfo(VBox emptyVBox) throws SQLException, IOException {
		//得到模型信息vbox
		VBox dataModuleInfoTable = (VBox) emptyVBox.lookup("#dataModuleInfoTable");

		//查询信息并封装
		List<Object> params = Arrays.asList(projectName, modulePath);
		List<List<Object>> paramsList = Arrays.asList(null, params);
		List<LinkedHashMap<Object, Object>> queryResult = SJMXMapper.queryDataModuleByName(paramsList);

		TableView<DataModuleInfoBean> dataModuleInfoTableView = new TableView<>();
		dataModuleInfoTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		//列
		TableColumn<DataModuleInfoBean, String> engFieldNameCol = new TableColumn<>("字段英文名");
		engFieldNameCol.setMinWidth(100);
		TableColumn<DataModuleInfoBean, String> chiFieldNameCol = new TableColumn<>("字段中文名");
		chiFieldNameCol.setMinWidth(100);
		TableColumn<DataModuleInfoBean, String> fieldDataTypeCol = new TableColumn<>("数据类型");
		fieldDataTypeCol.setMinWidth(180);
		TableColumn<DataModuleInfoBean, String> resourceTableNameCol = new TableColumn<>("来源表名");
		resourceTableNameCol.setMinWidth(220);
		TableColumn<DataModuleInfoBean, String> engResourceFieldNameCol = new TableColumn<>("来源字段");
		engResourceFieldNameCol.setMinWidth(180);
		TableColumn<DataModuleInfoBean, String> chiResourceFieldNameCol = new TableColumn<>("来源字段注释");
		chiResourceFieldNameCol.setMinWidth(150);
		dataModuleInfoTableView.getColumns().addAll(engFieldNameCol, chiFieldNameCol, fieldDataTypeCol, resourceTableNameCol, engResourceFieldNameCol, chiResourceFieldNameCol);

		ObservableList<DataModuleInfoBean> dataModuleInfoData = FXCollections.observableArrayList();

		for (LinkedHashMap<Object, Object> row : queryResult) {
			DataModuleInfoBean dataModuleInfoBean = getRowInstance(dataModuleInfoTableView, row);

			dataModuleInfoData.add(dataModuleInfoBean);
		}
		dataModuleInfoTableView.setItems(dataModuleInfoData);

		//字段英文列
		engFieldNameCol.setCellValueFactory(new PropertyValueFactory<>("engFieldName"));
		engFieldNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		engFieldNameCol.setOnEditCommit(event -> {
			//设置保存状态
			SJMXManager.getInstance().setUnSavedStatus();
			DataModuleInfoBean rowValue = event.getRowValue();
			rowValue.setEngFieldName(event.getNewValue());
		});

		//字段中文列
		chiFieldNameCol.setCellValueFactory(new PropertyValueFactory<>("chiFieldName"));
		chiFieldNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		chiFieldNameCol.setOnEditCommit(event -> {
			//设置保存状态
			SJMXManager.getInstance().setUnSavedStatus();
			DataModuleInfoBean rowValue = event.getRowValue();
			rowValue.setChiFieldName(event.getNewValue());
		});

		//设置数据类型单元格
		fieldDataTypeCol.setCellValueFactory(new PropertyValueFactory<>("fieldDataType"));
		String[] dataTypeList = {"INT", "STRING", "BIGINT", "FLOAT", "DOUBLE", "DATE", "BOOLEAN"};
		fieldDataTypeCol.setCellFactory(ComboBoxTableCell.forTableColumn(dataTypeList));
		fieldDataTypeCol.setOnEditCommit(event -> {
			//设置保存状态
			SJMXManager.getInstance().setUnSavedStatus();
			DataModuleInfoBean rowValue = event.getRowValue();
			rowValue.setFieldDataType(event.getNewValue());
		});

		//设置来源表名单元格
		resourceTableNameCol.setCellValueFactory(new PropertyValueFactory<>("resourceTableName"));
		Set<String> resourceTableNameSet = new HashSet<>();
		resourceTableNameSet.add("");
		List<LinkedHashMap<Object, Object>> allDataModule = getDataModuleInfoAll();
		for (LinkedHashMap<Object, Object> row : allDataModule) {
			String originPath = (String) row.get(SystemLibConstant.DATAMODULEDETAIL_PATH);
			resourceTableNameSet.add(originPath.substring(5));
		}
		resourceTableNameCol.setCellFactory(ComboBoxTableCell.forTableColumn(resourceTableNameSet.toArray(new String[0])));
		resourceTableNameCol.setOnEditCommit(tableNameChangedEvent -> {
			//设置保存状态
			SJMXManager.getInstance().setUnSavedStatus();
			DataModuleInfoBean rowValue = tableNameChangedEvent.getRowValue();
			rowValue.setResourceTableName(tableNameChangedEvent.getNewValue());

			rowValue.setEngResourceFieldName("");
			rowValue.setChiResourceFieldName("");
			dataModuleInfoTableView.refresh();
		});

		//设置来源字段单元格
		engResourceFieldNameCol.setCellValueFactory(new PropertyValueFactory<>("engResourceFieldName"));
		engResourceFieldNameCol.setCellFactory(ComboBoxTableCell.forTableColumn());
		engResourceFieldNameCol.setOnEditStart(event ->{
			DataModuleInfoBean rowValue = event.getRowValue();
			Set<String> resourceFieldComboBoxSet = null;
			try {
				resourceFieldComboBoxSet = getResourceFieldComboBoxSet(rowValue.getResourceTableName());
			} catch (SQLException | IOException e) {
				throw new RuntimeException(e);
			}
			ComboBoxTableCell<DataModuleInfoBean, String> tableCell = (ComboBoxTableCell<DataModuleInfoBean, String>) event.getTableColumn().getCellFactory().call(event.getTableColumn());
			tableCell.getItems().setAll(resourceFieldComboBoxSet);
		});
		engResourceFieldNameCol.setOnEditCommit(engResourceFieldNameChangedEvent ->  {
			//设置保存状态
			SJMXManager.getInstance().setUnSavedStatus();
			DataModuleInfoBean rowValue = engResourceFieldNameChangedEvent.getRowValue();
			rowValue.setEngResourceFieldName(engResourceFieldNameChangedEvent.getNewValue());

			String resourceFieldCommentLabel = "";
			try {
				resourceFieldCommentLabel = getResourceFieldCommentLabel(rowValue.getResourceTableName(), engResourceFieldNameChangedEvent.getNewValue());
				rowValue.setChiResourceFieldName(resourceFieldCommentLabel);
			} catch (SQLException | IOException e) {
				throw new RuntimeException(e);
			}
			dataModuleInfoTableView.refresh();
		});

		//设置来演字段注释单元格
		chiResourceFieldNameCol.setCellValueFactory(new PropertyValueFactory<>("chiResourceFieldName"));

		dataModuleInfoTableView.setEditable(true);
		dataModuleInfoTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		dataModuleInfoTable.getChildren().clear();
		dataModuleInfoTable.getChildren().add(dataModuleInfoTableView);

		return emptyVBox;
	}


	private static String generateCreateTableHql(HiveTableInfo hiveTableInfo) {
		// 创建一个StringBuilder对象，用来拼接sql字符串
		StringBuilder sb = new StringBuilder();
		// 拼接create table语句
		sb.append("create table if not exists ");
		// 加上表名和左括号
		sb.append(hiveTableInfo.getTableName()).append(" (\\n");

		// 获取字段列表
		List<HiveFieldInfo> hiveFieldInfos = hiveTableInfo.getHiveFieldInfos();
		// 遍历字段列表
		for (int i = 0; i < hiveFieldInfos.size(); i++) {
			// 获取当前字段对象
			HiveFieldInfo hiveFieldInfo = hiveFieldInfos.get(i);
			// 拼接字段名和数据类型
			sb.append("  ").append(hiveFieldInfo.getFieldName());
			sb.append(" ").append(hiveFieldInfo.getDataType());
			// 如果有注释，就加上注释
			if (hiveFieldInfo.getComment() != null && !hiveFieldInfo.getComment().isEmpty()) {
				sb.append(" comment '").append(hiveFieldInfo.getComment()).append("'");
			}
			// 如果不是最后一个字段，就加上逗号和换行符
			if (i < hiveFieldInfos.size() - 1) {
				sb.append(",\\n");
			}
		}
		// 拼接右括号
		sb.append("\\n);");
		return sb.toString();
	}

	private List<LinkedHashMap<Object, Object>> getDataModuleInfoAll() throws SQLException, IOException {
		//查询该项目下的所有数据模型
		List<Object> params = Arrays.asList(projectName, SomeConstant.MODULE);
		List<List<Object>> paramsList = Arrays.asList(null, params);

		return SJMXMapper.queryDataModuleAll(paramsList);
	}

	/**
	 * 查询某字段的注释，并返回
	 * @param resourceTableName 来源表
	 * @param resourceField 来源字段
	 * @return
	 */
	private String getResourceFieldCommentLabel(String resourceTableName, String resourceField) throws SQLException, IOException {
		String resourceFieldComment = "";

		if (!CommonUtil.isNullOrEmpty(resourceTableName) && !CommonUtil.isNullOrEmpty(resourceField)){
			List<Object> params = Arrays.asList(projectName, "root." + resourceTableName, resourceField);
			List<List<Object>> paramsList = Arrays.asList(null, params);
			List<LinkedHashMap<Object, Object>> queryResult = SJMXMapper.queryFieldComment(paramsList);

			if (!CommonUtil.isNullOrEmpty((String) queryResult.get(0).get(SystemLibConstant.DATAMODULEDETAIL_CHIFIELDNAME))){
				resourceFieldComment = (String) queryResult.get(0).get(SystemLibConstant.DATAMODULEDETAIL_CHIFIELDNAME);
			}
		}

		return resourceFieldComment;
	}

	/**
	 * 查询该来源表下的所有字段，并返回
	 * @param resourceTableName 来源表
	 * @return
	 * @throws SQLException
	 */
	private Set<String> getResourceFieldComboBoxSet(String resourceTableName) throws SQLException, IOException {
		HashSet<String> resourceFieldComboBoxSet = new HashSet<>();

		if (!CommonUtil.isNullOrEmpty(resourceTableName)){
			List<Object> params = Arrays.asList(projectName, "root." + resourceTableName);
			List<List<Object>> paramsList = Arrays.asList(null, params);
			List<LinkedHashMap<Object, Object>> queryResult = SJMXMapper.queryFieldAll(paramsList);

			resourceFieldComboBoxSet.add("");
			for (LinkedHashMap<Object, Object> row : queryResult) {
				resourceFieldComboBoxSet.add((String) row.get(SystemLibConstant.DATAMODULEDETAIL_ENGFIELDNAME));
			}
		}

		return resourceFieldComboBoxSet;
	}

	private DataModuleInfoBean getRowInstance(TableView<DataModuleInfoBean> dataModuleInfoTableView, LinkedHashMap<Object, Object> row) throws SQLException, IOException {
		//封装实例数据
		DataModuleInfoBean dataModuleInfoBean = new DataModuleInfoBean(projectName);

		if (row != null){
			dataModuleInfoBean.setEngFieldName((String) row.get(SystemLibConstant.DATAMODULEDETAIL_ENGFIELDNAME));
			dataModuleInfoBean.setChiFieldName((String) row.get(SystemLibConstant.DATAMODULEDETAIL_CHIFIELDNAME));
			dataModuleInfoBean.setFieldDataType((String) row.get(SystemLibConstant.DATAMODULEDETAIL_FIELDDATATYPE));
			String resTableName = (String) row.get(SystemLibConstant.DATAMODULEDETAIL_RESOURCETABLENAME);
			if (resTableName.length()>5){
				dataModuleInfoBean.setResourceTableName(((String) row.get(SystemLibConstant.DATAMODULEDETAIL_RESOURCETABLENAME)).substring(5));
			}else {
				dataModuleInfoBean.setResourceTableName(((String) row.get(SystemLibConstant.DATAMODULEDETAIL_RESOURCETABLENAME)));
			}
			dataModuleInfoBean.setEngResourceFieldName((String) row.get(SystemLibConstant.DATAMODULEDETAIL_ENGRESOURCEFIELDNAME));
			dataModuleInfoBean.setChiResourceFieldName((String) row.get(SystemLibConstant.DATAMODULEDETAIL_CHIRESOURCEFIELDNAME));
		}

		return dataModuleInfoBean;
	}
}