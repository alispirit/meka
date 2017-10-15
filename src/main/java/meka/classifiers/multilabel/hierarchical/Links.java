package meka.classifiers.multilabel.hierarchical;

import java.util.ArrayList;

/**
 * Created by alireza on 11/12/16.
 */
public class Links {
    public String data;
    public int flag;
    private int  maxldepth=0;
    private int mindepth=0;
    public ArrayList<Links> pLink = new ArrayList<Links>();//parrent link
    public ArrayList<Links> cLink = new ArrayList<Links>();//child link

    //Link constructor
    public Links(String data1) {
        data = data1;
        flag=0;
    }


    public void updatedepth(){
        for ( Links l: this.pLink) {
            this.maxldepth = this.maxldepth < l.maxldepth+1 ? l.maxldepth+1 : this.maxldepth ;
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
        return this.maxldepth;
    }
}
