package constants;

/**
 * ClassName:SqlConstant
 * Package:com.common.constants
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/7/21 - 23:33
 */
public interface SqlConstant {
    String database_change = "use `%s`;";
    String select_all = "use `%s`;select * from `%s`;";
    String select_limit = "use `%s`;select * from `%s` limit 1000;";
    String table_drop = "use `%s`;drop table `%s`;";
    String database_drop = "drop database `%s`;";
    String database_create = "create database `%s`;";
    String database_show = "show databases;";
    String table_show = "use `%s`;show tables;";
    String table_describe = "use `%s`;show create table `%s`;";
    String table_field_describe = "use `%s`;describe `%s`;";
}
