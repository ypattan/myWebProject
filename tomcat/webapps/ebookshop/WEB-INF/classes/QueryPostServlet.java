// Saved as "ebookshop\WEB-INF\classes\QueryServlet.java"
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/querypost")
// Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 up)

public class QueryPostServlet extends HttpServlet {
	// The doGet() runs once per HTTP GET request to this servlet
	@Override
	public void doPost (HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		doGet(request, response); //Re-direct POST request to doGet()
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		//Set the MIME type for the response message
		response.setContentType("text/html");
		//Get an output writer to write the reponse message into the network socket
		PrintWriter out = response.getWriter();		
		Connection conn = null;
		Statement stmt = null;

		
		
		try {
			
			// load and register JDBC driver for MySQL
			//Step 1: create a database "Connection" object
			// out.println("create database Connection object");
			conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrival=true&useSSL=false&serverTimezone=UTC", "myuser", "xxxx");
			
			//Step 2:Create a "Statement" object inside the "Connection"
			stmt = conn.createStatement();
			
			//Step 3: Execute a SQL Select query
			String sqlStr = "SELECT * FROM books WHERE author = " + "'" + request.getParameter("author") + "'" + " AND qty > 0 ORDER BY author ASC, title ASC";
			
			//print an HTML page as output of query
			out.println("<html><head><title>Query Results</title></head.<body>");
			out.println("<h2>Thank you for this query.</h2>");
			//Echo for debugging
			out.println("<p>You query is: " + sqlStr + "</p>");
			//send the query to the server
			ResultSet rset = stmt.executeQuery(sqlStr);
			
			//Step 4: Process the query result
			int count = 0;
			while(rset.next()) {
				//Print a paragraph <p>... </p> for each row
				out.println("<p>" + rset.getString("author") + ", " + rset.getString("title") + ", $" + rset.getDouble("price") + "</p>");
				++count;
			}
			
			out.println("<p>====" + count + " records found =====</p>");
			out.println("</body></html>");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} 
		finally {
			out.close();
			try {
				//Step 5: Close the Statement and Connection
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}