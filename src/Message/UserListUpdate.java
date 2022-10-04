package Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/10/4 20:45
 */
public class UserListUpdate extends Message implements Serializable {
    public LinkedList<String> userList;
    public int i;
    public UserListUpdate(String message, String senderID, LinkedList<String> userList, int i) {
        super(message, senderID);
        this.userList = userList;
        this.i=i;
    }
}
