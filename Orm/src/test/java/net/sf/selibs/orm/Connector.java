/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import net.sf.selibs.utils.misc.UHelper;
import org.apache.log4j.BasicConfigurator;
import org.postgresql.ds.PGSimpleDataSource;

/**
 *
 * @author selibs
 */
public class Connector {

    private static Connection con;
    private static PGSimpleDataSource ds;

    public static Connection getConnection() throws SQLException {
        con.setAutoCommit(true);
        return con;
    }

    public static DataSource getDS() throws SQLException {
        return ds;
    }

    static {
        BasicConfigurator.configure();
        try {
            con = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres");
            ds = new PGSimpleDataSource();
            ds.setServerName("127.0.0.1");
            ds.setPortNumber(5432);
            ds.setDatabaseName("postgres");
            ds.setPassword("postgres");
            ds.setUser("postgres");
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                UHelper.close(con);
            }
        });
    }
}
