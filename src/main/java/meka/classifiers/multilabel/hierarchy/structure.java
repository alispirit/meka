package meka.classifiers.multilabel.hierarchy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by alireza on 11/12/16.
 */
public class structure {
    public String path ;

    public structure(){
//        path = System.getProperty("user.dir") + "/src/hierarchi/test.txt";
        path = System.getProperty("user.dir") + "/data/test.txt";

    }


    public  ArrayList<Link> getData(){
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        String p = this.path;
        dag dagobj = new dag();
        int nodeCreated = 0 ;

        try (BufferedReader br = new BufferedReader(new FileReader(p))) {
            String line;
            while ((line = br.readLine()) != null) {
                nodeCreated = 0;// say node not created - 0 it means NEW node
                String[] parts = line.split("@");
                for ( int i=1 ; i < parts.length ; i++) {
                    dagobj.add( parts[i] , nodeCreated);
//                    System.out.println(part);
                    nodeCreated = 1;
                }
                System.out.println(" ------------------ ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<Link> dag = dagobj.getlist();

        dagobj.sortmindepth();
        return dag;
    }

}
