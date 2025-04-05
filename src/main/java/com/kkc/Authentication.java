package com.kkc;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/authentication")
public class Authentication extends HttpServlet {
	
	public static final String PASSWORD = "ABcDefgHijk";
	
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("authentication");
		String password = request.getParameter("password");
		boolean bool = false;
		if(password == null)
			response.getWriter().write(getJSON(bool));
		
		if(password.equals(PASSWORD))
			bool = true;
		
		response.getWriter().write(getJSON(bool));
	}
	
	 private String getJSON(boolean bool) {
			StringBuffer sb = new StringBuffer("");
			sb.append("{ \"isSuccess\": \"" + bool + "\" }");
			return sb.toString();
	}
}
