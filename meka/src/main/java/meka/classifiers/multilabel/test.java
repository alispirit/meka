package meka.classifiers.multilabel;

import meka.core.MLUtils;
import meka.core.MultiLabelDrawable;
import meka.core.SystemInfo;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Map;

/**
 * Created by alireza on 8/18/17.
 */

public class test extends ProblemTransformationMethod implements MultiLabelDrawable {
    protected Classifier m_MultiClassifiers[] = null;
    protected Instances m_InstancesTemplates[] = null;

    @Override
    public Map<Integer, Integer> graphType() {
        return null;
    }

    @Override
    public Map<Integer, String> graph() throws Exception {
        return null;
    }

    @Override
    public void buildClassifier(Instances D) throws Exception {
        testCapabilities(D);

        int L = D.classIndex();

        if(getDebug()) System.out.print("Creating "+L+" models ("+m_Classifier.getClass().getName()+"): ");
        m_MultiClassifiers = AbstractClassifier.makeCopies(m_Classifier,L);
        m_InstancesTemplates = new Instances[L];

        for(int j = 0; j < L; j++) {

            //Select only class attribute 'j'
            Instances D_j = MLUtils.keepAttributesAt(new Instances(D),new int[]{j},L);
            D_j.setClassIndex(0);
            String str = new String();
            str = MLUtils.toDebugString(new Instances(D));
            System.out.print(  str  );


            //Build the classifier for that class
            m_MultiClassifiers[j].buildClassifier(D_j);
            if(getDebug()) System.out.print(" " + (D_j.classAttribute().name()));

            m_InstancesTemplates[j] = new Instances(D_j, 0);
        }

        System.out.println("------------------------");
    }

    @Override
    public double[] distributionForInstance(Instance i) throws Exception {
        return new double[0];
    }

    public static void main(String args[]) {

    }

}
