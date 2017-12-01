/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package meka.classifiers.multilabel;

/**
 * BR3.java - The Binary Relevance Method.
 * The standard baseline Binary Relevance method (BR3) -- create a binary problems for each label and learn a model for them individually.
 * See also <i>BR3</i> from the <a href=http://mulan.sourceforge.net>MULAN</a> framework
 * @author 	Jesse Read (jmr30@cs.waikato.ac.nz)
 */

import meka.classifiers.multilabel.hierarchical.Links;
import meka.classifiers.multilabel.hierarchical.dag;
import meka.core.MLUtils;
import meka.core.MultiLabelDrawable;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Drawable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class HBR2 extends ProblemTransformationMethod implements MultiLabelDrawable {

    /** for serialization. */
    private static final long serialVersionUID = -5390512540469007904L;

    protected Classifier m_MultiClassifiers[] = null;
    protected Instances m_InstancesTemplates[] = null;
    protected static ArrayList<Links> dagobj = new ArrayList<>();
    protected static String hpath = "./target/data/datasets_GO_5/hierarchical.txt";
    /**
     * Description to display in the GUI.
     *
     * @return		the description
     */
    @Override
    public String globalInfo() {
        return
                "hierarchical test classifier.\n"
                        + "develope MEKA:\n";
    }

    @Override
    public void buildClassifier(Instances D) throws Exception {
        testCapabilities(D);
//        System.out.println("build function-------------------------------");
//        System.out.println(D.numInstances());
//        System.out.println(D.numAttributes());
//        System.out.println(D.classIndex());

        int numInstances = D.numInstances();

        int L = D.classIndex();

        if(getDebug()) System.out.print("Creating "+L+" models ("+m_Classifier.getClass().getName()+"): ");
        m_MultiClassifiers = AbstractClassifier.makeCopies(m_Classifier,L);
        m_InstancesTemplates = new Instances[L];



//        System.out.println("loop : "+L);
        for(int j = 0; j < L; j++) {
            Instances tempdata = new Instances(D, 0);
            Links currentnode = dagobj.get(j);
//            System.out.println("currntnode : " +currentnode.data + " depth : " + currentnode.mindepth());
//            if(j==0){// if current node is root
//                tempdata = new Instances(D);
//            }else {
//                for (int k = 0; k < numInstances; k++)// for all instances
//                {
//                    boolean validinstance = false;
//                    Instance currentinstance = D.instance(k);
//                    for (int l = 0; l < currentnode.pLink.size(); l++) {
//                        int indexofparrent = dagobj.indexOf(currentnode.pLink.get(l));
//
//                        String temp = currentinstance.stringValue(indexofparrent);
//                        if (temp.equals("1")) {
//                            validinstance = true;
//                        }
//                    }
//                    if (validinstance) {
//                        tempdata.add(currentinstance);
//                    }
////                    System.out.print(+"," );
//
//
//                }
//            }
            tempdata = new Instances(D);
//            System.out.println("instance number : "+tempdata.numInstances());


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
//            System.out.println(y[j]);
        }


//        System.out.println("===============================================================");

        return y;
    }

    /**
     * Returns the type of graph representing
     * the object.
     *
     * @return the type of graph representing the object (label index as key)
     */
    public Map<Integer,Integer> graphType() {
        Map<Integer,Integer>	result;
        int						i;

        result = new HashMap<Integer,Integer>();

        if (m_MultiClassifiers != null) {
            for (i = 0; i < m_MultiClassifiers.length; i++) {
                if (m_MultiClassifiers[i] instanceof Drawable) {
                    result.put(i, ((Drawable) m_MultiClassifiers[i]).graphType());
                }
            }
        }

        return result;
    }

    /**
     * Returns a string that describes a graph representing
     * the object. The string should be in XMLBIF ver.
     * 0.3 format if the graph is a BayesNet, otherwise
     * it should be in dotty format.
     *
     * @return the graph described by a string (label index as key)
     * @throws Exception if the graph can't be computed
     */
    public Map<Integer,String> graph() throws Exception {
        Map<Integer,String>		result;
        int						i;

        result = new HashMap<Integer,String>();

        if (m_MultiClassifiers != null) {
            for (i = 0; i < m_MultiClassifiers.length; i++) {
                if (m_MultiClassifiers[i] instanceof Drawable) {
                    result.put(i, ((Drawable) m_MultiClassifiers[i]).graph());
                }
            }
        }

        return result;
    }


    @Override
    public String getRevision() {
        return RevisionUtils.extract("$Revision: 9117 $");
    }

    public static void main(String args[]) throws IOException {
        System.out.println("HBR : hierarchical BR classification");
        String dpath = hpath;
        String content = new Scanner(new File(hpath)).useDelimiter("\\Z").next();
        String[] parts = content.split(",");
        dag hierarchy = new dag();
//        for(String s: args){
//            System.out.println(s);
//        }

//        create dag and sort by mindepth-------------------------
        for (int i = 0; i < parts.length; i++) {
            hierarchy.add(parts, i, 0);
        }
        ArrayList<Links> dag = hierarchy.getlist();
//        HBR2 obj = new HBR2();
        dagobj = hierarchy.sortmindepth();

//        for(Links a:dag){
//            System.out.println(dagobj.indexOf(a)+" - "+a.data +" length : "+a.mindepth());
//        }
//        System.out.println("dag size is :"+dagobj.size());


        ProblemTransformationMethod.evaluation(new HBR2(), args);
    }

}
