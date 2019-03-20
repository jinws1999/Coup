package reformationserver;

import java.util.*;

public class Player {
    String name;
    int coin;
    Game game;
    ArrayList<Integer>cards=new ArrayList();
    public Player(String n,Game g){
        name=n;
        coin=2;
        game=g;
        getCard();
        getCard();
    }
    public void getCard(){
        if(game.newcard.size()<1&&game.oldcard.size()>0)game.refreshCard();
        if(game.newcard.size()>0){
            cards.add(game.newcard.get(0));
            game.newcard.remove(0);
        }
    }
    public void loseCard(int num){
        if(cards.size()>num){
            game.oldcard.add(cards.get(num));
            cards.remove(num);
        }
    }
    
}
