package program;

import java.io.*;
import java.net.URLDecoder;
import java.util.Properties;

public class Util {

    private static String backupName;
    private static String[] backupSrcs;
    private static String mysqlServer;
    private static String mysqlUser;
    private static String mysqlPass;
    private static String[] mysqlDbs;
    private static String backupDir;
    private static String backupFilePasswd;
    private static String qiniuBucket;
    private static String qiniuAccessKey;
    private static String qiniuSecrectKey;

    static {
        String basePath = Util.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            basePath = URLDecoder.decode(basePath,"UTF-8");
            basePath = basePath.substring(0,basePath.lastIndexOf("/"));
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(basePath+"/charset")));
            String charset = "UTF-8";
            String line;
            while ((line=br.readLine())!=null){
                charset = line;
            }
            br.close();
            InputStreamReader isr = new InputStreamReader(new FileInputStream(basePath+"/setting.properties"),charset);
            Properties p = new Properties();
            p.load(isr);
            backupName = p.getProperty("backup_name");
            backupSrcs = p.getProperty("backup_src").split(",");
            mysqlServer = p.getProperty("mysql_server");
            mysqlUser = p.getProperty("mysql_user");
            mysqlPass = p.getProperty("mysql_pass");
            mysqlDbs = p.getProperty("mysql_dbs").split("\\s+");
            backupDir = p.getProperty("backup_dir");
            backupFilePasswd = p.getProperty("backup_file_passwd");
            qiniuBucket = p.getProperty("qiniu_bucket");
            qiniuAccessKey = p.getProperty("qiniu_access_key");
            qiniuSecrectKey = p.getProperty("qiniu_secrect_key");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getBackupName() {
        return backupName;
    }

    public static String[] getBackupSrcs() {
        return backupSrcs;
    }

    public static String getMysqlServer() {
        return mysqlServer;
    }

    public static String getMysqlUser() {
        return mysqlUser;
    }

    public static String getMysqlPass() {
        return mysqlPass;
    }

    public static String[] getMysqlDbs() {
        return mysqlDbs;
    }

    public static String getBackupDir() {
        return backupDir;
    }

    public static String getBackupFilePasswd() {
        return backupFilePasswd;
    }

    public static String getQiniuBucket() {
        return qiniuBucket;
    }

    public static String getQiniuAccessKey() {
        return qiniuAccessKey;
    }

    public static String getQiniuSecrectKey() {
        return qiniuSecrectKey;
    }
}
