package com.example.Database.Container;
import java.sql.*;

public class Datenbankverbindung {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        Connection con = DriverManager.getConnection("jdbc:sqlserver://prsoftwareengineering.database.windows.net:1433;database=SoftwareEngineering;user=KevinSchuetz@prsoftwareengineering;password={Reitstall13-!};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;");
        PreparedStatement stmt = con.prepareStatement("DROP TABLE REGISTRATION");
        stmt.executeUpdate();


        String create = "CREATE TABLE REGISTRATION " +
                "(id INTEGER not NULL, " +
                " PRIMARY KEY ( id ))";

        stmt = con.prepareStatement(create);
        stmt.executeUpdate();

        stmt = con.prepareStatement("INSERT INTO REGISTRATION values(110)");
        stmt.executeUpdate();

        con.close();

    }



}

