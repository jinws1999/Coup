package reformationserver;

import java.io.*;
import java.util.*;

public class EnterThread extends Thread{
    ReformationServer control;
    User user;
    public EnterThread(ReformationServer r,User u){
        super();
        control=r;
        user=u;
    }
    public void run(){
        
        while(true){
            try {
                String code=user.reader.readLine();
                if(code!=null&&(!user.killed)){
                    //controller
                    if(code.length()>7&&code.substring(0,7).equals("control")){
                        String mes=code.substring(7);
                        if(user.controller){
                            if(mes.length()>11&&mes.substring(0,11).equals("processtime")){
                                control.processtime=atoi(mes.substring(11));
                                user.writer.println("controlSucceed in changing processtime into "+control.processtime);
                            }
                            if(mes.length()>9&&mes.substring(0,9).equals("doubttime")){
                                control.doubttime=atoi(mes.substring(9));
                                user.writer.println("controlSucceed in changing doubttime into "+control.doubttime);
                            }
                            if(mes.length()>4&&mes.substring(0,4).equals("kill")){
                                String bekilling=mes.substring(4);
                                int killnum=0;
                                for(int i=0;i<control.users.size();i++){
                                    if(control.users.get(i).name.equals(bekilling)){
                                        control.users.get(i).status=0;
                                        control.update();
                                        control.users.get(i).killed=true;
                                        killnum++;
                                    }
                                }
                                user.writer.println("controlSucceed in killing "+killnum+" players whose names are "+bekilling);
                            }
                        }
                        else{
                            if(mes.equals(control.password)){
                                user.controller=true;
                                user.writer.println("controlWelcome, Controller");
                                user.writer.println("controlcodes:");
                                user.writer.println("controlprocesstime25");
                                user.writer.println("controldoubttime15");
                                user.writer.println("controlkillguesthhhh");
                            }
                        }
                    }
                    if(user.status<100){
                        if(code.length()>6&&code.substring(0,6).equals("rename")){
                            String prename=code.substring(6);
                            if((!prename.contains("!"))&&(!prename.equals(""))){user.name=prename;control.update();}
                        }
                        if(code.length()>6&&code.substring(0,7).equals("prepare")){
                            synchronized("prepare"){
                                if(user.status<100){
                                    if(user.status!=1){
                                        user.status=1;
                                    }
                                    else{
                                        int num=atoi(code.substring(7));
                                        if(num<2)num=2;
                                        if(num>6)num=6;
                                        user.status=num;
                                        if(control.joinnum(num)>=num){
                                            ArrayList<User> group=control.formgroup(num);
                                            for(int i=0;i<group.size();i++){
                                                group.get(i).status+=100;
                                            }
                                            //////开始游戏
                                            Game game=new Game(control,group);
                                            game.start();
                                        }
                                    }
                                }
                            }
                            control.update();
                        }
                    }
                    else{
                        user.gamecode=code;
                    }
                }
                else{
                    synchronized("prepare"){
                        //kill
                        user.reader.close();
                        user.writer.close();
                        user.socket.close();
                        user.status=0;
                        control.update();
                        break;
                    }
                }
            } catch (Exception ex) {}
        }
    }
    public static int atoi(String a){
        int i=0;
        for(int index=0;index<a.length();index++){
            int n=a.charAt(index)-'0';
            i=i*10+n;
        }
        return i;
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