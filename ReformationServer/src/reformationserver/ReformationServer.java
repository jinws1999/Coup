package reformationserver;
import java.io.*;
import java.net.*;
import java.util.*;

public class ReformationServer {
    ServerSocket server;
    ArrayList<User>users;
    int processtime=25;
    int doubttime=15;
    int sleeptime=100;
    String password="Password123";
    public ReformationServer(){
        try {
            server= new ServerSocket(10001);
        } catch (Exception ex) {}
        users=new ArrayList();
    }
    
    public void update(){
        for(int i=0;i<users.size();i++){
            synchronized("update"){
                if(users.get(i).status!=0){
                    users.get(i).writer.println("clear");
                    for(int j=0;j<users.size();j++){
                        if(users.get(j).status!=0){
                            users.get(i).writer.println("add"+users.get(j).status+"!"+users.get(j).name);
                        }
                    }
                    users.get(i).writer.println("load");
                }
                
            }
        }
    }
    
    public int joinnum(int group){
        int num=0;
        for(int i=0;i<users.size();i++){
            if(users.get(i).status==group)num++;
        }
        return num;
    }
    
    public ArrayList<User> formgroup(int group){
        ArrayList<User> members=new ArrayList();
        for(int i=0;i<users.size();i++){
            if(users.get(i).status==group)members.add(users.get(i));
        }
        return members;
    }
    
    public void accept(){
        while(true){
            User user=new User(this,server,users.size());
            users.add(user);
            update();
        }
    }
    
    public static void main(String[] args) {
        ReformationServer control=new ReformationServer();
        control.accept();
        
    }
        
}
/*
boardjws
clear
add10!jws
load

prepare5
renamejws



*/