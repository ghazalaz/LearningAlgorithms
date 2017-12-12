import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class main {
    public static void main(String[] args) {
        String [][] datasets = {{"data/car.data","LAST"},{"data/ecoli.data","LAST"},
                {"data/breast-cancer-wisconsin.data","LAST"},{"data/mushroom.data","FIRST"},{"data/letter-recognition.data","FIRST"}};
        HashMap.Entry<Double,Double> acc_delta;
        Classifier clf ;
        for (int i = 0; i <datasets.length ; i++) {
            System.out.println(datasets[i][0]);
            String labelIndex = datasets[i][1];
            ArrayList<List<String>> data = new ArrayList<List<String>>();
            List<String> labels = new ArrayList<String>();
            /* ID3 */
            System.out.println("ID3");
            clf = new ID3(datasets[i][0],data,labelIndex,labels);
            if (datasets[i][0].equalsIgnoreCase("data/ecoli.data")){
                clf.setData(PreProcess.desctretize(data,0.05));
                clf.setAttributes();
            }
            if(datasets[i][0].equalsIgnoreCase("data/ecoli.data") || datasets[i][0].equalsIgnoreCase("data/breast-cancer-wisconsin.data")){
                clf.setData(PreProcess.removeIDColumn(data));
                clf.setAttributes();
            }
            acc_delta = clf.crossValidate(5,10);
            System.out.println(acc_delta);
            /*RANDOM FOREST */
            System.out.println("Random Forest");
            clf = new RandomForest(10,0.5,datasets[i][0],data,labelIndex,labels);
            if (datasets[i][0].equalsIgnoreCase("data/ecoli.data")){
                clf.setData(PreProcess.desctretize(data,0.05));
                clf.setAttributes();
            }
            if(datasets[i][0].equalsIgnoreCase("data/ecoli.data") || datasets[i][0].equalsIgnoreCase("data/breast-cancer-wisconsin.data")){
                clf.setData(PreProcess.removeIDColumn(data));
                clf.setAttributes();
            }
            acc_delta = clf.crossValidate(5,10);
            System.out.println(acc_delta);
            /*Naive Bayes*/
            System.out.println("Naive Bayes");
            clf = new NaiveBayes(datasets[i][0],data,labelIndex,labels);
            System.out.println("NB");
            if (datasets[i][0].equalsIgnoreCase("data/ecoli.data")){
                clf.setData(PreProcess.desctretize(data,0.05));
                clf.setAttributes();
            }
            if(datasets[i][0].equalsIgnoreCase("data/ecoli.data") || datasets[i][0].equalsIgnoreCase("data/breast-cancer-wisconsin.data")){
                clf.setData(PreProcess.removeIDColumn(data));
                clf.setAttributes();
            }
            acc_delta = clf.crossValidate(5,10);
            System.out.println(acc_delta);

            /* ADABOOST */
            Classifier wl = new ID3(datasets[i][0],data,labelIndex,labels);
            System.out.println("AdaBoost ID3");
            clf = new Adaboost(1000,wl,datasets[i][0],data,labelIndex,labels);
            if (datasets[i][0].equalsIgnoreCase("data/ecoli.data")){
                clf.setData(PreProcess.desctretize(data,0.05));
                clf.setAttributes();
            }
            if(datasets[i][0].equalsIgnoreCase("data/ecoli.data") || datasets[i][0].equalsIgnoreCase("data/breast-cancer-wisconsin.data")){
                clf.setData(PreProcess.removeIDColumn(data));
                clf.setAttributes();
            }
            acc_delta = clf.crossValidate(5,10);
            System.out.println(acc_delta);
            System.out.println("AdaBoost NB");
            wl = new NaiveBayes(datasets[i][0],data,labelIndex,labels);
            clf = new Adaboost(1000,wl,datasets[i][0],data,labelIndex,labels);
            if (datasets[i][0].equalsIgnoreCase("data/ecoli.data")){
                clf.setData(PreProcess.desctretize(data,0.05));
                clf.setAttributes();
            }
            if(datasets[i][0].equalsIgnoreCase("data/ecoli.data") || datasets[i][0].equalsIgnoreCase("data/breast-cancer-wisconsin.data")){
                clf.setData(PreProcess.removeIDColumn(data));
                clf.setAttributes();
            }
            acc_delta = clf.crossValidate(5,10);
            System.out.println(acc_delta);
            /* KNN */
            System.out.println("KNN");
            clf = new KNN(10,datasets[i][0],data,labelIndex,labels);
            if (datasets[i][0].equalsIgnoreCase("data/ecoli.data")){
                clf.setData(PreProcess.desctretize(data,0.05));
                clf.setAttributes();
            }
            if(datasets[i][0].equalsIgnoreCase("data/ecoli.data") || datasets[i][0].equalsIgnoreCase("data/breast-cancer-wisconsin.data")){
                clf.setData(PreProcess.removeIDColumn(data));
                clf.setAttributes();
            }
            acc_delta = clf.crossValidate(5,10);
            System.out.println(acc_delta);

        }


    }
}
