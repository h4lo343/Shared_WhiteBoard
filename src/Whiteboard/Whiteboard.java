package Whiteboard;

import Message.Message;
import Message.normalShape;
import Message.Shapes;
import Message.textShape;

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
public class Whiteboard {
    Graphics2D g;
    BoardListener l;
    Socket s;
    InputStream is;
    OutputStream os;
    String userID;


    public static void main(String[] args) throws IOException {

        Whiteboard wb = new Whiteboard();
        wb.start();

    }

    //"100.93.54.162"
    public void start() throws IOException {

        // connect to the socket and set up relevant I/O stream
        Socket s = new Socket(InetAddress.getLocalHost(), 8888);
        this.s = s;
        this.is = s.getInputStream();
        this.os = s.getOutputStream();

        // init the listener
        BoardListener listener = new BoardListener();
        this.l = listener;
        boardUI();

        // create a random userName, and set it to class and listener
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            char c = (char) (new Random().nextInt(26) + 'a');
            sb.append(c);
        }
        String userID = new String(sb);
        this.userID = userID;
        listener.setUserID(userID);

        // open a receiver
        Receive r = new Receive(s, l);
        r.start();
    }

    public void boardUI() throws IOException {

        /*
         * init the overall whiteboard interface
         */
        JFrame board = new JFrame();
        board.setTitle("Shared Whiteboard");
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

            }
        });

        // add the open funciton
        openCanva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
        l.setGraphSave(graphSave);
        l.setGraph(g);
        l.setOutPutStream(this.os);

        // when codes reach here, it means the client has started all the GUI and monitor thread
        // send hello to server
        // invoke method on listener, send hello to server
        l.sendHello();
    }

    // the class for board to receive message from server
    public class Receive extends Thread {

        ObjectInputStream ois;
        BoardListener listener;

        public Receive(Socket s, BoardListener listener) throws IOException {
            this.ois = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
            this.listener = listener;
        }

        @Override
        public void run() {
            while (true) {
                try {

                    Message m = (Message) this.ois.readObject();

                    // if the message is a shape, then it must be the shaped drawn by other peer
                    // draw these shapes on its own canvas using different drawing methods
                    if (m instanceof Shapes) {
                        System.out.println(m.message);
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

                                case "Rectangle":
                                    this.listener.drawRectangle(shape.x, shape.x1, shape.y, shape.y1, shape.color);

                                default:
                                    return;
                            }
                        }
                        if (m instanceof textShape) {
                            textShape text = (textShape) m;
                            System.out.println(text.text);
                            this.listener.drawText(text.x, text.y, text.text, text.color);
                        }
                    }
                    // do other things if the message is not a shapes message (like updating chat window)
                    else {
                        String message = m.message;
                        switch (message) {
                            case "chat":
                        }
                    }


                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}