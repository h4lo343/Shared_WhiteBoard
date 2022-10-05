package Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/10/4 20:45
 */
public class UserListUpdate extends Message implements Serializable {
    public String userName;
    public String type;
    public UserListUpdate(String message, String senderID, String userName, String type) {
        super(message, senderID);
        this.userName = userName;
        this.type = type;

    }
}
