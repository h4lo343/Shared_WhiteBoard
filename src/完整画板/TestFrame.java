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
		//创建窗体
		JFrame jf=new JFrame("客户端");
		jf.setSize(1000,500);
		jf.setDefaultCloseOperation(3);
		jf.setLocationRelativeTo(null);
		jf.setResizable(false);
		
		//设置窗体界面布局为网格布局，设置布局为一行两列
		GridLayout grid=new GridLayout(1,2);
		jf.setLayout(grid);
		
		//设置窗体显示风格（这一步可以省略）
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		//聊天界面（窗体左边的部分）
		JPanel jpLeft=new JPanel();
		
		JLabel jlb1=new JLabel("消息窗口");
		jpLeft.add(jlb1);
		//接收消息框
		jta1=new JTextArea(9,40);
		jta1.setLineWrap(true);//设置文本框内容自动换行
		jta1.setWrapStyleWord(true);//设置文本框内容在单词结束处换行
		jta1.append("开始聊天吧~");//向消息框内添加文本
		jta1.setEditable(false);//聊天框内容不可修改
		
		//添加滚动条
		JScrollPane jsp1=new JScrollPane(jta1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jpLeft.add(jsp1);
		
		JLabel jlb2=new JLabel("输入窗口");
		jpLeft.add(jlb2);
		
		//发送消息框
		jta2=new JTextArea(9,40);
		jta2.setLineWrap(true);//设置消息框内的文本每满一行就自动换行
		jta2.setWrapStyleWord(true);//设置消息框内文本按单词分隔换行
		
		//添加滚动条
		JScrollPane jsp2=new JScrollPane(jta2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jpLeft.add(jsp2);
		
		//发送和取消按钮
		JButton jb1=new JButton("发送");
		jpLeft.add(jb1);
		JButton jb2=new JButton("取消");
		jpLeft.add(jb2);
		
		//绘画界面
		JPanel jpRight=new JPanel();
		BorderLayout board=new BorderLayout();
		jpRight.setLayout(board);//设置绘画界面为板式布局
		
		//画板
		JPanel paintBoard=new JPanel();
		paintBoard.setBackground(Color.white);
		
		//右侧功能按键区
		JPanel buttonBoard=new JPanel();
		buttonBoard.setPreferredSize(new Dimension(80,0));
		
		//添加功能按钮
		String[]buttonNames={"直线","圆形","矩形","铅笔"};
		JButton[]jbtList=new JButton[buttonNames.length];
		for(int i=0;i<buttonNames.length;i++){
			jbtList[i]=new JButton(buttonNames[i]);
			buttonBoard.add(jbtList[i]);
		}
		
		//添加颜色按钮
		Color[]colors={Color.red,Color.yellow,Color.blue,Color.green,Color.black,Color.white};
		String[]colorButtonNames={"红","黄","蓝","绿","黑","白"};
		JButton[]CjbtList=new JButton[colorButtonNames.length];
		for(int i=0;i<colorButtonNames.length;i++){
			CjbtList[i]=new JButton();
			CjbtList[i].setActionCommand(colorButtonNames[i]);
			CjbtList[i].setBackground(colors[i]);
			buttonBoard.add(CjbtList[i]);
		}
		
		//将画板和按键功能区添加到右侧容器中
		jpRight.add(paintBoard,BorderLayout.CENTER);
		jpRight.add(buttonBoard,BorderLayout.EAST);
		
		//将左右两个JPanle添加到窗体上
		jf.add(jpLeft);
		jf.add(jpRight);
		
		jf.setVisible(true);
		
		Listener lis=new Listener();
		paintBoard.addMouseMotionListener(lis);
		
		//获取画笔
		g=paintBoard.getGraphics();
	}
	
	class Listener implements MouseMotionListener{
		public void mouseDragged(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}
	}
}
