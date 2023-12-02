package beans.sjzy;

/**
 * ClassName:MysqlFieldInfo
 * Package:com.dataTransmit.beans
 * Description:数据开发模块——mysql——表字段信息（解析并存放前端传来的json数据）
 *
 * @Author: thechen
 * @Create: 2023/6/24 - 14:22
 */
public class MysqlFieldInfo {

    private String fieldName;
    private String comment;
    private String dataType;
    private String isKey;
    private String isUnique;
    private String isNotNull;
    private String isAutoIncrement;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getIsKey() {
        return isKey;
    }

    public void setIsKey(String isKey) {
        this.isKey = isKey;
    }

    public String getIsUnique() {
        return isUnique;
    }

    public void setIsUnique(String isUnique) {
        this.isUnique = isUnique;
    }

    public String getIsNotNull() {
        return isNotNull;
    }

    public void setIsNotNull(String isNotNull) {
        this.isNotNull = isNotNull;
    }

    public String getIsAutoIncrement() {
        return isAutoIncrement;
    }

    public void setIsAutoIncrement(String isAutoIncrement) {
        this.isAutoIncrement = isAutoIncrement;
    }

}
