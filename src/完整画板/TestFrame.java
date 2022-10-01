package mychat_paintroom210323_V2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class TestFrame {
	private JTextArea jta1;
	private JTextArea jta2;
	private Graphics g;
	
	public static void main(String[]arguments){
		TestFrame tf=new TestFrame();
		tf.showFrame();
	}
	
	private void showFrame() {
		//��������
		JFrame jf=new JFrame("�ͻ���");
		jf.setSize(1000,500);
		jf.setDefaultCloseOperation(3);
		jf.setLocationRelativeTo(null);
		jf.setResizable(false);
		
		//���ô�����沼��Ϊ���񲼾֣����ò���Ϊһ������
		GridLayout grid=new GridLayout(1,2);
		jf.setLayout(grid);
		
		//���ô�����ʾ�����һ������ʡ�ԣ�
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		//������棨������ߵĲ��֣�
		JPanel jpLeft=new JPanel();
		
		JLabel jlb1=new JLabel("��Ϣ����");
		jpLeft.add(jlb1);
		//������Ϣ��
		jta1=new JTextArea(9,40);
		jta1.setLineWrap(true);//�����ı��������Զ�����
		jta1.setWrapStyleWord(true);//�����ı��������ڵ��ʽ���������
		jta1.append("��ʼ�����~");//����Ϣ��������ı�
		jta1.setEditable(false);//��������ݲ����޸�
		
		//��ӹ�����
		JScrollPane jsp1=new JScrollPane(jta1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jpLeft.add(jsp1);
		
		JLabel jlb2=new JLabel("���봰��");
		jpLeft.add(jlb2);
		
		//������Ϣ��
		jta2=new JTextArea(9,40);
		jta2.setLineWrap(true);//������Ϣ���ڵ��ı�ÿ��һ�о��Զ�����
		jta2.setWrapStyleWord(true);//������Ϣ�����ı������ʷָ�����
		
		//��ӹ�����
		JScrollPane jsp2=new JScrollPane(jta2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jpLeft.add(jsp2);
		
		//���ͺ�ȡ����ť
		JButton jb1=new JButton("����");
		jpLeft.add(jb1);
		JButton jb2=new JButton("ȡ��");
		jpLeft.add(jb2);
		
		//�滭����
		JPanel jpRight=new JPanel();
		BorderLayout board=new BorderLayout();
		jpRight.setLayout(board);//���û滭����Ϊ��ʽ����
		
		//����
		JPanel paintBoard=new JPanel();
		paintBoard.setBackground(Color.white);
		
		//�Ҳ๦�ܰ�����
		JPanel buttonBoard=new JPanel();
		buttonBoard.setPreferredSize(new Dimension(80,0));
		
		//��ӹ��ܰ�ť
		String[]buttonNames={"ֱ��","Բ��","����","Ǧ��"};
		JButton[]jbtList=new JButton[buttonNames.length];
		for(int i=0;i<buttonNames.length;i++){
			jbtList[i]=new JButton(buttonNames[i]);
			buttonBoard.add(jbtList[i]);
		}
		
		//�����ɫ��ť
		Color[]colors={Color.red,Color.yellow,Color.blue,Color.green,Color.black,Color.white};
		String[]colorButtonNames={"��","��","��","��","��","��"};
		JButton[]CjbtList=new JButton[colorButtonNames.length];
		for(int i=0;i<colorButtonNames.length;i++){
			CjbtList[i]=new JButton();
			CjbtList[i].setActionCommand(colorButtonNames[i]);
			CjbtList[i].setBackground(colors[i]);
			buttonBoard.add(CjbtList[i]);
		}
		
		//������Ͱ�����������ӵ��Ҳ�������
		jpRight.add(paintBoard,BorderLayout.CENTER);
		jpRight.add(buttonBoard,BorderLayout.EAST);
		
		//����������JPanle��ӵ�������
		jf.add(jpLeft);
		jf.add(jpRight);
		
		jf.setVisible(true);
		
		Listener lis=new Listener();
		paintBoard.addMouseMotionListener(lis);
		
		//��ȡ����
		g=paintBoard.getGraphics();
	}
	
	class Listener implements MouseMotionListener{
		public void mouseDragged(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}
	}
}
