package com.example.android.testing.notes.datastructures;

import java.util.HashSet;

/**
 * Created by cyrus on 1/23/18.
 */
public class linkedlist{

    public linkedlist(int[]values){
      for(int value : values){
          this.add(value);
      }
    }
    public  linkedlist(){

    }
    public class node {
        public  node(int val){
            this.value = val;
        }
        public node next;
        public node prev;
        public int value;
    }

    public node head;

    public node add(int value){
        if(head == null) {
            head = new node(value);
            return head;
        } else {
            node n = head;
            while(n.next!=null) {
                n = n.next;
            }
            n.next = new node(value);
            n.next.prev = n;
            return n.next;
        }
    }

    public void swap(node before, node after){
        if(after.next!=null) after.next.prev = before;
        if(before.prev!=null) before.prev.next = after;
        before.next = after.next;
        after.prev = before.prev;
        before.prev = after;
        after.next = before;
        if(after.prev == null) head = after;
    }

    public void reposition(node target, int partitionValue) {
        if (target.value < partitionValue && target.prev!= null && target.prev.value >= partitionValue) {
            node tmp = target.prev;
            swap(target.prev, target);
            reposition(target, partitionValue);
            reposition(tmp, partitionValue);
        } else if(target.value >= partitionValue && target.next!= null && target.next.value < partitionValue) {
            node tmp = target.next;
            swap(target, target.next);
            reposition(target, partitionValue);
            reposition(tmp, partitionValue);
        }
    }

    public void repositionAll(int partitionValue) {
        if(head != null){
            node n = head;
            while(n!= null && n.next != null){
                reposition(n, partitionValue);
                n = n.next;
            }
            if(n!=null) {
                reposition(n, partitionValue);
            }
        }
    }

    public void remove(node target){
        if(target.next!=null) target.next.prev = target.prev;
        if(target.prev!=null) target.prev.next = target.next;
    }

    public void removeDuplicates(){
        HashSet<Integer> dup = new HashSet();
        node n = head;
        if(n!=null){
            while(n.next!=null){
                if(dup.contains(n.value)){
                    remove(n);
                } else {
                    dup.add(n.value);
                }
                n = n.next;
            }
            if(dup.contains(n.value)){
                remove(n);
            }
        }
    }

    public void removeDuplicates2(){
        if(head != null){
            node n = head;
            while(n.next != null){
                node forward = n.next;
                while(forward.next != null){
                    if(forward.value == n.value) {
                        remove(forward);
                    }
                    forward = forward.next;
                }
                if(forward.value == n.value) {
                    remove(forward);
                }
                n = n.next;
            }
        }
    }


    public String print() {
        if(head == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            node n = head;
            while(n.next!=null){
                sb.append(n.value);
                sb.append("-");
                n = n.next;
            }
            sb.append(n.value);

            sb.append(" --- ");

            while(n.prev!=null){
                sb.append(n.value);
                sb.append("-");
                n = n.prev;
            }
            sb.append(n.value);
            return sb.toString();
        }
    }

}


