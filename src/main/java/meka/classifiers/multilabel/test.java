package meka.classifiers.multilabel;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Drawable;
import meka.core.MultiLabelDrawable;
import meka.core.MLUtils;
import weka.core.RevisionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import meka.classifiers.multilabel.hierarchy.* ;
/**
 * Created by alireza on 4/28/17.
 */

public class test extends ProblemTransformationMethod {
//    public ArrayList<Link> hrr;
    public static ArrayList<Link> hierarchical(){
        hierarchy obj = new hierarchy();
        return obj.get_hierarchy();
    }



    public void buildClassifier(Instances D) throws Exception {

        testCapabilities(D);
        int L = D.classIndex();
        m_Classifier.buildClassifier(D);
        if (getDebug()) System.out.print("Creating " + L + " models (" + m_Classifier.getClass().getName() + "): ");

//        m_MultiClassifiers = AbstractClassifier.makeCopies(m_Classifier,L);
//        m_InstancesTemplates = new Instances[L];
//        structure strc = new structure();

        System.out.println("=======hierarchical stracture======");
        ArrayList<Link> hrr = hierarchical();
        for (Link node : hrr) {
            System.out.println("data node = " + node.data);
            System.out.println("data minheight = " + node.mindepth());
            System.out.println("data maxheight = " + node.maxdepth());
            System.out.println("--------------");
        }

        System.out.println("=======build classifier======");

        for(int j = 0; j < hrr.size(); j++) {
            System.out.println(hrr.size());
        }



    }
    public static void main(String[] args) {
//        structure strc = new structure();
//        System.out.println("=======hierarchical stracture======");
//        ArrayList<Link> hrr = hierarchical();
//        for( Link node : hrr ){
//            System.out.println("data node = "+ node.data);
//            System.out.println("data minheight = "+ node.mindepth());
//            System.out.println("data maxheight = "+ node.maxdepth());
//            System.out.println("--------------");

//        }
        System.out.println("main function aaaaa");


    }

    public double[] distributionForInstance(Instance x) throws Exception {

            int L = x.classIndex();

            double y[] = new double[L];

//            for (int j = 0; j < L; j++) {
//                Instance x_j = (Instance)x.copy();
//                x_j.setDataset(null);
//                x_j = MLUtils.keepAttributesAt(x_j,new int[]{j},L);
//                x_j.setDataset(m_InstancesTemplates[j]);
//                //y[j] = m_MultiClassifiers[j].classifyInstance(x_j);
//                y[j] = m_MultiClassifiers[j].distributionForInstance(x_j)[1];
//            }

            return y;
    }



}
