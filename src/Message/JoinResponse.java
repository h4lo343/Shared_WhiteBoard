package Message;

import java.io.Serializable;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/10/4 16:25
 */
public class JoinResponse extends Message implements Serializable {
    public boolean agree;
    public JoinResponse(String message, String senderID, boolean agree) {
        super(message, senderID);
        this.agree = agree;
    }
}
