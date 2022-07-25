package com.thx.priv.msgr;

import com.thx.priv.data.Message;
import com.thx.priv.util.Codes;
import com.thx.priv.util.Encrypt;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

@WebServlet(name = "Messages", value = "/messages")
public class Messages extends HttpServlet {

    private static Connection conn = null;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String messageParameter = request.getParameter("message");
        String messageId = request.getParameter("msgid");
        String passwordParameter = request.getParameter("password");
        String deleteId = request.getParameter("delete");

        if(conn == null) {
            conn = (Connection) request.getServletContext().getAttribute("conn");
        }

        if(messageParameter != null && !messageParameter.isEmpty()) {
            try {
                // write the new one
                Statement stmt = conn.createStatement();

                int[] coordinates = Codes.getRandCoordinates();
                String pass1 = Codes.getWord(coordinates[0], coordinates[1]);
                String pass2 = Codes.getWord(coordinates[2], coordinates[3]);

                String cipherText = new Encrypt().encrypt(messageParameter, pass1 + " " + pass2);

                String insertQuery = "INSERT INTO Message(message) VALUES(?)";
                PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
                preparedStatement.setObject(1, new Message(cipherText, coordinates));
                preparedStatement.execute();
                conn.commit();

                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(
                (messageId != null && !messageId.isEmpty())
                &&
                (passwordParameter != null && !passwordParameter.isEmpty())
        ) {
            try {
                Statement stmt = conn.createStatement();
                String sql = "SELECT id, message FROM Message WHERE id = " + messageId;
                ResultSet rs = stmt.executeQuery(sql);
                rs.next();
                Message msg = (Message) rs.getObject("message");
                msg.setId(rs.getInt("id"));

                String decryptedText = new Encrypt().decrypt(msg.getCipherText(), passwordParameter);
                if(decryptedText != null && !decryptedText.isEmpty()) {
                    msg.setCipherText(decryptedText);
                    msg.setDecoded(true);

                    String updateQuery = "UPDATE Message SET message = ? WHERE id = " + messageId;
                    PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);
                    preparedStatement.setObject(1, msg);
                    preparedStatement.execute();
                    conn.commit();
                    preparedStatement.close();
                }

                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(deleteId != null && !deleteId.isEmpty()) {
            try {
                Statement stmt = conn.createStatement();
                String sql = "DELETE FROM Message WHERE id = " + deleteId;
                stmt.executeUpdate(sql);
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            // read all msgs each time to include the newly added one
            Statement stmt = conn.createStatement();
            ArrayList<Message> messages = new ArrayList<Message>();

            String sql = "SELECT id, message FROM Message";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                Message msg = (Message) rs.getObject("message");
                msg.setId(rs.getInt("id"));
                messages.add(msg);
            }

            request.getServletContext().setAttribute("messages", messages);

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestDispatcher req = request.getRequestDispatcher("index.jsp");
        req.forward(request, response);

    }

}
