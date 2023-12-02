package beans.sjmx;

import beans.SJMXManager;
import constants.SomeConstant;
import constants.SystemLibConstant;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import mapper.SJMXMapper;
import utils.CommonUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:DataModuleInfoBean
 * Package:beans.sjmx
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/10/11 - 18:53
 */
public class DataModuleInfoBean {
	private String chiFieldName;
	private String engFieldName;
	private String fieldDataType;
	private String resourceTableName;
	private String chiResourceFieldName;
	private String engResourceFieldName;


	private String projectName;

	public DataModuleInfoBean(String projectName) {
		this.projectName = projectName;
	}

	public String getChiFieldName() {
		return chiFieldName;
	}

	public void setChiFieldName(String chiFieldName) {
		//字段中文名
		this.chiFieldName = chiFieldName;
	}

	public String getEngFieldName() {
		return engFieldName;
	}

	public void setEngFieldName(String engFieldName) {
		//字段英文名
		this.engFieldName = engFieldName;
	}

	public String getFieldDataType() {
		return fieldDataType;
	}

	public void setFieldDataType(String fieldDataType) {
		this.fieldDataType = fieldDataType;
	}

	public String getResourceTableName() {
		return resourceTableName;
	}

	public void setResourceTableName(String resourceTableName)  {
		this.resourceTableName = resourceTableName;
	}

	public String getChiResourceFieldName() {
		return chiResourceFieldName;
	}

	public void setChiResourceFieldName(String chiResourceFieldName) {
		//来源字段注释
		this.chiResourceFieldName = chiResourceFieldName;
	}

	public String getEngResourceFieldName() {
		return engResourceFieldName;
	}

	public void setEngResourceFieldName(String engResourceFieldName) {
		this.engResourceFieldName = engResourceFieldName;
	}
}
