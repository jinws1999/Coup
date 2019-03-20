package reformation;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;



/**
 *
 * @author jinw2
 */
public class Reformation {
    //time
    int sleeptime=100;
    int messagestaytime=3000;
    //connect
    Socket socket;
    BufferedReader reader;
    PrintWriter writer;
    String ipaddress="142.93.120.48";
    
    //enter GUI
    JFrame enter;
    Container entercon;
    JButton renamebutton;
    JButton preparebutton;
    JLabel preparelabel;
    JTextField preparetext;
    JTextField renametext;
    JTextArea inftext;
    ImageIcon background;
    JLabel backgroundlabel;
    
    //game GUI
    JFrame game;
    Container gamecon;
    Screen screen;
    ImageIcon images[]=new ImageIcon[7];
    JButton cards[]=new JButton[4];
    JButton options[]=new JButton[11];
    JLabel selfname;
    JLabel selfpicture;
    JLabel selfcoin;
    
    //win GUI
    JFrame win;
    Container wincon;
    JLabel winl;
    
    //lose GUI
    JFrame lose;
    Container losecon;
    JLabel losel;
    
    //information(wait)
    ArrayList<String> names=new ArrayList();
    ArrayList<String> status=new ArrayList();
    
    //information(game)
    ArrayList<Player> players=new ArrayList();
    int self=-1;
    int current=-1;
    int cardchoice=-1;
    int playerchoice=-1;
    
    ArrayList<String>waitmessages=new ArrayList();
    String messages[]={"","",""};
    int messagetimes[]={0,0,0};
    int time=0;
    String hint="";
    
    
    public Reformation (){
        //fail GUI
        JFrame fail=new JFrame("Fail");
        Container failcon=fail.getContentPane();
        fail.setDefaultCloseOperation(3);
        JLabel faill=new JLabel("与服务器连接失败");
        failcon.add(faill);
        fail.setBounds(300, 300, 450, 400);
        failcon.setLayout(null);
        faill.setBounds(115,175,220,50);
        
        
        //enter GUI
        enter=new JFrame("Coup: Reformation 1.0");
        entercon=enter.getContentPane();
        enter.setDefaultCloseOperation(3);
        enter.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {  
                super.windowClosing(e);
                try{
                    if(writer!=null){writer.close();}
                    if(socket!=null){socket.close();}
                }catch(Exception ex){}
            }
        });
        enter.setBounds(320, 100, 800, 650);
        entercon.setLayout(null);
        
        background=new ImageIcon("eback.jpg");
        backgroundlabel=new JLabel(background);
        renamebutton=new JButton("Rename");
        preparebutton=new JButton("Prepare");
        preparelabel=new JLabel("Number of Players:");
        preparelabel.setForeground(Color.WHITE);
        preparetext=new JTextField();
        renametext=new JTextField();
        inftext=new JTextArea();
        inftext.setEditable(false);
        inftext.setForeground(Color.red);
        inftext.setOpaque(false);
        inftext.setFont(new Font("宋体",1,17));
        
        entercon.add(renamebutton);entercon.add(preparebutton);entercon.add(preparelabel);
        entercon.add(preparetext);entercon.add(renametext);entercon.add(inftext);
        entercon.add(backgroundlabel,new Integer(Integer.MIN_VALUE));
        
        renamebutton.setBounds(380, 50, 150, 30);
        preparebutton.setBounds(570, 300, 120, 50);
        preparelabel.setBounds(540, 180, 130, 30);
        preparetext.setBounds(670, 180, 30, 30);
        renametext.setBounds(250, 50, 100, 30);
        inftext.setBounds(150, 150, 390, 450);
        backgroundlabel.setBounds(0, 0, background.getIconWidth(),background.getIconHeight());
        
        renamebutton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String newname=renametext.getText();
                if(newname!=""){writer.println("rename"+newname);}
            }
        });
        preparebutton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                writer.println("prepare"+preparetext.getText());
                if(preparebutton.getText().equals("Prepare")){preparebutton.setText("Cancel");}
                else{preparebutton.setText("Prepare");}
            }
        });
        
        //game GUI
        game=new JFrame("Coup: Reformation 1.0");
        gamecon=game.getContentPane();
        game.setDefaultCloseOperation(3);
        game.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {  
                super.windowClosing(e);
                try{
                    if(writer!=null){writer.close();}
                    if(socket!=null){socket.close();}
                }catch(Exception ex){}
            }
        });
        game.setBounds(320, 100, 800, 700);
        gamecon.setBackground(Color.black);
        gamecon.setLayout(null);
        
        screen=new Screen(this);
        screen.addMouseListener(new MouseListener(){
            public void mouseClicked(MouseEvent e) {
                int x=e.getX(),y=e.getY();
                for(int i=0;i<screen.control.players.size();i++){
                    int[]p=Screen.getPlayerPosition(screen.control.players.size(),screen.control.self,i);
                    if(p.length==2){
                        if(x>=p[0]&&x<=(p[0]+135)&&y>=p[1]&&y<=(p[1]+170)){
                            if(screen.control.players.get(i).cards>0)screen.control.playerchoice=i;
                            break;
                        }
                    }
                }
            }
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            
        });
        
        
        images[0]=new ImageIcon("player.jpg");
        images[1]=new ImageIcon("nan.jpg");
        images[2]=new ImageIcon("ci.jpg");
        images[3]=new ImageIcon("da.jpg");
        images[4]=new ImageIcon("dui.jpg");
        images[5]=new ImageIcon("bo.jpg");
        images[6]=new ImageIcon("shen.jpg");
        cards[0]=new JButton();cards[1]=new JButton();cards[2]=new JButton();cards[3]=new JButton();
        options[0]=new JButton("税收");options[1]=new JButton("外援");options[2]=new JButton("政变");
        options[3]=new JButton("收税-男");options[4]=new JButton("刺杀-刺");options[5]=new JButton("交换-使");
        options[6]=new JButton("偷窃-队");options[7]=new JButton("交换-审");options[8]=new JButton("审查-审");
        options[9]=new JButton("是");options[10]=new JButton("否");
        selfname=new JLabel("jws");
        selfpicture=new JLabel(images[0]);
        selfcoin=new JLabel("Coin: 0");
        selfname.setFont(new Font("宋体",1,20));
        selfcoin.setFont(new Font("宋体",1,20));
        selfname.setForeground(Color.blue);
        selfcoin.setForeground(Color.blue);
        
        class Act implements ActionListener{
            int num;
            Reformation control;
            Boolean iscard;
            public Act(int cardnum,Reformation ref,boolean card){
                num=cardnum;
                control=ref;
                iscard=card;
            }
            public void actionPerformed(ActionEvent e) {
                if(iscard){
                    if(control.cardchoice!=-1)control.cards[control.cardchoice].setBackground(Color.black);
                    control.cardchoice=num;
                    control.cards[control.cardchoice].setBackground(Color.yellow);
                }
                else{
                    if(num<2||num==10){control.writer.println(""+num);}
                    if(num==2){control.writer.println(""+num+"!"+control.playerchoice);}
                    if(num==3||num==5||num==7){control.writer.println(""+num+"!"+control.cardchoice);}
                    if(num==4||num==6||num==8||num==9){control.writer.println(""+num+"!"+control.cardchoice+"!"+control.playerchoice);}
                }
                
            }
        }
        
        for(int i=0;i<cards.length;i++){
            gamecon.add(cards[i]);
            cards[i].setBounds(0,0,0,0);
            cards[i].setOpaque(true);
            cards[i].setBackground(Color.black);
            cards[i].addActionListener(new Act(i,this,true));
        }
        
        for(int i=0;i<options.length;i++){
            gamecon.add(options[i]);
            options[i].setBounds(0,0,0,0);
            options[i].addActionListener(new Act(i,this,false));
        }
        
        gamecon.add(screen);
        gamecon.add(selfname);
        gamecon.add(selfpicture);
        gamecon.add(selfcoin);
        
        screen.setBounds(0,0,800,450);
        selfname.setBounds(670,485,80,40);
        selfpicture.setBounds(650,525,90,90);
        selfcoin.setBounds(660,620,100,50);
        
        
        //win GUI
        win=new JFrame("Win");
        wincon=win.getContentPane();
        winl=new JLabel("CONGRATULATION! YOU WIN!");
        wincon.add(winl);
        win.setBounds(300, 300, 450, 400);
        wincon.setLayout(null);
        winl.setBounds(115,175,220,50);
        
        //lose GUI
        lose=new JFrame("Lose");
        losecon=lose.getContentPane();
        losel=new JLabel("YOU LOSE!");
        losecon.add(losel);
        lose.setBounds(300, 300, 450, 400);
        losecon.setLayout(null);
        losel.setBounds(115,175,220,50);
        
        //controlthread
        class Controller extends Thread{
            Reformation control;
            Scanner sc=new Scanner(System.in);
            public Controller(Reformation ref){
                super();
                control=ref;
                
            }
            public void run(){
                while(true){
                    control.writer.println("control"+sc.nextLine());
                }
            }
        }
        
        //mainthread
        class Connection extends Thread{
            Reformation control;
            public Connection(Reformation ref){
                super();
                control=ref;
                
            }
            public void run(){
                while(true){
                    try {
                        String code=control.reader.readLine();
                        if(code!=null){
                            String decode=code+"++++++++++";
                            
                            //wait
                            if(decode.substring(0,7).equals("control")){
                                String mes=code.substring(7);
                                System.out.println("Server: "+mes+"\n");
                            }
                            if(decode.substring(0,5).equals("board")){
                                String newname=code.substring(5);
                                control.preparebutton.setText("Prepare");
                                control.preparetext.setText("");
                                control.renametext.setText(newname);
                                control.enter.setVisible(true);
                            }
                            if(decode.substring(0,5).equals("clear")){
                                while(control.names.size()>0){control.names.remove(0);}
                                while(control.status.size()>0){control.status.remove(0);}
                            }
                            if(decode.substring(0,3).equals("add")){
                                int index=code.indexOf("!");
                                String statstr=code.substring(3, index);
                                String newname=code.substring(index+1);
                                int statint=atoi(statstr);
                                control.names.add(newname);
                                if(statint==1){control.status.add("dreaming");}
                                else{
                                    if(statint<100){control.status.add("waiting("+statint+")");}
                                    else{control.status.add("playing("+(statint-100)+")");}
                                }
                            }
                            if(decode.substring(0,4).equals("load")){
                                control.inftext.setText("");
                                for(int i=0;i<control.names.size();i++){
                                    control.inftext.append("\t"+control.names.get(i)+"\t"+control.status.get(i)+"\n");
                                }
                            }
                            
                            
                            //game in
                            if(decode.substring(0,4).equals("game")&&code.length()>9&&code.indexOf("!")!=-1){//023jws!jjj!www!sss
                                control.current=0;
                                control.cardchoice=-1;
                                control.playerchoice=-1;
                                control.self=(code.charAt(4)-'0');
                                String members=code.substring(7);
                                while(control.players.size()>0)control.players.remove(0);
                                while(members.indexOf("!")!=-1){
                                    int index=members.indexOf("!");
                                    control.players.add(new Player(members.substring(0,index)));
                                    members=members.substring(index+1);
                                }
                                control.players.add(new Player(members));
                                
                                synchronized("message"){
                                    control.time=0;
                                    control.hint="";
                                    while(control.waitmessages.size()>0)control.waitmessages.remove(0);
                                    control.messagetimes[0]=0;
                                    control.messagetimes[1]=0;
                                    control.messagetimes[2]=0;
                                    control.waitmessages.add("游戏开始");
                                }
                                
                                setCards(code.substring(5,7)+"00");
                                setOpt(0);
                                
                                control.game.setVisible(true);
                                control.enter.setVisible(false);
                                control.screen.setStart(true);
                            }
                            
                            //win lose
                            if(decode.substring(0,3).equals("win")){
                                control.preparebutton.setText("Prepare");
                                control.screen.setStart(false);
                                control.enter.setVisible(true);
                                control.game.setVisible(false);
                                if(code.length()==3)control.win.setVisible(true);
                                else {
                                    control.losel.setText("YOU LOSE! Winner is "+code.substring(3)+"!");
                                    control.lose.setVisible(true);
                                }
                            }
                            
                            //game operation
                            
                            if(decode.substring(0,4).equals("over")){
                                synchronized("message"){
                                    int p=decode.charAt(4)-'0';
                                    if(p>=0&&p<players.size())control.waitmessages.add("游戏结束，胜利者为"+control.players.get(p).name);
                                }
                                
                            }
                            
                            if(decode.substring(0,4).equals("coin")){
                                if(code.length()==7){
                                    int c=(code.charAt(6)-'0'>=0)&&(code.charAt(6)-'0'<control.players.size())?code.charAt(6)-'0':0;
                                    control.players.get(c).coins=code.charAt(4)-'0';
                                    synchronized("message"){
                                        control.waitmessages.add(control.players.get(c).name+"最新资产变更为"+control.players.get(c).coins);
                                    }
                                }
                                if(code.length()==8){
                                    int c=(code.charAt(7)-'0'>=0)&&(code.charAt(7)-'0'<control.players.size())?code.charAt(7)-'0':0;
                                    control.players.get(c).coins=atoi(code.substring(4,6));
                                    synchronized("message"){
                                        control.waitmessages.add(control.players.get(c).name+"最新资产变更为"+control.players.get(c).coins);
                                    }
                                }
                                
                            }
                            
                            if(code.length()==5&&code.substring(0,4).equals("hurt")){
                                control.playerchoice=-1;
                                int c=(code.charAt(4)-'0'>=0)&&(code.charAt(4)-'0'<control.players.size())?code.charAt(4)-'0':0;
                                if(control.players.get(c).cards>0)control.players.get(c).cards--;
                                synchronized("message"){
                                    control.waitmessages.add(control.players.get(c).name+"失去了一张角色牌");
                                    if(control.players.get(c).cards<1)control.waitmessages.add(control.players.get(c).name+"死亡");
                                }
                            }
                            
                            if(code.length()==8&&code.substring(0,4).equals("card")){
                                if(control.cardchoice!=-1){
                                    control.cards[control.cardchoice].setBackground(Color.black);
                                    control.cardchoice=-1;
                                }
                                
                                control.setCards(code.substring(4));
                            }
                            
                            if(code.length()>4&&code.substring(0,4).equals("time")){
                                synchronized("message"){control.time=atoi(code.substring(4))*1000/control.sleeptime;}
                            }
                            
                            if(code.length()==5&&code.substring(0,4).equals("turn")){
                                control.current=code.charAt(4)-'0';
                                synchronized("message"){control.waitmessages.add("轮到"+control.players.get(control.current).name+"决策了");}
                            }
                            if(decode.substring(0,3).equals("ask")){
                                if(code.length()==3){
                                    control.setOpt(1);
                                    control.hint="请选择下一步的行动";
                                }
                                if(decode.substring(0,6).equals("askoff")){
                                    control.setOpt(0);
                                    control.hint="";
                                }
                                if(code.length()==10&&code.substring(0,8).equals("askdoubt")){
                                    control.setOpt(2);
                                    int p=code.charAt(8)-'0';
                                    p=(p>=0&&p<control.players.size()?p:0);
                                    control.hint=control.players.get(p).name+"声称自己是"+getCharacter(code.charAt(9)-'0')+"，是否质疑";
                                }
                                if(code.equals("ask1")){
                                    control.setOpt(2);
                                    control.hint="是否声称自己是男爵来阻止外援";
                                }
                                if(code.equals("ask0")){
                                    control.setOpt(2);
                                    control.hint="是否声称自己是大使或队长或审判官以免于被偷窃";
                                }
                                if(code.equals("ask5")){
                                    control.setOpt(2);
                                    control.hint="是否声称自己是伯爵夫人以免于被刺杀";
                                }
                                if(code.equals("ask6")){
                                    control.setOpt(2);
                                    control.hint="是否要替换对方的牌（摸一张弃一张）";
                                }
                                if(code.equals("askdrop")){
                                    control.setOpt(2);
                                    control.hint="请选择自己的一张牌并弃掉";
                                }
                                if(code.length()==8&&code.substring(0,7).equals("askdrop")){
                                    control.setOpt(2);
                                    int p=code.charAt(7)-'0';
                                    p=(p>=0&&p<control.players.size()?p:0);
                                    control.hint="请帮"+control.players.get(p).name+"选择一张牌并弃掉";
                                }
                            }
                            if(code.length()>=8&&code.substring(0,7).equals("message")){
                                synchronized("message"){
                                    int p=code.charAt(7)-'0';
                                    p=((p>=0&&p<control.players.size())?p:0);
                                    if(code.length()==8){
                                        control.waitmessages.add(control.players.get(p).name+"正在决策");
                                    }
                                    if(code.length()==10&&code.substring(8,9).equals("s")){
                                        String trueidentity=getCharacter(code.charAt(9)-'0');
                                        control.waitmessages.add(control.players.get(p).name+"质疑成功,被质疑者的实际身份是"+trueidentity);
                                    }
                                    if(code.length()==9&&code.substring(8,9).equals("f")){
                                        control.waitmessages.add(control.players.get(p).name+"质疑失败");
                                    }
                                    if(code.length()==11&&code.substring(8,11).equals("be1")){
                                        control.waitmessages.add(control.players.get(p).name+"自称男爵，要阻止外援");
                                    }
                                    if(code.length()==11&&code.substring(8,11).equals("be5")){
                                        control.waitmessages.add(control.players.get(p).name+"自称伯爵夫人，要阻止刺杀");
                                    }
                                    if(code.length()==11&&code.substring(8,11).equals("be0")){
                                        control.waitmessages.add(control.players.get(p).name+"自称大使或队长或审判官，要阻止偷窃");
                                    }
                                    if(code.length()==11&&code.substring(8,11).equals("be6")){
                                        control.waitmessages.add(control.players.get(p).name+"决定改变对方的牌");
                                    }
                                    if(code.length()==12&&code.substring(8,10).equals("dt")){
                                        int c=code.charAt(10)-'0';
                                        c=((c>=0&&c<control.players.size())?c:0);
                                        control.waitmessages.add(control.players.get(p).name+"质疑"+control.players.get(c).name+getCharacter(code.charAt(11)-'0')+"的身份");
                                    }
                                    if(decode.substring(8,10).equals("do")){
                                        int c=decode.charAt(11)-'0';
                                        int a=decode.charAt(10)-'0';
                                        c=((c>=0&&c<control.players.size())?c:0);
                                        String another=control.players.get(c).name;
                                        control.waitmessages.add(control.players.get(p).name+actionName(a,another));
                                    }
                                }
                            }
                            
                        }
                    } catch (IOException ex) {}
                }
            }
        }
        
        try {
            socket=new Socket(ipaddress,10001);
            writer=new PrintWriter(socket.getOutputStream(),true);
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            Connection connection=new Connection(this);
            connection.start();
            Controller controller=new Controller(this);
            controller.start();
            
        } catch (Exception ex) {
            fail.setVisible(true);
        }
        
    }
    
    public void setCards(String set){
        ArrayList<Integer>cardset=new ArrayList();
        if(set.length()==4){
            for(int index=0;index<4;index++){
                if(set.charAt(index)=='0')break;
                cardset.add(set.charAt(index)-'0');
            }
        }
        for(int index=0;index<4;index++){
            if(index>=cardset.size())cards[index].setBounds(0,0,0,0);
            else{
                cards[index].setIcon(images[cardset.get(index)]);
                cards[index].setBounds(380-65*cardset.size()+index*130,520,90,120);
            }
        }
        
    }
    
    public void setOpt(int type){
        switch(type){
            case 0:
                for(int i=0;i<11;i++){
                    options[i].setBounds(0,0,0,0);
                }
                break;
            case 1:
                for(int i=0;i<9;i++){
                    options[i].setBounds(20+85*i,455,80,25);
                }
                for(int i=9;i<11;i++){
                    options[i].setBounds(0,0,0,0);
                }
                break;
            case 2:
                for(int i=0;i<9;i++){
                    options[i].setBounds(0,0,0,0);
                }
                for(int i=0;i<2;i++){
                    options[i+9].setBounds(300+120*i,455,80,30);
                }
                
        }
    }
    
    public static void main(String[] args) {
        Reformation control=new Reformation();
    }
    public static int atoi(String a){
        int i=0;
        for(int index=0;index<a.length();index++){
            int n=a.charAt(index)-'0';
            i=i*10+n;
        }
        return i;
    }
    public static String getCharacter(int i){
        switch(i){
            case 0: return "大使或队长或审判官";
            case 1: return "男爵";
            case 2: return "刺客";
            case 3: return "大使";
            case 4: return "队长";
            case 5: return "伯爵夫人";
            case 6: return "审判官";
        }
        return "";
    }
    public static String actionName(int action,String another){
        switch(action){
            case 0: return "决定税收（得一钱）";
            case 1: return "决定申请外援（得二钱）";
            case 2: return "决定向"+another+"发动政变";
            case 3: return "自称男爵，要收税（得三钱）";
            case 4: return "自称刺客，要刺杀"+another;
            case 5: return "自称大使，要换两张牌";
            case 6: return "自称队长，要对"+another+"进行偷窃";
            case 7: return "自称审判官，要换一张牌";
            case 8: return "自称审判官，要审查"+another+"的牌";
        }
        return "";
    }
        
}
/*
boardjws
clear
add10!jws
load

prepare5
renamejws

game023jws!jjj!www!sss
win
winjws
message1
message1do1
message1do25
message1dt21
message1s2
message1f
message1be1
message1be5
message1be0
message1be6
ask
askoff
askdoubt31
askdoubt30
ask1
ask0
ask5
ask6
askdrop
askdrop3
coin9!3
hurt3
card2410
turn1
time30

1
2!3
4!1!3
*/