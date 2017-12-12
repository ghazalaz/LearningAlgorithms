import java.util.*;
import java.util.stream.IntStream;
public class RandomForest extends Classifier {
    ArrayList<ID3> DTrees = new ArrayList<ID3>();
    ArrayList<Set<Integer>> DTreesAttributes = new ArrayList<>();
    int numTrees = 0;
    int numBranches = 0;
    public RandomForest(int numTrees, double numBranches,String trainfile, ArrayList<List<String>> data, String labelIndex,List<String> labels){
        super(trainfile, data, labelIndex, labels);
        this.numTrees = numTrees;
        this.numBranches = (int) (numBranches * data.get(0).size());
    }
    public void learn(ArrayList<List<String>> training,List<String> trainingLabels){
        //System.out.println("Learning RF");
        for (int i = 0; i < numTrees; i++) {
            //System.out.println("Learning tree "+ i);
            ID3 tree = new ID3(training,trainingLabels);
            tree.learn(training,trainingLabels,(int)Math.sqrt(attributes.length));
            DTrees.add(tree);
            //DTreesAttributes.add(randAttr);
        }
    }

    @Override
    String classify(List<String> test) {
        return null;
    }

    @Override
    List<String> classify(ArrayList<List<String>> test){
        ArrayList<List<String>> predictions = new ArrayList<List<String>>();
        //System.out.println("Classify forest");
        List<String> finalPredictions = new ArrayList<>();
        for (int i=0; i<DTrees.size(); i++ ) {
            ID3 tree = DTrees.get(i);
            List<String> tmpPredictions ;
            tmpPredictions = tree.classify(test);
            predictions.add(tmpPredictions);
        }
        for (int i = 0; i < test.size(); i++) {
            List<String> testPredictions = new ArrayList<>();
            for (List<String> tmpPred : predictions) {
                testPredictions.add(tmpPred.get(i));
            }
            Set<String> uniqeLabels = new HashSet<>(testPredictions);
            HashMap<String,Integer> votes = new HashMap<>();
            for (int j = 0; j < labels.size(); j++) {
                votes.putIfAbsent(labels.get(j),0);
            }
            votes.put("0",0);
            for (String label: uniqeLabels) {
                if(testPredictions.contains(label)){
                    votes.replace(label,votes.get(label)+1);
                }
            }
            /* max vote*/
            HashMap.Entry<String, Integer> maxEntry = null;
            for (HashMap.Entry<String, Integer> entry : votes.entrySet())
            {
                //System.out.println(entry.getKey()+"/"+entry.getValue());
                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                {
                    maxEntry = entry;
                }
            }
            finalPredictions.add(maxEntry.getKey());
        }
        return finalPredictions;
    }
}
