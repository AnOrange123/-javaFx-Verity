package service.sjtb;

import beans.ProjectVariableManager;
import beans.sjtb.JobFieldMapperBean;
import beans.sjtb.dataxJsonBeans.*;
import beans.sjzy.HiveFieldInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import constants.SomeConstant;
import constants.SystemLibConstant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import mapper.SJTBMapper;
import mapper.SJZYMapper;
import service.sjkf.SJKFController;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import utils.CommonUtil;
import utils.PropertiesUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

/**
 * ClassName:JobEditController
 * Package:service.sjtb
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/24 - 20:24
 */
public class JobEditController {
	private static String pathContainsRoot;

	public VBox createJobEditWindow(String pathContainsRoot) throws IOException, SQLException {
		this.pathContainsRoot = pathContainsRoot;

		//获取数据同步界面窗口
		FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("sjtb/JobEdit.fxml"));
		VBox jobEditMainVBox = fxmlLoader.load();

		//获取控件
		Label jobNameLabel = (Label) jobEditMainVBox.lookup("#jobNameLabel");
		TextField sourceDataTypeTextField = (TextField) jobEditMainVBox.lookup("#sourceDataTypeTextField");
		TextField targetDataTypeTextField = (TextField) jobEditMainVBox.lookup("#targetDataTypeTextField");
		TextField sourceDatabaseTextField = (TextField) jobEditMainVBox.lookup("#sourceDatabaseTextField");
		TextField targetDatabaseTextField = (TextField) jobEditMainVBox.lookup("#targetDatabaseTextField");
		TextField sourceDatatableTextField = (TextField) jobEditMainVBox.lookup("#sourceDatatableTextField");
		TextField targetDatatableTextField = (TextField) jobEditMainVBox.lookup("#targetDatatableTextField");
		TableView<JobFieldMapperBean> fieldMapperTableView = (TableView<JobFieldMapperBean>) jobEditMainVBox.lookup("#fieldMapperTableView");

		//设置任务基本信息
		List<Object> params = Arrays.asList(ProjectVariableManager.getInstance().getProjectName(), pathContainsRoot, SomeConstant.JOB);
		List<List<Object>> paramsList = Arrays.asList(null, params);
		List<LinkedHashMap<Object, Object>> baseInfoQueryResult = SJTBMapper.queryJobBaseInfo(paramsList);

		jobNameLabel.setText(pathContainsRoot.substring(pathContainsRoot.lastIndexOf(".")+1));
		sourceDataTypeTextField.setText((String) baseInfoQueryResult.get(0).get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_SOURCEDATATYPE));
		targetDataTypeTextField.setText((String) baseInfoQueryResult.get(0).get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_TARGETDATATYPE));
		sourceDatabaseTextField.setText((String) baseInfoQueryResult.get(0).get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_SOURCEDATABASE));
		targetDatabaseTextField.setText((String) baseInfoQueryResult.get(0).get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_TARGETDATABASE));
		sourceDatatableTextField.setText((String) baseInfoQueryResult.get(0).get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_SOURCEDATATABLE));
		targetDatatableTextField.setText((String) baseInfoQueryResult.get(0).get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_TARGETDATATABLE));

		//设置任务字段映射信息
		List<Object> params2 = Arrays.asList(ProjectVariableManager.getInstance().getProjectName(), pathContainsRoot);
		List<List<Object>> paramsList2 = Arrays.asList(null, params2);
		List<LinkedHashMap<Object, Object>> detailInfoQueryResult = SJTBMapper.queryJobDetailInfo(paramsList2);

		ObservableList<JobFieldMapperBean> dataList = FXCollections.observableArrayList();
		for (LinkedHashMap<Object, Object> row : detailInfoQueryResult) {
			JobFieldMapperBean jobFieldMapperBean = new JobFieldMapperBean();
			jobFieldMapperBean.setSourceField((String) row.get(SystemLibConstant.SYNCHRONIZEJOBDETAILTABLE_SOURCEFIELD));
			jobFieldMapperBean.setTargetFiled((String) row.get(SystemLibConstant.SYNCHRONIZEJOBDETAILTABLE_TARGETFIELD));
			dataList.add(jobFieldMapperBean);
		}
		fieldMapperTableView.setItems(dataList);
		fieldMapperTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		//设置目标字段列下拉列表
		TableColumn<JobFieldMapperBean, String> sourceFieldCol = (TableColumn<JobFieldMapperBean, String>) fieldMapperTableView.getColumns().get(0);
		TableColumn<JobFieldMapperBean, String> targetFieldCol = (TableColumn<JobFieldMapperBean, String>) fieldMapperTableView.getColumns().get(1);

		sourceFieldCol.setCellValueFactory(new PropertyValueFactory<>("sourceField"));
		targetFieldCol.setCellValueFactory(new PropertyValueFactory<>("targetFiled"));

		targetFieldCol.setCellFactory(ComboBoxTableCell.forTableColumn());
		targetFieldCol.setOnEditStart(event ->{
			JobFieldMapperBean rowValue = event.getRowValue();
			Set<String> fieldSet = null;
			try {
				fieldSet = getFieldSet(targetDataTypeTextField.getText(), targetDatabaseTextField.getText(), targetDatatableTextField.getText());
			} catch (SQLException | IOException e) {
				throw new RuntimeException(e);
			}
			//去除已有的字段
			for (JobFieldMapperBean tableViewItem : fieldMapperTableView.getItems()) {
				fieldSet.remove(tableViewItem.getTargetFiled());
			}
			ComboBoxTableCell<JobFieldMapperBean, String> tableCell = (ComboBoxTableCell<JobFieldMapperBean, String>) event.getTableColumn().getCellFactory().call(event.getTableColumn());
			tableCell.getItems().setAll(fieldSet);
		});
		targetFieldCol.setOnEditCommit(event ->{
			JobFieldMapperBean rowValue = event.getRowValue();
			rowValue.setTargetFiled(event.getNewValue());

			fieldMapperTableView.refresh();
		});

		return jobEditMainVBox;
	}

	public static Set<String> getFieldSet(String sourceDataType, String sourceDatabase, String sourceDatatable) throws SQLException, IOException {
		HashSet<String> fieldSet = new HashSet<>();

		List<Object> params = Arrays.asList(sourceDatabase, sourceDatatable);
		List<List<Object>> paramsList = Arrays.asList(null, params);
		List<LinkedHashMap<Object, Object>> queryResult = SJZYMapper.queryFieldsAll(sourceDataType, paramsList);

		if ("mysql".equals(sourceDataType)){
			for (LinkedHashMap<Object, Object> row : queryResult) {
				fieldSet.add((String) row.get(SomeConstant.INFORMATION_SCHEMA_COLUMNS_COL));
			}
		}else{
			for (LinkedHashMap<Object, Object> row : queryResult) {
				fieldSet.add((String) row.get(SomeConstant.METASTORE_COL_COLNAME));
			}
		}

		return fieldSet;
	}

	@FXML
	private TableView<JobFieldMapperBean> fieldMapperTableView;

	@FXML
	private Label jobNameLabel;
	@FXML
	private TextField sourceDataTypeTextField;

	@FXML
	private TextField sourceDatabaseTextField;

	@FXML
	private TextField sourceDatatableTextField;

	@FXML
	private TextField targetDataTypeTextField;

	@FXML
	private TextField targetDatabaseTextField;

	@FXML
	private TextField targetDatatableTextField;

	@FXML
	void addMapperButtonClick(ActionEvent event) throws SQLException, IOException {
		new MapperAddController().createMapperAddWindow(sourceDataTypeTextField.getText(), sourceDatabaseTextField.getText(), sourceDatatableTextField.getText(), fieldMapperTableView);
	}

	@FXML
	void automatchButtonClick(ActionEvent event) throws SQLException, IOException {
		Set<String> fieldSet = getFieldSet(targetDataTypeTextField.getText(), targetDatabaseTextField.getText(), targetDatatableTextField.getText());
		ObservableList<JobFieldMapperBean> fieldMapperBeans = fieldMapperTableView.getItems();
		for (JobFieldMapperBean fieldMapperBean : fieldMapperBeans) {
			fieldMapperBean.setTargetFiled(null);
			if (fieldSet.contains(fieldMapperBean.getSourceField())){
				fieldMapperBean.setTargetFiled(fieldMapperBean.getSourceField());
			}
		}

		fieldMapperTableView.refresh();
	}

	@FXML
	void deleteMapperButtonClick(ActionEvent event) {
		ObservableList<JobFieldMapperBean> selectedItems = fieldMapperTableView.getSelectionModel().getSelectedItems();
		fieldMapperTableView.getItems().removeAll(selectedItems);
	}

	@FXML
	void executeButtonClick(ActionEvent event) throws SQLException, IOException, URISyntaxException {
		scriptPreviewButtonClick(event);
	}

	@FXML
	void saveButtonClick(ActionEvent event) throws SQLException, IOException {
		int rowsize = 0;
		List<List<Object>> paramsList = new ArrayList<>();
		paramsList.add(null);
		paramsList.add(Arrays.asList(ProjectVariableManager.getInstance().getProjectName(), pathContainsRoot));

		ObservableList<JobFieldMapperBean> fieldMapperBeans = fieldMapperTableView.getItems();
		for (JobFieldMapperBean fieldMapperBean : fieldMapperBeans) {
			if (!CommonUtil.isNullOrEmpty(fieldMapperBean.getSourceField()) && !CommonUtil.isNullOrEmpty(fieldMapperBean.getTargetFiled())){
				rowsize++;
				paramsList.add(Arrays.asList(ProjectVariableManager.getInstance().getProjectName(), pathContainsRoot, fieldMapperBean.getSourceField(), fieldMapperBean.getTargetFiled()));
			}
		}

		SJTBMapper.updateJobDetailInfo(paramsList, rowsize);
	}

	@FXML
	void scriptPreviewButtonClick(ActionEvent event) throws SQLException, IOException, URISyntaxException {
		List<Object> params = Arrays.asList(ProjectVariableManager.getInstance().getProjectName(), pathContainsRoot, SomeConstant.JOB);
		List<LinkedHashMap<Object, Object>> queryResult = SJTBMapper.queryJobBaseInfo(Arrays.asList(null, params));
		String dataxScriptJson = "";
		ObservableList<JobFieldMapperBean> fieldMapperBeans = fieldMapperTableView.getItems();
		try {
		if (!CommonUtil.isNullOrEmpty((String) queryResult.get(0).get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_CONTENT))){
			//转成json对象，修改reader和writer的列内容
			dataxScriptJson = setDataxScriptJson(fieldMapperBeans, (String) queryResult.get(0).get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_CONTENT));
		}else{
			dataxScriptJson = getDataxScriptJson(fieldMapperBeans);
		}
		new DataxScriptPreviewController().createDataxScriptPreviewWindow(event, dataxScriptJson, pathContainsRoot);
		}catch (Exception e){
			dataxScriptJson = getDataxScriptJson(fieldMapperBeans);
			new DataxScriptPreviewController().createDataxScriptPreviewWindow(event, dataxScriptJson, pathContainsRoot);
		}
	}

	private String setDataxScriptJson(ObservableList<JobFieldMapperBean> fieldMapperBeans, String jsonText) throws SQLException, IOException, URISyntaxException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		JsonRootBean jsonRootBean = gson.fromJson(jsonText, JsonRootBean.class);

		//重新获取columns
		//获取来源字段List和目标字段List
		List<String> sourceFieldList = new ArrayList<>();
		List<String> targetFieldList = new ArrayList<>();
		for (JobFieldMapperBean fieldMapperBean : fieldMapperBeans) {
			if (!CommonUtil.isNullOrEmpty(fieldMapperBean.getSourceField()) && !CommonUtil.isNullOrEmpty(fieldMapperBean.getTargetFiled())){
				sourceFieldList.add(fieldMapperBean.getSourceField());
				targetFieldList.add(fieldMapperBean.getTargetFiled());
			}
		}

		//设置reader和writer的列
		//设置reader
		Reader reader = jsonRootBean.getJob().getContent().get(0).getReader();
		LinkedTreeMap<Object, Object> readerParameter = (LinkedTreeMap<Object, Object>) reader.getParameter();
		if ("mysql".equals(sourceDataTypeTextField.getText())){
			readerParameter.put("column", sourceFieldList);
		}else{
			List<Object> params = Arrays.asList(sourceDatabaseTextField.getText(), sourceDatatableTextField.getText());
			//获取列
			List<List<Object>> paramsList = Arrays.asList(null, params);
			List<LinkedHashMap<Object, Object>> colQueryResult = SJZYMapper.queryFieldsAll(sourceDataTypeTextField.getText(), paramsList);
			List<Column> colInfos = new ArrayList<>();
			for (String sourceField : sourceFieldList) {
				for (int i = 0; i < colQueryResult.size(); i++) {
					String fieldName = (String) colQueryResult.get(i).get(SomeConstant.METASTORE_COL_COLNAME);
					if (sourceField.equals(fieldName)){
						Column column = new Column();
						column.setIndex((Integer) colQueryResult.get(i).get(SomeConstant.METASTORE_COL_INDEX));
						column.setType((String) colQueryResult.get(i).get(SomeConstant.METASTORE_COL_TYPE));
						colInfos.add(column);
						break;
					}
				}
			}

			readerParameter.put("column", colInfos);
		}

		//设置writer
		Writer writer = jsonRootBean.getJob().getContent().get(0).getWriter();
		LinkedTreeMap<Object, Object> writerParameter = (LinkedTreeMap<Object, Object>) writer.getParameter();
		if ("mysql".equals(targetDataTypeTextField.getText())){
			writerParameter.put("column", targetFieldList);
		}else{
			List<Object> params = Arrays.asList(targetDatabaseTextField.getText(), targetDatatableTextField.getText());
			//获取列
			List<List<Object>> paramsList = Arrays.asList(null, params);
			List<LinkedHashMap<Object, Object>> colQueryResult = SJZYMapper.queryFieldsAll(targetDataTypeTextField.getText(), paramsList);
			List<Column> colInfos = new ArrayList<>();
			for (String targetField : targetFieldList) {
				for (int i = 0; i < colQueryResult.size(); i++) {
					String fieldName = (String) colQueryResult.get(i).get(SomeConstant.METASTORE_COL_COLNAME);
					if (targetField.equals(fieldName)){
						Column column = new Column();
						column.setName(targetField);
						column.setType((String) colQueryResult.get(i).get(SomeConstant.METASTORE_COL_TYPE));
						colInfos.add(column);
						break;
					}
				}
			}

			writerParameter.put("column", colInfos);
		}

		return gson.toJson(jsonRootBean);
	}

	private String getDataxScriptJson(ObservableList<JobFieldMapperBean> fieldMapperBeans) throws SQLException, IOException, URISyntaxException {
		//获取来源字段List和目标字段List
		List<String> sourceFieldList = new ArrayList<>();
		List<String> targetFieldList = new ArrayList<>();
		for (JobFieldMapperBean fieldMapperBean : fieldMapperBeans) {
			if (!CommonUtil.isNullOrEmpty(fieldMapperBean.getSourceField()) && !CommonUtil.isNullOrEmpty(fieldMapperBean.getTargetFiled())){
				sourceFieldList.add(fieldMapperBean.getSourceField());
				targetFieldList.add(fieldMapperBean.getTargetFiled());
			}
		}

		Reader reader;
		//设置reader
		if ("mysql".equals(sourceDataTypeTextField.getText())){
			reader = new Reader<MysqlReaderParameter>();
			Connection connection = new Connection();
			connection.setJdbcUrl(PropertiesUtil.getProperty("mysql.jdbc.url").split("\\?")[0] + "/" + sourceDatabaseTextField.getText() +"?useUnicode=true&characterEncoding=utf-8");
			connection.setTable(List.of(sourceDatatableTextField.getText()));

			MysqlReaderParameter mysqlReaderParameter = new MysqlReaderParameter();
			mysqlReaderParameter.setColumn(sourceFieldList);
			mysqlReaderParameter.setConnection(List.of(connection));
			mysqlReaderParameter.setPassword(PropertiesUtil.getProperty("mysql.jdbc.password"));
			mysqlReaderParameter.setUsername(PropertiesUtil.getProperty("mysql.jdbc.user"));
			mysqlReaderParameter.setSplitPk("");

			reader.setParameter(mysqlReaderParameter);
			reader.setName("mysqlreader");
		}else{
			reader = new Reader<HdfsReaderParameter>();
			List<Object> params = Arrays.asList(sourceDatabaseTextField.getText(), sourceDatatableTextField.getText());
			//获取路径
			List<LinkedHashMap<Object, Object>> pathQueryResult = SJZYMapper.queryHdfsFilePath(Arrays.asList(null, params));
			String completePath = (String) pathQueryResult.get(0).get(SomeConstant.METASTORE_SDS_LOC);
			URI uri = new URI(completePath);
			String path = uri.getPath();
			//获取列
			List<List<Object>> paramsList = Arrays.asList(null, params);
			List<LinkedHashMap<Object, Object>> colQueryResult = SJZYMapper.queryFieldsAll(sourceDataTypeTextField.getText(), paramsList);
			List<Column> colInfos = new ArrayList<>();
			for (String sourceField : sourceFieldList) {
				for (int i = 0; i < colQueryResult.size(); i++) {
					String fieldName = (String) colQueryResult.get(i).get(SomeConstant.METASTORE_COL_COLNAME);
					if (sourceField.equals(fieldName)){
						Column column = new Column();
						column.setIndex((Integer) colQueryResult.get(i).get(SomeConstant.METASTORE_COL_INDEX));
						column.setType((String) colQueryResult.get(i).get(SomeConstant.METASTORE_COL_TYPE));
						colInfos.add(column);
						break;
					}
				}
			}

			HdfsReaderParameter hdfsReaderParameter = new HdfsReaderParameter();
			hdfsReaderParameter.setColumn(colInfos);
			hdfsReaderParameter.setEncoding("UTF-8");
			hdfsReaderParameter.setDefaultFS(PropertiesUtil.getProperty("hdfs.url"));
			hdfsReaderParameter.setFieldDelimiter(",");
			hdfsReaderParameter.setPath(path);
			hdfsReaderParameter.setFileType("text");

			reader.setParameter(hdfsReaderParameter);
			reader.setName("hdfsreader");
		}

		Writer writer;
		//设置writer
		if ("mysql".equals(targetDataTypeTextField.getText())){
			writer = new Writer<MysqlWriterParameter>();
			Connection connection = new Connection();
			connection.setJdbcUrl(PropertiesUtil.getProperty("mysql.jdbc.url").split("\\?")[0] + "/" + targetDatabaseTextField.getText() +"?useUnicode=true&characterEncoding=utf-8");
			connection.setTable(List.of(targetDatatableTextField.getText()));

			MysqlWriterParameter mysqlWriterParameter = new MysqlWriterParameter();
			mysqlWriterParameter.setColumn(targetFieldList);
			mysqlWriterParameter.setConnection(List.of(connection));
			mysqlWriterParameter.setPassword(PropertiesUtil.getProperty("mysql.jdbc.password"));
			mysqlWriterParameter.setUsername(PropertiesUtil.getProperty("mysql.jdbc.user"));
			mysqlWriterParameter.setWriteMode("replace");

			writer.setParameter(mysqlWriterParameter);
			writer.setName("mysqlwriter");
		}else{
			writer = new Writer<HdfsWriterParameter>();
			List<Object> params = Arrays.asList(targetDatabaseTextField.getText(), targetDatatableTextField.getText());
			//获取路径
			List<LinkedHashMap<Object, Object>> pathQueryResult = SJZYMapper.queryHdfsFilePath(Arrays.asList(null, params));
			String completePath = (String) pathQueryResult.get(0).get(SomeConstant.METASTORE_SDS_LOC);
			URI uri = new URI(completePath);
			String path = uri.getPath();
			//获取列
			List<List<Object>> paramsList = Arrays.asList(null, params);
			List<LinkedHashMap<Object, Object>> colQueryResult = SJZYMapper.queryFieldsAll(targetDataTypeTextField.getText(), paramsList);
			List<Column> colInfos = new ArrayList<>();
			for (String targetField : targetFieldList) {
				for (int i = 0; i < colQueryResult.size(); i++) {
					String fieldName = (String) colQueryResult.get(i).get(SomeConstant.METASTORE_COL_COLNAME);
					if (targetField.equals(fieldName)){
						Column column = new Column();
						column.setName(targetField);
						column.setType((String) colQueryResult.get(i).get(SomeConstant.METASTORE_COL_TYPE));
						colInfos.add(column);
						break;
					}
				}
			}

			HdfsWriterParameter hdfsWriterParameter = new HdfsWriterParameter();
			hdfsWriterParameter.setColumn(colInfos);
			hdfsWriterParameter.setCompress("");
			hdfsWriterParameter.setFileName(targetDatatableTextField.getText());
			hdfsWriterParameter.setWriteMode("append");
			hdfsWriterParameter.setDefaultFS(PropertiesUtil.getProperty("hdfs.url"));
			hdfsWriterParameter.setFieldDelimiter(",");
			hdfsWriterParameter.setPath(path);
			hdfsWriterParameter.setFileType("text");

			writer.setParameter(hdfsWriterParameter);
			writer.setName("hdfswriter");
		}

		//设置setting
		Speed speed = new Speed();
		speed.setChannel(3);
		ErrorLimit errorLimit = new ErrorLimit();
		errorLimit.setPercentage(0.02);
		errorLimit.setRecord(0);
		Setting setting = new Setting();
		setting.setErrorLimit(errorLimit);
		setting.setSpeed(speed);

		//完成封装
		Content content = new Content();
		content.setReader(reader);
		content.setWriter(writer);
		Job job = new Job();
		job.setContent(List.of(content));
		job.setSetting(setting);
		JsonRootBean jsonRootBean = new JsonRootBean();
		jsonRootBean.setJob(job);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(jsonRootBean);
	}
}