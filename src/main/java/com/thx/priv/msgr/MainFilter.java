package com.thx.priv.msgr;

import com.thx.priv.data.Message;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.net.*;

@WebFilter(filterName = "MainFilter", value = "/*")
public class MainFilter implements Filter {

    private static Connection conn = null;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // proceed to next step
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        // init connection and place on context
        try {
            // Class.forName ("org.h2.Driver");
            // conn = DriverManager.getConnection ("jdbc:h2:file:~msgsdb;CIPHER=AES", "msgs","filepwd userpwd");
            
            Class.forName ("org.postgresql.Driver");
            URI dbUri = new URI(System.getenv("DATABASE_URL"));

            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

            conn = DriverManager.getConnection(dbUrl, username, password);
        
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS Message " + 
                "(id int PRIMARY KEY, decoded boolean, message text, row1 int, col1 int, row2 int, col2 int)";
            stmt.executeUpdate(sql);

            stmt = conn.createStatement();
            // read all current msgs
            ArrayList<Message> messages = new ArrayList<Message>();

            ResultSet rs = stmt.executeQuery("SELECT id, decoded, message, row1, col1, row2, col2 FROM Message");
            while(rs.next()) {
                int[] coord = { rs.getInt("row1"), rs.getInt("col1"), rs.getInt("row2"), rs.getInt("col2") };
                Message msg = new Message(
                    rs.getInt("id"), 
                    rs.getBoolean("decoded"), 
                    rs.getString("message"), 
                    coord
                );
                messages.add(msg);
            }

            filterConfig.getServletContext().setAttribute("messages", messages);
            filterConfig.getServletContext().setAttribute("conn", conn);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
