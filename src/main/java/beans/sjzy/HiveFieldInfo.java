package beans.sjzy;

/**
 * ClassName:HiveFieldInfo
 * Package:com.dataTransmit.parseFrontEndBeans
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/7/2 - 2:04
 */
public class HiveFieldInfo {
    private String fieldName;
    private String comment;
    private String dataType;
    private String isPar;

    public String getIsPar() {
        return isPar;
    }

    public void setIsPar(String isPar) {
        this.isPar = isPar;
    }

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
}
