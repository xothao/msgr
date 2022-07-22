package com.thx.priv.msgr;

import com.thx.priv.data.Message;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

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
            Class.forName ("org.h2.Driver");
            conn = DriverManager.getConnection ("jdbc:h2:~/msgs;CIPHER=AES", "msgs","5888525 2097");

            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS Message (id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, message JAVA_OBJECT)";
            stmt.executeUpdate(sql);

            stmt = conn.createStatement();
            // read all current msgs
            var messages = new ArrayList<Message>();

            ResultSet rs = stmt.executeQuery("SELECT id, message FROM Message");
            while(rs.next()) {
                Message msg = (Message) rs.getObject("message");
                msg.setId(rs.getInt("id"));
                messages.add(msg);
            }

            filterConfig.getServletContext().setAttribute("messages", messages);
            filterConfig.getServletContext().setAttribute("conn", conn);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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
