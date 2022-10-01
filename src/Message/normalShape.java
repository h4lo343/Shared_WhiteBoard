package Message;

import java.awt.*;
import java.io.Serializable;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/9/30 15:00
 */
public class normalShape extends Shapes implements Serializable {
   public int x,x1,y,y1;
   public Color color;
    public normalShape(String message, String senderID, int x, int x1, int y, int y1, Color color) {
        super(message, senderID);
        this.x=x;
        this.x1=x1;
        this.y=y;
        this.y1=y1;
        this.color = color;
    }
}
