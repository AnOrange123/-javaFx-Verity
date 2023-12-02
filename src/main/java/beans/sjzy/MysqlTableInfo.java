package beans.sjzy;

import java.util.List;

/**
 * ClassName:MysqlTableInfo
 * Package:com.dataTransmit.beans
 * Description:数据开发模块——mysql——新建数据表（解析并存放前端传来的json数据）
 *
 * @Author: thechen
 * @Create: 2023/6/24 - 14:22
 */
public class MysqlTableInfo {

    private String dataBase;
    private String tableName;
    private String comment;

    private List<MysqlFieldInfo> mysqlFieldInfos;

    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<MysqlFieldInfo> getFieldInfos() {
        return mysqlFieldInfos;
    }

    public void setFieldInfos(List<MysqlFieldInfo> mysqlFieldInfos) {
        this.mysqlFieldInfos = mysqlFieldInfos;
    }
}
