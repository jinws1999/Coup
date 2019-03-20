package reformationserver;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Game extends Thread{
    ReformationServer control;
    ArrayList<User>users;
    ArrayList<Player>players=new ArrayList();
    ArrayList<Integer>newcard=new ArrayList();
    ArrayList<Integer>oldcard=new ArrayList();
    int times=0;
    public Game(ReformationServer r,ArrayList<User>p){
        super();
        control=r;
        users=p;
        setCard();
        for(int i=0;i<users.size();i++){
            players.add(new Player(users.get(i).name,this));
        }
    }
    public void run(){
        //set up
        String members=players.get(0).name;
        for(int i=1;i<users.size();i++){
            members+="!"+users.get(i).name;
        }
        for(int i=0;i<users.size();i++){
            users.get(i).writer.println("game"+i+players.get(i).cards.get(0)+players.get(i).cards.get(1)+members);
        }
        times=(int)((double)users.size()*Math.random());
        while(true){
            int turn=times%players.size();
            times++;
            
            int winner=findWinner();
            if(winner!=-1){quit(winner);break;}
            
            if(players.get(turn).cards.size()>0){//alive
                //turn starts
                spread("turn"+turn);
                process(turn);
            }
        }
    }
    
    public void process(int turn){
        spread("message"+turn);
        spread("time"+control.processtime);
        users.get(turn).writer.println("ask");
        users.get(turn).gamecode="";
        
        //wait for player choice
        int decision=-1;int cardchoice=0;int target=0;
        if(users.get(turn).status!=0){
            for(int t=0;t<control.processtime*1000/control.sleeptime;t++){
                try {sleep(control.sleeptime);} catch (Exception ex) {}
                if(!users.get(turn).gamecode.equals("")){
                    String code=users.get(turn).gamecode;
                    users.get(turn).gamecode="";
                    if(code.length()==3&&code.substring(0,2).equals("2!")&&players.get(turn).coin>6){
                        target=code.charAt(2)-'0';
                        if(target>=0&&target<players.size()&&players.get(target).cards.size()>0){decision=2;break;}
                    }
                    if(players.get(turn).coin<10){
                        int pre=code.charAt(0)-'0';
                        if(pre==0||pre==1){decision=pre;break;}
                        if(code.length()==3&&(pre==3||pre==5||pre==7)){
                            cardchoice=code.charAt(2)-'0';
                            if(cardchoice>=0&&cardchoice<players.get(turn).cards.size()){
                                decision=pre;break;
                            }
                        }
                        if(code.length()==5&&((pre==4&&players.get(turn).coin>2)||pre==6||pre==8)){
                            cardchoice=code.charAt(2)-'0';
                            target=code.charAt(4)-'0';
                            if(cardchoice>=0&&cardchoice<players.get(turn).cards.size()){
                                if(target>=0&&target<players.size()&&players.get(target).cards.size()>0){
                                    decision=pre;break;
                                }
                                
                            }
                        }
                    }
                    
                }
            }
        }
        
        //choose by machine
        if(decision==-1){
            if(players.get(turn).coin<10)decision=0;
            else{
                decision=2;
                int alive=0;
                for(int i=0;i<players.size();i++){
                    if(i!=turn&&players.get(i).cards.size()>0)alive++;
                }
                int alivechoice=(int)((double)alive*Math.random());
                for(int i=0;i<players.size();i++){
                    if(i!=turn&&players.get(i).cards.size()>0){
                        if(alivechoice==0){target=i;break;}
                        else alivechoice--;
                    }
                }
            }
        }
        
        //tell everyone the choice
        spread("time0");
        users.get(turn).writer.println("askoff");
        if(decision==0||decision==1||decision==3||decision==5||decision==7){
            spread("message"+turn+"do"+decision);
        }
        else spread("message"+turn+"do"+decision+target);
        
        //action
        switch(decision){
            case 0:
                players.get(turn).coin++;
                spread("coin"+players.get(turn).coin+"!"+turn);
                break;
            case 1:
                boolean prevent=false;
                for(int i=turn+1;i<turn+players.size();i++){
                    int preventor=i%players.size();
                    if(players.get(preventor).cards.size()>0)prevent=preventprocess(preventor,1);
                    if(prevent)break;
                }
                if(!prevent){
                    //get money
                    players.get(turn).coin+=2;
                    spread("coin"+players.get(turn).coin+"!"+turn);
                }
                break;
            case 2:
                players.get(turn).coin-=7;
                spread("coin"+players.get(turn).coin+"!"+turn);
                losecardprocess(target,target);
                spread("hurt"+target);
                break;
            case 3:
                if(!doubtprocess(turn,1,cardchoice)){
                    players.get(turn).coin+=3;
                    spread("coin"+players.get(turn).coin+"!"+turn);
                }
                break;
            case 4:
                players.get(turn).coin-=3;
                spread("coin"+players.get(turn).coin+"!"+turn);
                if((!doubtprocess(turn,2,cardchoice))&&players.get(target).cards.size()>0){
                    if((!preventprocess(target,5))&&players.get(target).cards.size()>0){
                        losecardprocess(target,target);
                        spread("hurt"+target);
                    }
                }
                break;
            case 5:
                if(!doubtprocess(turn,3,cardchoice)){
                    players.get(turn).getCard();
                    players.get(turn).getCard();
                    users.get(turn).writer.println("card"+getCardInf(players.get(turn).cards));
                    losecardprocess(turn,turn);
                    losecardprocess(turn,turn);
                }
                break;
            case 6:
                if((!doubtprocess(turn,4,cardchoice))&&players.get(target).cards.size()>0){
                    if((!preventprocess(target,0))&&players.get(target).cards.size()>0){
                        int moneystolen=(players.get(target).coin>1?2:players.get(target).coin);
                        players.get(target).coin-=moneystolen;
                        players.get(turn).coin+=moneystolen;
                        spread("coin"+players.get(target).coin+"!"+target);
                        spread("coin"+players.get(turn).coin+"!"+turn);
                    }
                }
                break;
            case 7:
                if(!doubtprocess(turn,6,cardchoice)){
                    players.get(turn).getCard();
                    users.get(turn).writer.println("card"+getCardInf(players.get(turn).cards));
                    losecardprocess(turn,turn);
                }
                break;
            case 8:
                if((!doubtprocess(turn,6,cardchoice))&&players.get(target).cards.size()>0){
                    examineprocess(turn,target);
                }
                break;
        }
    }
    
    public void examineprocess(int officer,int target){
        users.get(officer).writer.println("card"+getCardInf(players.get(target).cards));
        spread("time"+control.doubttime);
        users.get(officer).writer.println("ask6");
        users.get(officer).gamecode="";
        
        //wait for player choice
        int decision=-1;int cardchoice=0;
        if(users.get(officer).status!=0){
            for(int t=0;t<control.doubttime*1000/control.sleeptime;t++){
                try {sleep(control.sleeptime);} catch (Exception ex) {}
                if(!users.get(officer).gamecode.equals("")){
                    String code=users.get(officer).gamecode;
                    users.get(officer).gamecode="";
                    if(code.equals("10")){decision=10;break;}
                    if(code.substring(0,1).equals("9")){decision=9;break;}
                }
            }
        }
        spread("time0");
        users.get(officer).writer.println("askoff");
        if(decision==9){
            spread("message"+officer+"be6");
            players.get(target).getCard();
            users.get(officer).writer.println("card"+getCardInf(players.get(target).cards));
            losecardprocess(officer,target);
        }
        users.get(officer).writer.println("card"+getCardInf(players.get(officer).cards));
        users.get(target).writer.println("card"+getCardInf(players.get(target).cards));
        
    }
    
    public boolean preventprocess(int preventor,int identity){
        spread("time"+control.doubttime);
        users.get(preventor).writer.println("ask"+identity);
        users.get(preventor).gamecode="";
        
        //wait for player choice
        int decision=-1;int cardchoice=0;
        if(users.get(preventor).status!=0){
            for(int t=0;t<control.doubttime*1000/control.sleeptime;t++){
                try {sleep(control.sleeptime);} catch (Exception ex) {}
                if(!users.get(preventor).gamecode.equals("")){
                    String code=users.get(preventor).gamecode;
                    users.get(preventor).gamecode="";
                    if(code.equals("10")){decision=10;break;}
                    if(code.length()>=3&&code.substring(0,1).equals("9")){
                        cardchoice=code.charAt(2)-'0';
                        if(cardchoice>=0&&cardchoice<players.get(preventor).cards.size()){
                            decision=9;break;
                        }
                    }
                }
            }
        }
        spread("time0");
        users.get(preventor).writer.println("askoff");
        if(decision!=9)return false;
        
        spread("message"+preventor+"be"+identity);
        return !doubtprocess(preventor,identity,cardchoice);
    }
    
    public boolean doubtprocess(int be,int identity,int cardchoice){
        for(int i=be+1;i<be+players.size();i++){
            int doubter=i%players.size();
            if(players.get(doubter).cards.size()>0){
                spread("time"+control.doubttime);
                users.get(doubter).writer.println("askdoubt"+be+identity);
                users.get(doubter).gamecode="";
                
                //wait for player choice
                int decision=-1;
                if(users.get(doubter).status!=0){
                    for(int t=0;t<control.doubttime*1000/control.sleeptime;t++){
                        try {sleep(control.sleeptime);} catch (Exception ex) {}
                        if(!users.get(doubter).gamecode.equals("")){
                            String code=users.get(doubter).gamecode;
                            users.get(doubter).gamecode="";
                            if(code.equals("10")){decision=10;break;}
                            if(code.substring(0,1).equals("9")){decision=9;break;}
                        }
                    }
                }
                spread("time0");
                users.get(doubter).writer.println("askoff");
                if(decision==9){
                    spread("message"+doubter+"dt"+be+identity);
                    ///判定胜负
                    int realCard=players.get(be).cards.get(cardchoice);
                    if((realCard==identity)||(identity==0&&(realCard==3||realCard==4||realCard==6))){
                        //质疑失败
                        spread("message"+doubter+"f");
                        //被质疑者换牌
                        players.get(be).loseCard(cardchoice);
                        players.get(be).getCard();
                        users.get(be).writer.println("card"+getCardInf(players.get(be).cards));
                        //质疑者丢牌
                        losecardprocess(doubter,doubter);
                        spread("hurt"+doubter);
                        return false;
                    }
                    else{
                        //质疑成功
                        spread("message"+doubter+"s"+realCard);
                        players.get(be).loseCard(cardchoice);
                        users.get(be).writer.println("card"+getCardInf(players.get(be).cards));
                        spread("hurt"+be);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public void losecardprocess(int loser,int owner){
        spread("time"+control.doubttime);
        
        if(owner==loser)users.get(loser).writer.println("askdrop");
        else users.get(loser).writer.println("askdrop"+owner);
        users.get(loser).gamecode="";
        
        //wait for player choice
        int decision=-1;int cardchoice=0;
        if(users.get(loser).status!=0){
            for(int t=0;t<control.doubttime*1000/control.sleeptime;t++){
                try {sleep(control.sleeptime);} catch (Exception ex) {}
                if(!users.get(loser).gamecode.equals("")){
                    String code=users.get(loser).gamecode;
                    users.get(loser).gamecode="";
                    if(code.length()>=3&&code.substring(0,1).equals("9")){
                        cardchoice=code.charAt(2)-'0';
                        if(cardchoice>=0&&cardchoice<players.get(owner).cards.size()){
                            decision=9;break;
                        }
                    }
                }
            }
        }
        spread("time0");
        users.get(loser).writer.println("askoff");
        if(decision!=9)cardchoice=0;
        
        players.get(owner).loseCard(cardchoice);
        users.get(loser).writer.println("card"+getCardInf(players.get(owner).cards));
    }
    
    public String getCardInf(ArrayList<Integer>cards){
        String cardstring="";
        for(int i=0;i<4;i++){
            if(cards.size()>i)cardstring+=(""+cards.get(i));
            else cardstring+="0";
        }
        return cardstring;
    }
    
    public void spread(String information){
        for(int i=0;i<users.size();i++){
            users.get(i).writer.println(information);
        }
    }
    
    public void quit(int winner){
        synchronized("message"){
            spread("over"+winner);
        }
        try {
            sleep(7000);
        } catch (Exception ex) {}
        synchronized("prepare"){
            for(int i=0;i<users.size();i++){
                if(users.get(i).status!=0){
                    users.get(i).status=1;
                    users.get(i).writer.println((i==winner?"win":"win"+users.get(winner).name));
                }
            }
            control.update();
        }
        
    }
    
    public void setCard(){
        for(int type=1;type<=6;type++){
            for(int t=1;t<=3;t++){
                oldcard.add(type);
            }
        }
        refreshCard();
    }
    public void refreshCard(){
        while(oldcard.size()>0){
            int choice=(int)((double)oldcard.size()*Math.random());
            newcard.add(oldcard.get(choice));
            oldcard.remove(choice);
        }
    }
    public int findWinner(){
        int winner=-1;
        for(int i=0;i<players.size();i++){
            if(players.get(i).cards.size()>0){
                if(winner==-1)winner=i;
                else {winner=-1;break;}
            }
        }
        return winner;
    }
}
