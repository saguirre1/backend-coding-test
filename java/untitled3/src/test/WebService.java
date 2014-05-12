package test;
import com.sun.net.httpserver.HttpServer;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/shipshape")
public class WebService {
    private String dbURL = "jdbc:mysql://localhost:3306/test";
    private String username ="root";
    private String password = "root";
    private Connection dbCon = null;
    private PreparedStatement stmt = null;
    private ResultSet rs = null;
    String insertQuery ="insert into expenses (date, amount, reason) values (?, ?, ?)";
    String retrieveQuery ="select * from expenses";

    public void saveToDatabase(String date, String amount, String reason){
        try {
            dbCon = DriverManager.getConnection(dbURL, username, password);
            stmt = dbCon.prepareStatement(insertQuery);
            stmt.setString(1, date);
            stmt.setString(2, amount);
            stmt.setString(3, reason);
            rs = stmt.executeQuery(insertQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<ExpenseBean> retrieveFromDatabase() {
        List<ExpenseBean> allData = new ArrayList<ExpenseBean>();
        try {
            dbCon = DriverManager.getConnection(dbURL, username, password);
            stmt = dbCon.prepareStatement(retrieveQuery);
            rs = stmt.executeQuery(retrieveQuery);
            while (rs.next()){
                ExpenseBean expenseBean = new ExpenseBean();
                expenseBean.setDate(rs.getString(1));
                expenseBean.setAmount(rs.getString(2));
                expenseBean.setReason(rs.getString(3));
                int vat = Integer.parseInt(expenseBean.getAmount())*20/100;
                expenseBean.setVat(Integer.toString(vat));
                allData.add(expenseBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allData;
    }

    @GET
    @Path("/saveExpenses")
    public void saveExpenses(@QueryParam("date") String date,
                               @QueryParam("amount") String amount,
                               @QueryParam("reason") String reason) {
        saveToDatabase(date, amount, reason);
    }

    @GET
    @Path("/retrieveExpenses")
    public void retrieveExpenses() {
        retrieveFromDatabase();
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServerFactory.create("http://127.0.0.1:9998/");
        server.start();

        System.out.println("Server running");
        System.out.println("Visit: http://127.0.0.1:9998/shipshape/saveExpenses?date=11&amount=22&reason=xx");
        System.out.println("or http://127.0.0.1:9998/shipshape/retrieveExpenses");
        System.out.println("Hit return to stop...");
        System.in.read();
        System.out.println("Stopping server");
        server.stop(0);
        System.out.println("Server stopped");
    }
}