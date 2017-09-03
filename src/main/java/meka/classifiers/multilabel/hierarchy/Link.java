package meka.classifiers.multilabel.hierarchy;

import java.util.ArrayList;

/**
 * Created by alireza on 11/12/16.
 */
public class Link {
    public String data;
    public int flag;
    private int  maxdepth=0;
    private int mindepth=0;
    public ArrayList<Link> pLink = new ArrayList<Link>();//parrent link
    public ArrayList<Link> cLink = new ArrayList<Link>();//child link

    //Link constructor
    public Link(String data1) {
        data = data1;
    }


    public void updatedepth(){
        for ( Link l: this.pLink) {
            this.maxdepth = this.maxdepth < l.maxdepth+1 ? l.maxdepth+1 : this.maxdepth ;
            this.mindepth = this.mindepth > 0 ? this.mindepth > l.mindepth+1 ? l.mindepth+1: this.mindepth : l.mindepth+1;
        }
    }
    //Print Link data
    public void printLink() {
        System.out.print("{" + data + "} ");
    }
    public int mindepth() {
        return this.mindepth;
    }
    public int maxdepth() {
        return this.maxdepth;
    }
}
