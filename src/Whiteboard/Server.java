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
    LinkedList<String> userID = new LinkedList<String>();


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

            // put client socket into the socket list
            sockets.add(client);
            ObjectInputs.add(new ObjectInputStream(client.getInputStream()));
            ObjectOutputs.add(new ObjectOutputStream(client.getOutputStream()));

            // new ObjectInputStream， new ObjectOutputStream usually time sometime, for
            // the list consistent, wait for 1 second
            Thread.sleep(1000);


            // if the user is the first in the server, give
            // the user the manager authority
            if (sockets.size()==1) {
                ObjectOutputStream oos = ObjectOutputs.get(sockets.size()-1);
                oos.writeObject(new Message("authorization", "server"));
                // record the position of manager socket in list
                managerIndex = sockets.size()-1;
                oos.flush();
            }
            // if it is the normal client, ask manager whether to let the client join
            else {
                ObjectOutputStream oos = ObjectOutputs.get(managerIndex);
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

                    // if the server receive a hello message,
                    // get the sender's ID and store it in list
                    if (m.message.equals("Hello")) {
                        System.out.println("received a client: " + m.senderID);


                        boolean duplicate = false;
                        // check whether the username already exist
                        for (int i=0;i<=userID.size()-1;i++) {
                            if (userID.get(i) != null) {
                                if (userID.get(i).equals(m.senderID)) {
                                    ObjectOutputStream oos = ObjectOutputs.get(socketNum);
                                    oos.writeObject(new Message("duplicate", m.senderID));
                                    oos.flush();
                                    System.out.println("send duplicate delete");
                                    userID.add(null);
                                    ObjectOutputs.set(socketNum, null);
                                    sockets.set(socketNum, null);
                                    ObjectInputs.set(socketNum, null);
                                    duplicate = true;
                                    break;
                                }
                            }
                        }

                        // if it is a duplicate username, just end the monitor thread
                        if (duplicate == true) {
                            break;
                        }

                        userID.add(m.senderID);

                        // after receivd hello from client, start to init
                        // client's canvas with previous shapes
                        Thread init = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // load new client with current stored shapes
                                try {
                                    Init(sockets.size()-1);
                                } catch (IOException | InterruptedException e) {

                                }
                            }
                        });

                        init.start();

                        // after receive a hello from a client, update the userID
                        // and send all clients the latest id list
                        for (int i = 0; i<sockets.size(); i++) {
                            if(sockets.get(i)!=null && sockets.size() == ObjectOutputs.size()) {
                                ObjectOutputStream oos = ObjectOutputs.get(i);
                                for (int j =0;j<userID.size();j++) {
                                    if(userID.get(j)!=null) {
                                        oos.writeObject(new UserListUpdate("updateUserList", "server", userID.get(j), "add"));
                                        oos.flush();
                                    }
                                }
                            }
                        }

                    }

                    // if the server received new canvas message from manager
                    // it should broadcast it to every client to empty their canvas
                    // and clean the shape list in the server
                    if (m.message.equals("new")) {
                        for (int i = 0; i<sockets.size(); i++) {
                            if(sockets.get(i)!=null && sockets.size() == ObjectOutputs.size()) {
                                ObjectOutputStream oos = ObjectOutputs.get(i);
                                oos.writeObject(new Message("new", "server"));
                                oos.flush();
                                shapes.clear();
                            }
                        }
                    }


                    // response to kick request from manager, remove that user
                    // let send him an inform
                    if (m.message.equals("kick")) {
                        KickRequest k = (KickRequest)m;
                        System.out.println("receive kick for: "+k.userID);

                        int kickedIndex=0;
                        // first look for that user in list
                        for (int i =0;i<userID.size();i++) {
                            if (k.userID.equals(userID.get(i))){
                                kickedIndex=i;
                            }
                        }
                        System.out.println("send kick to: "+ userID.get(kickedIndex));
                        ObjectOutputStream oos = ObjectOutputs.get(kickedIndex);
                        oos.writeObject(new Message("kick",k.userID));
                        oos.flush();

                    }

                    // if the message is a joinReply from manager
                    // server has to tell the client whether is has been invited
                    if (m.message.equals("reply")) {
                        JoinReply reply = (JoinReply)m;
                        ObjectOutputStream oos = ObjectOutputs.get(reply.socketNum);
                        if (reply.agree) {
                            oos.writeObject(new JoinResponse("response", "server", true ));
                            oos.flush();
                        }
                        else {
                            oos.writeObject(new JoinResponse("response", "server", false ));
                            oos.flush();
                            sockets.set(reply.socketNum, null);
                            ObjectOutputs.set(reply.socketNum, null);
                            ObjectInputs.set(reply.socketNum, null);
                        }

                    }

                    // receive the chat message and send it to every clients
                    if (m.message.equals("chat")) {
                        ChatMessage chat = new ChatMessage("chat", m.senderID,  ((ChatRequest)m).content);
                        for (int i = 0; i<sockets.size(); i++) {
                            if(sockets.get(i)!=null && sockets.size() == ObjectOutputs.size()) {
                                ObjectOutputStream oos = ObjectOutputs.get(i);
                                oos.writeObject(chat);
                                oos.flush();
                            }
                        }
                    }

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

                            ObjectOutputStream oos =   ObjectOutputs.get(i);
                            oos.writeObject(m);
                            oos.flush();
                        }

                    }

                } catch (IOException | ClassNotFoundException e)  {

                    // this if is for user who are rejected by manager at first
                    // for those clients, their user name has not been added to userID list,
                    // we should set a if to avoid outOfBound exception
                    if (socketNum> userID.size()-1) {
                        // add a null in userID, to keep the userID list consistent with other 3 lists
                        userID.add(null);
                        sockets.set(socketNum, null);
                        ObjectOutputs.set(socketNum, null);
                        ObjectInputs.set(socketNum, null);
                        break;}

                        // if manager left, shut down server and inform every client to close the application
                        if (socketNum == 0) {
                            try {

                                for (int i = 1; i<sockets.size(); i++) {
                                    if(sockets.get(i)!=null && sockets.size() == ObjectOutputs.size()) {
                                        ObjectOutputStream oos =  ObjectOutputs.get(i);
                                        oos.writeObject(new ManagerLeave("leave", "server"));
                                        oos.flush();
                                        System.out.println("manager left, shut down the server");
                                        System.exit(0);
                                    }
                                }
                                System.exit(0);
                            } catch (Exception e2) {

                            }
                        }
                        // if one client exited, set its socket position and I/O position in lists as null
                        if (socketNum<=sockets.size()-1) {
                            System.out.println("client left: "+userID.get(socketNum));
                        }
                        sockets.set(socketNum, null);
                        ObjectOutputs.set(socketNum, null);
                        ObjectInputs.set(socketNum, null);

                        //once a client leave, we have to send all other clients the updated userlist
                        try {
                            for (int i = 0; i<sockets.size(); i++) {
                                if(sockets.get(i)!=null && sockets.size() == ObjectOutputs.size()) {
                                    ObjectOutputStream oos =  ObjectOutputs.get(i);
                                    oos.writeObject(new UserListUpdate("updateUserList", "server", userID.get(socketNum),"delete"));
                                    oos.flush();
                                }
                            }
                        } catch (Exception e2) {

                        }
                        userID.set(socketNum, null);

                        break;

                }
            }
        }
    }

    // if a client joined, send it all the shapes stored in shapelist to init
    // the canvas of that client
    public void Init(int socketNum) throws IOException, InterruptedException {
        Thread.sleep(2000);
        ObjectOutputStream os =  ObjectOutputs.get(socketNum);
        for (int i = 0; i<shapes.size();i++) {

            os.writeObject(shapes.get(i));
            os.flush();
        }
    }
}