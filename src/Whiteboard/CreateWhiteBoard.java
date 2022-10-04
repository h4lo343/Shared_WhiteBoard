package Whiteboard;

import Message.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

/**
 * @author XIANGNAN ZHOU_1243072
 * @date 2022/9/27 22:32
 */
public class CreateWhiteBoard {
    Graphics2D g;
    BoardListener l;
    Socket s;
    InputStream is;
    OutputStream os;

    public static void main(String[] args) throws IOException {

        CreateWhiteBoard wb = new CreateWhiteBoard();
        wb.start();

    }


    public void start() throws IOException {
        Socket s = new Socket("100.93.54.162", 8888);

        this.s = s;
        this.is = s.getInputStream();
        this.os = s.getOutputStream();

        // init the listener
        BoardListener listener = new BoardListener();
        this.l = listener;
        l.setOutPutStream(this.os);

        // open a receiver, the reason to put it in a thread is:
        // if not do this, it would block the boot of gut in the next lines
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // open a receiver
                Receive r = null;
                try {
                    r = new Receive(is, l);
                    r.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();


        Thread checker = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean showed = false;
                while(true) {
                    if(l.authorized==false&&l.approved==false) {
                        if (showed == false) {
                            // only show once of the window
                            JOptionPane.showMessageDialog(null, "wait to be approved by manager or be the manager");
                            showed = true;
                        }
                    }
                    if (l.authorized == true) {
                        try {
                            boardUI();
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        checker.start();


    }

    public void boardUI() throws IOException {

        /*
         * init the overall whiteboard interface
         */
        JFrame board = new JFrame();
        board.setTitle("Shared CreateWhiteBoard");
        board.setSize(1200, 800);
        board.setLocationRelativeTo(null);
        board.setVisible(true);
        board.setResizable(false);
        // use border layout
        board.setLayout(new BorderLayout());

        // set default color
        l.setColor(Color.black);

        // create the image I/O and related graphics2D object
        BufferedImage bi = new BufferedImage(1200, 800, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphSave = bi.createGraphics();
        graphSave.setBackground(Color.WHITE);
        graphSave.fillRect(0, 0, 1200, 800);

        /*
         * add the main canva
         */
        JPanel canva = new JPanel();
        canva.setPreferredSize(new Dimension(1200, 800));
        canva.setBackground(Color.white);
        board.add(canva, BorderLayout.CENTER);

        canva.addMouseListener(l);
        canva.addMouseMotionListener(l);



        /*
         * draw the menu
         */
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menu.add(fileMenu);
        JMenuItem save = new JMenuItem("Save Canva");
        JMenuItem saveAsJpg = new JMenuItem("Save Canva As JPG");
        JMenuItem saveAsPng = new JMenuItem("Save Canva As PNG");
        JMenuItem newCanva = new JMenuItem("New Canva");
        JMenuItem closeCanva = new JMenuItem("Close Cantva");
        JMenuItem openCanva = new JMenuItem("Open Canva");


        // add save as function to the save as button
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // checker whether the operator is manager
                if (l.authorized == false) {
                    JOptionPane.showMessageDialog(null, "wait to be approved by manager or be the manager");
                }
            }
        });

        newCanva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // checker whether the operator is manager
                if (l.authorized == false) {
                    JOptionPane.showMessageDialog(null, "wait to be approved by manager or be the manager");
                }
                canva.repaint();
            }
        });

        // add the open funciton
        openCanva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // checker whether the operator is manager
                if (l.authorized == false) {
                    JOptionPane.showMessageDialog(null, "wait to be approved by manager or be the manager");
                }
                // empty the current canva
                canva.repaint();

                try {

                    // start a file choosing window
                    JFileChooser open = new JFileChooser();
                    open.setDialogTitle("Open");
                    open.showSaveDialog(null);
                    File newFile = open.getSelectedFile();
                    Image read = ImageIO.read(open.getSelectedFile());
                    l.graph.drawImage(read, 0, 0, new ImageObserver() {
                        @Override
                        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                            return false;
                        }
                    });
                    l.graphSave.drawImage(read, 0, 0, new ImageObserver() {
                        @Override
                        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                            return false;
                        }
                    });


                } catch (Exception e1) {
                    System.out.println(e1);
                }
            }
        });

        // add save current canvas as jpg function
        saveAsJpg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // checker whether the operator is manager
                if (l.authorized == false) {
                    JOptionPane.showMessageDialog(null, "wait to be approved by manager or be the manager");
                }
                JFileChooser saveAs = new JFileChooser("Save As .jpg");
                saveAs.setDialogTitle("Save as JPG");
                saveAs.showSaveDialog(null);

                try {
                    File file = saveAs.getSelectedFile();
                    String name = saveAs.getName(file);

                    ImageIO.write(bi, "PNG", new File(saveAs.getCurrentDirectory(), name + ".jpg"));
                } catch (Exception e2) {
                    System.out.println(e2);
                }

            }
        });

        // add save current canvas as png function
        saveAsPng.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // checker whether the operator is manager
                if (l.authorized == false) {
                    JOptionPane.showMessageDialog(null, "wait to be approved by manager or be the manager");
                }
                JFileChooser saveAs = new JFileChooser("Save As .png");
                saveAs.setDialogTitle("Save as PNG");
                saveAs.showSaveDialog(null);

                try {
                    File file = saveAs.getSelectedFile();
                    String name = saveAs.getName(file);

                    ImageIO.write(bi, "png", new File(saveAs.getCurrentDirectory(), name + ".png"));
                } catch (Exception e2) {
                    System.out.println(e2);
                }

            }
        });

        fileMenu.add(save);
        fileMenu.add(saveAsPng);
        fileMenu.add(saveAsJpg);
        fileMenu.add(newCanva);
        fileMenu.add(closeCanva);
        fileMenu.add(openCanva);

        board.setJMenuBar(menu);

        /*
         * draw the function bar
         */
        JPanel functionBar = new JPanel();
        functionBar.setBackground(Color.gray);
        board.add(functionBar, BorderLayout.NORTH);
        String[] functionButtons = {"Pen", "Line", "Rectangle", "Circle", "Triangle", "Eraser"};

        // add buttons to the functionbar
        for (int i = 0; i <= functionButtons.length - 1; i++) {
            JButton button = new JButton(functionButtons[i]);
            functionBar.add(button);
            button.addActionListener(l);
        }
        JLabel text = new JLabel("input your text:");
        functionBar.add(text);
        JTextField textInput = new JTextField();
        textInput.setPreferredSize(new Dimension(170, 27));
        functionBar.add(textInput);

        // once click the text button
        // the content in text area would be passed
        // to listener and penType would be set as "Text"
        JButton textButton = new JButton("Text");
        textButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = textInput.getText();
                l.setPenType("Text");
                l.setText(content);
            }
        });
        functionBar.add(textButton);

        /*
         * Draw the Color Bar
         */

        JPanel colorBar = new JPanel();
        JLabel colorLabel = new JLabel("Choose Color:");
        colorBar.add(colorLabel);
        colorBar.setBackground(Color.gray);
        board.add(colorBar, BorderLayout.SOUTH);

        // Add color button
        Color[] colors = {Color.BLACK, Color.gray, Color.pink, Color.red, Color.CYAN, Color.green, Color.BLUE,
                Color.yellow, Color.DARK_GRAY, Color.ORANGE, new Color(46, 64, 83), Color.magenta, Color.LIGHT_GRAY, new Color(108, 52, 131), new Color(203, 67, 53)
                , new Color(120, 66, 18), new Color(52, 152, 219), new Color(131, 145, 146)

        };

        for (int i = 0; i <= colors.length - 1; i++) {

            JButton button = new JButton();
            button.setBackground(colors[i]);
            button.setPreferredSize(new Dimension(40, 40));

            button.addActionListener(l);
            colorBar.add(button);
        }


        /*
         * Draw the chatroom interface
         */
        JPanel chatWindow = new JPanel();
        chatWindow.setBackground(Color.lightGray);
        chatWindow.setPreferredSize(new Dimension(200, 1000));
        board.add(chatWindow, BorderLayout.WEST);

        // draw the message window
        JLabel message = new JLabel("Message: ");
        chatWindow.add(message);
        JTextArea messageWindow = new JTextArea();
        messageWindow.setPreferredSize(new Dimension(200, 400));
        messageWindow.setWrapStyleWord(true);
        messageWindow.setLineWrap(true);
        messageWindow.setEditable(false);
        messageWindow.append("Message would be shown here:");
        chatWindow.add(messageWindow);

        // draw the input window
        JButton send = new JButton("send");
        send.setPreferredSize(new Dimension(70, 29));
        send.setSize(1, 1);
        chatWindow.add(send);
        JTextArea inputWindow = new JTextArea();
        inputWindow.setBounds(30, 100, 100, 100);
        inputWindow.setPreferredSize(new Dimension(200, 300));
        chatWindow.add(inputWindow);
        inputWindow.setLineWrap(true);
        inputWindow.setWrapStyleWord(true);

        /*
         * Draw the userList
         */
        JPanel userList = new JPanel();
        userList.setPreferredSize(new Dimension(200, 1000));
        userList.setBackground(Color.lightGray);
        board.add(userList, BorderLayout.EAST);
        JLabel list = new JLabel("User List:");
        userList.add(list);
        JTextArea listArea = new JTextArea();
        listArea.setPreferredSize(new Dimension(200, 400));
        userList.add(listArea);
        listArea.setEditable(false);
        listArea.setWrapStyleWord(true);
        listArea.setLineWrap(true);


        // create graphics object for the canvas
        // and do some the basic settings
        Graphics2D g = (Graphics2D) canva.getGraphics();
        this.g = g;
        // set default stroke
        g.setStroke(new BasicStroke(3));
        graphSave.setStroke(new BasicStroke(3));
        // set default color
        l.setColor(Color.BLACK);
        l.setGraphSave(graphSave);
        l.setGraph(g);


        // create a random username, assign it to listener
        StringBuffer sb = new StringBuffer();
        for (int i=0;i<=5;i++) {
            sb.append(((char)(new Random().nextInt(26)+'A')));
        }
        String id = new String(sb);
        l.setSenderID(id);
        l.sendHello();

    }

    // the class for board to receive message from server
    public class Receive extends Thread {

        ObjectInputStream ois;
        BoardListener listener;

        public Receive(InputStream is, BoardListener listener) throws IOException {
            this.listener = listener;

            this.ois = new ObjectInputStream(new BufferedInputStream(is));

        }

        @Override
        public void run() {
            System.out.println("receiver started");
            while (true) {
                try {
                    Message m = (Message) this.ois.readObject();

                    // if the message is a shape, then it must be the shaped drawn by other peer
                    // draw these shapes on its own canvas using different drawing methods
                    if (m instanceof Shapes) {
                        if (m instanceof normalShape) {
                            normalShape shape = (normalShape) m;
                            String type = m.message;
                            // the switch is used to judge the type of shape
                            switch (type) {
                                case "Line":
                                    this.listener.drawStraightLine(shape.x, shape.y, shape.x1, shape.y1, shape.color);
                                    break;

                                case "Pen":
                                    this.listener.drawPen(shape.x, shape.y, shape.x1, shape.y1, shape.color);
                                    break;

                                case "Triangle":
                                    this.listener.drawTriangle(shape.x, shape.x1, shape.y, shape.y1, shape.color);
                                    break;

                                case "Circle":
                                    this.listener.drawCircle(shape.x, shape.x1, shape.y, shape.y1, shape.color);
                                    break;

                                case "Rectangle":
                                    this.listener.drawRectangle(shape.x, shape.x1, shape.y, shape.y1, shape.color);
                                    break;

                                case "Eraser":
                                    this.listener.drawEraser(shape.x,shape.y,shape.x1,shape.y1);
                                    break;
                            }
                        }

                        else if (m instanceof textShape) {
                            textShape text = (textShape) m;
                            System.out.println(text.text);
                            this.listener.drawText(text.x, text.y, text.text, text.color);
                        }

                        }
                    // if the message is not in shape type
                    // solve them in the else
                    else {
                        String message = m.message;
                        switch (message) {
                            // if it is an authorization message
                            // set the authorized state in listener as true
                            // to indicate this client is a manager
                            case "authorization" :
                                l.setAuthorized();
                                JOptionPane.showMessageDialog(null, "you are the manager");
                                break;

                            case "request":

                                String ip = ((JoinRequest)m).joiner;
                                System.out.println("get access request from server for: "+ ((JoinRequest)m).joiner);
                                int input = JOptionPane.showConfirmDialog(null, "client: "+ ip+" wants to join the board:","Agree or not", JOptionPane.YES_NO_OPTION);
                                if (input == 0) {
                                    l.sendReply(((JoinRequest)m).socketNum,true);
                                }
                                else {
                                    l.sendReply(((JoinRequest)m).socketNum,false);
                                }
                                break;

                            case "response":
                                System.out.println("received response: "+((JoinResponse) m).agree);
                                JoinResponse response = (JoinResponse) m;
                                System.out.println(response.agree);
                        }
                    }


                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}