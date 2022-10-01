package 画板4;
import java.awt.Graphics;

import javax.imageio.ImageIO;
import javax.print.DocFlavor.STRING;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.filechooser.FileNameExtensionFilter;
public class Drawpad {
    static Color color1;
    public static void main(String[] args) {
        Drawpad dp = new Drawpad();
        dp.initUI();

    }
    //创建一个JFrame图形窗口
    public void initUI() {
        JFrame jf = new JFrame();
        jf.setTitle("创意画图板(勿拖动)");
        jf.setSize(1500,1000);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭时退出
        jf.setLocationRelativeTo(null);//居中，不用定位窗口大小
        //创建字体，之后所有的字体为该字体
        Font f=new Font("方正仿宋简体", Font.BOLD, 20);
        //创建画笔监听器
        DrawListener  dl = new DrawListener();
        //创建读取图片BufferedImage(将图片加载到drawPanel面板中)和画笔g，画笔g为在保存图片上进行图画。
        BufferedImage bi = new BufferedImage(1300,800, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        //初始化时填充白色
        g.setColor(Color.WHITE);
        //先将图片填充为白色
        g.fillRect(0, 0, 1300,800);


        //设置增加菜单栏，包括保存和新建两个按钮
        JMenuBar box=new JMenuBar();
        //在窗体上加菜单条,做一个菜单条,是菜单条，不是工具栏
        //创建menubtn1保存按钮，并加上监听器，以图片的形式保存绘画板上的内容
        JButton menubtn1=new JButton("保存");
        //为保存按钮注册监听器
        menubtn1.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                //创建文件保存窗口
                JFileChooser f=new JFileChooser("保存");
                int returnVal = f.showSaveDialog(null);

                File    file1=null;
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    file1 =f.getSelectedFile();
                    String name = f.getName(file1);
                    try {

                        ImageIO.write(bi, "PNG", new File(f.getCurrentDirectory(),name+".png"));
                    } catch (IOException e) {
                        //需抛出异常
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        });
         /*JButton menubtn2=new JButton("打开");
          //为打开按钮注册监听器
          menubtn1.addActionListener(new ActionListener(){
               @Override
               //获取当前画笔粗细
               public void actionPerformed(ActionEvent arg0) {
                   BufferedImage bimg = null;
                   JFileChooser f=new JFileChooser("打开");
                 int returnVal = f.showOpenDialog(null);
                  
                File    file1=null;
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    file1 =f.getSelectedFile();
                    String name = f.getName(file1);
                    try {
                       
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
 
                }
                    
                    
               }
            });*/

        //创建menubtn3退出按钮，并加上监听器，退出程序
        JButton menubtn3=new JButton("退出");
        menubtn3.addActionListener(new ActionListener(){
            @Override
            //获取当前画笔粗细
            public void actionPerformed(ActionEvent arg0) {
                int ret=JOptionPane.showConfirmDialog(null, "你确定要退出吗", "确认退出", JOptionPane.YES_NO_OPTION);
                if(ret==JOptionPane.YES_OPTION){
                    //“确认”退出程序
                    System.exit(0);
                }
            }
        });
        box.add(menubtn1);
        // box.add(menubtn2);
        box.add(menubtn3);
        //jf.setJMenuBar(box);

        jf.setJMenuBar(box);

        //jf用BorderLayout布局

        //北边,画板模式功能栏
        JPanel funcPanel=new JPanel();
        jf.add(funcPanel,BorderLayout.NORTH);

        //中间,画布
        JPanel drawPanel=new JPanel();
        jf.add(drawPanel,BorderLayout.CENTER);
        drawPanel.setPreferredSize(new Dimension(1000,700));
        drawPanel.setBackground(dl.background);
        //一定要在画布上加上监听器!!1若画布没有加上监听器，无法显示
        drawPanel.addMouseListener(dl);
        drawPanel.addMouseMotionListener(dl);

        //南边,为画笔颜色选择按钮
        JPanel colorPanel=new JPanel();
        jf.add(colorPanel,BorderLayout.SOUTH);

        //右边,为选择背景颜色按钮、画笔粗细选择按钮
        JPanel backgroundPanel=new JPanel();
        jf.add(backgroundPanel,BorderLayout.EAST);
        backgroundPanel.setPreferredSize(new Dimension(150,1000));

        //左边,获取当前状态如:背景颜色、画笔颜色、画笔性质
        JPanel nowPanel=new JPanel();
        jf.add(nowPanel,BorderLayout.WEST);
        nowPanel.setPreferredSize(new Dimension(180,1000));

        //左边放入当前状态Panel
        nowPanel.setBackground(Color.WHITE);
        JLabel label2=new JLabel("当前背景颜色");
        label2.setFont(f);
        nowPanel.add(label2);
        //放入当前背景颜色
        JButton nowbackgroundColor=new JButton();
        nowbackgroundColor.setPreferredSize(new Dimension(60,60));
        nowbackgroundColor.setBackground(Color.WHITE);//背景初始化为灰色
        nowPanel.add(nowbackgroundColor);
        //放入当前画笔
        JLabel label3=new JLabel("请选择画笔模式");
        label3.setFont(f);
        nowPanel.add(label3);
        //放入当前画笔颜色
        JButton nowColor=new JButton();
        nowColor.setPreferredSize(new Dimension(60,60));
        nowColor.setBackground(Color.BLACK);//画笔颜色初始化为黑色色
        nowPanel.add(nowColor);

        //获取当前画笔模式
        JLabel label4=new JLabel("当前画笔模式");
        label4.setFont(f);
        nowPanel.add(label4);
        JTextField text=new JTextField(dl.btncontent); //获得选择画笔模式的按钮内容，得到当前画笔模式
        text.setPreferredSize(new Dimension (160,60));
        text.setFont(f);
        text.setEditable(false);  //不可改
        nowPanel.add(text);
        //获取当前画笔粗细状态
        JLabel label6=new JLabel("当前画笔粗细(中)");  //默认粗细为中
        label6.setFont(f);
        nowPanel.add(label6);
        JTextField text1=new JTextField("请选择画笔粗细");
        text1.setPreferredSize(new Dimension (160,60));
        text1.setFont(f);
        text1.setEditable(false); //不可编辑
        nowPanel.add(text1);
        //输入需要添加的文字
        JLabel label7=new JLabel("请输入文字:");
        label7.setFont(f);
        nowPanel.add(label7);
        JTextField text2=new JTextField();
        text2.setPreferredSize(new Dimension (160,60));
        text2.setFont(f);
        nowPanel.add(text2);
        JLabel label8=new JLabel("请输入文字样式:");
        label8.setFont(f);
        nowPanel.add(label8);
        JTextField text3=new JTextField("方正仿宋简体");
        text3.setPreferredSize(new Dimension (160,60));
        text3.setFont(f);
        nowPanel.add(text3);
        JLabel label9=new JLabel("请输入文字大小:");
        label9.setFont(f);
        nowPanel.add(label9);
        JTextField text4=new JTextField("20");
        text4.setPreferredSize(new Dimension (160,60));
        text4.setFont(f);
        nowPanel.add(text4);
        //为获取文字内容加一个按钮并加上监听器
        JButton getcontent=new JButton("获取文字");
        getcontent .setFont(f);
        getcontent.setBackground(Color.YELLOW);
        getcontent.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String content=text2.getText();
                String mode=text3.getText();
                String size=text4.getText();
                dl.mode=mode; //获取文字样式
                dl.content=content; //获取文字内容
                dl.size=size; //获取文字大小
            }
        });
        nowPanel.add(getcontent);

        //最后在当前状态画板中加一个清除画布内容的功能
        JButton clear=new JButton("清除");
        clear.setFont(f);
        clear.setBackground(Color.RED);
        clear.addActionListener(dl);
        nowPanel.add(clear);

        //添加按钮到北边（每个按钮写两行代码太多，通过数组方式添加按钮）
        //加入标签（选择画笔模式）
        JLabel labelh =new JLabel("选择画笔模式");
        labelh.setFont(f);
        funcPanel.add(labelh);
        //将按钮名字保存在数组中，后依次存储
        String[] btnstr= {"画笔","直线","矩形","填充矩形","圆","填充圆","弧线","喷枪","波形","分形","长方体","九宫格递归","文字","橡皮"};
        //将画笔状态按钮防置panel中
        for( int i=0;i<btnstr.length;i++) {
            JButton btn=new JButton(btnstr[i]);
            funcPanel.add(btn);
            btn .setFont(f);
            btn.setBackground(Color.white);
            //加上画笔监听器
            btn.addActionListener(dl);
            //加上监听器：获取当前 画笔模式
            btn.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    text.setText(btn.getText()); //在当前模式加入选取的画笔模式
                }
            });

        };

        //在BrderLayout布局SOUTH添加选择颜色按钮
        JLabel label =new JLabel("选择画笔(橡皮)颜色");
        label.setFont(f);
        colorPanel.add(label);

        //添加颜色按钮

        Color [] colorArray = {Color.BLACK, Color.gray, Color.pink, Color.red, Color.CYAN, Color.green, Color.BLUE,
                Color.yellow, Color.DARK_GRAY, Color.ORANGE,new Color(46, 64, 83), Color.magenta, Color.LIGHT_GRAY, new Color(108, 52, 131), new Color(203,  67,  53)
                , new Color(120, 66, 18) ,new Color(52, 152,219),new Color(131, 145,146)

        };

        //在布局管理器中添加颜色按钮
        for( int i=0;i<colorArray.length;i++) {

            JButton button = new JButton();
            button.setBackground(colorArray[i]);
            button.setPreferredSize(new Dimension(50, 50));
            button.addActionListener(dl);
            colorPanel.add(button);
            //获取当前状态的画笔颜色
            button.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    nowColor.setBackground(button.getBackground());  //在当前画笔颜色按钮加入选择的按钮颜色
                }
            });
        };

        funcPanel.setBackground(Color.gray);

        //添加背景主板颜色按钮，并设置监听器（背景颜色为按钮颜色）
        JLabel label1=new JLabel("选择背景颜色");
        label1.setFont(f);
        backgroundPanel.add(label1);
        Color[] backgroundArray= { Color.GREEN, Color.RED,
                Color.ORANGE,Color.PINK,Color.CYAN,
                Color.MAGENTA,Color.DARK_GRAY,Color.GRAY,
                Color.LIGHT_GRAY,Color.YELLOW,Color.WHITE,Color.BLACK};
        //将按钮加入进去
        for( int i=0;i<backgroundArray.length;i++) {

            JButton button = new JButton();
            button.setBackground(backgroundArray[i]);
            button.setPreferredSize(new Dimension(50, 50));
            backgroundPanel.add(button);
            //添加监听器，按下按钮改变背景颜色，同时体现当前状态
            button.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    drawPanel.setBackground(button.getBackground()); //将背景颜色改为选取的背景颜色
                    color1=button.getBackground();
                    dl.background=color1;  //将背景颜色传给DrawListener中的变量
                    System.out.println(color1);
                    g.setColor(color1);
                    g.fillRect(0, 0, 1300,800);  //图片画笔填充背景颜色
                    nowbackgroundColor.setBackground(button.getBackground());
                }
            });
        };

        //添加选择画笔粗细的按钮,可选择画笔的粗细
        JLabel label5=new JLabel("选择画笔粗细");
        label5.setFont(f);
        backgroundPanel.add(label5);
        String[] Size={"细","中","粗"};
        //选择画笔模式的按钮
        for(int i=0;i<3;i++){
            JButton graphsize=new JButton(Size[i]);
            graphsize.setFont(new Font("宋体", Font.BOLD, 15));
            graphsize.setBackground(Color.WHITE);
            graphsize.setPreferredSize(new Dimension(50, 50));
            backgroundPanel.add(graphsize);
            graphsize.addActionListener(dl);
            graphsize.addActionListener(new ActionListener(){
                @Override
                //获取当前画笔粗细
                public void actionPerformed(ActionEvent e) {
                    text1.setText(graphsize.getText()); //获取当前画笔模式
                }
            });
        }
        jf.setVisible(true);
        // 获取这个界面的ｇｒａｐｈｉｃｓ属性, 画笔 g
        //Graphics2D g = (Graphics2D) drawPanel.getGraphics();
        //drawPanel.paintComponent(g);
        Graphics2D g1= (Graphics2D) drawPanel.getGraphics();

        //为画笔添加监听器-
        drawPanel.addMouseListener(dl);
        dl.g =  g1;// 右传左 
        dl.g3 = g;// 右传左

    }
}