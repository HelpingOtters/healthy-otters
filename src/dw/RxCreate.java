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

		String isql = "INSERT INTO prescription (refill, quantity, drug_id, patient_id, dr_id, pharmacy_id)"
			+ "VALUES (?, ?, ?, ?, ?, ?);";
		
		String sql = "SELECT dr.first_name, dr.last_name, pa.first_name, pa.last_name, ph.name, pres.drug_id\n" + 
			"FROM prescription pres JOIN drug dg ON pres.drug_id=dg.drug_id\n" + 
			"JOIN patient pa ON pres.patient_id = pa.patient_id\n" + 
			"JOIN doctor dr ON pres.dr_id = dr.dr_id\n" + 
			"JOIN pharmacy ph ON pres.pharmacy_id = ph.pharmacy_id\n" + 
			"WHERE dr.first_name = ?\n" + 
			"AND dr.last_name = ?\n" + 
			"AND pa.first_name = ?\n" + 
			"AND pa.last_name = ? \n" + 
			"AND pres.drug_id = ?\n" + 
			"AND refill = ? \n" + 
			"AND quantity = ?\n" + 
			"AND pres.pharmacy_id = ?\n" + 
			"AND pres.patient_id = ? \n" + 
			"AND pres.dr_id = ?;";
		

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
			int pharm_id = Integer.parseInt(request.getParameter("pharm_id"));
			int drug_id = Integer.parseInt(request.getParameter("drug_id"));
			int refill = Integer.parseInt(request.getParameter("refill"));
			int quantity = Integer.parseInt(request.getParameter("quantity"));
			int patient_id = Integer.parseInt(request.getParameter("patient_id"));
			int dr_id = Integer.parseInt(request.getParameter("dr_id"));

			// prepare isql select
			PreparedStatement pstmt =  conn.prepareStatement(isql);
      // SET VALUES FOR PARAMETER MARKERS (isql)
			pstmt.setInt(1, refill);
			pstmt.setInt(2, quantity);
			pstmt.setInt(3, drug_id);
			pstmt.setInt(4, patient_id);
			pstmt.setInt(5, dr_id);
			pstmt.setInt(6, pharm_id);
			int row_count = pstmt.executeUpdate();
			
		// SET VALUES FOR PARAMETER MARKERS (sql)
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dr_firstname);
			pstmt.setString(2, dr_lastname);
			pstmt.setString(3, pa_firstname);
			pstmt.setString(4, pa_lastname);
			pstmt.setInt(5, drug_id);
			pstmt.setInt(6, refill);
			pstmt.setInt(7, quantity);
			pstmt.setInt(8, pharm_id);
			pstmt.setInt(9, patient_id);
			pstmt.setInt(10, dr_id);
			ResultSet rs = pstmt.executeQuery();
		

			out.println("<!DOCTYPE HTML><head><link rel=\"stylesheet\" href=\"search.css\"><html><body><div class=\"container\" style=\"width:100%; color:white;\">");
			out.println("<p>Rows updated = " + row_count + "</p>");
			out.println("<table> <tr><th>Doctor First Name</th> <tr><th>Doctor Last Name</th> <th>Patient First Name</th> "
				+ "<th>Patient Last Name</th> <th>Drug ID</th> <th>Number of Refills</th>"
				+ "<th>Dose Quantity</th> <th>Pharmacy Name</th></tr>");
			while (rs.next()) {
				out.println("<tr>");
				out.println("<td>" + rs.getString(1) + "</td>");
				out.println("<td>" + rs.getString(2) + "</td>");
				out.println("<td>" + rs.getString(3) + "</td>");
				out.println("<td>" + rs.getString(4) + "</td>");
				out.println("<td>" + rs.getInt(5) + "</td>");
				out.println("<td>" + rs.getInt(6) + "</td>");
				out.println("<td>" + rs.getInt(7) + "</td>");
				out.println("<td>" + rs.getString(8) + "</td>");
				out.println("<td>" + rs.getInt(9) + "</td>");
				out.println("<td>" + rs.getInt(10) + "</td>");
				out.println("</tr>");
			}
			
			rs.close();
			out.println("</table>");
			out.println("</div></body></head></html>");
			conn.commit();
		} catch (SQLException e) {
			// Handle errors
			e.printStackTrace();
		}  
	}

}
