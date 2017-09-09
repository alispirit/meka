package meka.classifiers.multilabel;

/**
 * Created by alireza on 9/2/17.
 */

import meka.core.MLUtils;
import meka.core.MultiLabelDrawable;
import meka.core.Result;
import meka.core.SystemInfo;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.rules.ZeroR;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.Drawable;
import weka.core.RevisionUtils;
import weka.classifiers.SingleClassifierEnhancer;
import meka.classifiers.multilabel.BR;
import meka.classifiers.multilabel.Evaluation;


import java.io.File;
import java.lang.*;
import java.util.ArrayList;
import java.util.Scanner;

import meka.classifiers.multilabel.hierarchical.*;


public class hbr2{
    public static BR m_MultiClassifiers[] = null;
    public static Instances m_InstancesTemplates[] = null;
    public static Classifier m_Classifier = new ZeroR();

    public static void main(String args[]) throws Exception {
        System.out.println("start hbr2 ");


        String dpath = new String(args[1]);
        String hpath = new String(args[2]);
        double percentage = Double.parseDouble(args[3]);

        String content = new Scanner(new File(hpath)).useDelimiter("\\Z").next();
        String[] parts = content.split(",");
        dag dagobj = new dag();


//        create dag and sor byr mindepth-------------------------
        for (int i = 0; i < parts.length; i++) {
            dagobj.add(parts, i, 0);
        }
        ArrayList<Links> dag = dagobj.getlist();
        dag = dagobj.sortmindepth();

        /*for(Links a:dag){
            System.out.println(a.data +" length : "+a.mindepth());
        }*/
//-----------------------------------------------------read data from file


        Instances data = ConverterUtils.DataSource.read(dpath);
        MLUtils.prepareData(data);


//------------split test and tran by persent
        int trainSize = (int) (data.numInstances() * percentage / 100.0);
        Instances train = new Instances(data, 0, trainSize);
        Instances test = new Instances(data, trainSize, data.numInstances() - trainSize);
//----------------------------------------prepare train data
        int numInstances = train.numInstances();
        int numAttrs = train.numAttributes();
        int classIndex = train.classIndex();


        int testnumInstances = test.numInstances();
        int testnumAttrs = test.numAttributes();
        int testclassIndex = test.classIndex();


        System.out.println("number of train instances : " + numInstances);
        System.out.println("number of attributes      : " + numAttrs);
        System.out.println("number of classindex      : " + classIndex);
        System.out.println("size of dag               : " + dag.size());
        System.out.println("number of test instances  : " + testnumInstances);
        System.out.println("--------------------");

        Instances[] hdata = new Instances[dag.size()];


//        m_MultiClassifiers = AbstractClassifier.makeCopies(m_Classifier, classIndex);
//        m_InstancesTemplates = new Instances[classIndex];



        /*
        * for any nodes we create new instances object
        * and add to that instances
        * that have  one or more parrents  class
        * */
        for (int i = 1; i < dag.size(); i++) {// for all classes
            Instances tempdata = new Instances(train, 0);
            Links currentnode = dag.get(i);
            System.out.println(currentnode.data + " depth : " + currentnode.mindepth());


            System.out.println("currntnode : " + currentnode.data);
            for (int j = 0; j < numInstances; j++)// for all instances
            {
                boolean validinstance = false;
                Instance currentinstance = train.instance(j);
                for (int k = 0; k < currentnode.pLink.size(); k++) {
                    int indexofparrent = dag.indexOf(currentnode.pLink.get(k));

                    String temp = currentinstance.stringValue(indexofparrent);
                    if (temp.equals("1")) {
                        validinstance = true;
                    }
                }
                if (validinstance) {
                    tempdata.add(currentinstance);
                }
//                    System.out.print(+"," );


            }

            //                  Select only class attribute 'j'
//            Instances D_j = MLUtils.keepAttributesAt(new Instances(tempdata), new int[]{i}, classIndex);
//            D_j.setClassIndex(0);

//            System.out.println("after select number instances" + D_j.numInstances());
//            System.out.println("after select numbet attr " + D_j.numAttributes());
//            System.out.println("after select classindex" + D_j.classIndex());




//                System.out.println(tempdata.numInstances());
            hdata[i] = tempdata;
//                System.out.println(hdata[i].numInstances());
            System.out.print("\n");


            BR classifier = new BR();
            classifier.buildClassifier(tempdata);
            String top = "PCut1";
            String vop = "3";
            Result result = Evaluation.evaluateModel(classifier, train, test, top, vop);

            System.out.println(result);
        }




//        for(int i=0;i<parts.length;i++){
//            String[] nodes = parts[i].split("/");
//        }
    }

}

