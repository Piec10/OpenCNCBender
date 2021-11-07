package com.opencncbender.logic;

import java.util.ArrayList;

/**
 * Created by Piec on 2020-01-03.
 */
public class Steps<T> {

    private ArrayList<T> stepsList = new ArrayList<>();

    public Steps() {
    }

    public void addStep(T singleStep){

        stepsList.add(singleStep);
    }

    public int size(){

        return stepsList.size();
    }

    public void trimToSize(){

        stepsList.trimToSize();
    }

    public T get(int i){

        return  stepsList.get(i);
    }

    public void remove(int index) {
        stepsList.remove(index);
    }

    public void clear() {
        stepsList.clear();
    }
}
