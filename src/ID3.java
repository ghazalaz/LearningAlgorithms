
import java.io.PrintWriter;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;

class Node {

    Node parent;
    Node children[];
    ArrayList<List<String>> instances;
    //String [] labels;
    List<String> labels;
    HashMap<String, Integer> S = new HashMap<String,Integer>();
    //double entropyS;
    Integer[] attributes;
    Integer splitAttr;
    String splitAttrValue;
    Integer sizeofS;
    String predictedLabel;
    Node(){}
    Node(Node parent, ArrayList<List<String>> instances, List<String> labels, Integer[] attributes,Integer splitAttr,String splitAttrValue) {
        this.parent = parent;
        this.instances = instances;
        this.labels = labels;
        this.sizeofS = setS(labels);
        this.splitAttr = splitAttr;
        this.splitAttrValue = splitAttrValue;
        //this.entropyS = entropy(this.S);
        this.attributes = attributes;
    }

    private Integer setS(List<String> labels){
        Set<String> setLabels = new HashSet(labels);
        for (String label :setLabels) {
            this.S.put(label, 0);
            for (int i = 0; i < labels.size() ; i++) {
                if (label.equals(labels.get(i))) {
                    S.replace(label, this.S.get(label) + 1);
                }
            }
        }
        int sum = 0;
        HashMap.Entry<String, Integer> maxEntry = null;
        for (HashMap.Entry<String, Integer> entry : this.S.entrySet())
        {
            //System.out.println(entry.getKey()+"/"+entry.getValue());
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
            sum += entry.getValue();
        }
        //System.out.println(sum);
        this.predictedLabel = maxEntry.getKey();
        return sum;
    }

    public int getSumLabels(){
        return this.sizeofS;
    }

    public String classify(List<String> test) {
        if (children == null){
            return predictedLabel;
        }
        for (int i = 0; i < children.length; i++) {
            if (test.get(children[i].splitAttr).equals(children[i].splitAttrValue)) {
                return children[i].classify(test);
            }
        }
        return "0";
    }


}

public class ID3 extends Classifier {
    Node root;
    ID3(){}
    ID3(ArrayList<List<String>> data,List<String> labels){
        this.data = data;
        this.labels = labels;
        this.setTraining(data);
        this.setTrainingLabels(labels);
        this.setAttributes();
        //this.mapLabels = PreProcess.getMapLabels(labels);

    }
    ID3(String trainfile, ArrayList<List<String>> data, String labelIndex,List<String> labels){
        super(trainfile, data, labelIndex, labels);
        //this.mapLabels = PreProcess.getMapLabels(labels);
    }

    public Node generate(ArrayList<List<String>> instances, List<String> labels, Integer[] attributes){
        Node root = new Node(null, instances, labels,attributes,null,null);
        split(root,0);
        return root;
    }
    public List<String> classify(ArrayList<List<String>> testInstances){
        List<String> predictions = new ArrayList<String>();
        for (List<String> t : testInstances) {
            String predictedCategory = root.classify(t);
            predictions.add(predictedCategory);
        }
        return predictions;
    }

    @Override
    String classify(List<String> test) {
        return root.classify(test);
    }

    public void learn(ArrayList<List<String>> training, List<String> trainingLabels) {
        this.root = generate(training,trainingLabels,attributes);
    }
    public void learn(ArrayList<List<String>> instances, List<String> labels,int numAttributes) {
        Node root = new Node(null, instances, labels,attributes,null,null);
        splitRandom(root,numAttributes,0);
        this.root = root;
    }
    public void splitRandom(Node node,int numAttributes,int depth){
        //System.out.println("Split "+ depth);
        double entropy = 0;
        double gain = 0;
        double maxGain = -1;
        int bestAttr=0;
        /* find attribute with highest information gain from random indices*/
        Random r = new Random();
        Set<Integer> randAttr = new HashSet<>();
        //System.out.println(training);
        //System.out.println(numAttributes);
        numAttributes = (int)Math.sqrt(node.attributes.length);
        while (randAttr.size() < numAttributes){
            //System.out.println(r.nextInt(numAttributes));
            randAttr.add(r.nextInt(numAttributes));
        }
        //System.out.println(randAttr);
        int size = Math.min(node.attributes.length,randAttr.size());
        //for (int i = 0; i < size; i++) {
        for(Integer i: randAttr){
            gain = calculateGain(node,node.attributes[i]);
            if (gain > maxGain){
                maxGain = gain;
                bestAttr = i;
            }
        }
        List<Integer> newAttributes = new ArrayList<Integer>();
        for (int i = 0; i < node.attributes.length; i++) {
            if ( i != bestAttr)
                newAttributes.add(node.attributes[i]);
        }
        Integer [] newAttributesArr = new Integer[newAttributes.size()];
        newAttributesArr = newAttributes.toArray(newAttributesArr);

        /* find attr's value range*/
        Set<String> values = new HashSet<>();
        for (int i = 0; i < node.instances.size() ; i++) {
            List<String> row = node.instances.get(i);
            values.add(row.get(bestAttr));
        }
        /* generate new S for each attr's value*/
        node.children = new Node[values.size()];
        int childIndex = 0;
        for (String value: values) {
            ArrayList<List<String>> newInstances = new ArrayList<List<String>>();
            List<String> labels = new ArrayList<String>();
            for (int j = 0; j < node.instances.size() ; j++) {
                List<String> row = node.instances.get(j);
                String attributeslabel = node.labels.get(j);
                if (value.equals(row.get(bestAttr))) {
                    newInstances.add(row);
                    labels.add(attributeslabel);
                }
            }
            node.children[childIndex] = new Node(node,newInstances,labels,newAttributesArr,bestAttr,value);
            childIndex++;
        }
        /* split each child*/
        if (maxGain > 0) {
            for (int i = 0; i < node.children.length; i++) {
                splitRandom(node.children[i],numAttributes,depth+1);
            }
        }
    }
    public void split(Node node,int depth){
        double entropy = 0;
        double gain = 0;
        double maxGain = -1;
        int bestAttr=0;
        /* find attribute with highest information gain*/
        for (int i = 0; i < node.attributes.length; i++) {
            gain = calculateGain(node,node.attributes[i]);
            if (gain > maxGain){
                maxGain = gain;
                bestAttr = i;
            }
        }
        List<Integer> newAttributes = new ArrayList<Integer>();
        for (int i = 0; i < node.attributes.length; i++) {
            if ( i != bestAttr)
                newAttributes.add(node.attributes[i]);
        }
        Integer [] newAttributesArr = new Integer[newAttributes.size()];
        newAttributesArr = newAttributes.toArray(newAttributesArr);
        //System.out.println(newAttributes);
        /* find attr's value range*/
        Set<String> values = new HashSet<>();
        for (int i = 0; i < node.instances.size() ; i++) {
            List<String> row = node.instances.get(i);
            //System.out.println(row.get(bestAttr));
            values.add(row.get(bestAttr));
        }
        /* generate new S for each attr's value*/
        node.children = new Node[values.size()];
        int childIndex = 0;
        for (String value: values) {

            ArrayList<List<String>> newInstances = new ArrayList<List<String>>();
            List<String> labels = new ArrayList<String>();
            for (int j = 0; j < node.instances.size() ; j++) {
                List<String> row = node.instances.get(j);
                String attributeslabel = node.labels.get(j);
                if (value.equals(row.get(bestAttr))) {
                    newInstances.add(row);
                    labels.add(attributeslabel);
                }
            }
            /*String[] labelsArr = new String[labels.size()];
            labelsArr = labels.toArray(labelsArr);*/
            //System.out.println(bestAttr);
            //System.out.println("length = "+node.attributes.length);
            //if(node.attributes.length == 0) bestAttr = 0;
            //else bestAttr = node.attributes[bestAttr];
            node.children[childIndex] = new Node(node,newInstances,labels,newAttributesArr,bestAttr,value);
            childIndex++;
        }
        /* split each child*/
        if (maxGain > 0) {
            //System.out.println(bestAttr);
            for (int i = 0; i < node.children.length; i++) {
                split(node.children[i],depth+1);
            }
        }


    }

    public double calculateGain(Node node,int attr){
        Set<String> values = new HashSet<>();
        for (int i = 0; i < node.instances.size() ; i++) {
            List<String> row = node.instances.get(i);
            values.add(row.get(attr));
        }

        double total = 0;
        for (String value: values) {
            HashMap<String, Integer> subS = new HashMap<>();
            subS.putAll(node.S);
            subS.replaceAll((k,v)->0);
            for (int i = 0; i < node.instances.size() ; i++) {
                List<String> row = node.instances.get(i);
                String label = node.labels.get(i);
                if (value.equals(row.get(attr))) {
                    subS.put(label,subS.get(label)+1);
                }
            }
            int subsLabels = 0;
            for (HashMap.Entry<String,Integer> entry : subS.entrySet()) {
                subsLabels += entry.getValue();
            }
            total += subsLabels*entropy(subS);
        }
        return entropy(node.S) - total/node.getSumLabels();
    }
    public double entropy(HashMap<String,Integer> s){
        double result = 0;
        int total = 0;
        for(HashMap.Entry<String, Integer> entry:s.entrySet()){
            total += entry.getValue();
        }
        for(HashMap.Entry<String, Integer> entry:s.entrySet()){
            double pi = ((double)entry.getValue()) / total;
            if (pi != 0)
                result += pi*Math.log(pi);
        }
        return result/Math.log(2)*-1;
    }

}