package mychat_paintroom210323_V2;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Client {
	private InputStream input;
	private OutputStream output;
	private JTextArea jta1;//消息接受框
	private JTextArea jta2;//消息发送框
	private JPanel paintBoard;
	private String clientName;
	private String nowButton="直线";//表示目前所选的绘制功能按钮（默认为绘制直线）
	private int[]shapePoint=new int[4];
	public Graphics g;
	
	public static void main(String[]arguments) throws IOException{
		Client client=new Client();
		client.start();
	}

	private void start() throws IOException {
		Random ran=new Random();
		clientName=(ran.nextInt(8999)+1000)+"";

		System.out.println(clientName+"已上线");
		//连接服务器
		Socket soc=new Socket("10.13.97.220",8888);
		//获取IO
		input=soc.getInputStream();
		output=soc.getOutputStream();

		System.out.println("已获取IO");
		
		
		//收消息
		Receive rec=new Receive();
		rec.start();
		
		showFrame();
		System.out.println("打开页面");
	}
	
	private void showFrame() {
		JFrame jf=new JFrame(clientName+"的客户端");
		jf.setSize(1000,500);
		jf.setDefaultCloseOperation(3);
		jf.setLocationRelativeTo(null);
		jf.setResizable(false);

		GridLayout grid=new GridLayout(1,2);
		jf.setLayout(grid);
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		//聊天界面
		JPanel jpLeft=new JPanel();
		
		JLabel jlb1=new JLabel("消息窗口");
		jpLeft.add(jlb1);
		//接收消息框
		jta1=new JTextArea(9,40);
		jta1.setLineWrap(true);//设置文本框内容自动换行
		jta1.setWrapStyleWord(true);//设置文本框内容在单词结束处换行
		jta1.append("开始聊天吧~");
		jta1.setEditable(false);//聊天框内容不可修改

		JScrollPane jsp1=new JScrollPane(jta1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		jpLeft.add(jsp1);
		JLabel jlb2=new JLabel("输入窗口");
		jpLeft.add(jlb2);
		//发送消息框
		jta2=new JTextArea(9,40);
		jta2.setLineWrap(true);
		jta2.setWrapStyleWord(true);
		
		JScrollPane jsp2=new JScrollPane(jta2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		jpLeft.add(jsp2);
		
		JButton jb1=new JButton("发送");
		jpLeft.add(jb1);
		JButton jb2=new JButton("取消");
		jpLeft.add(jb2);
		
		//绘画界面
		JPanel jpRight=new JPanel();
		BorderLayout board=new BorderLayout();
		jpRight.setLayout(board);
		paintBoard=new JPanel();
		paintBoard.setBackground(Color.white);
		
		JPanel buttonBoard=new JPanel();
		buttonBoard.setPreferredSize(new Dimension(80,0));
		
		String[]buttonNames={"直线","圆形","矩形","铅笔","清空"};
		JButton[]jbtList=new JButton[buttonNames.length];
		for(int i=0;i<buttonNames.length;i++){
			jbtList[i]=new JButton(buttonNames[i]);
			buttonBoard.add(jbtList[i]);
		}
		
		Color[]colors={Color.red,Color.yellow,Color.blue,Color.green,Color.black,Color.white};
		String[]colorButtonNames={"红","黄","蓝","绿","黑","白"};
		JButton[]CjbtList=new JButton[colorButtonNames.length];
		for(int i=0;i<colorButtonNames.length;i++){
			CjbtList[i]=new JButton();
			CjbtList[i].setActionCommand(colorButtonNames[i]);
			CjbtList[i].setBackground(colors[i]);
			buttonBoard.add(CjbtList[i]);
		}
		
		jpRight.add(paintBoard,BorderLayout.CENTER);
		jpRight.add(buttonBoard,BorderLayout.EAST);
		
		jf.add(jpLeft);
		jf.add(jpRight);
		
		//添加监听器
		Listener lis=new Listener(jta1,jta2);
		jb1.addActionListener(lis);
		jb2.addActionListener(lis);
		for(int i=0;i<buttonNames.length;i++){
			jbtList[i].addActionListener(lis);
		}
		for(int i=0;i<colorButtonNames.length;i++){
			CjbtList[i].addActionListener(lis);
		}
		
		paintBoard.addMouseListener(lis);
		paintBoard.addMouseMotionListener(lis);
		
		jf.setVisible(true);
		
		g=paintBoard.getGraphics();
		//将Graphics转为Graphics2D
		Graphics2D g2d=(Graphics2D) g;
		g2d.setStroke(new BasicStroke(3.0f));
	}
	
	class Listener implements ActionListener,MouseListener,MouseMotionListener{
		private JTextArea jta2;
		private JTextArea jta1;
		
		public Listener(JTextArea jta1,JTextArea jta2){
			this.jta1=jta1;
			this.jta2=jta2;
		}

		public void actionPerformed(ActionEvent e) {
			String name=e.getActionCommand();
			switch(name){
			case"发送":sendMsg();break;
			case"直线":nowButton=name;break;
			case"圆形":nowButton=name;break;
			case"矩形":nowButton=name;break;
			case"铅笔":nowButton=name;break;
			case"清空":paintBoard.paint(g);sendSimpleOP("清空画布");
			case"取消":jta2.setText("");break;
			case"红":System.out.println("change to red");g.setColor(Color.red);sendColor(Color.red.hashCode());break;
			case"黄":g.setColor(Color.yellow);sendColor(Color.yellow.hashCode());break;
			case"蓝":g.setColor(Color.blue);sendColor(Color.blue.hashCode());break;
			case"绿":g.setColor(Color.green);sendColor(Color.green.hashCode());break;
			case"黑":g.setColor(Color.black);sendColor(Color.black.hashCode());break;
			case"白":g.setColor(Color.white);sendColor(Color.white.hashCode());break;
			}
		}
		
		//只需要发送一个字符串的操作
		private void sendSimpleOP(String OP) {
			try {
				output.write(OP.getBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		private void sendColor(int colorHashCode) {
			try {
				String OP="更改颜色";
				output.write(OP.getBytes());
				
				//发送颜色哈希值
				output.write(getByte(colorHashCode));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		//发送消息
		public void sendMsg(){
			try {
				String OP="发送文字";//表示发送文字操作（一定要是8个字节长度的操作）
				output.write(OP.getBytes());
				
				//发送用户名（长度为4个字节）
				output.write(clientName.getBytes());
				
				//获得发送文本的字节长度
				int msglen=jta2.getText().getBytes().length;
				//发送字节长度（方便接收方定义用于接收数据的byte数组大小）
				output.write(getByte(msglen));
				//发送文本内容
				output.write(jta2.getText().getBytes());
				System.out.println("发送："+jta2.getText());
				
				//获得当前时间
				SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
				Date date = new Date(System.currentTimeMillis());
				//在多行文本框中显示发送的消息
				jta1.append("\n\r"+"我	"+formatter.format(date)+"\n\r"+jta2.getText());
				//将滚动条拖到最下方
				jta1.setCaretPosition(jta1.getText().length());
				//清空输入框的文本内容
				jta2.setText("");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		public void sendShape(){
			try {
				switch(nowButton){
					case"直线":
						System.out.println("发送直线");
						output.write("发送直线".getBytes());//表示发送文字操作（一定要是8个字节长度的操作）
						break;
					case"圆形":
						System.out.println("发送圆形");
						output.write("发送圆形".getBytes());//表示发送文字操作（一定要是8个字节长度的操作）
						break;
					case"矩形":
						System.out.println("发送矩形");
						output.write("发送矩形".getBytes());//表示发送文字操作（一定要是8个字节长度的操作）
						break;
					case"铅笔":
						System.out.println("发送铅笔");
						output.write("发送铅笔".getBytes());//表示发送文字操作（一定要是8个字节长度的操作）
						break;
				}
				
				//发送2个点（4个坐标）
				for(int i=0;i<4;i++){
					byte[]bt=getByte(shapePoint[i]);
					output.write(bt);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void mouseClicked(MouseEvent e) {
			
		}

		public void mousePressed(MouseEvent e) {
			//记录鼠标按下的坐标
			shapePoint[0]=e.getX();
			shapePoint[1]=e.getY();
		}

		public void mouseReleased(MouseEvent e) {
			//记录鼠标松开的坐标
			shapePoint[2]=e.getX();
			shapePoint[3]=e.getY();
			
			//判断最后按下的是哪个图形按钮
			switch(nowButton){
			case"直线":
				System.out.println("直线"+shapePoint[0]+" "+shapePoint[1]+" "+shapePoint[2]+" "+shapePoint[3]);
				//绘制直线
				g.drawLine(shapePoint[0],shapePoint[1],shapePoint[2],shapePoint[3]);
				//调用发送图形方法
				sendShape();
				break;
			case"圆形":
				System.out.println("圆形"+shapePoint[0]+" "+shapePoint[1]+" "+shapePoint[2]+" "+shapePoint[3]);
				//记录圆形左上角坐标点，并计算其宽高
				int x1=Math.min(shapePoint[0],shapePoint[2]);
				int y1=Math.min(shapePoint[1],shapePoint[3]);
				int width=Math.abs(shapePoint[0]-shapePoint[2]);
				int height=Math.abs(shapePoint[1]-shapePoint[3]);
				shapePoint[0]=x1;
				shapePoint[1]=y1;
				shapePoint[2]=width;
				shapePoint[3]=height;
				//绘制椭圆
				g.fillOval(shapePoint[0],shapePoint[1],shapePoint[2],shapePoint[3]);
				//调用发送图形方法
				sendShape();
				break;
			case"矩形":
				//实现方法与画圆类似
				System.out.println("矩形"+shapePoint[0]+" "+shapePoint[1]+" "+shapePoint[2]+" "+shapePoint[3]);
				x1=Math.min(shapePoint[0],shapePoint[2]);
				y1=Math.min(shapePoint[1],shapePoint[3]);
				width=Math.abs(shapePoint[0]-shapePoint[2]);
				height=Math.abs(shapePoint[1]-shapePoint[3]);
				shapePoint[0]=x1;
				shapePoint[1]=y1;
				shapePoint[2]=width;
				shapePoint[3]=height;
				g.fillRect(shapePoint[0],shapePoint[1],shapePoint[2],shapePoint[3]);
				sendShape();
				break;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseDragged(MouseEvent e) {
			if(nowButton.equals("铅笔")){
				shapePoint[2]=shapePoint[0];
				shapePoint[3]=shapePoint[1];
				
				shapePoint[0]=e.getX();
				shapePoint[1]=e.getY();
				
				g.drawLine(shapePoint[0],shapePoint[1],shapePoint[2],shapePoint[3]);
				
				sendShape();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

	}
	
	public byte[] getByte(int number){
		byte[]bt=new byte[4];
		bt[0]=(byte) ((number>>0) & 0xff);
		bt[1]=(byte) ((number>>8) & 0xff);
		bt[2]=(byte) ((number>>16) & 0xff);
		bt[3]=(byte) ((number>>24) & 0xff);
		return bt;
	}
	
	public int getInt(byte[]bt){
		int number=(bt[3]& 0xff)<<24|
				(bt[2]& 0xff)<<16|
				(bt[1]& 0xff)<<8|
				(bt[0]& 0xff)<<0;
		return number;
	}

	class Receive extends Thread{
		
		public void run(){
				while(true){
					try {
						byte[]OP=new byte[8];
						input.read(OP);
						
						switch(new String(OP)){
						case"发送文字":readMsg();break;
						//只要是传输两个点的图形绘制操作都可以用这一条
						case"发送直线":readShape("发送直线");break;
						case"发送圆形":readShape("发送圆形");break;
						case"发送矩形":readShape("发送矩形");break;
						case"发送铅笔":readShape("发送铅笔");break;
						case"更改颜色":changeColor();break;
						case"清空画布":readSimpleOP("清空画布");break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		
		//只接收一个字符串的简单操作
		private void readSimpleOP(String OP) {
			switch(OP){
			case"清空画布":paintBoard.paint(g);break;
			}
		}

		//更改颜色
		private void changeColor() {
			try {
				byte[]color=new byte[4];
				input.read(color);
				g.setColor(new Color(getInt(color)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void readShape(String OP) {
			//只要是传输两个点的图形绘制操作都可以用这一条
			try {
				//更新两个点的坐标
				for(int i=0;i<4;i++){
					byte[]bt=new byte[4];
					input.read(bt);
					shapePoint[i]=getInt(bt);
				}
				switch(OP){
				case"发送直线":
					g.drawLine(shapePoint[0],shapePoint[1],shapePoint[2],shapePoint[3]);break;
				case"发送圆形":
					g.fillOval(shapePoint[0],shapePoint[1],shapePoint[2],shapePoint[3]);break;
				case"发送矩形":
					g.fillRect(shapePoint[0],shapePoint[1],shapePoint[2],shapePoint[3]);break;
				case"发送铅笔":
					g.drawLine(shapePoint[0],shapePoint[1],shapePoint[2],shapePoint[3]);break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void readMsg() {
			try {
				//接收消息发送者编号
				byte[]otherName=new byte[4];
				input.read(otherName);
				
				//接收发送文本内容的长度
				byte[]bt1=new byte[4];
				input.read(bt1);
				int reclen=getInt(bt1);
				System.out.println("receive byte:"+reclen);
				
				//根据接收到的文本内容字节长度创建用于接收消息的byte数组
				byte[]bt2=new byte[reclen];
				//接收文本内容
				input.read(bt2);
				String recmsg=new String(bt2);
				System.out.println("接收到："+recmsg);
				//获得当前时间
				SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
				Date date = new Date(System.currentTimeMillis());
				//将接收的信息加入多行文本框
				jta1.append("\n\r"+"用户"+new String(otherName)+"	"+formatter.format(date)+"\n\r"+recmsg);
				//将滚动条拖到最下方
				jta1.setCaretPosition(jta1.getText().length());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
