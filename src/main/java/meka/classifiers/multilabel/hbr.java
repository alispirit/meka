package meka.classifiers.multilabel;

/**
 * Created by alireza on 9/2/17.
 */

import meka.core.MLUtils;
import meka.core.MultiLabelDrawable;
import meka.core.SystemInfo;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.lang.*;
import java.util.ArrayList;
import java.util.Scanner;
import meka.classifiers.multilabel.hierarchical.*;


public class hbr {
    public static void main(String args[]) throws Exception {
        System.out.println("start hbr");
        System.out.println("************************");

        String dpath = new String(args[1]);
        String hpath = new String(args[2]);

        String content = new Scanner(new File(hpath)).useDelimiter("\\Z").next();
        String[] parts = content.split(",");
        dag dagobj = new dag();

        for (int i = 0; i < parts.length; i++) {
//            System.out.println(parts[i]);
            dagobj.add(parts, i, 0);

        }
        ArrayList<Links> dag = dagobj.getlist();
        dag = dagobj.sortmindepth();

        Instances data = ConverterUtils.DataSource.read(dpath);
        MLUtils.prepareData(data);

        int numAttrs = data.numAttributes();
        int numInstances = data.numInstances();
        int numclass = data.classIndex();
        System.out.println(numInstances);
        System.out.println(numclass);
        System.out.println(numAttrs);
        System.out.println(dag.size());

        Instances[] hdata = new Instances[dag.size()];

        for(int i=1;i<dag.size();i++){// for all nodes
//        for(int i=1;i<10;i++){// for all nodes
            /*
            * for any nodes we create new instances object
            * and add to that instances
            * that have  one or more parrents  class
            * */
            Instances tempdata = new Instances(data,0);
            Links currentnode = dag.get(i);
            if( currentnode.mindepth()>0 ){
                System.out.println(currentnode.data+" depth : "+currentnode.mindepth());


                System.out.println("currntnode : "+currentnode.data);
                for (int j = 0; j < numInstances; j++)// for all instances
                {
                    boolean validinstance = false;
                    Instance currentinstance = data.instance(j);
                    for(int k=0; k < currentnode.pLink.size();k++ ){
                        int indexofparrent = dag.indexOf(currentnode.pLink.get(k));

                        String temp = currentinstance.stringValue(indexofparrent);
                        if(temp.equals("1")){
                            validinstance=true;
                        }
                    }
                    if(validinstance){
                        tempdata.add(currentinstance) ;
                    }
//                    System.out.print(+"," );


                }

                //                  Select only class attribute 'j'
                Instances D_j = MLUtils.keepAttributesAt(new Instances(tempdata),new int[]{i},numclass);
                D_j.setClassIndex(0);

//                System.out.println("after select "+D_j.numInstances());
                System.out.println("after select "+D_j.numAttributes());
//                System.out.println("after select "+D_j.numClasses());
                System.out.println("after select "+D_j.classIndex());

//                System.out.println("before select "+tempdata.numInstances());
                System.out.println("before select "+tempdata.numAttributes());
//                System.out.println("before select "+tempdata.numClasses());
                System.out.println("before select "+tempdata.classIndex());


//                System.out.println(tempdata.numInstances());
                hdata[i] = tempdata;
//                System.out.println(hdata[i].numInstances());
                System.out.println(hdata.length);
                System.out.print("\n");
            }

        }

//        for(int i=0;i<parts.length;i++){
//            String[] nodes = parts[i].split("/");
//        }
    }

}

