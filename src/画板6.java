import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

class Test {

    public static void main(String[] args) {

        DrawBoard db = new DrawBoard();

        db.initFrame();

    }

}


class DrawBoard extends JFrame {


    Graphics2D g;


    public void initFrame() {

        //设置窗体大小，直接关闭，居中，标题

        this.setSize(800, 600);

        this.setDefaultCloseOperation(3);

        this.setLocationRelativeTo(null);

        this.setTitle("画板");


        //边框布局

        this.setLayout(new BorderLayout());


        PanelLeft panelLeft = new PanelLeft(g);//添加面板

        PanelCenter panelCenter = new PanelCenter();

        PanelDown panelDown = new PanelDown(g, panelLeft);

        this.add(panelLeft, BorderLayout.WEST);

        this.add(panelCenter, BorderLayout.CENTER);

        this.add(panelDown, BorderLayout.SOUTH);


        panelLeft.click();//调用面板方法

        panelDown.clickColor();


        //画板可见

        this.setVisible(true);

        g = (Graphics2D) panelCenter.getGraphics();//中间面板取画笔并强制转子类

        panelLeft.g = g;//画笔传递

        panelDown.g = g;

        DrawListener mouse = new DrawListener(g, panelLeft);//添加鼠标监听器

        panelCenter.addMouseListener((MouseListener) mouse);

        panelCenter.addMouseMotionListener((MouseMotionListener) mouse);

    }

}  





class PanelLeft extends JPanel {


    String actionCommand = "6";

    int n;

    public Graphics2D g;

    Color color = Color.black;


    public PanelLeft(Graphics2D g) {

        this.g = g;

        setPreferredSize(new Dimension(70, 0));

    }


    public void click() {

        //给按钮添加监听器  

        ActionListener actionListener = new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent e) {


                actionCommand = e.getActionCommand();

                n = 0;

                if ("2".equals(actionCommand)) {


                    g.setColor(Color.white);

                    g.setStroke(new BasicStroke(20));//设置橡皮粗细和颜色  

                } else if ("7".equals(actionCommand)) {


                    g.setColor(color);

                    g.setStroke(new BasicStroke(20)); //设置刷子粗细和颜色  

                } else {


                    g.setColor(color);

                    g.setStroke(new BasicStroke(1));//初始化画笔粗细和颜色  

                }

            }

        };


        //添加按钮  

        for (int i = 0; i < 16; i++) {

            JButton button = new JButton();

            ImageIcon image1 = new ImageIcon("images/draw" + i + ".jpg");

            ImageIcon image2 = new ImageIcon("images/draw" + i + "-1.jpg");

            ImageIcon image3 = new ImageIcon("images/draw" + i + "-2.jpg");

            ImageIcon image4 = new ImageIcon("images/draw" + i + "-3.jpg");


            button.setIcon(image1);//给按钮添加图标  

            button.setRolloverIcon(image2);

            button.setPressedIcon(image3);

            button.setSelectedIcon(image4);


            button.setPreferredSize(new Dimension(25, 25));

            this.add(button);

            button.addActionListener(actionListener);//按钮添加动作监听  

            button.setActionCommand(i + "");

        }


    }

}


class PanelDown extends JPanel {

    Color[] colors = {Color.black, Color.gray, Color.red, Color.yellow, Color.blue};

    Color color = Color.black;

    Graphics2D g;

    PanelLeft panelLeft;


    public PanelDown(Graphics2D g, PanelLeft pl) {

        this.g = g;

        this.panelLeft = pl;//左边面板传参

    }

    public void clickColor() {


        ActionListener listener = new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent e) {


                String actionCommandColor = e.getActionCommand();

                int i = Integer.valueOf(actionCommandColor);// String -> int         <span style="white-space:pre">                                  </span>//字符串型转换成整型

                color = colors[i];

                g.setColor(color);

                panelLeft.color = color;//把颜色传给左边面板

                if ("2".equals(panelLeft.actionCommand)) {

                    g.setColor(Color.white);//橡皮设置白色

                }

            }

        };

        for (int i = 0; i < colors.length; i++) {

            // 创建颜色按钮

            JButton buttoncolor = new JButton();

            buttoncolor.setBackground(colors[i]);


            buttoncolor.setPreferredSize(new Dimension(25, 25));

            buttoncolor.addActionListener(listener);//颜色按钮添加动作监听器

            buttoncolor.setActionCommand(i + "");

            // 面板添加颜色按钮

            this.add(buttoncolor);

        }

    }

}


class PanelCenter extends JPanel {


    public PanelCenter() {

        setBackground(Color.white);//设置背景色

    }

}


class DrawListener implements MouseMotionListener, MouseListener {


    int x1, y1, x2, y2, a, b, xS, yS;

    PanelLeft panelLeft;

    Graphics2D g;

    Random random = new Random();


    public DrawListener(Graphics2D g, PanelLeft pl) {

        this.g = g;

        this.panelLeft = pl;

    }


    @Override
    public void mouseClicked(MouseEvent e) {

        int clickCount = e.getClickCount();//记录点击次数

        if (clickCount == 1) {


        } else if (clickCount == 2) {

            if ("13".equals(panelLeft.actionCommand)) {

                g.drawLine(x2, y2, xS, yS);//双击自动完成多边形绘制

                panelLeft.n = 0;

            }

        }

    }


    @Override
    public void mousePressed(MouseEvent e) {


        x1 = e.getX();

        y1 = e.getY();


        if ("13".equals(panelLeft.actionCommand)) {


            if (panelLeft.n != 0) {

                x1 = a;       //设置多边形中间值  

                y1 = b;

            }

            if (panelLeft.n == 0) {

                xS = e.getX();//记录多边形起点  

                yS = e.getY();

            }

        }

    }


    @Override
    public void mouseReleased(MouseEvent e) {

        x2 = e.getX();

        y2 = e.getY();


        if ("10".equals(panelLeft.actionCommand)) {

            //直线  

            g.drawLine(x1, y1, x2, y2);


        } else if ("12".equals(panelLeft.actionCommand)) {

            //矩形  

            g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));


        } else if ("14".equals(panelLeft.actionCommand)) {

            // 椭圆  

            g.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));


        } else if ("13".equals(panelLeft.actionCommand)) {

            //多边形  


            g.drawLine(x1, y1, x2, y2);

            a = x2;

            b = y2;

            panelLeft.n++;

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

        if ("6".equals(panelLeft.actionCommand)) {

            x2 = e.getX();

            y2 = e.getY();

            //绘制铅笔  

            g.drawLine(x1, y1, x2, y2);

            x1 = x2;

            y1 = y2;

        } else if ("2".equals(panelLeft.actionCommand)) {

            x2 = e.getX();

            y2 = e.getY();

            //橡皮  

            g.drawLine(x1, y1, x2, y2);

            x1 = x2;

            y1 = y2;

        } else if ("8".equals(panelLeft.actionCommand)) {

            x2 = e.getX();

            y2 = e.getY();

            // 喷枪  

            for (int i = 0; i < 30; i++) {

                // 生成的随机数  

                int nextInt1 = random.nextInt(8) - 4;// [0,9]->[-5,4]  

                int nextInt2 = random.nextInt(8) - 4;// [0,9]->[-5,4]  

                // 画点  

                g.drawLine(x2 + nextInt1, y2 + nextInt2, x2 + nextInt1, y2 + nextInt2);

            }

        } else if ("7".equals(panelLeft.actionCommand)) {

            x2 = e.getX();

            y2 = e.getY();

            //刷子  

            g.drawLine(x1, y1, x2, y2);

            x1 = x2;

            y1 = y2;

        }

    }


    @Override
    public void mouseMoved(MouseEvent e) {


    }


}
