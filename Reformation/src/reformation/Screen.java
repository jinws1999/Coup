package reformation;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import static reformation.Reformation.atoi;
import sun.java2d.pipe.*;
public class Screen extends JPanel{
    Reformation control;
    boolean isstart;
    public Screen(Reformation ref){
        super();
        control=ref;
        isstart=false;
    }
    public void paint(Graphics g1){
        super.paint(g1);
        Graphics2D g2=(Graphics2D)g1;
        Image back=Toolkit.getDefaultToolkit().getImage("gback.jpg");
        g2.drawImage(back,0,0,this);
        
        //draw players
        Image playerimg=Toolkit.getDefaultToolkit().getImage("player.jpg");
        for(int i=0;i<control.players.size();i++){
            if(i!=control.self||getPlayerPosition(control.players.size(),control.self,i).length!=0){
                int position[]=getPlayerPosition(control.players.size(),control.self,i);
                g2.drawImage(playerimg,10+position[0],30+position[1], this);
                g2.setColor(Color.red);
                g2.setFont(new Font("宋体",1,15));
                g2.drawString("Coin:"+control.players.get(i).coins+" Card:"+control.players.get(i).cards,10+position[0],160+position[1]);
                if(control.current==i)g2.setColor(Color.yellow);
                if(control.players.get(i).cards==0)g2.setColor(Color.black);
                g2.setFont(new Font("宋体",1,20));
                g2.drawString(control.players.get(i).name,30+position[0],20+position[1]);
                if(control.playerchoice==i)g2.setColor(Color.yellow);
                else g2.setColor(Color.red);
                if(control.players.get(i).cards==0)g2.setColor(Color.black);
                g2.draw(new Rectangle2D.Double(position[0],position[1],135,170));
            }
            else{
                control.selfname.setText(control.players.get(i).name);
                control.selfcoin.setText("Coin: "+control.players.get(i).coins);
                if(control.current==i)control.selfname.setForeground(Color.yellow);
                else control.selfname.setForeground(Color.blue);
                if(control.players.get(i).cards==0)control.selfname.setForeground(Color.black);
            }
        }
        synchronized("message"){
            //draw messages
            for(int i=0;i<3;i++){
                if(control.messagetimes[i]==0){
                    for(int j=i+1;j<3;j++){
                        if(control.messagetimes[j]!=0){
                            control.messagetimes[i]=control.messagetimes[j];
                            control.messagetimes[j]=0;
                            control.messages[i]=control.messages[j].substring(0);
                            control.messages[j]="";
                            break;
                        }
                    }
                    if(control.messagetimes[i]==0&&control.waitmessages.size()>0){
                        control.messages[i]=control.waitmessages.get(0);
                        control.messagetimes[i]=control.messagestaytime/control.sleeptime;
                        control.waitmessages.remove(0);
                    }
                    
                }
            }
            for(int i=0;i<3;i++){
                if(control.messagetimes[i]>0){
                    g2.setFont(new Font("宋体",1,18));
                    g2.setColor(Color.white);
                    g2.drawString(control.messages[i],250,250+40*i);
                    control.messagetimes[i]--;
                }
            }
            //draw times
            if(control.time>0){
                g2.setFont(new Font("宋体",1,20));
                g2.setColor(Color.white);
                g2.drawString(""+((control.time*control.sleeptime)/1000+((control.time*control.sleeptime)%1000>0?1:0)),385,370);
                control.time--;
            }
        }
        //draw hint
        if(!"".equals(control.hint)){
            g2.setFont(new Font("宋体",1,20));
            g2.setColor(Color.white);
            g2.drawString(control.hint,100,440);
        }
        
    }
    
    public void setStart(boolean tostart){
        if(tostart&&!isstart){
            isstart=true;
            class Painting extends Thread{
                Screen screen;
                public Painting(Screen s){
                    super();
                    screen=s;
                }
                public void run(){
                    while(true){
                        if(!screen.isstart)break;
                        try {
                            screen.repaint();
                            sleep(screen.control.sleeptime);
                        } catch (Exception ex) {}
                    }
                }
            }
            Painting p=new Painting(this);
            p.start();
        }
        if(!tostart)isstart=false;
    }
    
    public static int[] getPlayerPosition(int totalnum,int selfnum,int playernum) {
        int position[]=new int[2];int empty[]={};
        position[0]=0;position[1]=0;
        if(selfnum==playernum||playernum<0||selfnum<0||playernum>=totalnum||selfnum>=totalnum||totalnum>6)return empty;
        int num=(totalnum+playernum-selfnum)%totalnum;
        
        if(totalnum==2||(totalnum==4&&num==2)||(totalnum==6&&num==3)){
            position[0]=333;position[1]=20;
        }
        if((totalnum==3&&num==1)||(totalnum==5&&num==2)||(totalnum==6&&num==2)){
            position[0]=118;position[1]=20;
        }
        if((totalnum==3&&num==2)||(totalnum==5&&num==3)||(totalnum==6&&num==4)){
            position[0]=548;position[1]=20;
        }
        if((totalnum==4&&num==1)||(totalnum==5&&num==1)||(totalnum==6&&num==1)){
            position[0]=50;position[1]=230;
        }
        if((totalnum==4&&num==3)||(totalnum==5&&num==4)||(totalnum==6&&num==5)){
            position[0]=615;position[1]=230;
        }
        
        return position;
    }
}
