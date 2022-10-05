package Whiteboard;

import java.io.*;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/10/5 15:33
 */
public class MyObjectInputStream extends ObjectInputStream {


    public MyObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    protected MyObjectInputStream() throws IOException, SecurityException {
    }

    @Override
    protected void readStreamHeader() throws IOException {

    }

}
