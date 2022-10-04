package Whiteboard;

import Message.*;


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
    ArrayList<Socket> sockets = new ArrayList<Socket>(); // the socket list used to store client sockets
    ArrayList<Shapes> shapes = new ArrayList<Shapes>(); // the list used to store all the shapes on the canvas
    ArrayList<ObjectOutputStream> ObjectOutputs = new ArrayList<ObjectOutputStream>();
    ArrayList<ObjectInputStream> ObjectInputs = new ArrayList<ObjectInputStream>();
    ArrayList<String> userID = new ArrayList<String>();
    int managerIndex;

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
            System.out.println("receive client");

            // put client socket into the socket list
            sockets.add(client);
            ObjectInputs.add(new ObjectInputStream(client.getInputStream()));
            ObjectOutputs.add(new ObjectOutputStream(client.getOutputStream()));


            // if the user is the first in the server, give
            // the user the manager authority
            if (sockets.size()==1) {
                ObjectOutputStream oos = ObjectOutputs.get(sockets.size()-1);
                oos.writeObject(new Message("authorization", "server"));
                System.out.println("send manager authorization");
                // record the position of manager socket in list
                managerIndex = sockets.size()-1;
                oos.flush();
            }
            // if it is the normal client, ask manager whether to let the client join
            else {
                ObjectOutputStream oos = ObjectOutputs.get(managerIndex);
                System.out.println("send request to manager: "+ userID.get(managerIndex));
                oos.writeObject(new JoinRequest("request", "server", client.getInetAddress().toString(),sockets.size()-1 ));
            }

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
                    System.out.println("-----"+m.message);

                    // if the server receive a hello message,
                    // get the sender's ID and store it in list
                    if (m.message.equals("Hello")) {
                        System.out.println("received a client: " + m.senderID);
                        userID.add(m.senderID);
                    }
                    // if the message is a joinReply from manager
                    // server has to tell the client whether is has been invited
                    if (m.message.equals("reply")) {
                        JoinReply reply = (JoinReply)m;
                        System.out.println("received reply: "+reply.agree);
                        ObjectOutputStream oos = ObjectOutputs.get(reply.socketNum);
                        if (reply.agree) {
                            oos.writeObject(new JoinResponse("response", "server", true ));
                            oos.flush();
                        }
                        else {
                            oos.writeObject(new JoinResponse("response", "server", false ));
                            oos.flush();
                        }

                    }

                        // after receivd hello from client, start to init
                        // client's canvas with previous shapes
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


                    // if the message is a shape, save it in the shape list
                    if(m instanceof Shapes) {
                        shapes.add((Shapes)m);
                    }

                    for (int i=0 ; i<sockets.size() ; i++) {

                        if (i == socketNum) {
                            continue;
                        }

                        // here we need to check whether the sockets list's size equal's to the outputStream list' size
                        // because sometimes, such situation would happen: socket has been created and pushed to the
                        // list, however, outputStream takes a bit longer time which would lead to a outOfBound error
                        // in line: ObjectOutputStream oos = ObjectOutputs.get(i) as the outputStream list has not
                        // been incremented yet
                        if(sockets.get(i)!=null && sockets.size() == ObjectOutputs.size()) {

                            ObjectOutputStream oos = ObjectOutputs.get(i);
                            oos.writeObject(m);
                            oos.flush();
                        }

                    }

                } catch (IOException | ClassNotFoundException e)  {
                    // if one client exited, set its socket position and I/O position in lists as null
                    System.out.println("client left: "+userID.get(socketNum));
                    sockets.set(socketNum, null);
                    ObjectOutputs.set(socketNum, null);
                    ObjectInputs.set(socketNum, null);
                    userID.set(socketNum, null);
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

            os.writeObject(shapes.get(i));
            os.flush();
        }
    }
}