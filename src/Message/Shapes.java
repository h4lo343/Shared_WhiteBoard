package Message;

import java.io.Serializable;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/10/1 20:42
 */
public class Shapes extends Message implements Serializable {
    public Shapes(String message, String senderID) {
        super(message, senderID);
    }
}
