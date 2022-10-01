package Message;

import java.io.Serializable;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/9/30 14:46
 */
public class Text extends Message implements Serializable {
    String text;

    public Text(String message, String senderID, String text) {
        super(message, senderID);
        this.text = text;
    }
}
