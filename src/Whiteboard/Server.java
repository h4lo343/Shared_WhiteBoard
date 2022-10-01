package Whiteboard;

import Message.Message;
import Message.canvasShape;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/9/30 13:14
 */
public class Server {
    ArrayList<Socket> sockets = new ArrayList<Socket>(); // the socket list used to store client sockets
    ArrayList<Message> shapes = new ArrayList<Message>(); // the list used to store all the shapes on the canvas
    ArrayList<OutputStream> outputStreams = new ArrayList<OutputStream>();
    ArrayList<InputStream> inputStreams = new ArrayList<InputStream>();

    public static void main(String[] args) throws IOException {
        Server s = new Server();
        s.run();

    }

    public void run() throws IOException {
        System.out.println("server is online");
        ServerSocket s = new ServerSocket(8888);

        // open a while loop to take any incoming request
        while(true) {
            Socket client = s.accept();

            // put client socket into the socket list
            sockets.add(client);
            outputStreams.add(client.getOutputStream());
            inputStreams.add(client.getInputStream());
            System.out.println("received a client");

            // open monitor for that client
            Monitor monitor = new Monitor(sockets.size()-1);
            monitor.start();
        }

    }


    // the monitor class which monitor the coming message from clients
    class Monitor extends Thread {
        int socketNum;
        ObjectInputStream oi;
        ObjectOutputStream os;
        public Monitor(int socketNum) {
            this.socketNum = socketNum;
            Socket s = sockets.get(socketNum);
            try {
                this.oi = new ObjectInputStream(inputStreams.get(socketNum));
                this.os = new ObjectOutputStream(outputStreams.get(socketNum));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            while (true) {
                try {
                    Message m =((Message) oi.readObject());

                    // if the message is a shape, add it to the shape list
                    if(m instanceof canvasShape) {
                        shapes.add(m);
                    }

                    // send the message to any other peers except the sender itself
                    for (int i=0 ; i<sockets.size() ; i++) {
                            if (i == socketNum) {continue;}
                            else {
                                ObjectOutputStream os2 = new ObjectOutputStream(sockets.get(i).getOutputStream());
                                os2.writeObject(m);
                                os2.flush();
                            }
                    }

                } catch (IOException | ClassNotFoundException e)  {
                    e.printStackTrace();
                }
            }
        }
    }

//    class dispose extends Thread {
//        Message m;
//        int socketNum;
//        public dispose(Message m, int socketNum) {
//            this.m = m;
//            this.socketNum = socketNum;
//        }
//
//        @Override
//        public void run() {
//            if(m.message.equals("line")) {
//                for (int i=0 ; i<sockets.size() ; i++) {
//                    try{
//                        if (i == socketNum) {continue;}
//                        else {
//                            ObjectOutputStream oos = new ObjectOutputStream(sockets.get(i).getOutputStream());
//                            oos.writeObject(m);
//                            oos.flush();
//                            oos.close();
//                        }
//                    }catch (Exception e){
//                    }
//                }
//            }
//        }
//    }

}