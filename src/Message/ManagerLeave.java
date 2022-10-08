package Message;

import java.io.Serializable;

/**extends Message implements Serializable
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/10/8 20:28
 */
public class ManagerLeave extends Message implements Serializable {
    public ManagerLeave(String message, String senderID) {
        super(message, senderID);
    }
}
