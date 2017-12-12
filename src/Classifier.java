
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.*;

public abstract class Classifier {
    public ArrayList<List<String>> data;
    public ArrayList<List<String>> training;
    public List<String> labels;
    public List<String> trainingLabels;
    HashMap<String, Integer> mapLabels = new HashMap<String,Integer>();
    public Integer[] attributes;
    public Classifier(){}
    public Classifier(String trainfile, ArrayList<List<String>> data, String labelIndex,List<String> labels){
        this.load( trainfile, data, labelIndex, labels);
        //this.data = data;
        this.labels = labels;
        attributes = new Integer[data.get(0).size()];
        for (int j = 0; j < attributes.length; j++) {
            attributes[j] = j;
        }
        this.data = PreProcess.replaceMissingValues(data,labels,attributes);
        this.data = PreProcess.replaceCategoricalValues(data,labels,attributes);
        //this.mapLabels = PreProcess.getMapLabels(labels);

        this.setAttributes();
    }

    public void setData(ArrayList<List<String>> data){
        this.data = data;
    }
    public void setTraining(ArrayList<List<String>> training){
        this.training = training;
    }
    public void setTrainingLabels(List<String> trainingLabels){
        this.trainingLabels = trainingLabels;
    }
    public void setAttributes(){
        attributes = new Integer[data.get(0).size()];
        for (int j = 0; j < attributes.length; j++) {
            attributes[j] = j;
        }
        this.attributes = attributes;
    }
    public void setMapLabels(HashMap<String, Integer> mapLabels) {
        this.mapLabels = mapLabels;
    }
    public Set<Integer> splitData(ArrayList<List<String>> data, int proportion, ArrayList<List<String>>train,ArrayList<List<String>> test ,List<String> traininglabels, List<String> testlabels){
        Random r = new Random();
        Set<Integer> uniqueNumbers = new HashSet<>();
        int size = 0;
        if (proportion < 1) {
            size = data.size() * proportion;
        }else if (proportion > 1){
            size = data.size() * proportion / 100;
        }
        while (uniqueNumbers.size() < size){
            uniqueNumbers.add(r.nextInt(data.size()));
        }
        for (int i = 0; i < data.size(); i++) {
            if(uniqueNumbers.contains(i)){
                test.add(data.get(i));
                testlabels.add(labels.get(i));
            }else{
                train.add(data.get(i));
                traininglabels.add(labels.get(i));
            }
        }
        return uniqueNumbers;

    }

    public double computeAccuracy(List<String> predictions,
                                  ArrayList<List<String>> testInstances,
                                  List<String> labels) {
        if (predictions.size() != testInstances.size()) {
            System.out.println("size missmatch");
            return 0;
        } else {
            int right = 0, wrong = 0;
            for (int i = 0; i < predictions.size(); i++) {
                if (predictions.get(i) == null) {
                    wrong++;
                } else if (predictions.get(i).equals(labels.get(i))) {
                    right++;
                } else {
                    wrong++;
                }
            }
            //System.out.println(right);
            //System.out.println(wrong);
            return right * 1.0 / (right + wrong);
        }
    }
    abstract String classify(List<String> test);
    abstract List<String> classify(ArrayList<List<String>> test);
    abstract void learn(ArrayList<List<String>> training,List<String> trainingLabels);
    public HashMap.Entry<Double,Double> crossValidate(int fold, int experiment){
        double accruacy = 0;
        double sumAccuracy = 0;
        double standardDeviation = 0;
        List<Double> accuracies = new ArrayList<>();
        for (int e = 0; e < experiment; e++) {
            ArrayList<List<String>> train = new ArrayList<List<String>>();
            ArrayList<List<String>> test = new ArrayList<List<String>>();
            List<String> testlabels = new ArrayList<String>();
            List<String> traininglabels = new ArrayList<String>();
            splitData(data,100/fold,train,test,traininglabels,testlabels);
            this.training = train;
            this.trainingLabels = traininglabels;
            //System.out.println(testlabels);
            learn(train,traininglabels);
            List<String> predictions = classify(test);
            //System.out.println(predictions);
            accruacy = computeAccuracy(predictions,test,testlabels);
            sumAccuracy += accruacy;
            accuracies.add(accruacy);
            System.out.println("[Experiment "+ e+ " , Accuracy : "+accruacy+" ]");

        }
        double avg = sumAccuracy/experiment;
        for (Double acc:accuracies ) {
            standardDeviation += Math.pow((avg-acc),2);
        }

        return new AbstractMap.SimpleImmutableEntry<>(avg,Math.sqrt(standardDeviation/experiment));
    }

    public void load(String trainfile, ArrayList<List<String>> data, String labelIndex,List<String> labels) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(trainfile));
            String line;
            line = br.readLine();
            String[] sample = line.split(",|\\s+");
            int labelIndx = 0;
            if(labelIndex == "LAST")
                labelIndx = sample.length-1;
            br.close();
            br = new BufferedReader(new FileReader(trainfile));
            while ((line = br.readLine()) != null) {
                List<String> attributes = new ArrayList<String>();
                sample = line.split(",|\\s+");
                for (int i = 0; i < sample.length; i++) {
                    if (i != labelIndx ) {
                        attributes.add(sample[i]);
                    }else{
                        labels.add(sample[i]);
                    }
                }
                data.add(attributes);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        ArrayList<List<String>> train = new ArrayList<List<String>>();
        ArrayList<List<String>> test = new ArrayList<List<String>>();
        List<String> labels = new ArrayList<String>();
        List<String> testLabels = new ArrayList<String>();

        //Classifier clf = new Classifier(data, labels);

    }

}
