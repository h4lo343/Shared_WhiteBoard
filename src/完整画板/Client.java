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
	private JTextArea jta1;//��Ϣ���ܿ�
	private JTextArea jta2;//��Ϣ���Ϳ�
	private JPanel paintBoard;
	private String clientName;
	private String nowButton="ֱ��";//��ʾĿǰ��ѡ�Ļ��ƹ��ܰ�ť��Ĭ��Ϊ����ֱ�ߣ�
	private int[]shapePoint=new int[4];
	public Graphics g;
	
	public static void main(String[]arguments) throws IOException{
		Client client=new Client();
		client.start();
	}

	private void start() throws IOException {
		Random ran=new Random();
		clientName=(ran.nextInt(8999)+1000)+"";

		System.out.println(clientName+"������");
		//���ӷ�����
		Socket soc=new Socket("10.13.97.220",8888);
		//��ȡIO
		input=soc.getInputStream();
		output=soc.getOutputStream();

		System.out.println("�ѻ�ȡIO");
		
		
		//����Ϣ
		Receive rec=new Receive();
		rec.start();
		
		showFrame();
		System.out.println("��ҳ��");
	}
	
	private void showFrame() {
		JFrame jf=new JFrame(clientName+"�Ŀͻ���");
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
		
		//�������
		JPanel jpLeft=new JPanel();
		
		JLabel jlb1=new JLabel("��Ϣ����");
		jpLeft.add(jlb1);
		//������Ϣ��
		jta1=new JTextArea(9,40);
		jta1.setLineWrap(true);//�����ı��������Զ�����
		jta1.setWrapStyleWord(true);//�����ı��������ڵ��ʽ���������
		jta1.append("��ʼ�����~");
		jta1.setEditable(false);//��������ݲ����޸�

		JScrollPane jsp1=new JScrollPane(jta1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		jpLeft.add(jsp1);
		JLabel jlb2=new JLabel("���봰��");
		jpLeft.add(jlb2);
		//������Ϣ��
		jta2=new JTextArea(9,40);
		jta2.setLineWrap(true);
		jta2.setWrapStyleWord(true);
		
		JScrollPane jsp2=new JScrollPane(jta2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		jpLeft.add(jsp2);
		
		JButton jb1=new JButton("����");
		jpLeft.add(jb1);
		JButton jb2=new JButton("ȡ��");
		jpLeft.add(jb2);
		
		//�滭����
		JPanel jpRight=new JPanel();
		BorderLayout board=new BorderLayout();
		jpRight.setLayout(board);
		paintBoard=new JPanel();
		paintBoard.setBackground(Color.white);
		
		JPanel buttonBoard=new JPanel();
		buttonBoard.setPreferredSize(new Dimension(80,0));
		
		String[]buttonNames={"ֱ��","Բ��","����","Ǧ��","���"};
		JButton[]jbtList=new JButton[buttonNames.length];
		for(int i=0;i<buttonNames.length;i++){
			jbtList[i]=new JButton(buttonNames[i]);
			buttonBoard.add(jbtList[i]);
		}
		
		Color[]colors={Color.red,Color.yellow,Color.blue,Color.green,Color.black,Color.white};
		String[]colorButtonNames={"��","��","��","��","��","��"};
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
		
		//��Ӽ�����
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
		//��GraphicsתΪGraphics2D
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
			case"����":sendMsg();break;
			case"ֱ��":nowButton=name;break;
			case"Բ��":nowButton=name;break;
			case"����":nowButton=name;break;
			case"Ǧ��":nowButton=name;break;
			case"���":paintBoard.paint(g);sendSimpleOP("��ջ���");
			case"ȡ��":jta2.setText("");break;
			case"��":System.out.println("change to red");g.setColor(Color.red);sendColor(Color.red.hashCode());break;
			case"��":g.setColor(Color.yellow);sendColor(Color.yellow.hashCode());break;
			case"��":g.setColor(Color.blue);sendColor(Color.blue.hashCode());break;
			case"��":g.setColor(Color.green);sendColor(Color.green.hashCode());break;
			case"��":g.setColor(Color.black);sendColor(Color.black.hashCode());break;
			case"��":g.setColor(Color.white);sendColor(Color.white.hashCode());break;
			}
		}
		
		//ֻ��Ҫ����һ���ַ����Ĳ���
		private void sendSimpleOP(String OP) {
			try {
				output.write(OP.getBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		private void sendColor(int colorHashCode) {
			try {
				String OP="������ɫ";
				output.write(OP.getBytes());
				
				//������ɫ��ϣֵ
				output.write(getByte(colorHashCode));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		//������Ϣ
		public void sendMsg(){
			try {
				String OP="��������";//��ʾ�������ֲ�����һ��Ҫ��8���ֽڳ��ȵĲ�����
				output.write(OP.getBytes());
				
				//�����û���������Ϊ4���ֽڣ�
				output.write(clientName.getBytes());
				
				//��÷����ı����ֽڳ���
				int msglen=jta2.getText().getBytes().length;
				//�����ֽڳ��ȣ�������շ��������ڽ������ݵ�byte�����С��
				output.write(getByte(msglen));
				//�����ı�����
				output.write(jta2.getText().getBytes());
				System.out.println("���ͣ�"+jta2.getText());
				
				//��õ�ǰʱ��
				SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
				Date date = new Date(System.currentTimeMillis());
				//�ڶ����ı�������ʾ���͵���Ϣ
				jta1.append("\n\r"+"��	"+formatter.format(date)+"\n\r"+jta2.getText());
				//���������ϵ����·�
				jta1.setCaretPosition(jta1.getText().length());
				//����������ı�����
				jta2.setText("");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		public void sendShape(){
			try {
				switch(nowButton){
					case"ֱ��":
						System.out.println("����ֱ��");
						output.write("����ֱ��".getBytes());//��ʾ�������ֲ�����һ��Ҫ��8���ֽڳ��ȵĲ�����
						break;
					case"Բ��":
						System.out.println("����Բ��");
						output.write("����Բ��".getBytes());//��ʾ�������ֲ�����һ��Ҫ��8���ֽڳ��ȵĲ�����
						break;
					case"����":
						System.out.println("���;���");
						output.write("���;���".getBytes());//��ʾ�������ֲ�����һ��Ҫ��8���ֽڳ��ȵĲ�����
						break;
					case"Ǧ��":
						System.out.println("����Ǧ��");
						output.write("����Ǧ��".getBytes());//��ʾ�������ֲ�����һ��Ҫ��8���ֽڳ��ȵĲ�����
						break;
				}
				
				//����2���㣨4�����꣩
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
			//��¼��갴�µ�����
			shapePoint[0]=e.getX();
			shapePoint[1]=e.getY();
		}

		public void mouseReleased(MouseEvent e) {
			//��¼����ɿ�������
			shapePoint[2]=e.getX();
			shapePoint[3]=e.getY();
			
			//�ж�����µ����ĸ�ͼ�ΰ�ť
			switch(nowButton){
			case"ֱ��":
				System.out.println("ֱ��"+shapePoint[0]+" "+shapePoint[1]+" "+shapePoint[2]+" "+shapePoint[3]);
				//����ֱ��
				g.drawLine(shapePoint[0],shapePoint[1],shapePoint[2],shapePoint[3]);
				//���÷���ͼ�η���
				sendShape();
				break;
			case"Բ��":
				System.out.println("Բ��"+shapePoint[0]+" "+shapePoint[1]+" "+shapePoint[2]+" "+shapePoint[3]);
				//��¼Բ�����Ͻ�����㣬����������
				int x1=Math.min(shapePoint[0],shapePoint[2]);
				int y1=Math.min(shapePoint[1],shapePoint[3]);
				int width=Math.abs(shapePoint[0]-shapePoint[2]);
				int height=Math.abs(shapePoint[1]-shapePoint[3]);
				shapePoint[0]=x1;
				shapePoint[1]=y1;
				shapePoint[2]=width;
				shapePoint[3]=height;
				//������Բ
				g.fillOval(shapePoint[0],shapePoint[1],shapePoint[2],shapePoint[3]);
				//���÷���ͼ�η���
				sendShape();
				break;
			case"����":
				//ʵ�ַ����뻭Բ����
				System.out.println("����"+shapePoint[0]+" "+shapePoint[1]+" "+shapePoint[2]+" "+shapePoint[3]);
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
			if(nowButton.equals("Ǧ��")){
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
						case"��������":readMsg();break;
						//ֻҪ�Ǵ����������ͼ�λ��Ʋ�������������һ��
						case"����ֱ��":readShape("����ֱ��");break;
						case"����Բ��":readShape("����Բ��");break;
						case"���;���":readShape("���;���");break;
						case"����Ǧ��":readShape("����Ǧ��");break;
						case"������ɫ":changeColor();break;
						case"��ջ���":readSimpleOP("��ջ���");break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		
		//ֻ����һ���ַ����ļ򵥲���
		private void readSimpleOP(String OP) {
			switch(OP){
			case"��ջ���":paintBoard.paint(g);break;
			}
		}

		//������ɫ
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
			//ֻҪ�Ǵ����������ͼ�λ��Ʋ�������������һ��
			try {
				//���������������
				for(int i=0;i<4;i++){
					byte[]bt=new byte[4];
					input.read(bt);
					shapePoint[i]=getInt(bt);
				}
				switch(OP){
				case"����ֱ��":
					g.drawLine(shapePoint[0],shapePoint[1],shapePoint[2],shapePoint[3]);break;
				case"����Բ��":
					g.fillOval(shapePoint[0],shapePoint[1],shapePoint[2],shapePoint[3]);break;
				case"���;���":
					g.fillRect(shapePoint[0],shapePoint[1],shapePoint[2],shapePoint[3]);break;
				case"����Ǧ��":
					g.drawLine(shapePoint[0],shapePoint[1],shapePoint[2],shapePoint[3]);break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void readMsg() {
			try {
				//������Ϣ�����߱��
				byte[]otherName=new byte[4];
				input.read(otherName);
				
				//���շ����ı����ݵĳ���
				byte[]bt1=new byte[4];
				input.read(bt1);
				int reclen=getInt(bt1);
				System.out.println("receive byte:"+reclen);
				
				//���ݽ��յ����ı������ֽڳ��ȴ������ڽ�����Ϣ��byte����
				byte[]bt2=new byte[reclen];
				//�����ı�����
				input.read(bt2);
				String recmsg=new String(bt2);
				System.out.println("���յ���"+recmsg);
				//��õ�ǰʱ��
				SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
				Date date = new Date(System.currentTimeMillis());
				//�����յ���Ϣ��������ı���
				jta1.append("\n\r"+"�û�"+new String(otherName)+"	"+formatter.format(date)+"\n\r"+recmsg);
				//���������ϵ����·�
				jta1.setCaretPosition(jta1.getText().length());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
