package 画板3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class drawlistener implements MouseListener, ActionListener, MouseMotionListener {

    private int x1,x2,y1,y2,x3,y3,a,b,x4,y4;
    private Graphics gr;
    private int flag=1;
    String name;
    Shape shapeLarry[];
    int index;
    Color c=Color.BLACK;


    public void setShape(Shape shape[]) {//传入shape数组
        this.shapeLarry=shape;
    }
    public void setGr(Graphics graphics) {//传入画笔
        gr=graphics;
    }

    @Override
    public void mouseClicked(MouseEvent e) {//重写鼠标点击函数mouseClicked(MouseEvent e)
        x3=e.getX();//获取鼠标x坐标
        y3=e.getY();//获取鼠标y坐标
        if("多边形".equals(name)) {//如果点击多边形按钮
            if(flag == 2) {//如果是第一次画
                //gr.drawLine(x3, y3, x1, y1);
                gr.drawLine(x3, y3, x2, y2);
                Shape shape=new Shape(x3, x2, y3, y2, name);
                shape.setColor(c);
                shapeLarry[index++]=shape;
                a=x3;
                b=y3;
                flag++;
            }

            if(flag==3) {//如果不是第一次画
                gr.drawLine(x3, y3, a, b);
                Shape shape=new Shape(x3, a, y3, b, name);
                shape.setColor(c);
                shapeLarry[index++]=shape;
                a=x3;
                b=y3;
            }

            if(e.getClickCount()==2) {//如果双击，连接起点终点
                gr.drawLine(x3, y3, x1, y1);
                Shape shape=new Shape(x3, x1, y3, y1, name);
                shape.setColor(c);
                shapeLarry[index++]=shape;
                gr.drawLine(x3, y3, a, b);

                flag-=2;
            }
        }
        if("三角形".equals(name)) {//如果点击三角形按钮
            if(flag==2) {//如果不是第一次画，连接两端
                gr.drawLine(x3, y3, x1, y1);
                Shape shape=new Shape(x1, x3, y1, y3, name);
                shape.setColor(c);
                shapeLarry[index++]=shape;
                gr.drawLine(x3, y3, x2, y2);
                Shape shape2=new Shape(x2, x3, y2, y3, name);
                shape2.setColor(c);
                shapeLarry[index++]=shape2;
                flag--;
            }
        }
        System.out.println("flag="+flag);

    }

    @Override
    public void mousePressed(MouseEvent e) {//重写鼠标持续点击函数mousePressed(MouseEvent e)
        if(flag == 1) {
            x1=e.getX();
            y1=e.getY();
        }


    }


    @Override
    public void mouseReleased(MouseEvent e){//重写鼠标释放函数mouseReleased(MouseEvent e)
        if(flag==1) {
            x2=e.getX();
            y2=e.getY();
        }

        if("矩形".equals(name)) {//使用函数画图
            for(int i=0;i<25500;i++) {
                gr.setColor(new Color(i/100, (i+12)/100, 12));
                if((i+12)/100>=225) {
                    for(int i2=i;i2<30000;i2++) {
                        gr.setColor(new Color(i/100, (i+12)/100, 12));
                        if((Math.abs(x2-x1)-i2/50)<=0||(Math.abs(y2-y1)-i2/50)<=0)
                            break;
                        gr.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1)-i2/50, Math.abs(y2-y1)-i2/50);

                    }
                    break;
                }
                gr.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1)-i/50, Math.abs(y2-y1)-i/50);
            }
            Shape shape= new Shape(x1, x2, y1, y2, name);
            shapeLarry[index++]=shape;
            //gr.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));
        }
        if("椭圆".equals(name)) {//当鼠标释放时画椭圆
            gr.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));
            Shape shape=new Shape(x1, x2, y1, y2, name);
            shape.setColor(c);
            shapeLarry[index++]=shape;
        }
        if("多边形".equals(name) && flag==1) {//当鼠标释放时且不是最后一次画时画直线
            gr.drawLine(x1, y1, x2, y2);
            Shape shape=new Shape(x1, x2, y1, y2, name);
            shape.setColor(c);
            shapeLarry[index++]=shape;
            flag++;
        }
        if("三角形".equals(name) && flag==1) {
            gr.drawLine(x1, y1, x2, y2);
            Shape shape=new Shape(x1, x2, y1, y2, name);
            shape.setColor(c);
            shapeLarry[index++]=shape;
            flag++;
        }
        if("橡皮".equals(name)) {
            Graphics2D graphics2d=(Graphics2D) gr;
            BasicStroke basicStroke=new BasicStroke(1f);
            graphics2d.setColor(c);
            graphics2d.setStroke(basicStroke);
        }
        System.out.println("flag="+flag);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if("".equals(e.getActionCommand())) {
            JButton jb=(JButton)e.getSource();
            c=jb.getBackground();
            gr.setColor(c);
        }else if("多边形".equals(e.getActionCommand())==false ||"三角形".equals(e.getActionCommand())==false){
            flag=1;
            name=e.getActionCommand();
        }else {

            name=e.getActionCommand();
        }

    }


    @Override
    public void mouseDragged(MouseEvent e) {//重写鼠标拖拽函数mouseDragged(MouseEvent e)
        x4=e.getX();
        y4=e.getY();
        if("画线".equals(name)) {//画线主要是下一个点和上一个点连线组成

            gr.drawLine(x1, y1,x4, y4);
            Shape sh=new Shape(x4, y4, name, x1, y1);
            sh.setColor(c);
            shapeLarry[index++]=sh;
            x1=x4;
            y1=y4;

        }
        if("橡皮".equals(name)) {
            Graphics2D graphics2d=(Graphics2D) gr;
            BasicStroke basicStroke=new BasicStroke(10f);
            graphics2d.setColor(Color.WHITE);
            graphics2d.setStroke(basicStroke);
            gr.drawLine(x1, y1,x4, y4);
            Shape she=new Shape(x4, y4, name, x1, y1);
            she.setColor(Color.white);
            shapeLarry[index++]=she;
            x1=x4;
            y1=y4;
        }
        if("矩形".equals(name)) {

            gr.drawRect(Math.min(x1, x4), Math.min(y1, y4), Math.abs(x4-x1), Math.abs(y4-y1));
            gr.setColor(Color.white);
            gr.drawRect(Math.min(x1, a), Math.min(y1, b), Math.abs(a-x1), Math.abs(b-y1));
            gr.setColor(Color.black);
            a=x4;
            b=y4;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }


}