package meka.classifiers.multilabel.hierarchy;

//import java.util.ArrayList;
import java.util.*;

/**
 * Created by alireza on 11/12/16.
 */
public class dag {
    private static Link first;
    private Link temp;
    private Link newLInk;
    private static ArrayList<Link> linkToNode = new ArrayList<Link>();


    public dag(){
        first = new Link(null );
        linkToNode.add(first);
    }

    public String add(String str ,int created ){
        System.out.println("new node : " + str );
        String[] splitNode = str.split("/");
        //String last = splitNode[splitNode.length-1];
        int level = splitNode.length-1;
//        System.out.println(level);

        temp = first;

        String last = findParrent(splitNode,level);
//        find(splitNode);
//        System.out.println("after find temp.data is  : " + temp.data );
        System.out.println("********created is  : " + created );
        if(created == 0){
            newLInk = addnode(last);
        }else {
            addedge();
            Link a = temp;
        }

        linkToNode = linkToNode;
        return "aaa";
    }

    /**
        return 0 for empty dag
        return 1 for changed temp to latest node
     **/
    protected String findParrent(String[] splitNode,int level){
        temp = first;
        int i;
        String implodedList = new String();
        ArrayList implode = new ArrayList();
        String last =  splitNode[splitNode.length-1];


        for( i = 0 ; i < splitNode.length-1 ; i++){
            boolean inRange = false ;
            implode.add(splitNode[i]);
            for ( Link a : linkToNode ){
//                System.out.println(a.data+"-"+ splitNode[i]+"-?-" + splitNode[i].equals(a.data));
                if( splitNode[i].equals(a.data) ){
                    temp = a;
                    inRange = true;
                    break;
//                    System.out.println(a.data +"-"+ temp.data + "======");
                }
            }
            if(!inRange){
//                for(int j = 0 ; j <=i; j++){
//                    implodedList+=implodedList
//                }
                System.out.println("Not Exist Node :"  +implode.get(i));
                temp = addnode( implode.get(i).toString() );

            }

        }
        return last;
    }
    //===============================================================

    protected int find(String[] splitNode){
        int i;
        String implodedList = new String();
        ArrayList implode = new ArrayList();
//        System.out.println("find-"+splitNode[splitNode.length-1 ]);
        for( i = 0 ; i < splitNode.length-1 ; i++){
            boolean inRange = false ;

            implode.add(splitNode[i]);
            for ( Link a : temp.cLink ){
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
    }

    //===============================================================
    public Link addnode(String node){ //
        System.out.println(node +" add to  : "+temp.data);
        Link newNode = new Link(node);
        temp.cLink.add(newNode);
        this.addparrent(temp,newNode);
        addToList(newNode);
        return newNode;
    }

    //===============================================================
    public int addedge(){
        System.out.println(newLInk.data +" add LINK     to  : "+temp.data);
        temp.cLink.add(newLInk);
        this.addparrent(temp,newLInk);
        return 1;
    }
    //===============================================================
    public int addToList(Link l ){
        linkToNode.add(l);
        return 1;
    }
    //===============================================================
    public int addparrent(Link parrent,Link child ){
        child.pLink.add(parrent);
        child.updatedepth();
        return 1;
    }
    //===============================================================

    public ArrayList<Link> getlist(){
        return linkToNode;
    }
    //===============================================================
    public int getparrent(String s){

        return 1;
    }
    //======================================
    public  ArrayList<Link> sortmindepth(){
        int length = linkToNode.size();
            for(int i=0 ; i<length-1 ; i++)
                for ( int j = 0 ; j < length-i-1;j++){
                    if( linkToNode.get(j).mindepth() > linkToNode.get(j+1).mindepth() )
                        Collections.swap(linkToNode, j,j+1 );
                }
                return linkToNode;
    }
}
