/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.quartztest;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
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

                if (pool == null) {
                    throw new RuntimeException("Failed initializing database pool, aborting");
                }
                try {
                    List<Path> sqlFiles = Arrays.asList(
                            FileSystem.getQuartzHsqlScript(),
                            FileSystem.getLogbackHsqlScript(),
                            FileSystem.getApplicationHsqlScript()
                    );

                    try (Connection con = pool.getConnection()) {
                        runSqlFiles(con, sqlFiles);
                    }

                } catch (FileNotFoundException e) {
                    logger.error("Could not find file: {}", e);
                }
            }
        }

        return pool;
    }

    public static void runSqlFiles(Connection con, List<Path> sqlFiles) throws SQLException {

//        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        if (sqlFiles == null || sqlFiles.isEmpty()) {
            return;
        }

        for (Path sqlf : sqlFiles) {
            if (sqlf != null) {
                try {
                    logger.info("Running sql file: {}..", sqlf.toString());
                    SqlFile sqlFile = new SqlFile(sqlf.toFile());
                    sqlFile.setConnection(con);
                    sqlFile.setContinueOnError(false);
                    sqlFile.setAutoClose(true);
                    sqlFile.execute();
                    logger.info("Running sql file: {}.. SUCCESS", sqlf.toString());
                } catch (Throwable ex) {
                    logger.error("Running sql file: {}.. FAILED", sqlf.toString(), ex);
                }
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        DataSource ds = (JDBCPool) getDatasource();
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new RuntimeException("Connection is null");
        }
        conn.setAutoCommit(false);

        return conn;
    }
    
    public static void init() throws SQLException {
        try (Connection con = getConnection()) {
            con.isValid(5);
        }
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
