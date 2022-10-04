package Message;

import java.io.Serializable;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/10/4 16:21
 */
public class JoinReply extends Message implements Serializable {
    public int socketNum;
    public boolean agree;

    public JoinReply(String message, String senderID, int socketNum, boolean agree) {
        super(message, senderID);
        this.socketNum = socketNum;
        this.agree = agree;
    }
}
