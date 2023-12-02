package utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import service.common.CommonAlertController;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:HiveUtil
 * Package:com.common.utils
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/6/30 - 15:28
 */
public class HiveUtil {
    // 创建连接池对象
    private static DataSource dataSource;

    static {
        try {
            dataSource = init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 连接池的初始化
    private static DataSource init() throws Exception {

        HashMap<String, String> paramMap = new HashMap<String, String>();

        paramMap.put("driverClassName", PropertiesUtil.getProperty("hive.jdbc.driver.name"));
        paramMap.put("url", PropertiesUtil.getProperty("hive.jdbc.url"));
        paramMap.put("username", PropertiesUtil.getProperty("hive.jdbc.user"));
        paramMap.put("password", PropertiesUtil.getProperty("hive.jdbc.password"));
        paramMap.put("maxActive", PropertiesUtil.getProperty("hive.jdbc.datasource.size"));
        paramMap.put("maxWait", "3000");

        // 使用Druid连接池对象
        return DruidDataSourceFactory.createDataSource(paramMap);
    }

    // 从连接池中获取连接对象
    private static Connection getConnection() throws SQLException, IOException {
        try {
            return dataSource.getConnection();
            // 执行操作
        } catch (SQLException e) {
            CommonAlertController.loadCommonAlertWindow("警告", "连接失败\n1.请检查Hive连接配置是否正确\n2.请检查HiveServer2服务是否正常启动");
            return null;
        }
    }

    //执行查询
    private static List<LinkedHashMap<Object, Object>> executeQuery(Connection connection, String sql, List<Object> params) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<LinkedHashMap<Object, Object>> resultList = new ArrayList<>();

        try {
            preparedStatement = connection.prepareStatement(sql);

            if (params != null){
                // 设置参数
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i + 1, params.get(i));
                }
            }

            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                LinkedHashMap<Object, Object> rowMap = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object columnValue = resultSet.getObject(i);
                    rowMap.put(columnName, columnValue);
                }
                resultList.add(rowMap);
            }

            return resultList;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    //执行非查询sql
    private static void executeSql(Connection connection, String sql, List<Object> params) throws SQLException {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);

            if (params != null){
                // 设置参数
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i+1, params.get(i));
                }
            }

            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    //判断是否为查询语句
    public static boolean isQuerySql(String sql) {
        String trimmedSql = sql.trim().toLowerCase();
        return trimmedSql.startsWith("desc") || trimmedSql.startsWith("show") || trimmedSql.startsWith("select");
    }

    //判断是否为数据库变更sql
    public static boolean isDatabaseChangeSql(String sql) {
        String trimmedSql = sql.trim().toLowerCase();
        return trimmedSql.startsWith("use");
    }

    //多行hql同时执行
    public static List<LinkedHashMap<Object, Object>> processHql(String sql, List<List<Object>> paramsList) throws SQLException, IOException {

        List<LinkedHashMap<Object, Object>> resultList = null;
        Connection connection = getConnection();
        String[] sqlList = sql.split(";");

        for (int i = 0; i < sqlList.length; i++) {
            String sqlSingle = sqlList[i];
            if (!CommonUtil.isNullOrEmpty(sqlSingle)){
                boolean isQuery = HiveUtil.isQuerySql(sqlSingle);

                //是否有参数列表
                if (paramsList != null){
                    if (isQuery) {
                        resultList = HiveUtil.executeQuery(connection, sqlSingle, paramsList.get(i));
                    } else{
                        HiveUtil.executeSql(connection, sqlSingle, paramsList.get(i));
                    }
                }else{
                    if (isQuery) {
                        resultList = HiveUtil.executeQuery(connection, sqlSingle, null);
                    } else{
                        HiveUtil.executeSql(connection, sqlSingle, null);
                    }
                }
            }
        }

        connection.close();
        return resultList;
    }
}
