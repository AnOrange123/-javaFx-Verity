package beans.sjzy;

import java.util.HashMap;
import java.util.List;

/**
 * ClassName:HiveTableInfo
 * Package:com.dataTransmit.parseFrontEndBeans
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/7/2 - 2:04
 */
public class HiveTableInfo {
    private String dataBase;
    private String tableName;
    private String comment;
    private String location;
    private HashMap<String, String> partition = new HashMap<String, String>();
    private List<HiveFieldInfo> hiveFieldInfos;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public HashMap<String, String> getPartition() {
        return partition;
    }

    public void setPartition(HashMap<String, String> partition) {
        this.partition = partition;
    }

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

    public List<HiveFieldInfo> getHiveFieldInfos() {
        return hiveFieldInfos;
    }

    public void setHiveFieldInfos(List<HiveFieldInfo> hiveFieldInfos) {
        this.hiveFieldInfos = hiveFieldInfos;
    }
}
