package Whiteboard;

import Message.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/9/27 22:33
 */
public class BoardListener implements MouseListener, ActionListener, MouseMotionListener {
    // the index for mouse: x,y(the pressed pint), x1,y1(drag point), x2,y2(release point)
    int x, y, x1, y1, x2, y2;
    String penType = "Pen";
    Graphics2D graph;
    Graphics2D graphSave; // this gtaphics2D is used to save image file
    Color currentColor;
    String text;
    OutputStream os;
    ObjectOutputStream oos;
    String senderID;
    boolean approved = false;

    boolean authorized = false;

    public void setGraph(Graphics2D g) {
        this.graph = g;
    }

    public void setApproved() {
        this.approved = true;
    }

    public void setGraphSave(Graphics2D g) {
        this.graphSave = g;
    }

    public void setColor(Color c) {
        this.currentColor = c;
    }

    public void setPenType(String t) {
        this.penType = t;
    }

    public void setSenderID(String i) { this.senderID = i; }

    public void setAuthorized() { this.authorized = true; this.approved = true;}

    public void setText(String t) {
        this.text = t;
    }

    public void setOutPutStream(OutputStream os) throws IOException {
        this.os = os;
        this.oos = new ObjectOutputStream(os);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String source = e.getActionCommand();

        // if the click is from colored button,
        // set the color of the current color
        if (source.equals("")) {
            setColor(((JButton) e.getSource()).getBackground());
        } else {
            this.setPenType(e.getActionCommand());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // record the current index
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        x2 = e.getX();
        y2 = e.getY();

        // draw the straight line
        if (penType.equals("Line")) {
            drawStraightLine(x2, y2, x, y, currentColor);
            sendShape("Line", x2, x, y2, y);
        }

        // draw rectangle
        if (penType.equals("Rectangle")) {
            drawRectangle(x, x2, y, y2, currentColor);
            sendShape("Rectangle",x,x2,y,y2);
        }

        // draw circle
        if (penType.equals("Circle")) {
            drawCircle(x, x2, y, y2, currentColor);
            sendShape("Circle", x, x2, y,y2);
        }

        // draw triangle
        if (penType.equals("Triangle")) {
            drawTriangle(x, x2, y, y2, currentColor);
            sendShape("Triangle", x, x2, y, y2);
        }

        // insert the text
        if (penType.equals("Text")) {
            drawText(x2,y2, this.text, currentColor);
            sendText("Text",x2,y2,text);
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        x1 = e.getX();
        y1 = e.getY();
        graph.setColor(this.currentColor);
        graphSave.setColor(this.currentColor);

        // if it is pen mode now, drawing line
        if (penType.equals("Pen")) {

            drawPen(x, y, x1, y1, currentColor);
            sendShape("Pen", x, x1, y, y1);
            x = x1;
            y = y1;
        }

        // eraser operation
        if (penType.equals("Eraser")) {

            drawEraser(x, y, x1, y1);
            sendShape("Eraser", x, x1, y, y1);

            x = x1;
            y = y1;


        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    // the method is used to send shapes(triangle, rectangle, circle, pen, line)
    public void sendShape(String message, int x, int x1, int y, int y1) {
        try {
            Message m = new normalShape(message, senderID, x, x1, y, y1, currentColor);
            oos.writeObject(m);
            oos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // the method is used to send different text shape
    public void sendText(String message, int x2, int y2, String text) {
        try {
            Message m = new textShape(message, senderID, x2, y2, text, currentColor);
            oos.writeObject(m);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // the method is for manager to send reply to server for new coming client
    public void sendReply(int socketNum, boolean agree) {
        try {
            Message m = new JoinReply("reply", senderID,socketNum, agree);
            oos.writeObject(m);
            oos.flush();
            System.out.println("send reply to server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // for manager user to send kick request to server
    public void sendKick(String userID) throws IOException {
        KickRequest kick = new KickRequest("kick", senderID, userID);
        oos.writeObject(kick);
        oos.flush();
    }

    // send hello message to server
    public void sendHello() {
        try {
            Message m = new Message("Hello", this.senderID);
            oos.writeObject(m);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // used by client to send chat message
    public void sendChat(String chatContent) {
        try {
            ChatRequest c = new ChatRequest("chat", this.senderID, chatContent);
            oos.writeObject(c);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // draw straight line method
    public void drawStraightLine(int x2, int y2, int x, int y, Color color) {
        graph.setColor(color);
        graphSave.setColor(color);

        graph.drawLine(x2, y2, x, y);
        graphSave.drawLine(x2, y2, x, y);

        // restore the color
        graph.setColor(currentColor);
        graphSave.setColor(currentColor);
    }

    // draw pen method
    public void drawPen(int x, int y, int x1, int y1, Color color) {
        graph.setColor(color);
        graphSave.setColor(color);

        graph.drawLine(x, y, x1, y1);
        graphSave.drawLine(x, y, x1, y1);

        // restore the previous color and stroke
        graph.setColor(currentColor);
        graphSave.setColor(currentColor);

        graph.setStroke(new BasicStroke(3));
        graphSave.setStroke(new BasicStroke(3));
    }


    // draw triangle method
    public void drawTriangle(int x, int x2, int y, int y2, Color color) {
        graph.setColor(color);
        graphSave.setColor(color);

        // algorithm for drawing a triangle, overall idea is: draw a rectangle, and take a triangle from it
        // based on its length
        graph.drawPolygon(new int[]{Math.min(x, x2), Math.min(x, x2) + Math.abs(x - x2) / 2, Math.min(x, x2) + Math.abs(x - x2)}, new int[]{Math.min(y, y2) + Math.abs(y - y2), Math.min(y, y2), Math.min(y, y2) + Math.abs(y - y2)}, 3);
        graphSave.drawPolygon(new int[]{Math.min(x, x2), Math.min(x, x2) + Math.abs(x - x2) / 2, Math.min(x, x2) + Math.abs(x - x2)}, new int[]{Math.min(y, y2) + Math.abs(y - y2), Math.min(y, y2), Math.min(y, y2) + Math.abs(y - y2)}, 3);

        graph.setColor(currentColor);
        graphSave.setColor(currentColor);
    }

    // draw circle method
    public void drawCircle(int x, int x2, int y, int y2, Color color) {
        graph.setColor(color);
        graphSave.setColor(color);

        graph.drawOval(Math.min(x, x2), Math.min(y, y2), Math.abs(x - x2), Math.abs(y - y2));
        graphSave.drawOval(Math.min(x, x2), Math.min(y, y2), Math.abs(x - x2), Math.abs(y - y2));

        graph.setColor(currentColor);
        graphSave.setColor(currentColor);
    }

    // method that performs eraser function
    public void drawEraser(int x, int y, int x1, int y1) {

        // set the eraser color as white
        // to cover paints
        graph.setColor(Color.WHITE);
        graphSave.setColor(Color.WHITE);
        graph.setStroke(new BasicStroke(20));
        graphSave.setStroke(new BasicStroke(20));

        graph.drawLine(x, y, x1, y1);
        graphSave.drawLine(x, y, x1, y1);

        // recover the normal stroke and color
        graph.setStroke(new BasicStroke(3));
        graphSave.setStroke(new BasicStroke(3));
        graph.setColor(this.currentColor);
        graphSave.setColor(this.currentColor);
    }

    // draw rectangle method
    public void drawRectangle(int x, int x2, int y, int y2, Color color) {
        graph.setColor(color);
        graphSave.setColor(color);

        graph.drawRect(Math.min(x, x2), Math.min(y, y2), Math.abs(x2 - x), Math.abs(y2 - y));
        graphSave.drawRect(Math.min(x, x2), Math.min(y, y2), Math.abs(x2 - x), Math.abs(y2 - y));

        graph.setColor(currentColor);
        graphSave.setColor(currentColor);
    }

    // draw text method
    public void drawText(int x2, int y2, String text, Color color) {
        graph.setColor(color);
        graphSave.setColor(color);

        graph.drawString(text, x2, y2);
        graphSave.drawString(text, x2, y2);

        graph.setColor(currentColor);
        graphSave.setColor(currentColor);
    }
}