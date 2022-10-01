package Message;

import java.awt.*;
import java.io.Serializable;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/10/1 20:37
 */
public class textShape extends Shapes implements Serializable {
    public int x, y;
    public Color color;
    public String text;
    public textShape(String message, String senderID, int x, int y, String text, Color color) {
        super(message, senderID);
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
    }
}
