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

   // database URL
   static final String DB_URL = "jdbc:mysql://localhost/pharm";

   // Database credentials.  COMPLETE THE FOLLOWING STATEMENTS
   static final String USER = "root";
   static final String PASS = "Fbcjapan1!";

   protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
      

      /**
      String usql = "START TRANSACTION; " + 
         "UPDATE prescription " + 
         "SET is_filled = 1, " + 
         "number_of_refills = (number_of_refills + 1) " + 
         "WHERE patient_id = ? " + 
         "AND prescription_id = ?; " +
         "COMMIT";
         */
      
      String usql = 
         "UPDATE prescription " + 
         "SET is_filled = 1, " + 
         "WHERE patient_id = ? " + 
         "AND prescription_id = ?; ";
      
      String sql = "SELECT patient_id, prescription_id, trade_name, " + 
         "    is_filled " + 
         "FROM patient JOIN prescription USING (patient_id) " + 
         "    JOIN drug USING (drug_id) " + 
         "WHERE patient_id = ?" +
         "AND prescription_id = ?";
      
      String prescriptionID = request.getParameter("pres_id");
      String patientID = request.getParameter("patient_id");
      
      System.out.println("prescription ID: " + prescriptionID);

      response.setContentType("text/html"); // Set response content type
      PrintWriter out = response.getWriter();

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
         

         out.println("<!DOCTYPE HTML><html><body>");
         out.println("  <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\""
            + " integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">");
         out.println("  <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
         out.println("<link rel=\"stylesheet\" href=\"search.css\">");
         
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
         out.println("</table>");
         out.println("</body></html>");

         rs.close();
         conn.commit();
         
      } catch (SQLException e) {
         // Handle errors
         e.printStackTrace();
      }  
   }

}

