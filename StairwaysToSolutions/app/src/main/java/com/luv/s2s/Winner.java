package com.luv.s2s;

public class Winner {
    private String winner;
    private int wonTimes;

    public Winner(String winner, int wonTimes){
        this.winner = winner;
        this.wonTimes = wonTimes;
    }

    public String getWinner(){
        return winner;
    }

    public int getWonTimes(){
        return wonTimes;
    }

    public String toString(){
        return winner + "\n" + wonTimes;
    }
}
