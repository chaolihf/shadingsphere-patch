# shadingsphere-patch
## 1.支持pgsql数据库
```
mode:
    type: Standalone
    repository:
        type: JDBC
        props:
            provider: PostgreSQL
            jdbc_url: jdbc:postgresql://127.0.0.1:5432/zdrz
            username: ops_select
            password: 
            driverClassName: org.postgresql.Driver
```
## 2. 支持在持久化数据库中定义用户及权限
```
authority:
  users:
    - user: root@%
      password: root
      admin: true	  
  privilege:
     type: PERSIST_DATABASE_PERMITTED
```
## 3. 支持通过命令来创建账号
```
CREATE DIST USER app_user IDENTIFIED BY app_pwd
```