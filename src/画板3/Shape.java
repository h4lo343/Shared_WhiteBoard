package 画板3;

import java.awt.*;

public class Shape {
    private int x1,x2,y1,y2,x3,y3,x4,y4,a,b;
    String name;
    Color c1;

    public Shape(int x1,int x2,int y1,int y2,String name) {
        this.x1=x1;
        this.x2=x2;
        this.y1=y1;
        this.y2=y2;
        this.name=name;
    }
    public Shape(int x4,int y4,String name,int x1,int y1) {
        this.x1=x1;
        this.y1=y1;
        this.x4=x4;
        this.y4=y4;
        this.name=name;
    }

    public void setColor(Color c) {
        this.c1=c;

    }

    public void drawShape(Graphics g){
        g.setColor(c1);
        switch (name) {
            case "矩形":
                for(int i=0;i<25500;i++) {
                    g.setColor(new Color(i/100, (i+12)/100, 12));
                    if((i+12)/100>=225) {
                        for(int i2=i;i2<30000;i2++) {
                            g.setColor(new Color(i/100, (i+12)/100, 12));
                            if((Math.abs(x2-x1)-i2/50)<=0||(Math.abs(y2-y1)-i2/50)<=0)
                                break;
                            g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1)-i2/50, Math.abs(y2-y1)-i2/50);

                        }
                        break;
                    }
                    g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1)-i/50, Math.abs(y2-y1)-i/50);
                }
                break;

            case "椭圆":
                Graphics2D graphics2d4=(Graphics2D) g;
                BasicStroke basicStroke4=new BasicStroke(1f);
                graphics2d4.setColor(c1);
                graphics2d4.setStroke(basicStroke4);

                g.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));
                break;
            case "画线":
                Graphics2D graphics2d=(Graphics2D) g;
                BasicStroke basicStroke=new BasicStroke(1f);
                graphics2d.setColor(c1);
                graphics2d.setStroke(basicStroke);

                g.drawLine(x1, y1, x4, y4);
                break;
            case "橡皮":
                Graphics2D g2D=(Graphics2D)g;
                BasicStroke basicStroke2=new BasicStroke(10f);
                g2D.setColor(Color.WHITE);
                g2D.setStroke(basicStroke2);

                g2D.drawLine(x1, y1,x4, y4);
                break;
            case "三角形":
                Graphics2D graphics2d1=(Graphics2D) g;
                BasicStroke basicStroke1=new BasicStroke(1f);
                graphics2d1.setColor(c1);
                graphics2d1.setStroke(basicStroke1);

                g.drawLine(x1, y1, x2, y2);
                //g.drawLine(x3, y3, x1, y1);
                //g.drawLine(x3, y3, x2, y2);
                break;
            case "多边形":
                Graphics2D graphics2d2=(Graphics2D) g;
                BasicStroke basicStroke3=new BasicStroke(1f);
                graphics2d2.setColor(c1);
                graphics2d2.setStroke(basicStroke3);

                g.drawLine(x1, y1, x2, y2);
                break;
            default:
                break;
        }
    }
}