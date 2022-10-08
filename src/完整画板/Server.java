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
	
	//创建服务器
	public void create() throws IOException{
		
		ServerSocket sevsoc=new ServerSocket(8888);
		System.out.println("服务端上线！");
		
		while(true){
			//等待用户连接
			Socket soc=sevsoc.accept();
			sockets.add(soc);
			System.out.println("client "+(sockets.size()-1)+" is connected!");
			
			//获取IO
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
		
		@Override
		public void run(){
			while(true){
				try {
					byte[]OP=new byte[8];
					input.read(OP);
					
					switch(new String(OP)){
					case"发送文字":sendMsg();break;
					//只要是传输两个点的图形绘制操作都可以用这一条
					case"发送直线":sendShape("发送直线");break;
					case"发送圆形":sendShape("发送圆形");break;
					case"发送矩形":sendShape("发送矩形");break;
					case"发送铅笔":sendShape("发送铅笔");break;
					case"更改颜色":sendColor("更改颜色");break;
					case"清空画布":sendSimpleOP("清空画布");break;
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
				//表示发送文字操作（一定要是8个字节长度的操作）
				byte[]msg=OP.getBytes();
				for(int i=0;i<outputs.size();i++){
					if(i==socketNo){continue;}
					outputs.get(i).write(msg);
				}
				
				//接收并发送颜色的HashCode
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
			//只要是传输两个点的图形绘制操作都可以用这一条
			try {
				//表示发送文字操作（一定要是8个字节长度的操作）
				byte[]msg=OP.getBytes();
				for(int i=0;i<outputs.size();i++){
					if(i==socketNo){continue;}
					outputs.get(i).write(msg);
				}
				//传输4个坐标
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
				String OP="发送文字";//表示发送文字操作（一定要是8个字节长度的操作）
				byte[]msg=OP.getBytes();
				for(int i=0;i<outputs.size();i++){
					if(i==socketNo){continue;}
					outputs.get(i).write(msg);
				}
				
				//发送客户端编号
				byte[]clientName=new byte[4];
				input.read(clientName);
				for(int i=0;i<outputs.size();i++){
					if(i==socketNo){continue;}
					outputs.get(i).write(clientName);
				}
				System.out.println("发送者："+new String(clientName));
				
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
