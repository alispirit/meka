package meka.classifiers.multilabel;



import meka.classifiers.multilabel.BR;
import meka.core.MLUtils;
import weka.core.AttributeStats;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import java.util.Iterator;
import java.util.Scanner;


/**
 * Created by alireza on 6/7/17.
 */
public class test3 extends ProblemTransformationMethod  {
    public static void main(String[] args) throws Exception {
        System.out.println("......................");
        System.out.println("2");

        if (args.length != 2)
            throw new IllegalArgumentException("Required arguments: <train> <test>");

        int index;

        for (index = 0; index < args.length; ++index)
        {
            System.out.println("args[" + index + "]: " + args[index]);
        }

        System.out.println("Loading train: " + args[1]);
        Instances train = DataSource.read(args[1]);

        int numinstance = train.numInstances();
        int numattributes = train.numAttributes();
        MLUtils.prepareData(train);

//        testCapabilities(train);

        int L = train.classIndex();

        System.out.println("train class index :"+ L);
        System.out.println("train attr number :"+ train.numAttributes());

        System.out.println("------------------");
//        for (int instIdx = 0; instIdx < numinstance; instIdx++) {

        int ix=numinstance;
        int jx=L;
        double Data[][] = new double[ix][jx];
        for (int instIdx = 0; instIdx < 10; instIdx++) {
            Instance currInst = train.instance(instIdx);
//            System.out.println("---------------------------------");
//            System.out.println("instanse is : "+ instIdx );

//            System.out.println(currInst.enumerateAttributes().toString());
//            System.out.println("is nameric :"+currInst.attribute(L+1).isNumeric());
//            System.out.print("numeric value : "+currInst.attribute(L+1).numValues());

            for (int attrIdx = 0; attrIdx < L; attrIdx++) {
//                System.out.println("attribute "+ attrIdx + " :");
                Data[instIdx][attrIdx ] = train.instance(instIdx).value(attrIdx);
                Attribute currAttr = currInst.attribute(attrIdx);
//                System.out.println(currAttr.value(attrIdx));
//                if (currAttr.isNominal()) {
//                   System.out.print("=isNominal=");
//                }
//                else if (currAttr.isNumeric()) {
//                    System.out.print("=isNumeric=");
//                }

//                System.out.println("type is : " + (currAttr.type()==1 ? " is nominal " : " it is not nominal"));
/*                if (currAttr.isNominal()) {
                    System.out.println(currAttr.toString());
                }*/
//                  explain about attr 
//                System.out.println(currAttr.toString());

            }


        }
        double label_count[]=new double[jx];

        for(int i=0;i<ix;i++){
//            System.out.println("instance :" + i);
            for(int j=0;j<jx;j++){
//                System.out.print(Data[i][j]+" - ");
                label_count[j]+=Data[i][j];

            }
//            System.out.println(" ");
        }

        System.out.println("-----------------------------------------------------------------------");
        System.out.println(" labels :");
        for(int j=0;j<jx;j++){
            System.out.print( "attribute "+ j+" is :"+ train.instance(0).attribute(j).toString());
            System.out.print( "value is :"+ (int) label_count[j]+" - ");
            System.out.println("");
        }

    }
    public void getlabel(){


    }


    public  void buildClassifier(Instances trainingSet){

    };

    @Override
    public  double[] distributionForInstance(Instance i) {
        double a[]=new double[5];
        return a;
    }
}
