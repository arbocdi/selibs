/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm.relations;

import java.sql.SQLException;
import net.sf.selibs.orm.Connector;
import net.sf.selibs.orm.Database;
import net.sf.selibs.orm.ParentDAO;
import net.sf.selibs.orm.spec.DataAccessException;
import net.sf.selibs.orm.types.PostgresTypeConverter;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author root
 */
public class DatabaseTest3 {
    Database db;
    @Before
    public void before() throws SQLException, DataAccessException{
        db = new Database("net.sf.selibs.orm.relations");
        db.getDdlGenerator().setTypeConverter(new PostgresTypeConverter());
        db.setConnection(Connector.getConnection());
        for(Object obj:db.getTables().values()){
            ParentDAO dao = (ParentDAO) obj;
            try{
                dao.dropTable();
            }
            catch(Exception ex){
                ex.printStackTrace();;
            }
        }
    }
    @Test
    public void testCreateTables() throws Exception {
        System.out.println("============Database:testCreateTables===============");
        db.setClasses(SolarSystem.class,Planet.class,Moon.class,Star.class);
        db.createTables();
    }
}
