package program;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainClass {

    public static void main(String[] args) {
        File backupDir = new File(Util.getBackupDir());
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        String[] backupSrcs = Util.getBackupSrcs();
        for (String backupSrc : backupSrcs) {
            File dir = new File(Util.getBackupDir(), backupSrc);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String cmd = "cp -r " + backupSrc + " " + dir.getParentFile().getAbsolutePath();
            execute(cmd);
        }
        for (String db : Util.getMysqlDbs()) {
            PrintWriter pw = null;
            BufferedReader br = null;
            String cmd = "mysqldump -h" + Util.getMysqlServer() + " -u" + Util.getMysqlUser() + " -p" + Util.getMysqlPass() + " --set-charset=UTF8 " + db;
            try {
                Process process = Runtime.getRuntime().exec(cmd);
                new Thread(() -> {
                    InputStream is = process.getErrorStream();
                    BufferedReader error = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuilder sb = new StringBuilder();
                    try {
                        while ((line = error.readLine()) != null) {
                            sb.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(sb.toString());
                }).start();
                pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(Util.getBackupDir() + "/" + Util.getBackupName() + "-" + db + ".sql"), StandardCharsets.UTF_8), true);
                br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    pw.println(line);
                }
                process.waitFor();
                process.exitValue();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (pw != null) {
                    pw.close();
                }
            }
        }
        SimpleDateFormat smt = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = smt.format(new Date());
        String fileName = Util.getBackupName() + "-backup-" + date + ".zip";
        String cmd = "zip -rP " + Util.getBackupFilePasswd() + " " + Util.getBackupDir() + "/" + fileName;
        File dir = new File(Util.getBackupDir());
        StringBuilder sb = new StringBuilder(cmd);
        for (File file : dir.listFiles()) {
            sb.append(" ").append(file.getAbsolutePath());
        }
        execute(sb.toString());
        try {
            upload(fileName);
            cmd = "rm -rf " + Util.getBackupDir();
            execute(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void execute(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            new Thread(() -> {
                InputStream is = process.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder sb = new StringBuilder();
                try {
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(sb.toString());
            }).start();
            new Thread(() -> {
                InputStream is = process.getErrorStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder sb = new StringBuilder();
                try {
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(sb.toString());
            }).start();
            process.waitFor();
            process.exitValue();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Configuration cfg = new Configuration(Region.autoRegion());
    // 密钥配置
    private static Auth auth = Auth.create(Util.getQiniuAccessKey(), Util.getQiniuSecrectKey());
    // 创建上传对象
    private static UploadManager uploadManager = new UploadManager(cfg);

    // 简单上传，使用默认策略，只需要设置上传的空间名就可以了 //
    public static String getUpToken() {
        return auth.uploadToken(Util.getQiniuBucket());
    }

    // 普通上传
    public static void upload(String fileName) throws IOException {
        try {
            // 调用put方法上传
            Response res = uploadManager.put(Util.getBackupDir() + "/" + fileName, fileName, getUpToken());
            // 打印返回的信息

            System.out.println(res.isOK());

            System.out.println(res.bodyString());
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时打印的异常的信息
            System.out.println(r.toString());
            try {
                // 响应的文本信息
                System.out.println(r.bodyString());
            } catch (QiniuException e1) {
                // ignore
            }
        }
    }
}
