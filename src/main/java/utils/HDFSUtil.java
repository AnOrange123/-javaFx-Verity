package utils;

import beans.sjzy.HdfsFileObject;
import constants.SomeConstant;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import service.common.CommonAlertController;
import utils.PropertiesUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:HDFSUtil
 * Package:com.common.utils
 * Description:数据开发模块——hdfs
 *
 * @Author: thechen
 * @Create: 2023/6/25 - 20:08
 */
public class HDFSUtil {

    //获取hdfs连接
    private static FileSystem getHDFSConnect() throws URISyntaxException, IOException, InterruptedException {
        Configuration conf = new Configuration();
        conf.set("ipc.client.connect.timeout", "3000"); // 设置连接超时为5秒
        conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        FileSystem fs = FileSystem.get(URI.create(PropertiesUtil.getProperty("hdfs.url")),conf,PropertiesUtil.getProperty("hdfs.user"));
        return fs;
    }

    //关闭hdfs连接
    private static void closeHDFSConnect(FileSystem fileSystem) throws URISyntaxException, IOException, InterruptedException {
        fileSystem.close();
    }

    /**
     * 判断路径是否存在
     * @param path  要判断的路径
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean isExist(String path) throws URISyntaxException, IOException, InterruptedException {
        FileSystem fileSystem = HDFSUtil.getHDFSConnect();
        boolean exists = fileSystem.exists(new Path(path));
        HDFSUtil.closeHDFSConnect(fileSystem);
        return exists;
    }

    /**
     * 查询目录下的所有文件信息
     * @param path  要查询的目录路径
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public static List<LinkedHashMap<String, Object>> queryDirectory(String path) throws URISyntaxException, IOException, InterruptedException {
        FileSystem fileSystem = HDFSUtil.getHDFSConnect();

        Path pathObject = new Path(path);
        List<LinkedHashMap<String, Object>> pathMap = new ArrayList<>();

        //查询文件信息
        FileStatus[] fileStatuses = fileSystem.listStatus(pathObject);

        //遍历，将信息放入pathMap
        for (FileStatus fileStatus : fileStatuses) {
            LinkedHashMap<String, Object> pathInfo = new LinkedHashMap<>();
            pathInfo.put(SomeConstant.PERMISSION, (fileStatus.isDirectory() ? "d" : "f") + fsActionToRwx(fileStatus.getPermission().getUserAction()) + fsActionToRwx(fileStatus.getPermission().getGroupAction()) + fsActionToRwx(fileStatus.getPermission().getOtherAction()));
            pathInfo.put(SomeConstant.OWNER, fileStatus.getOwner());
            pathInfo.put(SomeConstant.GROUP, fileStatus.getGroup());
            pathInfo.put(SomeConstant.SIZE, fileStatus.getLen());
            pathInfo.put(SomeConstant.BLOCKSIZE, fileStatus.getBlockSize());
            pathInfo.put(SomeConstant.PATH, fileStatus.getPath().getName());
            pathInfo.put(SomeConstant.ISDirectory, fileStatus.isDirectory());
            pathMap.add(pathInfo);
        }

        HDFSUtil.closeHDFSConnect(fileSystem);
        return pathMap;
    }

    //创建目录
    public static boolean mkdir(String path)  throws URISyntaxException, IOException, InterruptedException{
        FileSystem fileSystem = HDFSUtil.getHDFSConnect();
        boolean resultFlag = fileSystem.mkdirs(new Path(path));
        HDFSUtil.closeHDFSConnect(fileSystem);
        return resultFlag;
    }

    //上传文件
    public static void uploadFile(HdfsFileObject hdfsFileObject)  throws URISyntaxException, IOException, InterruptedException{
        FileSystem fileSystem = HDFSUtil.getHDFSConnect();
        fileSystem.copyFromLocalFile(false, true, new Path(hdfsFileObject.getSourcePosition()), new Path(hdfsFileObject.getTargetPosition()));
        HDFSUtil.closeHDFSConnect(fileSystem);
    }

    //重命名
    public static boolean rename(HdfsFileObject hdfsFileObject)  throws URISyntaxException, IOException, InterruptedException{
        FileSystem fileSystem = HDFSUtil.getHDFSConnect();
        boolean resultFlag = fileSystem.rename(new Path(hdfsFileObject.getSourcePosition()), new Path(hdfsFileObject.getTargetPosition()));
        HDFSUtil.closeHDFSConnect(fileSystem);
        return resultFlag;
    }

    //删除
    public static boolean delete(String path)  throws URISyntaxException, IOException, InterruptedException{
        FileSystem fileSystem = HDFSUtil.getHDFSConnect();
        boolean resultFlag = fileSystem.delete(new Path(path), true);
        HDFSUtil.closeHDFSConnect(fileSystem);
        return resultFlag;
    }

    //文件下载
    public static void downloadFile(HdfsFileObject hdfsFileObject)  throws URISyntaxException, IOException, InterruptedException{
        FileSystem fileSystem = HDFSUtil.getHDFSConnect();
        fileSystem.copyToLocalFile(false, new Path(hdfsFileObject.getSourcePosition()), new Path(hdfsFileObject.getTargetPosition()), false);
        HDFSUtil.closeHDFSConnect(fileSystem);
    }

    //将查询出的文件信息中的权限012转化为rwx
    private static String fsActionToRwx(FsAction action) {
        StringBuilder str = new StringBuilder("___");

        if (action.implies(FsAction.READ)) {
            str.setCharAt(0, 'r');
        }
        if (action.implies(FsAction.WRITE)) {
            str.setCharAt(1, 'w');
        }
        if (action.implies(FsAction.EXECUTE)) {
            str.setCharAt(2, 'x');
        }

        return str.toString();
    }
}

