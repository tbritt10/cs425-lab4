package edu.jsu.mcis.cs425.Lab4;

import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.sql.*;
import javax.sql.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;

public class Rates {
    
    
    public static final String RATE_FILENAME = "rates.csv";
    public static Context envContext = null, initContext = null;
    public static DataSource ds = null;
    public static Connection conn = null;
    
    public static List<String[]> getRates(String path) {
        
        StringBuilder s = new StringBuilder();
        List<String[]> data = null;
        String line;
        
        try {
            
            /* Open Rates File; Attach BufferedReader */

            BufferedReader reader = new BufferedReader(new FileReader(path));
            
            /* Get File Data */
            
            while((line = reader.readLine()) != null) {
                s.append(line).append('\n');
            }
            
            reader.close();
            
            /* Attach CSVReader; Parse File Data to List */
            
            CSVReader csvreader = new CSVReader(new StringReader(s.toString()));
            data = csvreader.readAll();
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return List */
        
        return data;
        
    }
    
    public static String getRatesAsTable(List<String[]> csv) {
        
        StringBuilder s = new StringBuilder();
        String[] row;
        
        try {
            
            /* Create Iterator */
            
            Iterator<String[]> iterator = csv.iterator();
            
            /* Create HTML Table */
            
            s.append("<table>");
            
            while (iterator.hasNext()) {
                
                /* Create Row */
            
                row = iterator.next();
                s.append("<tr>");
                
                for (int i = 0; i < row.length; ++i) {
                    s.append("<td>").append(row[i]).append("</td>");
                }
                
                /* Close Row */
                
                s.append("</tr>");
            
            }
            
            /* Close Table */
            
            s.append("</table>");
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return Table */
        
        return (s.toString());
        
    }
    
    public static String getRatesAsJson(List<String[]> csv) {
        
        String results = "";
        String[] row;
        double r;
        
        try {
            
            /* Create Iterator */
            
            Iterator<String[]> iterator = csv.iterator();
            
            /* Create JSON Containers */
            
            JSONObject json = new JSONObject();
            JSONObject rates = new JSONObject();            
            int counter = 0;
            /* 
             * Add rate data to "rates" container and add "date" and "base"
             * values to "json" container.  See the "getRatesAsTable()" method
             * for an example of how to get the CSV data from the list, and
             * don't forget to skip the header row!
             * JSON format: "Key": Value
             * *** INSERT YOUR CODE HERE ***
             */
            
            while (iterator.hasNext()) {
                
                if (counter == 0) {
                    counter++;
                    row = iterator.next();
                    //Skip line is intentional
                    row = iterator.next();
                    r = Double.parseDouble(row[2]);
                    rates.put(row[1], r);
                }
                else {
                    row = iterator.next();
                    counter++;
                    for (int i = 0; i < row.length; ++i) {
                        r = Double.parseDouble(row[2]);
                        rates.put(row[1], r);
                    }
                }
          
                
            
            
                }
            
            /* Parse top-level container to a JSON string */
            json.put("base", "USD");
            json.put("date", "9-25-2019");
            json.put("rates", rates);
            //results = JSONValue.toJSONString(json);
            results = JSONValue.toJSONString(json);
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return JSON string */
        
        return (results.trim());
        
    }
    public static String getRatesAsJson(String code) {
        String results = "";
        String codes = "";
        String rate = "";
        String date = "";
        JSONObject json = new JSONObject();
        JSONObject rates = new JSONObject();
        String query = "";
        
        
        try{
            //Aquire Connection
            envContext = new InitialContext();
            initContext  = (Context)envContext.lookup("java:/comp/env");
            ds = (DataSource)initContext.lookup("jdbc/db_pool");
            conn = ds.getConnection();
        
        
            if(code == null){
                query = "SELECT * FROM rates";
            }
            else {
                query = "SELECT * FROM rates WHERE code = ?";
            }

            PreparedStatement statement = conn.prepareStatement(query);

            if (code != null)
                statement.setString(1, code);

            boolean hasresults = statement.execute();

            if (hasresults) {
                ResultSet resultset = statement.getResultSet();
                while (resultset.next()) {
                    System.err.println("Next row ...");
                    codes = resultset.getString("code");
                    rate = resultset.getString("rate");
                    date = resultset.getString("date");
                    rates.put(codes, rate);
                }

            json.put("rates", rates);
            json.put("date",date);
            json.put("base","USD");
            }
            results = JSONValue.toJSONString(json);
            closeConnection();
        }
        
        
        catch(Exception e){ e.printStackTrace(); }
        
        return (results.trim());
    }
    
    public Connection getConnection() { return conn; }
    
    public static void closeConnection() {
        
        if (conn != null) {
            
            try {
                conn.close();
            }
            
            catch (SQLException e) {}
            
        }
    
    } // End closeConnection()
   

}