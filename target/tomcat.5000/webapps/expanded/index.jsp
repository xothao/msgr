<%@ page import="java.util.ArrayList" %>
<%@ page import="com.thx.priv.data.Message" %>
<%@ page import="javax.imageio.ImageIO" %>
<%@ page import="javax.xml.bind.DatatypeConverter" %>
<%@ page import="java.awt.image.BufferedImage" %>
<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="com.google.zxing.qrcode.QRCodeWriter" %>
<%@ page import="com.google.zxing.common.BitMatrix" %>
<%@ page import="com.google.zxing.BarcodeFormat" %>
<%@ page import="com.google.zxing.client.j2se.MatrixToImageWriter" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Nothing to see here</title>
</head>
<body>
<br/>
<form action="/messages" method="post">
    <textarea id="message" name="message" cols="60" rows="6"></textarea>
    <br/>
    <input type="submit" value="Post">
</form>
<br/>
<form action="/messages" method="post">
<table>
<%
    ArrayList<Message> messages = (ArrayList<Message>) request.getServletContext().getAttribute("messages");
    if(messages != null) {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        boolean encodedMsgs = false;
        for (Message msg: messages) {
%>
    <tr>
        <%
            if(msg.isDecoded()) {
                BitMatrix bitMatrix = barcodeWriter.encode(msg.getCipherText(), BarcodeFormat.QR_CODE, 75, 75);
                BufferedImage bImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bImage, "png", baos);
                baos.flush();
                byte[] imageInByteArray = baos.toByteArray();
                baos.close();
                String b64 = DatatypeConverter.printBase64Binary(imageInByteArray);
        %>
        <td>
            <img src="data:image/png;base64, <%=b64%>" alt="png not found" />
        </td>
        <td><button name="delete" type="submit" value="<%=msg.getId()%>">Delete</button></td>
        <%
            } else {
                encodedMsgs = true;
        %>
        <td><%=msg.getCoordinates()[0] + 1%>:<%=msg.getCoordinates()[1] + 1%></td>
        <td><%=msg.getCoordinates()[2] + 1%>:<%=msg.getCoordinates()[3] + 1%></td>
        <td><button name="msgid" type="submit" value="<%=msg.getId()%>">Decode</button></td>
        <%
            }
        %>
    </tr>
<%
        }
        if(encodedMsgs) {
%>
</table>
<br/>
<input type="password" id="password" name="password">
<%
        }
    }
%>
</form>
</body>
</html>