package com.opencncbender.logic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiselectionEditableObservableList<T> {

    protected ObservableList<T> list = FXCollections.observableArrayList();

    public MultiselectionEditableObservableList() {
    }

   public void add(T element){

        list.add(element);
    }

    public int size(){

        return list.size();
    }

    public T get(int i){

        return  list.get(i);
    }

    public void remove(int index) {
        if((list != null) && (index >= 0) && (index < list.size() )) {
            list.remove(index);
        }
    }

    public void clear() {
        if(list != null) {
            list.clear();
        }
    }

    private boolean isSelectionValid(int firstSelectionIndex, int lastSelectionIndex){

        if((firstSelectionIndex >= 0) &&
                (lastSelectionIndex >= 0) &&
                (firstSelectionIndex < list.size()-1) &&
                (lastSelectionIndex < list.size()) &&
                (firstSelectionIndex < lastSelectionIndex)){
            return true;
        }
        else return false;
    }

    public void moveSelectedToTheStart(int firstSelectionIndex, int lastSelectionIndex) {

        if((list != null)&&(isSelectionValid(firstSelectionIndex,lastSelectionIndex))) {

            List<T> head, selected, tail;
            head = new ArrayList<>(list.subList(0,firstSelectionIndex));
            selected = new ArrayList<>(list.subList(firstSelectionIndex,lastSelectionIndex+1));
            tail = new ArrayList<>(list.subList(lastSelectionIndex+1,list.size()));

            list.clear();
            list.addAll(selected);
            list.addAll(head);
            list.addAll(tail);
        }
    }

    public void moveSelectedToTheEnd(int firstSelectionIndex, int lastSelectionIndex) {

        if((list != null)&&(isSelectionValid(firstSelectionIndex,lastSelectionIndex))) {

            List<T> head, selected, tail;
            head = new ArrayList<>(list.subList(0,firstSelectionIndex));
            selected = new ArrayList<>(list.subList(firstSelectionIndex,lastSelectionIndex+1));
            tail = new ArrayList<>(list.subList(lastSelectionIndex+1,list.size()));

            list.clear();
            list.addAll(head);
            list.addAll(tail);
            list.addAll(selected);
        }
    }

    public void reverseSelected(int firstSelectionIndex, int lastSelectionIndex) {

        if((list != null)&&(isSelectionValid(firstSelectionIndex,lastSelectionIndex))) {
            List<T> selected = list.subList(firstSelectionIndex,lastSelectionIndex+1);
            Collections.reverse(selected);
        }
    }

    public void reversePolyline() {
        if(list != null) {
            Collections.reverse(list);
        }
    }

    public void deleteSelected(int firstSelectionIndex, int lastSelectionIndex) {
        if((list != null)&&(isSelectionValid(firstSelectionIndex,lastSelectionIndex))) {
            List<T> selected = list.subList(firstSelectionIndex,lastSelectionIndex+1);
            selected.clear();
        }
    }

    public void addAll(List<T> addList) {
        list.addAll(addList);
    }

    public ObservableList<T> get() {
        return list;
    }

    public void set(int index, T element) {
        list.set(index,element);
    }
}
