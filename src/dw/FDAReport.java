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

@WebServlet("/FDAReport")
public class FDAReport extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// database URL
	static final String DB_URL = "jdbc:mysql://localhost/pharm";

	// Database credentials.  COMPLETE THE FOLLOWING STATEMENTS
	static final String USER = "root";
	static final String PASS = "@DevDan2020";

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		String sql =  "SELECT dr.first_name, dr.last_name, generic_name,\r\n" + 
				"	  SUM(quantity) AS Quantity_Prescribed\r\n" + 
				"FROM doctor dr JOIN prescription pres\r\n" + 
				"	ON dr.dr_id = pres.dr_id\r\n" + 
				"JOIN drug dg\r\n" + 
				"	ON pres.drug_id = dg.drug_id\r\n" + 
				"WHERE pres.date BETWEEN DATE_SUB(curdate(), INTERVAL 6 MONTH) AND \r\n" + 
				"curdate()\r\n" + 
				"GROUP BY first_name, last_name, generic_name\r\n" + 
				"ORDER BY first_name, last_name, generic_name;";

		response.setContentType("text/html"); // Set response content type
		PrintWriter out = response.getWriter();

		//String pharmacy_id = request.getParameter("pharmacy_id");

	
			try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {

				//Prepare Select
				PreparedStatement pstmt = conn.prepareStatement(sql);
				//pstmt.setString(1, pharmacy_id);
				ResultSet rs = pstmt.executeQuery();

				//Start html output
				out.println("<!DOCTYPE HTML><html><body>");
				out.println("	<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\""
						+ " integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">");
				out.println("	<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
				out.println("<link rel=\"stylesheet\" href=\"search.css\">");

				//out.println("<p align = \"center\"> Pharmacy: " + pharmacy_id + "</p>\n");


				//Table Headers
				out.println("<table align =\"center\">");
				out.println("<tr>");	
				//out.println("<th>Date</th>");
				out.println("<th>First Name</th>");
				out.println("<th>Last Name</th>");
				out.println("<th>Drug Name (Generic)</th>");
				out.println("<th>Quantity Prescripbed</th>");

				//creates rows with data for each row from the result set
				while(rs.next()) {
					out.println("<tr>");
					//out.println("<td>" + rs.getString("date") + "</td>");
					out.println("<td align =\"left\">" + rs.getString("first_name") + "</td>");
					out.println("<td align =\"left\">" + rs.getString("last_name") + "</td>");
					out.println("<td align =\"center\">" + rs.getString("generic_name") + "</td>");
					out.println("<td align =\"center\">" + rs.getString("Quantity_Prescribed") + "</td>");
					out.println("</tr>");
				}

				rs.close();
				out.println("</table>");
				out.println("</body></html>");
		


			} catch (SQLException e) {
				// Handle errors

				e.printStackTrace();
			}  
		
	}

}
