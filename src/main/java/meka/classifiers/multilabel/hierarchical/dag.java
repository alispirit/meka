package meka.classifiers.multilabel.hierarchical;

//import java.util.ArrayList;

import java.io.IOException;
import java.lang.management.LockInfo;
import java.util.*;
/**
 * Created by alireza on 11/12/16.
 */
public class dag {
    private static Links first;
    private Links temp = null;
    private Links newLInk;
    private static ArrayList<Links> linkToNode = new ArrayList<Links>();


    public dag() {
        first = new Links("root");
        linkToNode.add(first);
    }


    public String add(String[] str, int index, int created) throws IOException {
//        System.out.println("new node : " + str[index]);
        ArrayList<Links> a = new ArrayList();
        a = linkToNode;
        String[] splitNode = str[index].split("/");
        //String last = splitNode[splitNode.length-1];
        // nodeCreated==false : this is new node
        boolean nodeCreated = false;
        // check node  new or exist
        nodeCreated = this.check(splitNode[1]);
//        System.out.println(nodeCreated ? " node created before " : " new node ");
        Links parrent = findNode(splitNode, 0);
        if (nodeCreated) {// node exist
//            System.out.print("asdasd");
            Links existnode = findNode(splitNode, 1);
            this.addedge(parrent,existnode);
        } else {// new node
            //find parent of node
            this.addnode(parrent,splitNode[1]);

            //add node to linkToNode

        }
//        int level = splitNode.length-1;
//        System.out.println(level);

//        temp = first;

//        String last = findParrent(splitNode,level);
//        find(splitNode);
//        System.out.println("after find temp.data is  : " + temp.data );
//        System.out.println("********created is  : " + created );
//        if(created == 0){
//            newLInk = addnode(last);
//        }else {
//            addedge();
//            Links a = temp;
//        }

//        linkToNode = linkToNode;
        return "aaa";
    }

    public boolean check(String node) {
        boolean created = false;
        for (int i = 0; i < linkToNode.size(); i++) {
            if (linkToNode.get(i).data.equals(node)) {
                created = true;
            }
        }
        return created;
    }

    /**
     * return 0 for empty dag
     * return 1 for changed temp to latest node
     **/
    protected Links findNode(String[] splitNode, int node) {
        for (int i = 0; i < linkToNode.size(); i++) {
            if (linkToNode.get(i).data.equals(splitNode[node]))
                return linkToNode.get(i);
//                temp = linkToNode.get(i);
        }
        return first;
    }
    //===============================================================
/*
    protected int find(String[] splitNode){
        int i;
        String implodedList = new String();
        ArrayList implode = new ArrayList();
//        System.out.println("find-"+splitNode[splitNode.length-1 ]);
        for( i = 0 ; i < splitNode.length-1 ; i++){
            boolean inRange = false ;

            implode.add(splitNode[i]);
            for ( Links a : temp.cLink ){
                System.out.println(a.data+"-"+ splitNode[i]+"=>" + splitNode[i].equals(a.data));
                if( splitNode[i].equals(a.data) ){
                    temp = a;
                    inRange = true;
//                    System.out.println(a.data +"-"+ temp.data + "======");
                }
            }
            if(!inRange){
//                for(int j = 0 ; j <=i; j++){
//                    implodedList+=implodedList
//                }
                System.out.println("Not In Range" + i);
                implodedList = String.join("/",implode);
//                System.out.println(implodedList);
                add(implodedList,0);
            }

        }
        System.out.println("temp is : "+temp.data);

        return 1;
    }*/

    //===============================================================
    public Links addnode(Links parrent,String data) { //

        Links newNode = new Links(data);
//        System.out.println(data + " created . " );
        if (parrent != null) {
            this.addedge(parrent,newNode);
//            parrent.cLink.add(newNode);
//            this.addparrent(parrent, newNode);
        } else {
            first = newNode;
        }


        addToList(newNode);
        return newNode;
    }

    //===============================================================
    public int addedge(Links parrent,Links child) {
//        System.out.println(child.data + " add LINK     to  : " + parrent.data);
        parrent.cLink.add(child);
        this.addparrent(parrent, child);
        return 1;
    }

    //===============================================================
    public int addToList(Links l) {
        linkToNode.add(l);
        return 1;
    }

    //===============================================================
    public int addparrent(Links parrent, Links child) {
        child.pLink.add(parrent);
        child.updatedepth();
        return 1;
    }
    //===============================================================

    public ArrayList<Links> getlist() {
        return linkToNode;
    }

    //===============================================================
    public int getparrent(String s) {

        return 1;
    }

    //======================================
    public ArrayList<Links> sortmindepth() {
        int length = linkToNode.size();
        for (int i = 0; i < length - 1; i++)
            for (int j = 1; j < length - i - 1; j++) {
                if (linkToNode.get(j).mindepth() > linkToNode.get(j + 1).mindepth())
                    Collections.swap(linkToNode, j, j + 1);
            }
        return linkToNode;
    }
}
