package Whiteboard;

import Message.Message;
import Message.normalShape;
import Message.Shapes;
import Message.textShape;

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
    ArrayList<Shapes> shapes = new ArrayList<Shapes>(); // the list used to store all the shapes on the canvas
    ArrayList<ObjectOutputStream> ObjectOutputs = new ArrayList<ObjectOutputStream>();
    ArrayList<ObjectInputStream> ObjectInputs = new ArrayList<ObjectInputStream>();

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
            ObjectInputs.add(new ObjectInputStream(client.getInputStream()));
            ObjectOutputs.add(new ObjectOutputStream(client.getOutputStream()));
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
            this.oi = ObjectInputs.get(socketNum);
            this.os = ObjectOutputs.get(socketNum);
        }
        @Override
        public void run() {
            while (true) {
                try {
                    Message m =((Message) oi.readObject());

                    // if the message is a shape, save it in the shape list
                    if(m instanceof Shapes) {
                        shapes.add((Shapes)m);
                    }

                    for (int i=0 ; i<sockets.size() ; i++) {

                            if (i == socketNum) {
                                continue;
                                }
                            else {
                                ObjectOutputStream oos = ObjectOutputs.get(i);
                                oos.writeObject(m);
                                oos.flush();
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