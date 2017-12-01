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

import meka.classifiers.multilabel.cc.CNode;
import meka.classifiers.multilabel.hierarchical.Links;
import meka.classifiers.multilabel.hierarchical.dag;
import meka.core.A;
import meka.core.MultiLabelDrawable;
import meka.core.OptionUtils;
import weka.core.*;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * hcc.java - The Classifier Chains Method. Like BR, but label outputs become new inputs for the next classifiers in the chain.
 * <br>
 * See: Jesse Read, Bernhard Pfahringer, Geoff Holmes, Eibe Frank. <i>Classifier Chains for Multi-label Classification</i>. Machine Learning Journal. Springer. Vol. 85(3), pp 333-359. (May 2011).
 * <br>
 * See: Jesse Read, Bernhard Pfahringer, Geoff Holmes, Eibe Frank. <i>Classifier Chains for Multi-label Classification</i>. In Proc. of 20th European Conference on Machine Learning (ECML 2009). Bled, Slovenia, September 2009.
 * <br>
 *
 * Note that the code was reorganised substantially since earlier versions, to accomodate additional functionality needed for e.g., MCC, PCC.
 *
 * @author	Jesse Read
 * @version December 2013
 */
public class hcc2 extends ProblemTransformationMethod
        implements Randomizable, TechnicalInformationHandler, MultiLabelDrawable {

    private static final long serialVersionUID = -4115294965331340629L;

    protected CNode nodes[] = null;

    protected int m_S = getDefaultSeed();

    protected Random m_R = null;

    protected int m_Chain[] = null;
    protected int h_Chain[][] = null;//h_Chain[node number][chain contain]
    protected static ArrayList<Links> dagobj = new ArrayList<>();
    protected static String hpath = "./target/data/datasets_GO_5/hierarchical.txt";
    /**
     * Prepare a Chain. One of the following:<br>
     * - Use pre-set chain. If there is none, then <br>
     * - Use default chain (1,2,...,L). Unless a different random seed has been set, then <br>
     * - Use a random chain.
     * @param L		number of labels
     */
    protected void prepareChain(int L) {

        int chain[] = retrieveChain();

        // if has not yet been manually chosen ...
//        if (chain == null) {

            // create the standard order (1,2,...,L) ..
            chain = A.make_sequence(L);

            // and shuffle if m_S > 0
//            if (m_S != 0) {
//                m_R = new Random(m_S);
//                A.shuffle(chain,m_R);
//            }
//        }
//        for(int i : chain){
//            System.out.println("- "+i);
//        }
        // set it
        prepareChain(chain);

    }
 /*   public boolean getDebug(){
        return false;
    }*/

    public HashSet<Links> toroot(Links node) {
        ArrayList<Links> nodes = new ArrayList<Links>();
        nodes.add(node);
        HashSet<Links> links = new HashSet();

        while (!nodes.isEmpty()) {
            links.add(nodes.get(0));
            for (Links plink : nodes.get(0).pLink) {
//                                System.out.println("added :" + plink.data);
                nodes.add(plink);
            }
            nodes.remove(0);
        }
        links.remove(node);
        return links;
    }
    /**
     * Prepare a Chain. Set the specified 'chain'.
     * It must contain all indices [0,...,L-1] (but in any order)
     * @param chain		a specified chain
     */
    public void prepareChain(int chain[]) {
//        System.out.println("dag size is :"+dagobj.size());
        h_Chain=new int[dagobj.size()][];
        dag d = new dag();

        for(int i=0; i < dagobj.size();i++){
            if(getDebug() ) System.out.println("dag size is :"+dagobj.size());
//            System.out.println(i<dagobj.size());
//            System.out.println(" i :"+i+" dagsize :"+dagobj.size());
            if(getDebug() )  System.out.println("node is "+dagobj.get(i).data);
            HashSet<Links> prt = this.toroot(dagobj.get(i));
            ArrayList<Integer> c=new ArrayList<Integer>();
            ArrayList<Links> temp = new ArrayList<Links>(prt);
            temp = d.sortmindepth(temp);
            for(Links l:temp){
//                System.out.println(l.data+" depth :"+l.mindepth());
                c.add( dagobj.indexOf(l));
            }
//            System.out.println(c.toString());

            if(getDebug() ) System.out.println("i is :"+i);
            h_Chain[i] = new int[c.size()];
            for(int j=0;j<c.size();j++){
                h_Chain[i][j]=c.get(j);
            }
            if(getDebug()) System.out.println(Arrays.toString(h_Chain[i]));
            if(getDebug())System.out.println("-----------");
        }


        m_Chain = Arrays.copyOf(chain,chain.length);
        if(getDebug() )
            System.out.println("Chain s="+Arrays.toString(m_Chain));
    }

    public int[] retrieveChain() {
        return m_Chain;
    }

    @Override
    public void buildClassifier(Instances D) throws Exception {
        testCapabilities(D);

        int L = D.classIndex();

        prepareChain(L);

        if(getDebug() ) System.out.println("instances : "+D.numInstances());
        if(getDebug() ) System.out.println("index : "+L);
		/*
		 * make a classifier node for each label, taking the parents of all previous nodes
		 */
        if(getDebug()) System.out.print(":- Chain (");
        nodes = new CNode[L];
        int pa[] = new int[]{};


//        for(int j : m_Chain) {
//            if (getDebug())
//                System.out.print(" : - :"+D.attribute(j).name());
//            nodes[j] = new CNode(j, null, pa);
//            System.out.println("");
//            System.out.println(j+" - "+Arrays.toString(pa) );
//            nodes[j].build(D, m_Classifier);
//            pa = A.append(pa,j);
//        }
        Instances tempdata[]=new Instances[L];
        for(int j=0;j<h_Chain.length;j++){
            tempdata[j] = new Instances(D, 0);
            Links currentnode = dagobj.get(j);
            if(getDebug() )System.out.println("currntnode : " +currentnode.data + " depth : " + currentnode.mindepth());
            int mindepth = currentnode.mindepth();
            int numInstances=D.numInstances();

            if(mindepth<3){
                tempdata[j] = new Instances(D);
            }else {
                for (int k = 0; k < numInstances; k++)// for all instances
                {
                    boolean validinstance = false;
                    Instance currentinstance = D.instance(k);
                    ArrayList<Links> ancestor=new ArrayList<Links>();
                    // if father of node equals + add to tempdata
                    for (int l = 0; l < currentnode.pLink.size(); l++) {
                        Links father = currentnode.pLink.get(l);
                        int indexofparrent = dagobj.indexOf(father);

                        //add ancetor to an Arraylist since if father is - check if ancestor
                        for(int n=0;n<father.pLink.size();n++){
                            ancestor.add( father.pLink.get(n));
                        }
                        String check = currentinstance.stringValue(indexofparrent);
                        if (check.equals("1")) {
                            validinstance = true;
                        }
                    }
                    if (validinstance) {
                        tempdata[j].add(currentinstance);
                    }else{
                        //check if ancestor is + add to tempdata
                        for(int n=0;n<ancestor.size();n++){
                            Links currentancestor = ancestor.get(n);
                            int indexofancestor = dagobj.indexOf(currentancestor);
                            String check = currentinstance.stringValue(indexofancestor);
                            if (check.equals("1")) {
                                validinstance = true;
                            }
                        }
                        if (validinstance) {
                            tempdata[j].add(currentinstance);
                        }


                    }


                }
            }

            if (getDebug())
                System.out.print(" : - :"+tempdata[j].attribute(j).name());
            if (getDebug())System.out.println("\nnumInstances : "+tempdata[j].numInstances());
            nodes[j] = new CNode(j, null, h_Chain[j]);
            if(getDebug() )System.out.println("");
            if(getDebug() )System.out.println(j+" - "+Arrays.toString(h_Chain[j]) );
            nodes[j].build(tempdata[j], m_Classifier);

        }
        if (getDebug()) System.out.println(" ) -:");



        // to store posterior probabilities (confidences)
        confidences = new double[L];
    }

    protected double confidences[] = null;

    /**
     * GetConfidences - get the posterior probabilities of the previous prediction (after calling distributionForInstance(x)).
     */
    public double[] getConfidences() {
        return confidences;
    }

    @Override
    public double[] distributionForInstance(Instance x) throws Exception {
        int L = x.classIndex();
        double y[] = new double[L];

//        for(int j : h_Chain) {
//        System.out.println("dag size : "+dagobj.size());
//        System.out.println("chain size : "+h_Chain.length);

        y[0] = nodes[0].classify((Instance)x.copy(),y);
        dagobj.get(0).flag=1;

        for(int j=1;j<h_Chain.length;j++){
            // h_j : x,pa_j -> y_j
            Links currentnode= dagobj.get(j);
            int checkpredict = 0;
            for(int k=0; k < currentnode.pLink.size();k++){
                int index = dagobj.indexOf( currentnode.pLink.get(k) );
                if( y[index]>0.5  ){
                    checkpredict=1;
                }
            }

            if(checkpredict==1){
                y[j] = nodes[j].classify((Instance)x.copy(),y);
            }else{
                y[j]=0;
            }


        }
//        System.out.println(Arrays.toString(y));
        return y;
    }

    /**
     * SampleForInstance.
     * predict y[j] stochastically rather than deterministically (as with distributionForInstance(Instance x)).
     * @param	x	test Instance
     * @param	r	Random 			&lt;- TODO probably can use this.m_R instead
     */
    public double[] sampleForInstance(Instance x, Random r) throws Exception {
        int L = x.classIndex();
        double y[] = new double[L];

//        for(int j : m_Chain) {
        for(int j=0;j<h_Chain.length;j++){
            double p[] = nodes[j].distribution(x, y);
            y[j] = A.samplePMF(p,r);
            confidences[j] = p[(int)y[j]];
        }

        return y;
    }


    /**
     * GetTransformTemplates - pre-transform the instance x, to make things faster.
     * @return	the templates
     */
    public Instance[] getTransformTemplates(Instance x) throws Exception {
        int L = x.classIndex();
        Instance t_[] = new Instance[L];
        double ypred[] = new double[L];
//        for(int j : m_Chain) {
        for(int j=0;j<h_Chain.length;j++){
            t_[j] = this.nodes[j].transform(x,ypred);
        }
        return t_;
    }

    /**
     * SampleForInstance - given an Instance template for each label, and a Random.
     * @param	t_	Instance templates (pre-transformed) using #getTransformTemplates(x)
     */
    public double[] sampleForInstanceFast(Instance t_[], Random r) throws Exception {

        int L = t_.length;
        double y[] = new double[L];

//        for(int j : m_Chain) {
        for(int j=0;j<h_Chain.length;j++){
            double p[] = nodes[j].distribution(t_[j],y);               // e.g., [0.4, 0.6]
            y[j] = A.samplePMF(p,r);                                   // e.g., 0
            confidences[j] = p[(int)y[j]];                             // e.g., 0.4
            nodes[j].updateTransform(t_[j],y); 						   // need to update the transform #SampleForInstance(x,r)
        }

        return y;
    }

    /**
     * TransformInstances - this function is DEPRECATED.
     * this function preloads the instances with the correct class labels ... to make the chain much faster,
     * but CNode does not yet have this functionality ... need to do something about this!
     */
    public Instance[] transformInstance(Instance x) throws Exception {
        return null;
		/*
		//System.out.println("CHAIN : "+Arrays.toString(this.getChain()));
		int L = x.classIndex();
		Instance x_copy[] = new Instance[L];
		root.transform(x,x_copy);
		return x_copy;
		*/
    }

    /**
     * ProbabilityForInstance - Force our way down the imposed 'path'.
     * <br>
     * TODO rename distributionForPath ? and simplify like distributionForInstance ?
     * <br>
     * For example p (y=1010|x) = [0.9,0.8,0.1,0.2]. If the product = 1, this is probably the correct path!
     * @param	x		test Instance
     * @param	path	the path we want to go down
     * @return	the probabilities associated with this path: [p(Y_1==path[1]|x),...,p(Y_L==path[L]|x)]
     */
    public double[] probabilityForInstance(Instance x, double path[]) throws Exception {
        int L = x.classIndex();
        double p[] = new double[L];
        for(int j=0;j<h_Chain.length;j++){
//        for(int j : m_Chain) {
            // h_j : x,pa_j -> y_j
            double d[] = nodes[j].distribution((Instance)x.copy(),path);  // <-- posterior distribution
            int k = (int)Math.round(path[j]);                             // <-- value of interest
            p[j] = d[k];                                                  // <-- p(y_j==k) i.e., 'confidence'
            //y[j] = path[j];
        }

        return p;
    }

    /**
     * Rebuild - NOT YET IMPLEMENTED.
     * For efficiency reasons, we may want to rebuild part of the chain.
     * If chain[] = [1,2,3,4] and new_chain[] = [1,2,4,3] we only need to rebuild the final two links.
     * @param	new_chain	the new chain
     * @param	D			the original training data
     */
    public void rebuildClassifier(int new_chain[], Instances D) throws Exception {
    }

    public int getDefaultSeed() {
        return 0;
    }

    @Override
    public int getSeed() {
        return m_S;
    }

    @Override
    public void setSeed(int s) {
        m_S = s;
    }

    public String seedTipText() {
        return "The seed value for randomizing the data.";
    }

    @Override
    public Enumeration listOptions() {
        Vector result = new Vector();
        OptionUtils.addOption(result, seedTipText(), "" + getDefaultSeed(), 'S');
        OptionUtils.add(result, super.listOptions());
        return OptionUtils.toEnumeration(result);
    }

    @Override
    public void setOptions(String[] options) throws Exception {
        setSeed(OptionUtils.parse(options, 'S', getDefaultSeed()));
        super.setOptions(options);
    }

    @Override
    public String [] getOptions() {
        List<String> result = new ArrayList<>();
        OptionUtils.add(result, 'S', getSeed());
        OptionUtils.add(result, super.getOptions());
        return OptionUtils.toArray(result);
    }

    /**
     * Description to display in the GUI.
     *
     * @return		the description
     */
    @Override
    public String globalInfo() {
        return "Classifier Chains. " + "For more information see:\n" + getTechnicalInformation().toString();
    }

    @Override
    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation	result;
        TechnicalInformation	additional;

        result = new TechnicalInformation(Type.ARTICLE);
        result.setValue(Field.AUTHOR, "Jesse Read, Bernhard Pfahringer, Geoff Holmes, Eibe Frank");
        result.setValue(Field.TITLE, "Classifier Chains for Multi-label Classification");
        result.setValue(Field.JOURNAL, "Machine Learning Journal");
        result.setValue(Field.YEAR, "2011");
        result.setValue(Field.VOLUME, "85");
        result.setValue(Field.NUMBER, "3");
        result.setValue(Field.PAGES, "333-359");

        additional = new TechnicalInformation(Type.INPROCEEDINGS);
        additional.setValue(Field.AUTHOR, "Jesse Read, Bernhard Pfahringer, Geoff Holmes, Eibe Frank");
        additional.setValue(Field.TITLE, "Classifier Chains for Multi-label Classification");
        additional.setValue(Field.BOOKTITLE, "20th European Conference on Machine Learning (ECML 2009). Bled, Slovenia, September 2009");
        additional.setValue(Field.YEAR, "2009");

        result.add(additional);

        return result;
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

        if (nodes != null) {
            for (i = 0; i < nodes.length; i++) {
                if (nodes[i].getClassifier() instanceof Drawable) {
                    result.put(i, ((Drawable) nodes[i].getClassifier()).graphType());
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

        if (nodes != null) {
            for (i = 0; i < nodes.length; i++) {
                if (nodes[i].getClassifier() instanceof Drawable) {
                    result.put(i, ((Drawable) nodes[i].getClassifier()).graph());
                }
            }
        }

        return result;
    }

    /**
     * Returns a string representation of the model.
     *
     * @return      the model
     */
    public String getModel() {
        StringBuilder   result;
        int             i;

        if (nodes == null)
            return "No model built yet";

        result = new StringBuilder();
        for (i = 0; i < nodes.length; i++) {
            if (i > 0)
                result.append("\n\n");
            result.append(getClass().getName() + ": Node #" + (i+1) + "\n\n");
            result.append(nodes[i].getClassifier().toString());
        }

        return result.toString();
    }

    @Override
    public String toString() {
        return Arrays.toString(retrieveChain());
    }

    public static void main(String args[]) throws IOException {
        System.out.println("HCC : classifier");
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
//        hbr3 obj = new hbr3();
        dagobj = hierarchy.sortmindepth();

//        for(Links a:dag){
//            System.out.println(dagobj.indexOf(a)+" - "+a.data +" length : "+a.mindepth());
//        }
        System.out.println("dag size is :"+dagobj.size());


        ProblemTransformationMethod.evaluation(new hcc2(), args);
//        ProblemTransformationMethod.

//        Evaluation evaluation = new Evaluation(trainInstances);
//        evaluation.evaluateModel(scheme, testInstances);
//        System.out.println(evaluation.toSummaryString());
    }
}
