package com.socket;

import com.ui.ChatFrame;
import java.io.*;
import java.net.*;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class SocketClient implements Runnable{
    
    public int port;
    public String serverAddr;
    public Socket socket;
    public ChatFrame ui;
    public ObjectInputStream In;
    public ObjectOutputStream Out;
    public History hist;
    
    public SocketClient(ChatFrame frame) throws IOException{
        ui = frame; this.serverAddr = ui.serverAddr; this.port = ui.port;
        socket = new Socket(InetAddress.getByName(serverAddr), port);
            
        Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());
        
        hist = ui.hist;
    }

    @Override
    public void run() {
        boolean keepRunning = true;
        while(keepRunning){
            try {
                int posisi = ui.jButton9.getBounds().x;                         
                Message msg = (Message) In.readObject();
                System.out.println("Incoming : "+msg.toString());
                
                if(msg.type.equals("message")){
                    String[] splitStr = msg.content.split("\\s+");
                    /*
                    if(splitStr[0].equals("PEMENANG")){
                    ui.jTextArea1.append("GAME OVER, Jangan Sedih Coba Lagi Pasti lain kali kalah juga");
                    ui.jButton4.setEnabled(false);
                    }
                    */
                    
                    if((splitStr[0].equals("M")) || (splitStr[0].equals("U"))){
                        if(ui.jButton9.getBounds().y<=140){
                            ui.jTextArea1.append("ANDA MENANG !!!");
                            ui.jButton11.setEnabled(false);
                            ui.jButton12.setEnabled(false);
                            ui.jButton13.setEnabled(false);
                            ui.jButton14.setEnabled(false);
                        }
                        else if(ui.jButton10.getBounds().y<=140){
                            ui.jTextArea1.append("LAWAN MENANG !!");
                            ui.jButton11.setEnabled(false);
                            ui.jButton12.setEnabled(false);
                            ui.jButton13.setEnabled(false);
                            ui.jButton14.setEnabled(false);
                        }
                    }
                     
                    if(splitStr[0].equals("M")){
                        //ui.jTextArea1.append("masuk ke M");
                        //ui.jTextArea1.append("Pengirim:"+msg.sender);
                        //ui.jTextArea1.append(ui.username);
                        
                        if(msg.sender.equals(ui.username)){
                            
                        //mobil saya
                        ui.jButton9.setBounds(ui.jButton9.getBounds().x,
                                Integer.parseInt(splitStr[1])-10 ,
                                ui.jButton9.getBounds().width, 
                                ui.jButton9.getBounds().height);
                        }else{
                        //mobil lawan
                        ui.jButton10.setBounds(ui.jButton10.getBounds().x,
                                Integer.parseInt(splitStr[1])-10 ,
                                ui.jButton10.getBounds().width, 
                                ui.jButton10.getBounds().height);
                        }
                        
                    }
                    
                    if(splitStr[0].equals("MU")){
                        if(msg.sender.equals(ui.username)){
                            //mobil saya
                            ui.jButton9.setBounds(ui.jButton9.getBounds().x,
                                    Integer.parseInt(splitStr[1])+10 ,
                                    ui.jButton9.getBounds().width, 
                                    ui.jButton9.getBounds().height);
                            }else{
                            //mobil lawan
                            ui.jButton10.setBounds(ui.jButton10.getBounds().x,
                                    Integer.parseInt(splitStr[1])+10 ,
                                    ui.jButton10.getBounds().width, 
                                    ui.jButton10.getBounds().height);
                            }
                    }
                    
                    if (checkCollision(ui.jButton11, ui.jButton9) || checkCollision(ui.jButton12, ui.jButton10) || checkCollision(ui.jButton13, ui.jButton10) || checkCollision(ui.jButton14, ui.jButton10)) {
                        ui.jTextArea1.append("MUTER BALEK !!!");
                        ui.jButton12.setEnabled(false);
                    } else {
                        ui.jButton12.setEnabled(true);
                    }
                    
//                    if(posisi>=390){
//                        ui.jButton12.setEnabled(false);
//                    } else {
//                        ui.jButton12.setEnabled(true);
//                    }
                    
                    
                    if(msg.recipient.equals(ui.username)){
                        ui.jTextArea1.append("["+msg.sender +" > Me] : " + msg.content + "\n");
                    }
                    else{
                        ui.jTextArea1.append("["+ msg.sender +" > "+ msg.recipient +"] : " + msg.content + "\n");
                    }
                                            
                    if(!msg.content.equals(".bye") && !msg.sender.equals(ui.username)){
                        String msgTime = (new Date()).toString();
                        
                        try{
                            hist.addMessage(msg, msgTime);
                            DefaultTableModel table = (DefaultTableModel) ui.historyFrame.jTable1.getModel();
                            table.addRow(new Object[]{msg.sender, msg.content, "Me", msgTime});
                        }
                        catch(Exception ex){}  
                    }
                }
                else if(msg.type.equals("login")){
                    if(msg.content.equals("TRUE")){
                        ui.jButton2.setEnabled(false); ui.jButton3.setEnabled(false);                        
                        ui.jButton4.setEnabled(true); ui.jButton5.setEnabled(true);
                        ui.jTextArea1.append("[SERVER > Me] : Login Successful\n");
                        ui.jTextField3.setEnabled(false); ui.jPasswordField1.setEnabled(false);
                    }
                    else{
                        ui.jTextArea1.append("[SERVER > Me] : Login Failed\n");
                    }
                }
                else if(msg.type.equals("test")){
                    ui.jButton1.setEnabled(false);
                    ui.jButton2.setEnabled(true); ui.jButton3.setEnabled(true);
                    ui.jTextField3.setEnabled(true); ui.jPasswordField1.setEnabled(true);
                    ui.jTextField1.setEditable(false); ui.jTextField2.setEditable(false);
                    ui.jButton7.setEnabled(true);
                }
                else if(msg.type.equals("newuser")){
                    if(!msg.content.equals(ui.username)){
                        boolean exists = false;
                        for(int i = 0; i < ui.model.getSize(); i++){
                            if(ui.model.getElementAt(i).equals(msg.content)){
                                exists = true; break;
                            }
                        }
                        if(!exists){ ui.model.addElement(msg.content); }
                    }
                }
                else if(msg.type.equals("signup")){
                    if(msg.content.equals("TRUE")){
                        ui.jButton2.setEnabled(false); ui.jButton3.setEnabled(false);
                        ui.jButton4.setEnabled(true); ui.jButton5.setEnabled(true);
                        ui.jTextArea1.append("[SERVER > Me] : Singup Successful\n");
                    }
                    else{
                        ui.jTextArea1.append("[SERVER > Me] : Signup Failed\n");
                    }
                }
                else if(msg.type.equals("signout")){
                    if(msg.content.equals(ui.username)){
                        ui.jTextArea1.append("["+ msg.sender +" > Me] : Bye\n");
                        ui.jButton1.setEnabled(true); ui.jButton4.setEnabled(false); 
                        ui.jTextField1.setEditable(true); ui.jTextField2.setEditable(true);
                        
                        for(int i = 1; i < ui.model.size(); i++){
                            ui.model.removeElementAt(i);
                        }
                        
                        ui.clientThread.stop();
                    }
                    else{
                        ui.model.removeElement(msg.content);
                        ui.jTextArea1.append("["+ msg.sender +" > All] : "+ msg.content +" has signed out\n");
                    }
                }
                else if(msg.type.equals("upload_req")){
                    
                    if(JOptionPane.showConfirmDialog(ui, ("Accept '"+msg.content+"' from "+msg.sender+" ?")) == 0){
                        
                        JFileChooser jf = new JFileChooser();
                        jf.setSelectedFile(new File(msg.content));
                        int returnVal = jf.showSaveDialog(ui);
                       
                        String saveTo = jf.getSelectedFile().getPath();
                        if(saveTo != null && returnVal == JFileChooser.APPROVE_OPTION){
                            Download dwn = new Download(saveTo, ui);
                            Thread t = new Thread(dwn);
                            t.start();
                            //send(new Message("upload_res", (""+InetAddress.getLocalHost().getHostAddress()), (""+dwn.port), msg.sender));
                            send(new Message("upload_res", ui.username, (""+dwn.port), msg.sender));
                        }
                        else{
                            send(new Message("upload_res", ui.username, "NO", msg.sender));
                        }
                    }
                    else{
                        send(new Message("upload_res", ui.username, "NO", msg.sender));
                    }
                }
                else if(msg.type.equals("upload_res")){
                    if(!msg.content.equals("NO")){
                        int port  = Integer.parseInt(msg.content);
                        String addr = msg.sender;
                        
                        ui.jButton5.setEnabled(false); ui.jButton6.setEnabled(false);
                        Upload upl = new Upload(addr, port, ui.file, ui);
                        Thread t = new Thread(upl);
                        t.start();
                    }
                    else{
                        ui.jTextArea1.append("[SERVER > Me] : "+msg.sender+" rejected file request\n");
                    }
                }
                else{
                    ui.jTextArea1.append("[SERVER > Me] : Unknown message type\n");
                }
            }
            catch(Exception ex) {
                keepRunning = false;
                ui.jTextArea1.append("[Application > Me] : Connection Failure\n");
                ui.jButton1.setEnabled(true); ui.jTextField1.setEditable(true); ui.jTextField2.setEditable(true);
                ui.jButton4.setEnabled(false); ui.jButton5.setEnabled(false); ui.jButton5.setEnabled(false);
                
                for(int i = 1; i < ui.model.size(); i++){
                    ui.model.removeElementAt(i);
                }
                
                ui.clientThread.stop();
                
                System.out.println("Exception SocketClient run()");
                ex.printStackTrace();
            }
        }
    }
    
    public void send(Message msg){
        try {
            Out.writeObject(msg);
            Out.flush();
            System.out.println("Outgoing : "+msg.toString());
            
            if(msg.type.equals("message") && !msg.content.equals(".bye")){
                String msgTime = (new Date()).toString();
                try{
                    hist.addMessage(msg, msgTime);               
                    DefaultTableModel table = (DefaultTableModel) ui.historyFrame.jTable1.getModel();
                    table.addRow(new Object[]{"Me", msg.content, msg.recipient, msgTime});
                }
                catch(Exception ex){}
            }
        } 
        catch (IOException ex) {
            System.out.println("Exception SocketClient send()");
        }
    }
    
    public void closeThread(Thread t){
        t = null;
    }
    
    private boolean checkCollision(javax.swing.JButton btn1, javax.swing.JButton btn2) {
        int bufferDistance = 30; 
        java.awt.Rectangle bounds1 = btn1.getBounds();
        java.awt.Rectangle bounds2 = btn2.getBounds();

        boolean closeHorizontally = Math.abs(bounds1.x - bounds2.x) <= bufferDistance || 
                                    Math.abs((bounds1.x + bounds1.width) - bounds2.x) <= bufferDistance;
        boolean closeVertically = Math.abs(bounds1.y - bounds2.y) <= bufferDistance || 
                                  Math.abs((bounds1.y + bounds1.height) - bounds2.y) <= bufferDistance;

        return bounds1.intersects(bounds2) || (closeHorizontally && closeVertically);
    }
}
