package dw;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/FillPrescription")
public class FillPrescription extends HttpServlet {
   private static final long serialVersionUID = 1L;

   private String sqlCheckFilled;
   private String usql;
   private String sql;
   private String sqlGetRefills;

   private String prescriptionID;
   private String patientID;

   private HttpServletResponse theResponse;
   private PrintWriter out;

   // database URL
   static final String DB_URL = "jdbc:mysql://localhost/pharm";

   // Database credentials.  COMPLETE THE FOLLOWING STATEMENTS
   static final String USER = "root";
   static final String PASS = "Fbcjapan1!";

   protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

      createSQLStatements();

      prescriptionID = request.getParameter("pres_id");
      patientID = request.getParameter("patient_id");

      theResponse = response;
      theResponse.setContentType("text/html"); // Set response content type
      out = theResponse.getWriter();

      if(rXisFilled()) {
         displayFilledError();
      } 
      else if (!moreRefillsAvailable()) {
         displayNoRefillError();
      }
      else {
         fillPrescription();
      }


   }
   
   private boolean rXisFilled()
   {
      String isFilled = "";
      try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
         conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
         conn.setAutoCommit(false); 

         // prepare statement to update the prescription
         PreparedStatement pstmt =  conn.prepareStatement(sqlCheckFilled);
         // SET VALUES FOR PARAMETER MARKERS 
         pstmt.setString(1, prescriptionID);
         ResultSet rs = pstmt.executeQuery();
         
         while (rs.next()) {
            isFilled = rs.getString(1);
         }
         rs.close();
         conn.commit();

      } catch (SQLException e) {
         // Handle errors
         e.printStackTrace();
      }  
      
      // Check if the select statement returned true or false
      if(isFilled.equals("1")) {
         return true;
      } 
      else {
         return false;
      }
   }
   
   private void displayFilledError()
   {
      // Start HTML code and styling
      out.println("<!DOCTYPE HTML><head><link rel=\"stylesheet\" href=\"search.css\"><html><body><div class=\"container\" style=\"width:100%; color:white;\">");
      out.println("  <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\""
         + " integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">");
      out.println("  <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
      out.println("<link rel=\"stylesheet\" href=\"search.css\">");

      // Create Page header 
      out.println("<h5><br/> This Prescription is Already Filled <br/><br/></h5>");
      out.println("</div></body></head></html>");

   }
   
   private boolean moreRefillsAvailable()
   {
      int refillsAvail = 0;
      try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
         conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
         conn.setAutoCommit(false); 

         // prepare statement to update the prescription
         PreparedStatement pstmt =  conn.prepareStatement(sqlGetRefills);
         // SET VALUES FOR PARAMETER MARKERS 
         pstmt.setString(1, prescriptionID);
         ResultSet rs = pstmt.executeQuery();
         
         while (rs.next()) {
            refillsAvail = Integer.parseInt(rs.getString(1));
         }
         rs.close();
         conn.commit();

      } catch (SQLException e) {
         // Handle errors
         e.printStackTrace();
      }  
      
      // Check if the select statement returned true or false
      if(refillsAvail > 0) {
         return true;
      } 
      else {
         return false;
      }
   }

   private void displayNoRefillError()
   {
      // Start HTML code and styling
      out.println("<!DOCTYPE HTML><head><link rel=\"stylesheet\" href=\"search.css\"><html><body><div class=\"container\" style=\"width:100%; color:white;\">");
      out.println("  <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\""
         + " integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">");
      out.println("  <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
      out.println("<link rel=\"stylesheet\" href=\"search.css\">");

      // Create Page header 
      out.println("<h5><br/> There are no more refills for this prescription. <br/><br/></h5>");
      out.println("</div></body></head></html>");
      
   }

   private void fillPrescription()
   {
      try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
         conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
         conn.setAutoCommit(false); 

         // prepare statement to update the prescription
         PreparedStatement pstmt =  conn.prepareStatement(usql);
         // SET VALUES FOR PARAMETER MARKERS 
         pstmt.setString(1, patientID);
         pstmt.setString(2, prescriptionID);
         pstmt.executeUpdate();

         // prepare select statement to confirm prescription
         pstmt =  conn.prepareStatement(sql);
         // SET VALUES FOR PARAMETER MARKERS 
         pstmt.setString(1, patientID);
         pstmt.setString(2, prescriptionID);
         ResultSet rs = pstmt.executeQuery();

         // Start HTML code and styling
         out.println("<!DOCTYPE HTML><head><link rel=\"stylesheet\" href=\"search.css\"><html><body><div class=\"container\" style=\"width:100%; color:white;\">");
         out.println("  <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\""
            + " integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">");
         out.println("  <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
         out.println("<link rel=\"stylesheet\" href=\"search.css\">");

         // Create Page header 
         out.println("<h3><br/> Confirmation - Prescription Filled <br/></h3>");

         // Create table headers 
         out.println("<table align =\"center\">");
         out.println("<tr><th>Patient ID</th>");
         out.println("<th>Prescription ID</th> ");
         out.println("<th>Trade Name</th> ");
         out.println("<th>Rx Filled</th></tr>");

         while (rs.next()) {
            out.println("<tr>");
            out.println("<td>" + rs.getString(1) + "</td>");
            out.println("<td>" + rs.getString(2) + "</td>");
            out.println("<td>" + rs.getString(3) + "</td>");

            // Check if this prescription is filled or not
            if(rs.getString(4).equals("1")) {
               out.println("<td>" + "Yes" + "</td>");
            } else {
               out.println("<td>" + "No" + "</td>");

            }
            out.println("</tr>");
         }
         out.println("</table><br/>");
         out.println("</div></body></head></html>");

         rs.close();
         conn.commit();

      } catch (SQLException e) {
         // Handle errors
         e.printStackTrace();
      }  

   }

   private void createSQLStatements()
   {
      sqlCheckFilled = "SELECT is_filled \n" + 
         "FROM prescription\n" + 
         "WHERE prescription_id = ?;";
      
      sqlGetRefills = "SELECT refill - number_of_refills AS remaining_refills  \n" + 
         "   FROM prescription\n" + 
         "    WHERE prescription_id = ?;";

      usql = 
         "UPDATE prescription\n" + 
            "SET is_filled = 1,\n" + 
            "number_of_refills = number_of_refills + 1\n" + 
            "WHERE patient_id = ?\n" + 
            "AND prescription_id = ?;";

      sql = "SELECT patient_id, prescription_id, trade_name, " + 
         "    is_filled " + 
         "FROM patient JOIN prescription USING (patient_id) " + 
         "    JOIN drug USING (drug_id) " +
         "WHERE patient_id = ?" + 
         "AND prescription_id = ?;";
   }
   
}

