package meka.classifiers.multilabel;

import meka.core.MLUtils;
import meka.core.MultiLabelDrawable;
import meka.core.SystemInfo;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.lang.*;

import java.util.Map;

/**
 * Created by alireza on 8/19/17.
 */


/*
structure:
6 labels
null > 1
null > 2
1 > 3
1 > 4
2 > 5
2 > 6
*/
public class test2 {


    public static void main(String args[]) throws Exception {
        System.out.println("main");

        System.out.println();
        String handle = args[0], datapath;
        if (handle.equals("-t"))
            datapath = args[1];
        else
            throw new IllegalArgumentException("Required arguments: <dataset>");
        System.out.println("no test");
        System.out.println("Loading data: " + datapath);
        Instances data = ConverterUtils.DataSource.read(datapath);
        MLUtils.prepareData(data);

        int numAttrs = data.numAttributes();
        int numInstances = data.numInstances();
        int numclass = data.classIndex();

        Instances m_InstancesTemplates[] = null;
        m_InstancesTemplates = new Instances[numclass];


        for(int j = 0; j < numclass; j++) { //create dataset for each label contains : one class and all attrs

            //Select only class attribute 'j'
            Instances D_j = MLUtils.keepAttributesAt(new Instances(data),new int[]{j},numclass);
            D_j.setClassIndex(0);
            System.out.println("number of instances is : " + D_j.numInstances() + "\nnumber of attr is : " +  D_j.numAttributes() + "\nnumber of class is : " +  D_j.classIndex() );

            //Build the classifier for that class
//            m_MultiClassifiers[j].buildClassifier(D_j);
//            if(getDebug()) System.out.print(" " + (D_j.classAttribute().name()));

            m_InstancesTemplates[j] = new Instances(D_j);
        }

        for(int j = 0; j < numclass; j++) {
            Instances data_n = m_InstancesTemplates[j];
            double[] a = data_n.attributeToDoubleArray(0);
            for(int n = data_n.numInstances()-1; n >= 0 ; n-- ){
                System.out.println("attr is : "+ a[n] );
                System.out.println("attr index is : "+n);

            }
//            for(double i : a){
//                System.out.println("attr is : "+i);
//            }
            System.out.println("++++++++++++++++");
//            for(int n = data_n.numInstances(); n > 0 ; n-- ){
////                Attribute class_val = data_n.attribute( n);
//
//
//
//            }

        }






//        for (int instIdx = 0; instIdx < numInstances; instIdx++) {
//            Instance currInst = data.instance(instIdx);
//
//
//        }
        System.out.println("number of instances is : " + numInstances + "\nnumber of attr is : " + numAttrs + "\nnumber of class is : " + numclass);
        System.out.println("new data");
        System.out.println("number of instances is : " + m_InstancesTemplates[0].numInstances() + "\nnumber of attr is : " +  m_InstancesTemplates[0].numAttributes() + "\nnumber of class is : " +  m_InstancesTemplates[0].classIndex() );
        System.out.println("number of instances is : " + m_InstancesTemplates[1].numInstances() + "\nnumber of attr is : " +  m_InstancesTemplates[1].numAttributes() + "\nnumber of class is : " +  m_InstancesTemplates[1].classIndex() );

        System.out.println("Build BR classifier");
        BR classifier = new BR();
        // further configuration of classifier
        classifier.buildClassifier(data);
    }


}
