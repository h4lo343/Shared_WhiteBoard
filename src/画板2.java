package Swing;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.TileObserver;
import java.awt.image.WritableRenderedImage;

class MyPaiinterPanel extends JPanel{//画布
    BufferedImage image;

    public MyPaiinterPanel(BufferedImage image){
        this.image=image;
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(image,0,0,null );//绘制指定图像中当前可用的图像
    }
}

class paint {
    JFrame jf=new JFrame();
    BufferedImage image=new BufferedImage(600,600,BufferedImage.TYPE_3BYTE_BGR);// 构造一个类型为预定义图像类型之一的 BufferedImage
    Graphics g= image.getGraphics();
    int x1,x2,y1,y2;
    String xz="直线";
    public void fun(String ys){//设置画笔的颜色g
        if(ys.equals("红色")){
            g.setColor(Color.red);
        }
        if(ys.equals("白色")){
            g.setColor(Color.white);
        }
        if(ys.equals("绿色")){
            g.setColor(Color.green);
        }
    }
    public void funn(String ys){//设置画的图形
        this.xz=ys;
    }

    public void init(){
        MyPaiinterPanel mp=new MyPaiinterPanel(image);
        mp.addMouseListener(new MouseAdapter() {//添加监听

            @Override
            public void mousePressed(MouseEvent e) {//记录第一次单击的位置x1,y1
                x1=e.getX();
                y1=e.getY();
            }
            @Override
            public void mouseReleased(MouseEvent e) {//第二次单击的位置x2,y2
                x2=e.getX();
                y2=e.getY();
                if(xz.equals("直线")){//两点确定直线。。。
                    g.drawLine(x1,y1,x2,y2);
                }else
                if(xz.equals("圆")){
                    g.drawOval(x1,y1,Math.abs(x2-x1),Math.abs(y2-y1));
                }else
                if(xz.equals("矩形")){
                    g.drawRect(x1,y1,Math.abs(x2-x1),Math.abs(y2-y1));
                }
                if(xz.equals("清空")){
                    //jf.dispose();
                    //new paint().init();
                    g.clearRect(0,0,600,600);//橡皮擦
                }
                mp.repaint();//刷新画布
            }
        });

        JMenuBar bar=new JMenuBar();//设置任务栏按钮
        JMenu jm=new JMenu("颜色");
        JMenuItem[] item=new JMenuItem[7];
        item[0]=new JMenu("红色");
        item[1]=new JMenu("白色");
        item[2]=new JMenu("绿色");
        item[3]=new JMenu("直线");
        item[4]=new JMenu("圆");
        item[5]=new JMenu("矩形");
        item[6]=new JMenu("清空");
        jm.add(item[0]);
        jm.add(item[1]);
        jm.add(item[2]);
        bar.add(jm);
        JMenu jm1=new JMenu("形状");
        jm1.add(item[3]);
        jm1.add(item[4]);
        jm1.add(item[5]);
        jm1.add(item[6]);
        bar.add(jm1);

        for(int i=0;i< item.length;i++){//任务栏按钮监听
            int finalI = i;
            item[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    if(item[finalI].getText().equals("红色")){
                        fun("红色");
                    }
                    if(item[finalI].getText().equals("白色")){
                        fun("白色");
                    }
                    if(item[finalI].getText().equals("绿色")){
                        fun("绿色");
                    }
                    if(item[finalI].getText().equals("直线")){
                        funn("直线");
                    }
                    if(item[finalI].getText().equals("圆")){
                        funn("圆");
                    }
                    if(item[finalI].getText().equals("矩形")){
                        funn("矩形");
                    }
                    if(item[finalI].getText().equals("清空")){
                        funn("清空");
                    }
                }
            });
        }
        jf.setJMenuBar(bar);//设置任务栏

        jf.add(mp);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(600,600);
        jf.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new paint().init();//调用方法
    }
}