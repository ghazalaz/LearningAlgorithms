//import jdk.internal.util.xml.impl.Pair;

import java.util.*;

public class NaiveBayes extends Classifier {
    HashMap<Map.Entry<Map.Entry<Integer,String>,String>,Double> P_attrVal_label= new HashMap<>();
    HashMap<String,Double> P_labels = new HashMap<>();
    double P_estimate = 0;
    HashMap<Map.Entry<Integer,String>,Integer> sum_attr = new HashMap<>();
    public NaiveBayes(){}
    public NaiveBayes(String trainfile, ArrayList<List<String>> data, String labelIndex, List<String> labels) {
        super(trainfile, data, labelIndex, labels);
        this.mapLabels = PreProcess.getMapLabels(labels);
        this.setTraining(data);
        this.setTrainingLabels(labels);
        this.setAttributes();
    }

    public void learn(ArrayList<List<String>> training,List<String> trainingLabels) {
        //System.out.println("Learning");
        HashMap<Map.Entry<Map.Entry<Integer,String>,String>,Integer> sum_attrVal_label= new HashMap<>();
        //Set<String> setLabels = new HashSet(Arrays.asList(labels));
        int totallabels = 0;
        HashMap<String,Integer> sum_labels = new HashMap<>();
        attributes = new Integer[training.get(0).size()];
        for (int j = 0; j < attributes.length; j++) {
            attributes[j] = j;
        }
        for (int j = 0; j < training.size(); j++) {
            for (int i = 0; i < attributes.length; i++) {
                List<String> row = training.get(j);
                Map.Entry<Integer,String> xi = new AbstractMap.SimpleImmutableEntry<>(i,row.get(i));
                Map.Entry<Map.Entry<Integer,String>,String> xi_label = new AbstractMap.SimpleImmutableEntry<>(xi,trainingLabels.get(j));

                sum_attrVal_label.putIfAbsent(xi_label,0);
                sum_attrVal_label.replace(xi_label,sum_attrVal_label.get(xi_label)+1);

                sum_attr.putIfAbsent(xi,0);
                sum_attr.replace(xi,sum_attr.get(xi)+1);
            }
            sum_labels.putIfAbsent(trainingLabels.get(j),0);
            sum_labels.replace(trainingLabels.get(j),sum_labels.get(trainingLabels.get(j))+1);
        }
        /*for (Map.Entry<String,Integer> entry: sum_labels.entrySet()) {
            P_labels.putIfAbsent(entry.getKey(),0.0);
            P_labels.replace(entry.getKey(),(double)entry.getValue()/trainingLabels.size());
        }*/
        //System.out.println(mapLabels);
        //System.out.println(trainingLabels);
        for(Map.Entry<String,Integer> entry: mapLabels.entrySet()){
            P_labels.putIfAbsent(entry.getKey(),0.0);
            if(sum_labels.containsKey(entry.getKey())) {
                int val = sum_labels.get(entry.getKey());
                P_labels.replace(entry.getKey(), (double) val / trainingLabels.size());
            }
        }
        //System.out.println(P_labels);
        for (Map.Entry<Map.Entry<Map.Entry<Integer,String>,String>,Integer> entry:sum_attrVal_label.entrySet() ) {
            P_attrVal_label.putIfAbsent(entry.getKey(),0.0);
            int sum = sum_labels.get(entry.getKey().getValue());
            P_attrVal_label.replace(entry.getKey(),(double)entry.getValue()/sum);
        }
        /*int sum = 0;
        for (Map.Entry<String,Integer> entry: sum_labels.entrySet()) {
            sum += entry.getValue();
        }
        P_estimate =(double) 1/sum;*/
        //System.out.println(P_labels);
        //System.out.println(P_attrVal_label);

    }

    @Override
    List<String> classify(ArrayList<List<String>> testInstances) {
        //System.out.println("Classifying");
        List<String> predictions = new ArrayList<>();
        Set<String> setLabels = new HashSet<>(labels);
        //System.out.println("classify");
        for (List<String> row:testInstances ) {
            //System.out.println(row);
            double maxPred = -1;
            String labelPred = "";
            for (String label :setLabels) {
                double multiplication = 1;
                for (int j = 0; j < attributes.length; j++) {
                    Map.Entry<Integer,String> xi = new AbstractMap.SimpleImmutableEntry<>(j,row.get(j));
                    Map.Entry<Map.Entry<Integer,String>,String> att_val_labels = new AbstractMap.SimpleImmutableEntry<>(xi,label);
                    if (P_attrVal_label.containsKey(att_val_labels)) {
                        multiplication *= P_attrVal_label.get(att_val_labels);
                    }
                    else{
                        //System.out.println("estimate [attr,val,label] ["+j+","+row.get(j)+","+labels.get(i)+"]");
                        //multiplication *= P_estimate;
                        //multiplication *= (double)(1/(P_labels.get(label)+1));
                        //System.out.println((double) (1/(sum_attr.get(xi)+mapLabels.get(label))));
                        //System.out.println(sum_attr.get(xi));
                        int sumAttr = 0;
                        if (sum_attr.get(xi) != null){
                            sumAttr = sum_attr.get(xi);
                        }
                        multiplication *= (double) (1/(sumAttr+mapLabels.get(label)));
                    }
                }
                double pred = multiplication*P_labels.get(label);
                if (pred > maxPred){
                    maxPred = pred;
                    labelPred = label;
                }
            }
            predictions.add(labelPred);

        }
        return predictions;
    }

    @Override
    String classify(List<String> test) {
        double maxPred = -1;
        String labelPred = "";
        Set<String> setLabels = new HashSet<>(trainingLabels);
        for (String label :setLabels) {
            double multiplication = 1;
            for (int j = 0; j < attributes.length; j++) {
                Map.Entry<Integer,String> xi = new AbstractMap.SimpleImmutableEntry<>(j,test.get(j));
                Map.Entry<Map.Entry<Integer,String>,String> att_val_labels = new AbstractMap.SimpleImmutableEntry<>(xi,label);
                if (P_attrVal_label.containsKey(att_val_labels)) {
                    multiplication *= P_attrVal_label.get(att_val_labels);
                }
                else{
                    int sumAttr = 0;
                    if (sum_attr.get(xi) != null){
                        sumAttr = sum_attr.get(xi);
                    }
                    multiplication *= (double) (1/(sumAttr+mapLabels.get(label)));
                }
            }
            double pred = multiplication*P_labels.get(label);
            if (pred > maxPred){
                maxPred = pred;
                labelPred = label;
            }
        }
        return labelPred;
    }
}
