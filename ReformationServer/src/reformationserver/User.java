package reformationserver;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    boolean controller=false;
    boolean killed=false;
    String name;
    Socket socket;
    BufferedReader reader;
    PrintWriter writer;
    int status;
    EnterThread enter;
    ReformationServer control;
    String gamecode="";
    
    
    public User(ReformationServer con,ServerSocket server,int num){
        try {
            control=con;
            socket=server.accept();
            name="guest"+num;
            status=1;
            writer=new PrintWriter(socket.getOutputStream(),true);
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            enter=new EnterThread(control,this);
            writer.println("board"+name);
            enter.start();
        } catch (Exception ex) {}
    }
    
    
}
