package constants;

/**
 * ClassName:SystemLibConstant
 * Package:com.common.constants
 * Description:系统库的库名表名字段名常量
 *
 * @Author: thechen
 * @Create: 2023/7/11 - 11:40
 */
public interface SystemLibConstant {
    String SYSTEMDATABASE = "Verity";
        String USERINFOTABLE = "user_info";
            String USERINFOTABLE_USERNAME = "username";
            String USERINFOTABLE_PASSWORD = "passwd";
            String USERINFOTABLE_PHONENUM = "phoneNumber";
            String USERINFOTABLE_CREATETIME = "createTime";
            String USERINFOTABLE_LASTLOGINTIME = "lastLoginTime";

        String SQLFILEINFOTABLE = "sql-file_info";

            String SQLFILEINFO_PROJECTNAME = "projectName";
            String SQLFILEINFO_FILENAME = "fileName";
            String SQLFILEINFO_PARENTFILENAME = "parentFileName";
            String SQLFILEINFO_FILETYPE = "fileType";
            String SQLFILEINFO_CONTENT = "content";

        String DATAMODULEINFOTABLE = "data-module_info";
            String DATAMODULEINFO_MODULENAME = "dataModuleName";
            String DATAMODULEINFO_POSTSCRIPT = "dataModulePostscript";
            String DATAMODULEINFO_OWNER = "owner";
        String DATAMODULEFILETREETABLE = "data-module_file-tree";
            String DATAMODULEFILETREE_MODULENAME = "dataModuleName";
            String DATAMODULEFILETREE_PATH = "dataModulePath";
            String DATAMODULEFILETREE_TYPE = "dataModuleType";
        String DATAMODULEDETAILTABLE = "data-module_detail";
            String DATAMODULEDETAIL_ROWNUM = "rowNum";
            String DATAMODULEDETAIL_MODULENAME = "dataModuleName";
            String DATAMODULEDETAIL_PATH = "dataModulePath";
            String DATAMODULEDETAIL_CHIFIELDNAME = "chiFieldName";
            String DATAMODULEDETAIL_ENGFIELDNAME = "engFieldName";
            String DATAMODULEDETAIL_FIELDDATATYPE = "fieldDataType";
            String DATAMODULEDETAIL_RESOURCETABLENAME = "resourceTableName";
            String DATAMODULEDETAIL_CHIRESOURCEFIELDNAME = "chiResourceFieldName";
            String DATAMODULEDETAIL_ENGRESOURCEFIELDNAME = "engResourceFieldName";
        String USEPROJECTPERMISSIONTABLE = "user-project_permission";
            String USEPROJECTPERMISSIONTABLE_USER = "user";
            String USEPROJECTPERMISSIONTABLE_PROJECTNAME = "projectName";
        String SYNCHRONIZEJOBINFOTABLE = "synchronize-job_info";
            String SYNCHRONIZEJOBINFOTABLE_PROJECTNAME = "projectName";
            String SYNCHRONIZEJOBINFOTABLE_JOBPATH = "jobPath";
            String SYNCHRONIZEJOBINFOTABLE_PATHTYPE = "pathType";
            String SYNCHRONIZEJOBINFOTABLE_CONTENT = "content";
            String SYNCHRONIZEJOBINFOTABLE_SOURCEDATATYPE = "sourceDataType";
            String SYNCHRONIZEJOBINFOTABLE_TARGETDATATYPE = "targetDataType";
            String SYNCHRONIZEJOBINFOTABLE_SOURCEDATABASE = "sourceDatabase";
            String SYNCHRONIZEJOBINFOTABLE_TARGETDATABASE = "targetDatabase";
            String SYNCHRONIZEJOBINFOTABLE_SOURCEDATATABLE = "sourceDatatable";
            String SYNCHRONIZEJOBINFOTABLE_TARGETDATATABLE = "targetDatatable";
        String SYNCHRONIZEJOBDETAILTABLE = "synchronize-job_detail";
            String SYNCHRONIZEJOBDETAILTABLE_PROJECTNAME = "projectName";
            String SYNCHRONIZEJOBDETAILTABLE_JOBPATH = "jobPath";
            String SYNCHRONIZEJOBDETAILTABLE_SOURCEFIELD = "sourceField";
            String SYNCHRONIZEJOBDETAILTABLE_TARGETFIELD = "targetField";
}
