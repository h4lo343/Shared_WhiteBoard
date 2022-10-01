package mychat_paintroom210323_V2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private ArrayList<Socket>sockets=new ArrayList<>();
	private ArrayList<InputStream>inputs=new ArrayList<>();
	private ArrayList<OutputStream>outputs=new ArrayList<>();
	
	public static void main(String[]arguments) throws IOException{
		Server sev=new Server();
		sev.create();
	}
	
	//����������
	public void create() throws IOException{
		
		ServerSocket sevsoc=new ServerSocket(8888);
		System.out.println("��������ߣ�");
		
		while(true){
			//�ȴ��û�����
			Socket soc=sevsoc.accept();
			sockets.add(soc);
			System.out.println("client "+(sockets.size()-1)+" is connected!");
			
			//��ȡIO
			InputStream input=soc.getInputStream();
			OutputStream output=soc.getOutputStream();
			inputs.add(input);
			outputs.add(output);

			System.out.println("get client "+(sockets.size()-1)+"'s IOflow");
			
			MessageChannel channel=new MessageChannel(sockets.size()-1,input,outputs);
			channel.start();
		}
	}
	
	class MessageChannel extends Thread{
		private InputStream input;
		private ArrayList<OutputStream> outputs;
		private int socketNo=0;
		
		public MessageChannel(int socketNo,InputStream input,ArrayList<OutputStream> outputs){
			this.input=input;
			this.outputs=outputs;
			this.socketNo=socketNo;
		}
		
		public void run(){
			while(true){
				try {
					byte[]OP=new byte[8];
					input.read(OP);
					
					switch(new String(OP)){
					case"��������":sendMsg();break;
					//ֻҪ�Ǵ����������ͼ�λ��Ʋ�������������һ��
					case"����ֱ��":sendShape("����ֱ��");break;
					case"����Բ��":sendShape("����Բ��");break;
					case"���;���":sendShape("���;���");break;
					case"����Ǧ��":sendShape("����Ǧ��");break;
					case"������ɫ":sendColor("������ɫ");break;
					case"��ջ���":sendSimpleOP("��ջ���");break;
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void sendSimpleOP(String OP) {
			try {
				byte[]msg=OP.getBytes();
				for(int i=0;i<outputs.size();i++){
					if(i==socketNo){continue;}
					outputs.get(i).write(msg);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		private void sendColor(String OP) {
			try {
				//��ʾ�������ֲ�����һ��Ҫ��8���ֽڳ��ȵĲ�����
				byte[]msg=OP.getBytes();
				for(int i=0;i<outputs.size();i++){
					if(i==socketNo){continue;}
					outputs.get(i).write(msg);
				}
				
				//���ղ�������ɫ��HashCode
				byte[]color=new byte[4];
				input.read(color);

				for(int i=0;i<outputs.size();i++){
					if(i==socketNo){continue;}
					outputs.get(i).write(color);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void sendShape(String OP) {
			//ֻҪ�Ǵ����������ͼ�λ��Ʋ�������������һ��
			try {
				//��ʾ�������ֲ�����һ��Ҫ��8���ֽڳ��ȵĲ�����
				byte[]msg=OP.getBytes();
				for(int i=0;i<outputs.size();i++){
					if(i==socketNo){continue;}
					outputs.get(i).write(msg);
				}
				//����4������
				for(int i=0;i<4;i++){
					byte[]bt=new byte[4];
					input.read(bt);
					for(int j=0;j<outputs.size();j++){
						if(j==socketNo){continue;}
						outputs.get(j).write(bt);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void sendMsg() {
			try {
				String OP="��������";//��ʾ�������ֲ�����һ��Ҫ��8���ֽڳ��ȵĲ�����
				byte[]msg=OP.getBytes();
				for(int i=0;i<outputs.size();i++){
					if(i==socketNo){continue;}
					outputs.get(i).write(msg);
				}
				
				//���Ϳͻ��˱��
				byte[]clientName=new byte[4];
				input.read(clientName);
				for(int i=0;i<outputs.size();i++){
					if(i==socketNo){continue;}
					outputs.get(i).write(clientName);
				}
				System.out.println("�����ߣ�"+new String(clientName));
				
				byte[]bt1=new byte[4];
				input.read(bt1);
				int reclen=getInt(bt1);
				for(int i=0;i<outputs.size();i++){
					if(i==socketNo){continue;}
					outputs.get(i).write(reclen);
				}
				System.out.print("reclen:"+reclen);
				
				byte[]msg2=new byte[reclen]; 
				input.read(msg2);
				for(int i=0;i<outputs.size();i++){
					if(i==socketNo){continue;}
					outputs.get(i).write(msg2);
				}
				System.out.println(" sending:"+new String(msg2));
			} catch (IOException e) {
				e.printStackTrace();
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
	}

}
