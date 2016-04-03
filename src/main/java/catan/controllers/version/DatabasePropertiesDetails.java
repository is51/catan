package catan.controllers.version;

import org.springframework.core.env.Environment;

class DatabasePropertiesDetails {
    private String driverClassName;
    private String url;
    private String username;

    private String dialect;
    private String showSql;
    private String formatSql;
    private String hbm2ddlAuto;
    private String writeDelay;

    public DatabasePropertiesDetails() {
    }

    public DatabasePropertiesDetails(Environment environment) {
        this.driverClassName = environment.getRequiredProperty("jdbc.driverClassName");
        this.url = environment.getRequiredProperty("jdbc.url");
        this.username = environment.getRequiredProperty("jdbc.username");
        this.dialect = environment.getRequiredProperty("hibernate.dialect");
        this.showSql = environment.getRequiredProperty("hibernate.show_sql");
        this.formatSql = environment.getRequiredProperty("hibernate.format_sql");
        this.hbm2ddlAuto = environment.getRequiredProperty("hibernate.hbm2ddl.auto");
        this.writeDelay = environment.getRequiredProperty("hibernate.hsqldb.write_delay");
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getShowSql() {
        return showSql;
    }

    public void setShowSql(String showSql) {
        this.showSql = showSql;
    }

    public String getFormatSql() {
        return formatSql;
    }

    public void setFormatSql(String formatSql) {
        this.formatSql = formatSql;
    }

    public String getHbm2ddlAuto() {
        return hbm2ddlAuto;
    }

    public void setHbm2ddlAuto(String hbm2ddlAuto) {
        this.hbm2ddlAuto = hbm2ddlAuto;
    }

    public String getWriteDelay() {
        return writeDelay;
    }

    public void setWriteDelay(String writeDelay) {
        this.writeDelay = writeDelay;
    }
}
