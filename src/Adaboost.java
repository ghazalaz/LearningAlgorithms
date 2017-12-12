import java.util.*;

public class Adaboost extends Classifier{
    public ArrayList<Double> weights = new ArrayList<>();
    int M;// number of weak learners
    //HashMap<Integer,Map.Entry<Classifier,Double>> clf = new HashMap<>(); // classifier, alpha
    List<HashMap.Entry<Classifier,Double>> classifiers = new ArrayList<>();
    Adaboost(int M, Classifier clf,String trainfile, ArrayList<List<String>> data, String labelIndex,List<String> labels){
        super(trainfile,data,labelIndex,labels);
        setWeights(data);
        this.M = M;
        clf.setData(data);
        clf.setTraining(training);
        clf.setTrainingLabels(labels);
        clf.setAttributes();
        //clf.setMapLabels(this.mapLabels);
        for (int i = 0; i < M; i++) {
            classifiers.add(new AbstractMap.SimpleEntry<Classifier,Double>(clf,.0));
        }
    }
    public void setWeights(ArrayList<List<String>> training){
        for (int i = 0; i < training.size(); i++) {
            weights.add(1.);
        }
    }

    public void learn(ArrayList<List<String>> training,List<String> trainingLabels){
        setWeights(training);
        for (int i = 0; i < M; i++) {
            HashMap.Entry<Classifier,Double> entry = classifiers.get(i);
            Classifier clf = entry.getKey();
            Map.Entry<ArrayList<List<String>>,List<String>> newdata = createNewDataset(training,trainingLabels);
            //clf.setData(data);
            ArrayList<List<String>> newtraining = newdata.getKey();
            List<String> newtrainingLabels = newdata.getValue();
            clf.setTraining(newtraining);
            clf.setTrainingLabels(newtrainingLabels);
            clf.setAttributes();
            clf.setMapLabels(PreProcess.getMapLabels(newtrainingLabels));
            clf.learn(newtraining,newtrainingLabels);
            double err = computeError(training,trainingLabels,clf);
            //System.out.println("Error "+err);
            double alpha = computeAlpha(err);
            //System.out.println("Alpha "+alpha);
            updateWeights(training,trainingLabels,clf,alpha);
            classifiers.set(i,new AbstractMap.SimpleEntry<>(clf,alpha));
            //classifiers.get(i).setValue(alpha);
        }
    }

    private Map.Entry<ArrayList<List<String>>,List<String>> createNewDataset(ArrayList<List<String>> training,List<String> trainingLabels){
        ArrayList<List<String>> newDataset =new ArrayList<List<String>>();
        List<String> newLabels = new ArrayList<>();
        Random r = new Random();
        while(newDataset.size() < Math.ceil(training.size()/10)){
            int index = r.nextInt(training.get(0).size());
            newDataset.add(training.get(index));
            newLabels.add(trainingLabels.get(index));
        }
        return new AbstractMap.SimpleImmutableEntry<>(newDataset,newLabels);
        //return new AbstractMap.SimpleImmutableEntry<>(training,trainingLabels);
    }

    double computeError(ArrayList<List<String>> training,List<String> trainingLabels,Classifier clf){
        double err = 0;
        double total = 0;
        for (int i = 0; i < training.size(); i++) {
            String pred = clf.classify(training.get(i));
            if(!pred.equals(trainingLabels.get(i))){
                err += weights.get(i);
                //System.out.println("Plus weight");
            }
            total += weights.get(i);

        }
        return err/total;
    }
    double computeAlpha(double err){
        return Math.log((1-err)/err);
    }

    public void updateWeights(ArrayList<List<String>> training,List<String> trainingLabels,Classifier clf,double alpha){
        for (int i = 0; i < training.size(); i++) {
            if (!clf.classify(training.get(i)).equals(trainingLabels.get(i))){
                weights.set(i,Math.exp(alpha));
            }
        }
        normalizeWeights();
    }

    private void normalizeWeights(){
        double sum=0;
        for(int i=0;i<weights.size();i++)
            sum +=weights.get(i);
        for(int i=0;i<weights.size();i++)
            weights.set(i,weights.get(i)/sum);
    }

    @Override
    List<String> classify(ArrayList<List<String>> testInstances) {
        List<String> predictions = new ArrayList<>();
        for (List<String> test: testInstances ) {
            predictions.add(classify(test));
        }
        return predictions;
    }

    @Override
    String classify(List<String> test) {
        Map<String,Double> clfMap = new HashMap<>();
        for (Map.Entry<Classifier,Double> entry:classifiers) {
            Classifier clf = entry.getKey();
            double alpha = entry.getValue();
            String pred = clf.classify(test);
            clfMap.putIfAbsent(pred,0.);
            clfMap.replace(pred,clfMap.get(pred)+alpha);
        }
        Map.Entry<String,Double> max = null;
        for (Map.Entry<String,Double> entry : clfMap.entrySet()) {
            if (max == null || max.getValue() > entry.getValue()) {
                max = entry;
            }
        }
        return max.getKey();
    }
}