package beans;

import beans.sjmx.DataModuleInfoBean;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import mapper.SJMXMapper;
import utils.CommonUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * ClassName:SJKFTabManager
 * Package:beans
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/10/29 - 0:51
 */
public class SJMXManager {
	//当前数据模型的保存状态  0：已保存  1：已修改未保存
	private static int isSaveFlag = 0;
	private static SJMXManager sjmxManager;
	private static VBox dataModuleInfoTable;

	private static String projectName;
	private static String modulePath;

	private SJMXManager() {
	}

	public void dataModuleSave() throws SQLException, IOException {
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
	}

	public static SJMXManager getInstance(){
		if (sjmxManager == null) {
			sjmxManager = new SJMXManager();
		}
		return sjmxManager;
	}

	//返回当前模型的保存状态
	public int getStatus(){
		return isSaveFlag;
	}

	//将状态置为已保存
	public void setSavedStatus(){
		setIsSaveFlag(0);
	}

	//将状态置为已修改未保存
	public void setUnSavedStatus(){
		setIsSaveFlag(1);
	}

	private int getIsSaveFlag() {
		return isSaveFlag;
	}

	private void setIsSaveFlag(int isSaveFlag) {
		SJMXManager.isSaveFlag = isSaveFlag;
	}

	public VBox getDataModuleInfoTable() {
		return dataModuleInfoTable;
	}

	public void setDataModuleInfoTable(VBox dataModuleInfoTable) {
		SJMXManager.dataModuleInfoTable = dataModuleInfoTable;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		SJMXManager.projectName = projectName;
	}

	public String getModulePath() {
		return modulePath;
	}

	public void setModulePath(String modulePath) {
		SJMXManager.modulePath = modulePath;
	}
}