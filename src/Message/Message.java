package Message;

import java.io.Serializable;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/9/30 14:41
 */
public class Message implements Serializable {
    public String message;
    public String senderID;

    public Message(String message, String senderID) {
        this.message = message;
        this.senderID = senderID;
    }

}
