# backupqiniu
备份博客信息到七牛云

### 注册七牛云账号并认证

[七牛云地址](https://portal.qiniu.com)

注册之后创建对象储存,区域随便选,访问控制选择私有。

然后去右上角个人头像位置选择秘钥管理AK,和SK需要用到

### 安装jdk1.8以上版本,并配置环境变量

略

### 修改配置文件

```
backup_name=princelei
backup_src=/var/www/html/usr/uploads
mysql_server=localhost
mysql_user=root
mysql_pass=123456
mysql_dbs=xxx ttt zzz
backup_dir=/tmp/backuptoqiniu
backup_file_passwd=aaaa
qiniu_bucket=dasd
qiniu_access_key=a2ae
qiniu_secrect_key=asdasd
```
- backup_name: 备份名称随便填写
- backup_src: 要备份的目录,多个用逗号分隔
- mysql_server: 要备份的mysql的ip地址
- mysql_user: 要备份的mysql的用户名
- mysql_pass: 要备份的mysql的密码
- mysql_dbs: 要备份的mysql的数据库,多个用空格分隔
- backup_dir: 备份中要用的临时文件夹
- backup_file_passwd: 备份压缩包的密码
- qiniu_bucket: 七牛云储存空间名称
- qiniu_access_key: 七牛云AK
- qiniu_secrect_key: 七牛云SK

### 启动jar包

```
java -jar backupqiniu.jar
```

运行成功之后查看七牛云上是否有文件
成功之后可以设置定时任务

```
crontab -e
```

添加

```
* * * */1 * java -jar (jar包路径)
```

