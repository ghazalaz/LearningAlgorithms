import java.util.*;

public class KNN extends Classifier{
    int k;
    KNN(int k,String trainfile, ArrayList<List<String>> data, String labelIndex,List<String> labels){
        super(trainfile, data, labelIndex, labels);
        this.k = k;
    }
    @Override
    List<String> classify(ArrayList<List<String>> testInstances) {
        String label = "";
        List<String> predictions = new ArrayList<>();
        for (int j = 0; j < testInstances.size(); j++) {
            double dis=0;
            double minDis = 1000000000;
            List<String> test = testInstances.get(j);
            HashMap<Double,String> distances = new HashMap<>();
            //System.out.println("test " +j);
            for (int i = 0; i < data.size(); i++) {
                List<String> sample = data.get(i);
                dis = getDistance(test,sample);
                distances.putIfAbsent(dis,labels.get(i));
                /*if ( dis < minDis ){
                    minDis = dis;
                    label = labels.get(i);
                }*/
            }
            /*find k nearest*/
            List<String> knearest = new ArrayList<>();
            HashMap<String,Integer> votes = new HashMap<>();
            for (int l = 0; l <k ; l++) {
                //System.out.println("in loop");
                Map.Entry<Double, String> min = null;
                for (Map.Entry<Double, String> entry : distances.entrySet()) {
                    if (min == null || min.getKey() > entry.getKey()) {
                        min = entry;
                    }
                }
                knearest.add(min.getValue());
                votes.putIfAbsent(min.getValue(),0);
                votes.replace(min.getValue(), votes.get(min.getValue())+1);
                //System.out.println("adding"+min.getValue());
                distances.remove(min);
            }
            Map.Entry<String,Integer> max = null;
            for (Map.Entry<String,Integer> entry : votes.entrySet()) {
                if (max == null || max.getValue() > entry.getValue()) {
                    max = entry;
                }
            }
            //System.out.print(dis);
            //System.out.print(label+", ");
            predictions.add(max.getKey());
        }
        System.out.println(predictions);
        return predictions;
    }

    public double getDistance(List<String> row, List<String> test){
        //return manhattanDistance(row,test);
        return euqledianDistance(row,test);
    }

    public double euqledianDistance(List<String> row, List<String> test){
        double distance = 0;
        for (int i = 0; i < row.size(); i++) {
            if(row.get(i).matches("-?\\d+(\\.\\d+)?")) { // if attribute is a number
                distance += Math.pow(Double.parseDouble(row.get(i)) - Double.parseDouble(test.get(i)),2);
            }else if (row.get(i).matches("[a-zA-Z]")){
                //double val = row.get(i).charAt(0) - 'a' + 1;
                //double testVal = row.get(i).charAt(0) - 'a' + 1;
                int val = (int)row.get(i).charAt(0);
                int testVal = (int )row.get(i).charAt(0);
                //System.out.println("Val"+val);
                //System.out.println("test val"+testVal);
                distance += Math.pow(val - testVal,2);
            }
        }
        return Math.sqrt(distance/row.size());
    }
    public double manhattanDistance(List<String> row, List<String> test){
        double distance = 0;
        for (int i = 0; i < row.size(); i++) {
            if(row.get(i).matches("-?\\d+(\\.\\d+)?")) { // if attribute is a number
                distance += Double.parseDouble(row.get(i)) - Double.parseDouble(test.get(i));
            }else if (row.get(i).matches("[a-zA-Z]")){
                //double val = row.get(i).charAt(0) - 'a' + 1;
                //double testVal = row.get(i).charAt(0) - 'a' + 1;
                int val = (int)row.get(i).charAt(0);
                int testVal = (int )row.get(i).charAt(0);
                //System.out.println("Val"+val);
                //System.out.println("test val"+testVal);
                distance += val - testVal;
            }
        }
        return Math.abs(distance/(double)row.size());
    }

    @Override
    String classify(List<String> test) {
        return null;
    }

    @Override
    void learn(ArrayList<List<String>> training, List<String> trainingLabels) {

    }
}
