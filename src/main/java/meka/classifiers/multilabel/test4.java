package meka.classifiers.multilabel;

import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by alireza on 7/15/17.
 */
public class test4 extends ProblemTransformationMethod  {


    @Override
    public void buildClassifier(Instances trainingSet) throws Exception {
        
    }

    @Override
    public double[] distributionForInstance(Instance i) throws Exception {
        return new double[0];
    }
}
