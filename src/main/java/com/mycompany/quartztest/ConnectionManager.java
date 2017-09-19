/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.quartztest;

import static com.mycompany.quartztest.Main.RESOURCES_PATH;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.hsqldb.cmdline.SqlFile;
import org.hsqldb.jdbc.JDBCPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nikolay
 */
public class ConnectionManager {

    static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    private static final int INIT_POOL_SIZE = 8;
    private static final int WAIT_SHUTDOWN_SECONDS = 3;
    private static final String USERNAME = "SA";
    private static final String PASSWORD = "";
    
    private static final String DB_URL = "jdbc:hsqldb:file:/opt/db/testdb;ifexists=false";

    private static JDBCPool pool;

    private static DataSource getDatasource() throws SQLException {
        if (pool == null) {
            // setup datasource if null
            synchronized (ConnectionManager.class) {
                logger.info("intializing hsqldb JDBCPool with a size of {}", INIT_POOL_SIZE);
                if (pool == null) {
                    pool = new JDBCPool(INIT_POOL_SIZE);
                    pool.setUrl(DB_URL);
                    pool.setUser(USERNAME);
                    pool.setPassword(PASSWORD);
                }
            }
        }

        return pool;
    }

    public static void initQuartzSqlTables() throws SQLException {
        
//        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            if (conn != null) {
                try {
                    SqlFile sqlFile = new SqlFile(new File(RESOURCES_PATH + "/hsql_tables.sql"));
                    sqlFile.setConnection(conn);
                    sqlFile.setContinueOnError(false);
                    sqlFile.setAutoClose(true);
                    sqlFile.execute();
                    logger.info("Finished running .sql file");
                } catch (Throwable ex) {
                    logger.error("Failed running .sql init file", ex);
                }
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        DataSource ds = (JDBCPool) getDatasource();
        Connection conn = ds.getConnection();
        conn.setAutoCommit(false);
        return conn;
    }

    public static void shutdown() throws SQLException {
        try (Connection conn = getConnection()) {
            try (Statement stmnt = conn.createStatement()) {
                stmnt.execute("SHUTDOWN");
                logger.info("running database SHUTDOWN..");
            }
        }
        logger.info("closing pool..");
        pool.close(WAIT_SHUTDOWN_SECONDS);
        logger.info("closed!");
    }
}
