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
      
      String prescriptionID = request.getParameter("pres_id").trim();
      String patientID = request.getParameter("patient_id").trim();
      
      if(prescriptionID != "" && patientID != "") {
         try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            conn.setAutoCommit(false); 


            // prepare select statement to get cost
            PreparedStatement pstmt =  conn.prepareStatement(sqlGetPresCost);
            // SET VALUES FOR PARAMETER MARKERS 
            pstmt.setString(1, prescriptionID);
            ResultSet rs = pstmt.executeQuery();


            // Create the html page to display result set 
            out.println("<!DOCTYPE HTML><html><body>");
            out.println("  <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\""
                  + " integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">");
            out.println("  <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
            out.println("<link rel=\"stylesheet\" href=\"search.css\">");


            // Create table headers 
            out.println("<table align =\"center\">");
            out.println("<tr>"); 
            out.println("<th>Rx ID</th>");
            out.println("<th>Price</th>");
            out.println("<th>Rx Filled</th></tr>");

           
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
            out.println("<button align=\"center\" type=\"submit\" class=\"btn btn-primary\" style=\"margin:2em 0 1.8em 0; width:50%;\" >Fill Prescription</button>");
            out.println("<input type=\"hidden\" name=\"pres_id\" value=" + prescriptionID + ">");
            out.println("<input type=\"hidden\" name=\"patient_id\" value=" + patientID + ">");
            
            out.println("</form>");
            out.println("</body></html>");
            
            conn.commit();
            
         } catch (SQLException e) {
            // Handle errors
            e.printStackTrace();
         }  
      } 
      else {
         out.println("<!DOCTYPE HTML><html><body>");
         out.println("  <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\""
               + " integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">");
         out.println("  <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
         out.println("<link rel=\"stylesheet\" href=\"search.css\">");
         out.println("<p> Invalid Entry </p>");
      }

      
   }

}
