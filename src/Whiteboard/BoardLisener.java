package Whiteboard;

import Message.Message;
import Message.canvasShape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/9/27 22:33
 */
public class BoardLisener implements MouseListener, ActionListener, MouseMotionListener {
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

    public void setGraph(Graphics2D g) {
        this.graph = g;
    }

    public void setGraphSave(Graphics2D g) {this.graphSave = g; }

    public void setColor(Color c) {
        this.currentColor = c;
    }

    public void setPenType(String t) {
        this.penType = t;
    }

    public void setText(String t) { this.text = t; }

    public void setOutPutStream(OutputStream os) throws IOException {  this.os = os; this.oos = new ObjectOutputStream(os); }

    @Override
    public void actionPerformed(ActionEvent e) {
        String source = e.getActionCommand();

        // if the click is from colored button,
        // set the color of the current color
        if (source.equals("")) {
            setColor(((JButton)e.getSource()).getBackground());
        }
        else {
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
        if(penType.equals("Line")) {
            graph.drawLine(x2,y2,x,y);
            graphSave.drawLine(x2,y2,x,y);
            sendShape("Line",x2,x,y2,y);
        }

        // draw rectangle
        if(penType.equals("Rectangle")) {
            graph.drawRect(Math.min(x,x2),Math.min(y,y2),Math.abs(x2-x),Math.abs(y2-y));
            graphSave.drawRect(Math.min(x,x2),Math.min(y,y2),Math.abs(x2-x),Math.abs(y2-y));
        }

        // draw circle
        if (penType.equals("Circle")) {
            graph.drawOval(Math.min(x,x2),Math.min(y,y2), Math.abs(x-x2),Math.abs(y-y2));
            graphSave.drawOval(Math.min(x,x2),Math.min(y,y2), Math.abs(x-x2),Math.abs(y-y2));
        }

        // draw triangle
        if (penType.equals("Triangle")) {
            graph.drawPolygon(new int[]{Math.min(x,x2),Math.min(x,x2)+Math.abs(x-x2)/2,Math.min(x,x2)+Math.abs(x-x2)},new int[]{Math.min(y,y2)+Math.abs(y-y2),Math.min(y,y2),Math.min(y,y2)+Math.abs(y-y2)},3);
            graphSave.drawPolygon(new int[]{Math.min(x,x2),Math.min(x,x2)+Math.abs(x-x2)/2,Math.min(x,x2)+Math.abs(x-x2)},new int[]{Math.min(y,y2)+Math.abs(y-y2),Math.min(y,y2),Math.min(y,y2)+Math.abs(y-y2)},3);
        }

        // insert the text
        if (penType.equals("Text")) {
            graph.setColor(currentColor);
            graphSave.setColor(currentColor);
            graph.drawString(this.text, x2, y2);
            graphSave.drawString(this.text, x2, y2);
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
        x1=e.getX();
        y1=e.getY();
        graph.setColor(this.currentColor);
        graphSave.setColor(this.currentColor);

        // if it is pen mode now, drawing line
        if (penType.equals("Pen")) {

            graph.drawLine(x, y, x1, y1);
            graphSave.drawLine(x, y, x1, y1);
//            sendShape("Pen",x,x1,y,y1);
            x=x1;
            y=y1;
        }

        // eraser operation
        if (penType.equals("Eraser")) {
            // set the eraser color as white
            // to cover paints
            graph.setColor(Color.WHITE);
            graphSave.setColor(Color.WHITE);
            graph.setStroke(new BasicStroke(20));
            graphSave.setStroke(new BasicStroke(20));
            graph.drawLine(x,y,x1,y1);
            graphSave.drawLine(x,y,x1,y1);

            // recover the normal stroke and color
            graph.setStroke(new BasicStroke(3));
            graphSave.setStroke(new BasicStroke(3));
            graph.setColor(this.currentColor);
            graphSave.setColor(this.currentColor);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void sendShape(String message, int x,int x1, int y, int y1)  {
        try {
            Message m = new canvasShape(message, "1",x,x1,y,y1);
            oos.writeObject(m);
            oos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
