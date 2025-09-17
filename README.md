# shadingsphere-patch
1.支持pgsql数据库
样例：
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