package dba;

import java.math.BigDecimal;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainDbProcedure {

    public static void main(String[] args) {
        System.out.println("Connecting database...");
        // driver 5.1
        Connection con = null;
        String url = "jdbc:mysql://localhost:3306/test"; 
        // driver 8.0
        // String url = "jdbc:mysql://localhost:3306/test?zeroDateTimeBehavior "
        // + "= convertToNull&serverTimezone = UTC";
        try {
            con = DriverManager.getConnection(url, "root", "root");
            System.out.println("Database connected to " + con.getCatalog());
        } catch (SQLException ex) {
            System.out.println("SQLException: \t" + ex.getMessage());
            System.out.println("SQLState: \t" + ex.getSQLState());
            System.out.println("VendorError: \t" + ex.getErrorCode());
            System.out.println("Cannot connect the database!" + ex);
            System.exit(1);
        }
        useStoredProcedure(con);
    }

    static void elencaTabella(Connection con) {
        Statement stmt;
        ResultSet rs;
        String SQL = "SELECT * FROM moto LIMIT 10;";
        try {
            System.out.println("Elenco delle moto");
            stmt = con.createStatement();
            rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                String targa = rs.getString("Targa");
                String marca = rs.getString("Marca");
                float tasse = rs.getFloat("Tasse");
                System.out.println("\t" + targa + "\t" + marca + "\t" + tasse);
            }
            stmt.close();
            con.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new IllegalStateException("Scorrimento tabella con errori", e);
        }
    }

    static void createStoredProcedure(Connection con) {
        Statement stmt = null;
        String SQL = "CREATE PROCEDURE QuantoSpendo (OUT spesa INT)"
                + "BEGIN SELECT SUM(tasse) FROM Moto INTO spesa;"
                + "SELECT spesa; END;";
        try {
            System.out.println("Inserimento procedura calcolo tasse.");
            stmt = con.createStatement();
            stmt.executeUpdate(SQL);
            System.out.println("Tutto è andato bene.");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    static void useStoredProcedure(Connection con) {
        ResultSet rs = null;
        CallableStatement cs=null;
        try {
            System.out.println("Chiamata procedura calcolo tasse.");
            cs = con.prepareCall("{call QuantoSpendo(?)}");
            rs = cs.executeQuery();
            if (rs.next()) {
                BigDecimal spesa = rs.getBigDecimal("spesa");
                System.out.println("Spesa in tasse: " + spesa+" €");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
