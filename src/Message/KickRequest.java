package Message;

import java.io.Serializable;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/10/5 14:54
 */
public class KickRequest extends Message implements Serializable {
    public String userID;
    public KickRequest(String message, String senderID, String userID) {
        super(message, senderID);
        this.userID = userID;
    }
}
