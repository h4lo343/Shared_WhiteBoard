package 画板4;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;


public class DrawListener implements MouseListener,ActionListener,MouseMotionListener {
    //获取画笔
    Graphics2D g;
    //获取在保存图片上的画笔
    Graphics2D g3;
    //获取按钮内容
    String btnstr;
    Color background=Color.white; //背景颜色默认为白色
    Color graphcolor=Color.BLACK; //画笔颜色默认为黑色
    JButton btn;
    int x1, y1, x2, y2;// 声明坐标变量
    int x3=400;
    int y3=0;
    int graphsize=3;//默认为中等画笔
    String btncontent="画笔"; //默认画笔模式为画笔
    String content;  //获取文字中的文字内容
    String mode="方正仿宋简体";  //文字样式默认为“方正仿宋简体”
    String size="20";

    //九宫格递归方法,画出九宫格
    public void dg(int x,int y,int width,int height) {
        //九宫格函数，九宫格的实现
        if(width<3) {
            return;
        }
        if(width>90) {
            g.fillRect(x+width/3, y+height/3, width/3, height/3);
            g3.fillRect(x+width/3, y+height/3, width/3, height/3);
            dg(x, y, width/3, height/3);
            dg(x+width/3, y, width/3, height/3);
            dg(x+(width/3)*2, y, width/3, height/3);
            dg(x, y+height/3, width/3, height/3);
            dg(x, y+(height/3)*2, width/3, height/3);

            dg(x+width/3, y+height/3, width/3, height/3);
            dg(x+width/3, y+(height/3)*2, width/3, height/3);

            dg(x+(width/3)*2, y+height/3, width/3, height/3);
            dg(x+(width/3)*2, y+(height/3)*2, width/3, height/3);

        }
        //九宫格的实现
        else {
            g.drawOval(x+width/3, y+height/3, width/3, height/3);
            g3.drawOval(x+width/3, y+height/3, width/3, height/3);
            dg(x, y, width/3, height/3);
            dg(x+width/3, y, width/3, height/3);
            dg(x+(width/3)*2, y, width/3, height/3);
            dg(x, y+height/3, width/3, height/3);
            dg(x, y+(height/3)*2, width/3, height/3);

            dg(x+width/3, y+height/3, width/3, height/3);
            dg(x+width/3, y+(height/3)*2, width/3, height/3);

            dg(x+(width/3)*2, y+height/3, width/3, height/3);
            dg(x+(width/3)*2, y+(height/3)*2, width/3, height/3);
        }

    }
    //判断是颜色按钮还是画笔按钮,改变的全部是画笔按钮
    public void actionPerformed(ActionEvent e) {
        btnstr=e.getActionCommand();  //获取按钮的文字内容
        //g.setColor(Color.black);
        //如果为颜色按钮，将画笔改颜色
        if(btnstr.equals("清除")){
            //重新填充背景，同时将画笔置为背景颜色
            System.out.println(background);
            g.setColor(background);//保存图片画笔填充背景颜色
            g.fillRect(0, 0, 1300, 800);
            g3.setColor(background);//画笔重新填充背景
            g3.fillRect(0, 0, 1300, 800);
            g.setColor(graphcolor);
            g3.setColor(graphcolor);
        }
        else{
            if(btnstr.equals("")) {
                //获取点击内容,将其内容强制转换成JButton
                btn=(JButton) e.getSource();
                //获取颜色按钮颜色
                graphcolor=btn.getBackground();

            }
            //若为画笔粗细，获取粗细大小
            else if(btnstr.equals("细")){
                graphsize=1;  //画笔大小为细，大小size为1
            }
            else if(btnstr.equals("中")){
                graphsize=3;
            }
            else if(btnstr.equals("粗")){
                graphsize=5;
            }
            else{
                btncontent=btnstr; //获取画笔模式按钮的内容
            }
        }
    }
    //鼠标点击方法
    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("点击");
    }
    //鼠标按下方法
    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("按下");
        x1=e.getX();
        y1 =e.getY();
    }
    //重写鼠标释放时的方法
    @Override
    public void mouseReleased(MouseEvent e) {
        g.setColor(graphcolor);//获取保存画笔的颜色
        g3.setColor(graphcolor); //获取画板画笔的颜色

        x2=e.getX();
        y2 =e.getY();
        //选取画笔模式为直线时
        if(btncontent.equals("直线")) {
            g.setStroke(new BasicStroke(graphsize)); //保存画笔进行画图
            g.drawLine(x1, y1, x2, y2);//画笔画直线
            g3.setStroke(new BasicStroke(graphsize));//置画笔大小
            g3.drawLine(x1, y1, x2, y2);
        }
        //选取画笔模式为波形时
        else if(btncontent.equals("波形")) {
            //波形函数
            g.setStroke(new BasicStroke(graphsize)); //置画笔大小
            g3.setStroke(new BasicStroke(graphsize));
            double x4 = 0,y4 = 0;
            double a2=1.40,b2=1.56,c2=1.40,d2=-6.56;
            //波形函数
            for(int i=0;i<5000;i++) {
                double x5=Math.sin(a2*x4)-Math.cos(b2*y4);
                double y5=Math.sin(c2*x4)-Math.cos(d2*y4);
                x4=x5;
                y4=y5;
                int px=(int)(x5*100+x1);
                int py=(int)(y5*100+y1);
                //画波形
                g.drawLine(px, py, px, py);
                g3.drawLine(px, py, px, py);
            }
        }
        //选取画笔模式为矩形时
        else if(btncontent.equals("矩形")) {
            g.setStroke(new BasicStroke(graphsize)); //获取矩形画笔的大小
            g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));//画矩形
            g3.setStroke(new BasicStroke(graphsize));
            g3.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));
        }
        //选取的画笔模式为填充矩形
        else if(btncontent.equals("填充矩形")){
            //画填充矩形
            g.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));
            g3.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));
        }
        //长方体函数
        else if(btncontent.equals("长方体")){
            g.setStroke(new BasicStroke(graphsize));//获取长方体画笔大小
            g.setColor(btn.getBackground());//将画笔颜色置选择画笔颜色按钮颜色
            //长方体函数
            g.fillRect(Math.min(x1,x2),Math.min(y1,y2),Math.abs(x1-x2),Math.abs(y1-y2));
            g3.setStroke(new BasicStroke(graphsize));
            g3.setColor(btn.getBackground());
            g3.fillRect(Math.min(x1,x2),Math.min(y1,y2),Math.abs(x1-x2),Math.abs(y1-y2));
            int a,b,c,d;
            a=Math.min(x1, x2);
            b=Math.max(x1, x2);
            c=Math.min(y1, y2);
            d=Math.max(y1, y2);

            int m=(int)((b-a)*Math.cos(Math.PI/4)*Math.sin(Math.PI/4));
            int n=(int)((b-a)*Math.cos(Math.PI/4)*Math.sin(Math.PI/4));
            //顶面
            g.setColor(btn.getBackground());
            g.fillPolygon(new int[] {a, a+m, b+m,b},new int[] {c,c-n,c-n,c},4);
            //右侧面
            g.setColor(btn.getBackground());
            g.fillPolygon(new int[] {b, b, b+m,b+m},new int[] {c,d,d-n,c-n},4);
            g3.setColor(btn.getBackground());
            g3.fillPolygon(new int[] {a, a+m, b+m,b},new int[] {c,c-n,c-n,c},4);
            //右侧面
            g3.setColor(btn.getBackground());
            g3.fillPolygon(new int[] {b, b, b+m,b+m},new int[] {c,d,d-n,c-n},4);
        }
        //分形函数
        else if(btncontent.equals("分形")){
            g.setStroke(new BasicStroke(graphsize));  //获取画笔大小
            g3.setStroke(new BasicStroke(graphsize));
            double x = 0,y = 0;
            //分形函数实现
            double a1=-1.8,b=-2.0,c=-0.5,d=-0.9;
            for(int i=0;i<5000;i++) {
                double x3=Math.sin(a1*y)-c*Math.cos(a1*x);
                double y3=Math.sin(b*x)-d*Math.cos(b*y);
                x=x3;
                y=y3;
                int px=(int)(x3*100+x1);
                int py=(int)(y3*100+y1);
                g.drawLine(px, py, px, py);
                g3.drawLine(px, py, px, py);
            }
        }
        //画圆
        else if(btncontent.equals("圆")) {
            g.setStroke(new BasicStroke(graphsize));//获取画笔大小
            g.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));//画圆
            g3.setStroke(new BasicStroke(graphsize));
            g3.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));
        }
        //画填充圆
        else if(btncontent.equals("填充圆")){
            g.fillOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));//画填充圆
            g3.fillOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));
        }
        //当选取模式为文字
        else if(btncontent.equals("文字")){
            //获取画笔大小
            g.setStroke(new BasicStroke(15));
            Font font = new Font(mode, Font.BOLD, Integer.parseInt(size)); //获得文字内容，文字大小，文字样式
            g.setFont(font); //在画笔中置文字样式和大小
            g.drawString(content, x1, y1); //写上文字内容
            g3.setStroke(new BasicStroke(15));
            g3.setFont(font);//放入文字样式和大小
            g3.drawString(content, x1, y1);
        }
        //当画笔模式为弧线时
        else if(btncontent.equals("弧线")){
            g.setStroke(new BasicStroke(graphsize));//获取画笔大小
            //弧线函数
            g.drawArc(x1, y1, 100, 60, 0, 180);
            g3.setStroke(new BasicStroke(graphsize));
            g3.drawArc(x1, y1, 100, 60, 0, 180);
        }
        //九宫格递归，调用九宫格函数
        else if(btncontent.equals("九宫格递归")) {
            //九宫格递归实现
            dg(0,50,600,600);
        }
        System.out.println("释放");

    }
    @Override
    //鼠标进入方法
    public void mouseEntered(MouseEvent e) {
        System.out.println("进入");
    }

    @Override
    //鼠标离开界面方法
    public void mouseExited(MouseEvent e) {
        System.out.println("离开");
    }
    @Override
    public void mouseMoved(MouseEvent e) {

    }
    //重写鼠标移动函数
    @Override
    public void mouseDragged(MouseEvent e) {
        g.setColor(graphcolor); //获取画笔颜色
        g3.setColor(graphcolor);
        // TODO Auto-generated method stub
        x2=e.getX();
        y2 =e.getY();
        //当为画笔时
        if(btncontent.equals("画笔")){

            g.setStroke(new BasicStroke(graphsize));    //获取当前画笔大小
            //画笔实现
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawLine(x1, y1, x2, y2);
            g3.setStroke(new BasicStroke(graphsize));
            g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g3.drawLine(x1, y1, x2, y2);
            x1 = x2;
            y1 = y2;
        }
        //橡皮擦
        if(btncontent.equals("橡皮")){
            //将画笔颜色置为背景颜色
            g.setColor(background);
            g3.setColor(background);
            g.setStroke(new BasicStroke(30));    //将橡皮擦的大小置大小为30
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawLine(x1, y1, x2, y2);

            g3.setStroke(new BasicStroke(30));
            g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g3.drawLine(x1, y1, x2, y2);
            x1 = x2;
            y1 = y2;
            //使用过后，将画笔颜色重新置为原来颜色
            g.setColor(graphcolor);
            g3.setColor(graphcolor);
        }
        //喷枪函数

        else if(btncontent.equals("喷枪")){
            g.setStroke(new BasicStroke(graphsize));      //不用加粗，获取画笔大小
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g3.setStroke(new BasicStroke(graphsize));      //不用加粗
            g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //喷枪实现函数
            for(int k=0;k<20;k++){
                Random i=new Random();
                int a=i.nextInt(10);
                int b=i.nextInt(20);
                g.drawLine(x2+a, y2+b, x2+a, y2+b);
                g3.drawLine(x2+a, y2+b, x2+a, y2+b);
            }
        }

    }


}