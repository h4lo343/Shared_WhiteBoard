package Whiteboard;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/10/5 15:33
 */
public class MyObjectOutPutStream extends ObjectOutputStream {
    public MyObjectOutPutStream(OutputStream out) throws IOException {
        super(out);
    }

    protected MyObjectOutPutStream() throws IOException, SecurityException {
    }

    @Override
    protected void writeStreamHeader() throws IOException {

    }

}
