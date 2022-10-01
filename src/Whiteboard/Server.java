package Whiteboard;

import Message.Message;
import Message.Shapes;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.LinkedList;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/9/30 13:14
 */
public class Server {
    LinkedList<Socket> sockets = new LinkedList<Socket>(); // the socket list used to store client sockets
    LinkedList<Shapes> shapes = new LinkedList<Shapes>(); // the list used to store all the shapes on the canvas
    LinkedList<ObjectOutputStream> ObjectOutputs = new LinkedList<ObjectOutputStream>();
    LinkedList<ObjectInputStream> ObjectInputs = new LinkedList<ObjectInputStream>();

    public static void main(String[] args) throws IOException {
        Server s = new Server();
        s.run();

    }

    public void run() throws IOException {
        System.out.println("server is online on: "+ InetAddress.getLocalHost().getHostAddress());
        ServerSocket s = new ServerSocket(8888);

        // open a while loop to take any incoming request
        while(true) {
            Socket client = s.accept();

            // put client socket and its object I/O streams into the lists
            sockets.add(client);
            ObjectInputs.add(new ObjectInputStream(client.getInputStream()));
            ObjectOutputs.add(new ObjectOutputStream(client.getOutputStream()));

//            // receive the hello message, and get the userID
//            try {
//                Message hello = (Message) ois.readObject();
//                System.out.println("received a client: "+hello.senderID);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }

            // load new client with current stored shapes
            Init(sockets.size()-1);

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

//                    // receive the hello message and get the user's ID
//                    if (m.message.equals("hello")) {
//
//                    }

                    // if the message is a shape, save it in the shape list, and
                    // send other peers the shape sent by a user except itself
                    // make the canvas concurrent
                    if(m instanceof Shapes) {
                        shapes.add((Shapes)m);
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
                    }



                } catch (IOException | ClassNotFoundException e)  {
                    e.printStackTrace();
                }
            }
        }
    }

    // if a client joined, send it all the shapes stored in shapelist to init
    // the canvas of that client
    public void Init(int socketNum) throws IOException {
        ObjectOutputStream os = ObjectOutputs.get(socketNum);
        for (int i = 0; i<shapes.size()-1;i++) {
            os.writeObject(shapes.get(i));
            os.flush();
        }
    }
}