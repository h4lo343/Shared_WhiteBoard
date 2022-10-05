package Message;

import java.io.Serializable;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/10/5 23:45
 */
public class ChatMessage extends Message implements Serializable {
    public String chatContent;
    public ChatMessage(String message, String senderID, String chatContent) {
        super(message, senderID);
        this.chatContent = chatContent;
    }
}
