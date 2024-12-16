package com.luv.s2s;

import java.util.Comparator;

public class WinnerComparator implements Comparator<Winner> {
    public int compare(Winner w1, Winner w2){
        return w1.getWonTimes() > w2.getWonTimes() ? -1 :(w1.getWonTimes() < w2.getWonTimes() ? 1 : 0);
    }
}
