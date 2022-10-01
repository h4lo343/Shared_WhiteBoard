package 画板3;


import javax.swing.*;
import java.awt.*;

public class draw extends JFrame {

    private Shape shape[]= new Shape[100000];//将所画的形状存储在shape数组中
    public static void main(String[] args) {
        draw simpleDraw = new draw();
        simpleDraw.showUI();//调用showUI()函数

    }
    public void showUI() {
        drawlistener drawListener = new drawlistener();


        java.awt.FlowLayout flowLayout = new FlowLayout();
        JButton jb1=new JButton("矩形");//添加一个叫“矩形”的按钮
        JButton jb2=new JButton("椭圆");
        JButton jb3=new JButton("多边形");
        JButton jb4=new JButton("三角形");
        JButton jb5=new JButton("画线");
        JButton jb6=new JButton("橡皮");
        java.awt.Dimension dimension = new Dimension(100, 30);
        jb1.setPreferredSize(dimension);//设置按钮的位置
        jb2.setPreferredSize(dimension);
        jb3.setPreferredSize(dimension);
        jb4.setPreferredSize(dimension);
        jb5.setPreferredSize(dimension);
        jb6.setPreferredSize(dimension);
        this.add(jb1);//在这个窗口上添加按钮
        this.add(jb2);
        this.add(jb3);
        this.add(jb4);
        this.add(jb5);
        this.add(jb6);
        Color []colors= {Color.BLUE,Color.GRAY,Color.YELLOW,Color.BLACK};//提供四种颜色选择，存储在colors数组中
        for(int i=0;i<4;i++) {//新建4个颜色选择的按钮
            JButton jButton=new JButton();
            jButton.setBackground(colors[i]);
            jButton.setPreferredSize(new Dimension(30, 30));
            this.add(jButton);//在这个窗口上添加次按钮
            jButton.addActionListener(drawListener);//设置按钮的位置
        }

        this.setLayout(flowLayout);//设置窗口布局
        this.setSize(800, 700);//设置窗口大小
        this.setTitle("画板");//设置窗口名称
        this.setLocationRelativeTo(null);//设置窗口位置
        this.setDefaultCloseOperation(3);
        this.setVisible(true);
        this.getContentPane().setBackground(Color.white);//设置窗口背景颜色


        this.addMouseMotionListener(drawListener);//窗口添加监听
        jb1.addActionListener(drawListener);//按钮添加监听
        jb2.addActionListener(drawListener);
        jb3.addActionListener(drawListener);
        jb4.addActionListener(drawListener);
        jb5.addActionListener(drawListener);
        jb6.addActionListener(drawListener);
        //-----------------
        java.awt.Graphics g = this.getGraphics();//在此窗口上获得画笔

        drawListener.setGr(g);//将画笔传给监听器drawListener
        drawListener.setShape(shape);//将数组传给监听器drawListener
        this.addMouseListener(drawListener);//画布添加监听


    }
    public void paint(Graphics g) {//重绘
        super.paint(g);
        for(int i=0;i<shape.length;i++) {//重绘shape数组中的所有图形
            Shape shape1=shape[i];
            if(shape1!=null) {
                shape1.drawShape(g);
            }else
                break;
        }
    }
}