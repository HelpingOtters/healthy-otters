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

@WebServlet("/RxCreate")
public class RxCreate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	// database URL
	static final String DB_URL = "jdbc:mysql://localhost/pharm";

	// Database credentials.  COMPLETE THE FOLLOWING STATEMENTS
	static final String USER = "root";
	static final String PASS = "Bandit0!";

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
		String doctor= "SELECT dr_id WHERE first_name = ? AND last_name = ?";
		
		String patient= "SELECT patient_id WHERE first_name = ? AND last_name = ?\n";
		
		String pharm_id = "SELECT pharmacy_id WHERE name LIKE “%?%”";
		
		String usql = "INSERT INTO prescription (refill, date, quantity, drug_id, patient_id, dr_id, pharmacy_id)"
			+ "VALUES (?, date, ?, ?, patient_id, dr_id, pharmacy_id)";

		response.setContentType("text/html"); // Set response content type
		PrintWriter out = response.getWriter();

		try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
			conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			conn.setAutoCommit(false); 
			
			// get data from form and convert to integer values  
      // COMPLETE THE STATEMENTS with input tag NAMES
			String dr_firstname = request.getParameter("dr.first_name");
			String dr_lastname = request.getParameter("dr.last_name");
			String pa_firstname = request.getParameter("pa.first_name");
			String pa_lastname = request.getParameter("pa.last_name");
			String pharmacy = request.getParameter("pharmacy");
			int drug_id = Integer.parseInt(request.getParameter("drug_id"));
			int refill = Integer.parseInt(request.getParameter("refill"));
			int quantity = Integer.parseInt(request.getParameter("quantity"));

			// prepare usql select
			PreparedStatement pstmt =  conn.prepareStatement(usql);
      // SET VALUES FOR PARAMETER MARKERS (usql)
			pstmt.setInt(1, refill);
			pstmt.setInt(2, quantity);
			pstmt.setInt(3, drug_id);
			pstmt.setString(4, pharmacy);
			int row_count = pstmt.executeUpdate();
			
		// SET VALUES FOR PARAMETER MARKERS (sql)
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(5, dr_firstname);
			pstmt.setString(6, dr_lastname);
			pstmt.setString(7, pa_firstname);
			pstmt.setString(8, pa_lastname);
			ResultSet rs = pstmt.executeQuery();

			out.println("<!DOCTYPE HTML><html><body>");
			out.println("<p>Rows updated = " + row_count + "</p>");
			out.println("<table> <tr><th>Doctor First Name</th> <tr><th>Doctor Last Name</th> <th>Patient First Name</th> "
				+ "<th>Patient Last Name</th> <th>Drug ID</th> <th>Number of Refills</th>"
				+ "<th>Dose Quantity</th> <th>Pharmacy Name</th></tr>");
			while (rs.next()) {
				out.println("<tr>");
				out.println("<td>" + rs.getString(5) + "</td>");
				out.println("<td>" + rs.getString(6) + "</td>");
				out.println("<td>" + rs.getString(7) + "</td>");
				out.println("<td>" + rs.getString(8) + "</td>");
				out.println("<td>" + rs.getInt(3) + "</td>");
				out.println("<td>" + rs.getInt(1) + "</td>");
				out.println("<td>" + rs.getInt(2) + "</td>");
//				out.println("<td>" + rs.getString(4) + "</td>");
				out.println("</tr>");
			}
			
			rs.close();
			out.println("</table>");
			out.println("</body></html>");
			conn.commit();
		} catch (SQLException e) {
			// Handle errors
			e.printStackTrace();
		}  
	}

}
