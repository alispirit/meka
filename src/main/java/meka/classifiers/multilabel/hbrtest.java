package meka.classifiers.multilabel;

import meka.classifiers.multilabel.hierarchical.Links;
import meka.classifiers.multilabel.hierarchical.dag;
import meka.core.MLUtils;
import meka.core.MultiLabelDrawable;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import  weka.classifiers.Classifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class hbrtest  extends ProblemTransformationMethod implements MultiLabelDrawable {

    protected Classifier m_MultiClassifiers[] = null;
    protected Instances m_InstancesTemplates[] = null;
    protected static ArrayList<Links> m_dag = null;


    @Override
    public void buildClassifier(Instances trainingSet) throws Exception {
//        testCapabilities(D);
//        int C = D.classIndex();
        System.out.println("build classifiers");
    }

    @Override
    public double[] distributionForInstance(Instance i) throws Exception {
        return new double[0];
    }

    @Override
    public Map<Integer, Integer> graphType() {
        return null;
    }

    @Override
    public Map<Integer, String> graph() throws Exception {
        return null;
    }

    public static void main(String args[]) throws Exception {
        System.out.println("start hbr ");


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
        m_dag = dagobj.getlist();
        m_dag = dagobj.sortmindepth();

        for(Links a:m_dag){
            System.out.println(a.data +" length : "+a.mindepth());
        }
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
        System.out.println("size of dag               : " + m_dag.size());
        System.out.println("number of test instances  : " + testnumInstances);
        System.out.println("--------------------");
        ProblemTransformationMethod.evaluation(new hbrtest(),args);



    }
}
