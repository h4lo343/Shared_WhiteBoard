package Message;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/10/4 20:45
 */
public class UserListUpdate extends Message implements Serializable {
    public ArrayList<String> userList;
    public UserListUpdate(String message, String senderID, ArrayList<String> userList) {
        super(message, senderID);
        this.userList = userList;
    }
}
