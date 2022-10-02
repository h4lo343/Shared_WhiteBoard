package Whiteboard;

import Message.Message;
import Message.normalShape;
import Message.Shapes;
import Message.textShape;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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

    public static void main(String[] args) throws IOException, InterruptedException {
        Server s = new Server();
        s.run();

    }

    public void run() throws IOException, InterruptedException {
        System.out.println("server is online on: "+ InetAddress.getLocalHost().getHostAddress());
        ServerSocket s = new ServerSocket(8888);

        // open a while loop to take any incoming request
        while(true) {
            Socket client = s.accept();

            // put client socket into the socket list
            sockets.add(client);
            ObjectInputs.add(new ObjectInputStream(client.getInputStream()));
            ObjectOutputs.add(new ObjectOutputStream(client.getOutputStream()));
            System.out.println("received a client");

            Thread.sleep(500);
            Thread init = new Thread(new Runnable() {
                @Override
                public void run() {
                    // load new client with current stored shapes
                    try {
                        Init(sockets.size()-1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            init.start();

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
                    System.out.println("receive:"+ m.message);
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
                            System.out.println("Pass send: "+m.message);
                            oos.writeObject(m);
                            oos.flush();
                        }

                    }

                } catch (IOException | ClassNotFoundException e)  {
                    sockets.remove(socketNum);
                    ObjectOutputs.remove(socketNum);
                    ObjectInputs.remove(socketNum);
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    // if a client joined, send it all the shapes stored in shapelist to init
    // the canvas of that client
    public void Init(int socketNum) throws IOException {
        ObjectOutputStream os = ObjectOutputs.get(socketNum);
        for (int i = 0; i<shapes.size();i++) {
            System.out.println("init send: "+shapes.get(i).message);
            os.writeObject(shapes.get(i));
            os.flush();
        }
    }
}