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

@WebServlet("/PatientRefill")
public class PatientRefill extends HttpServlet {
   private static final long serialVersionUID = 1L;

   // database URL
   static final String DB_URL = "jdbc:mysql://localhost/pharm";

   // Database credentials.  COMPLETE THE FOLLOWING STATEMENTS
   static final String USER = "root";
   static final String PASS = "Fbcjapan1!";

   protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {


      String sqlGetPresCost = "SELECT prescription_id, price, is_filled " + 
         "FROM prescription pres JOIN drug_price dp " + 
         "WHERE pres.pharmacy_id = dp.pharmacy_id " + 
         "AND pres.drug_id = dp.drug_id " + 
         "AND prescription_id = ?";


      response.setContentType("text/html"); // Set response content type
      PrintWriter out = response.getWriter();

      try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
         conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
         conn.setAutoCommit(false); 

         String prescriptionID = request.getParameter("pres_id");
         String patientID = request.getParameter("patient_id");

         // prepare select statement to get cost
         PreparedStatement pstmt =  conn.prepareStatement(sqlGetPresCost);
         // SET VALUES FOR PARAMETER MARKERS 
         pstmt.setString(1, prescriptionID);
         ResultSet rs = pstmt.executeQuery();
         

         out.println("<!DOCTYPE HTML><html><body>");
         out.println("<table align =\"center\">");
         
         out.println("<tr><th align = \"left\">Rx ID</th> ");
         out.println("<tr><th align = \"center\">Price</th></tr>");
         out.println("<tr><th align = \"right\">Rx Filled</th></tr>");
        
         while (rs.next()) {
            out.println("<tr>");
            out.println("<td align = \"left\">" + rs.getString(1) + "</td>");
            out.println("<td align = \"center\">" + rs.getString(2) + "</td>");
            
            // Check if this prescription is filled or not
            if(rs.getString(3).equals("1")) {
               out.println("<td align = \"right\">" + "Yes" + "</td>");
            } else {
               out.println("<td align = \"right\">" + "No" + "</td>");

            }
            out.println("</tr>");
         }

         rs.close();
         out.println("</table>");
         
         // HTML code for the "fill" prescription button
         out.println("<form action = \"FillPrescription\" method = \"POST\">");
         out.println("<h5 style=\"width:40%; text-align:center; align:center; display:inline-block;\">Prescription ID</h5> <input type=\"text\" name=\"pres_id\" /> <br />\n");
         out.println("<h5 style=\"width:40%; text-align:center; align:center; display:inline-block;\">Patient ID</h5> <input type=\"text\" name=\"patient_id\" /> <br />");
         out.println("<button align=\"right\" type=\"submit\" class=\"btn btn-primary\" style=\"margin:2em 0 1.8em 0; width:50%;\" >Fill Prescription</button>");
         out.println("</form>");
         out.println("</body></html>");
         
         conn.commit();
         
      } catch (SQLException e) {
         // Handle errors
         e.printStackTrace();
      }  
   }

}
