package meka.classifiers.multilabel;

import meka.classifiers.multilabel.hierarchical.Links;
import meka.classifiers.multilabel.hierarchical.dag;
import meka.classifiers.multilabel.hierarchy.Link;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Drawable;
import meka.core.MultiLabelDrawable;
import meka.core.MLUtils;
import weka.core.RevisionUtils;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class hbr3 extends ProblemTransformationMethod {

    /** for serialization. */
//    private static final long serialVersionUID = -5390512540469007904L;

    protected Classifier m_MultiClassifiers[] = null;
    protected Instances m_InstancesTemplates[] = null;
    protected static ArrayList<Links> dagobj = new ArrayList<>();


    @Override
    public void buildClassifier(Instances D) throws Exception {
        System.out.println("build function-------------------------------");
        System.out.println(D.numInstances());
        System.out.println(D.numAttributes());
        System.out.println(D.classIndex());
//        testCapabilities(D);
        int numInstances = D.numInstances();

        int L = D.classIndex();

//        if(getDebug()) System.out.print("Creating "+L+" models ("+m_Classifier.getClass().getName()+"): ");
        m_MultiClassifiers = AbstractClassifier.makeCopies(m_Classifier,L);
        m_InstancesTemplates = new Instances[L];

        
        System.out.println("loop : "+L);
        for(int j = 0; j < L; j++) {
            Instances tempdata = new Instances(D, 0);
            Links currentnode = dagobj.get(j);
            System.out.println("currntnode : " +currentnode.data + " depth : " + currentnode.mindepth());
            if(j==0){// if current node is root
                tempdata = new Instances(D);
            }else {
                for (int k = 0; k < numInstances; k++)// for all instances
                {
                    boolean validinstance = false;
                    Instance currentinstance = D.instance(k);
                    for (int l = 0; l < currentnode.pLink.size(); l++) {
                        int indexofparrent = dagobj.indexOf(currentnode.pLink.get(l));

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
            }

            System.out.println("instance number : "+tempdata.numInstances());


            //Select only class attribute 'j'
            Instances D_j = MLUtils.keepAttributesAt(new Instances(tempdata),new int[]{j},L);
            D_j.setClassIndex(0);

            //Build the classifier for that class
            m_MultiClassifiers[j].buildClassifier(D_j);
//            if(getDebug()) System.out.print(" " + (D_j.classAttribute().name()));

            m_InstancesTemplates[j] = new Instances(D_j, 0);
        }
        System.out.println("number of instances is : " + m_InstancesTemplates[0].numInstances() + "\nnumber of attr is : " +  m_InstancesTemplates[0].numAttributes() + "\nnumber of class is : " +  m_InstancesTemplates[0].classIndex() );
    }

    @Override
    public double[] distributionForInstance(Instance x) throws Exception {

        int L = x.classIndex();

        double y[] = new double[L];
        for (int j = 0; j < L; j++) {
            dagobj.get(j).flag=0;

        }
//        for predict root node root
//        System.out.println(dagobj.get(0).data);
        Instance x_j = (Instance)x.copy();
        x_j.setDataset(null);
        x_j = MLUtils.keepAttributesAt(x_j,new int[]{0},L);
        x_j.setDataset(m_InstancesTemplates[0]);
        //y[j] = m_MultiClassifiers[j].classifyInstance(x_j);
        y[0] = m_MultiClassifiers[0].distributionForInstance(x_j)[1];
        dagobj.get(0).flag=1;

        for (int j = 1; j < L; j++) {
            Links currentnode= dagobj.get(j);
            int checkpredict = 0;
            for(int k=0; k < currentnode.pLink.size();k++){
                int index = dagobj.indexOf( currentnode.pLink.get(k) );
                if( y[index]==1  ){
                    checkpredict=1;
                }
            }
            // for return 0/1
            if(checkpredict==1){
                y[j] = m_MultiClassifiers[j].classifyInstance(x_j);
//                y[j] = m_MultiClassifiers[j].distributionForInstance(x_j)[1];
            }else{
                y[j]=0;
            }
            //  for return num in [0,1]
            //y[j] = m_MultiClassifiers[j].classifyInstance(x_j);


        }

        return y;
    }




    public static void main(String args[]) throws Exception {
//        ProblemTransformationMethod.evaluation(new hbr3(), args);
        String dpath = new String(args[1]);
        String hpath = new String(args[2]);
        double percentage = Double.parseDouble(args[3]);

        String content = new Scanner(new File(hpath)).useDelimiter("\\Z").next();
        String[] parts = content.split(",");
        dag hierarchy = new dag();
        for(String s: args){
            System.out.println(s);
        }

//        create dag and sor byr mindepth-------------------------
        for (int i = 0; i < parts.length; i++) {
            hierarchy.add(parts, i, 0);
        }
        ArrayList<Links> dag = hierarchy.getlist();
        hbr3 obj = new hbr3();
        hbr3.dagobj = hierarchy.sortmindepth();

        for(Links a:dag){
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
        System.out.println("size of dag               : " + dag.size());
        System.out.println("number of test instances  : " + testnumInstances);
        System.out.println("--------------------");



        obj.buildClassifier(train);
//        for (int instIdx = 0; instIdx < numInstances; instIdx++) {
//        double[] res= new double[dagobj.size()];

        System.out.println("test-----------------------");
        for (int instIdx = 0; instIdx < 3; instIdx++) {
            Instance currInst = data.instance(instIdx);
            double []res = obj.distributionForInstance(currInst);
            System.out.println("\nfor "+instIdx+" instance");
            for(double d: res){
                System.out.print((int)d+",");

            }
        }
    }


}
