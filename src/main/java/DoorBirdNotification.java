import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.DataOutputStream;

@WebServlet(urlPatterns = {"/*"}, loadOnStartup = 1)
public class DoorBirdNotification extends HttpServlet {
	
	/**
	 * The name of the item that should be notified (provided as init parameter)
	 */
	private String item;
	
	/**
	 * The user name to be used for the authentication to access openHAB
	 */
	private String user;
	
	/**
	 * The password to be used for the authentication to access openHAB
	 */
	private String password;
	
	/**
	 * The IP address of openHAB
	 */
	private String ipAddress;
	
	/**
	 * The port of openHAB
	 */
	private String port;
	
	/**
	 * Reads the init parameters
	 */
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		this.item = servletConfig.getInitParameter("item");
		
		ServletContext context = getServletContext();
		this.user = context.getInitParameter("user");
		this.password = context.getInitParameter("password");
		this.ipAddress = context.getInitParameter("ipAddress");
		this.port = context.getInitParameter("port");
	}
	
	
	@Override 
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.getOutputStream().print(notifyItem() ? "OK" : "NOK");
	}
	
	/**
	 * Notfies the corresponding openHAB item via the REST API
	 */
	private boolean notifyItem() {
		try {
			URL url = new URL("http://" + this.user + ":" + this.password + "@" + this.ipAddress + ":"
					+ this.port + "/rest/items/" + this.item);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "text/plain");
			conn.setRequestProperty("Content-Length", "2");
			conn.setUseCaches(false);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes("ON");
			out.flush();
			out.close();
			
			return conn.getResponseCode() == 201;
			
		} catch (Exception e) {
			return false;
		}
	}
}
