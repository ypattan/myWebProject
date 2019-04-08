// Saved as "ebookshop\WEB-INF\classes\QueryServlet.java"
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/querymv")
// Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 up)

public class QueryMultiValueServlet extends HttpServlet {
	// The doGet() runs once per HTTP GET request to this servlet
	@Override
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

			System.out.println("Get JDBC connection.");
			conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC" , "myuser", "xxxx");

			
			//Step 2:Create a "Statement" object inside the "Connection"
			System.out.println("Create statement.");
			stmt = conn.createStatement();
			
			//Step 3: Execute a SQL Select query
			String[] authors = request.getParameterValues("author"); //returns an array
			//if there are no authors selected then will not be able to use author.length()
			//need to make sure that there is atleast one selection
			if (authors == null) {
				out.println("<h2>Please go back and select an author</h2>");
				return;
			}
			//now it is ok to perform the database query
			
			String sqlStr = "SELECT * FROM books WHERE author IN (";
			sqlStr += "'" + authors[0] + "'"; //First author
	
			for (int i = 1; i < authors.length; i++) {
				//subsequent authors need a leading comma
				sqlStr += ", '" + authors[i] + "'";
			}
			sqlStr += ") AND qty > 0 ORDER BY author ASC, title ASC";

			//print an HTML page as output of query
			out.println("<html><head><title>Query Results</title></head.<body>");
			out.println("<h2>Thank you for this query.</h2>");
			//Echo for debugging
			out.println("<p>Your query is: " + sqlStr + "</p>");

			//send the query to the server
			System.out.println("Execute SQL Query = " + sqlStr);			
			ResultSet rset = stmt.executeQuery(sqlStr);
			
			//Step 4: Process the query result
			int count = 0;
			while (rset.next()) {
				//Print a paragraph <p>... </p> for each row
				System.out.println("Print result # " + count);
				out.println("<p>" + rset.getString("author") + ", " + rset.getString("title") + ", $" + rset.getDouble("price") + "</p>");
				++count;
			}
			
			out.println("<p>====" + count + " records found =====</p>");
			out.println("</body></html>");
			System.out.println("Done");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} 
		finally {
			System.out.println("Close out stream");
			out.close();
			try {
				//Step 5: Close the Statement and Connection
				if (stmt != null) {
					System.out.println("Close statement");
					stmt.close();
				}
				if (conn != null) {
					System.out.println("Close connection");
					conn.close();
				}
			} catch (SQLException sqlex) {
				System.out.println("Got exception = " + sqlex);
				sqlex.printStackTrace();
			}
		}
		System.out.println("All done");
	}
}