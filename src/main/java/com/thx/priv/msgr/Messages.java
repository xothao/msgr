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

                String insertQuery = "INSERT INTO Message VALUES(decoded=?, message=?, row1=?, col1=?, row2=?, col2=?)";
                PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
                // decoded boolean NOT NULL, message text, row1 int, col1 int, row2 int, col2 int
                preparedStatement.setBoolean(1, false);
                preparedStatement.setString(2, cipherText);
                preparedStatement.setInt(3, coordinates[0]);
                preparedStatement.setInt(4, coordinates[1]);
                preparedStatement.setInt(5, coordinates[2]);
                preparedStatement.setInt(6, coordinates[3]);
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
                String sql = "SELECT id, decoded, message, row1, col1, row2, col2 FROM Message WHERE id = " + messageId;
                ResultSet rs = stmt.executeQuery(sql);
                rs.next();
                int[] coord = { rs.getInt("row1"), rs.getInt("col1"), rs.getInt("row2"), rs.getInt("col2") };
                Message msg = new Message(
                    rs.getInt("id"), 
                    rs.getBoolean("decoded"), 
                    rs.getString("message"), 
                    coord
                );

                String decryptedText = new Encrypt().decrypt(msg.getCipherText(), passwordParameter);
                if(decryptedText != null && !decryptedText.isEmpty()) {
                    msg.setCipherText(decryptedText);
                    msg.setDecoded(true);

                    String updateQuery = "UPDATE Message SET decoded = true, message = ? WHERE id = " + messageId;
                    PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);
                    preparedStatement.setString(1, decryptedText);
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

            ResultSet rs = stmt.executeQuery("SELECT id, decoded, message, coord FROM Message");
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

            request.getServletContext().setAttribute("messages", messages);

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestDispatcher req = request.getRequestDispatcher("index.jsp");
        req.forward(request, response);

    }

}
