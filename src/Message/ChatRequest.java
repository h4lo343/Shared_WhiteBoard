package Message;

import java.io.Serializable;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/10/8 18:59
 */
public class ChatRequest  extends Message implements Serializable {
    public String content;
    public ChatRequest(String message, String senderID, String content) {
        super(message, senderID);
        this.content = content;
    }
}
