package Message;

import java.io.Serializable;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/10/4 16:10
 */
public class JoinRequest extends Message implements Serializable {
    public String joiner;
    public int socketNum;
    public JoinRequest(String message, String senderID, String joiner, int socketNum) {
        super(message, senderID);
        this.joiner = joiner;
        this.socketNum = socketNum;
    }
}
