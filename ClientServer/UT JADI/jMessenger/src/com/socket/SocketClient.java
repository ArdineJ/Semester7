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
        
        
    }

    @Override
    public void run() {
        boolean keepRunning = true;
        while(keepRunning){
            try {
                Message msg = (Message) In.readObject();
                System.out.println("Incoming : "+msg.toString());
                
                if(msg.type.equals("yourid")){
                    if(ui.jTextField3.getText().length()==0){ui.jTextField3.setText(msg.content);}
                }
                else if(msg.type.equals("message")){
                    if(msg.recipient.equals(ui.username)){
                        ui.jTextArea1.append("["+msg.sender +" > Me] : " + msg.content + "\n");
                    }
                    else{
                        if(msg.content.substring(0, 4).equals("POSX")){
                           if(ui.username.equalsIgnoreCase("O")){
                             ui.jButton9.setEnabled(true);}
                           else{ui.jButton9.setEnabled(false);}
                        int posX = Integer.parseInt(msg.content.substring(5, msg.content.length()));
                        ui.TALI.setBounds(ui.map[posX][0], ui.map[posX][1], 20, 20);
                        if(posX>=35){ui.jButton7.setEnabled(false);ui.jButton8.setEnabled(false);
                        ui.jTextArea1.append("GAME OVER, The Winner is X !!!");
                        JOptionPane.showMessageDialog(null, "GameOver X Win !!");}
                        }
                        else if(msg.content.substring(0, 4).equals("POSO")){
                            if(ui.username.equalsIgnoreCase("X")){
                                ui.jButton9.setEnabled(true);}
                           else{ui.jButton9.setEnabled(false);}
                            
                        int posO = Integer.parseInt(msg.content.substring(5, msg.content.length()));
                        ui.TALI_O.setBounds(ui.map[posO][0], ui.map[posO][1], 20, 20);
                        if(posO>=35){ui.jButton7.setEnabled(false);ui.jButton8.setEnabled(false);
                        ui.jTextArea1.append("GAME OVER, The Winner is O !!!");
                        JOptionPane.showMessageDialog(null, "GameOver O Win !!");}
                        }//else if(msg.content.substring(0, 3).equals("MEN")){
                                  //ui.TKANAN.setEnabled(false);
                                  //ui.TKIRI.setEnabled(false);
                         //         ui.jTextArea1.append("GAME OVER, YANG MENANG:".concat(msg.content.substring(4, msg.content.length())));
                        //}
                        
                        ui.jTextArea1.append("["+ msg.sender +" > "+ msg.recipient +"] : " + msg.content + "\n");
                    }
                                            
                    if(!msg.content.equals(".bye") && !msg.sender.equals(ui.username)){
                        String msgTime = (new Date()).toString();
                        
                        
                    }
                }
                else if(msg.type.equals("login")){
                    if(msg.content.equals("TRUE")){
                        ui.jButton2.setEnabled(false);                         
                        ui.jButton4.setEnabled(true); 
                        ui.jButton5.setEnabled(true);
                        if (ui.username.equalsIgnoreCase("X")){
                        ui.jButton9.setEnabled(true);}
                        
                        ui.jTextArea1.append("[SERVER > Me] : Login Successful\n");
                        ui.jTextField3.setEnabled(false); 
                    }
                    else{
                        ui.jTextArea1.append("[SERVER > Me] : Login Failed\n");
                    }
                }
                else if(msg.type.equals("test")){
                    ui.jButton1.setEnabled(false);
                    ui.jButton2.setEnabled(true); 
                    ui.jTextField3.setEnabled(true); 
                    ui.jTextField1.setEditable(false); ui.jTextField2.setEditable(false);
                    
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
                        ui.jButton2.setEnabled(false); 
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
                else if(msg.type.equals("api_res")){
                    
                    ui.jTextArea1.append("[SERVER API] : "+msg.content+"\n");
                    
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
                
            }
        } 
        catch (IOException ex) {
            System.out.println("Exception SocketClient send()");
        }
    }
    
    public void closeThread(Thread t){
        t = null;
    }
}
