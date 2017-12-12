import org.w3c.dom.ls.LSException;

import java.util.*;

public class PreProcess {
    ArrayList<List<String>> data;
    Map<String, String> mapValues = new HashMap<String, String>();
    PreProcess(){
        /*buying , maint, safety*/
        mapValues.put("vhigh","4");
        mapValues.put("high","3");
        mapValues.put("med","2");
        mapValues.put("low","1");
        /*doors*/
        mapValues.put("5more","5");
        /*preson*/
        mapValues.put("more","3");
        /*lug_boot: small, med, big*/
        mapValues.put("small","1");
        mapValues.put("med","2");
        mapValues.put("big","3");
    }
    static HashMap<String,Integer> getMapLabels(List<String> labels){
        Set<String> setLabels = new HashSet(labels);
        HashMap<String,Integer> map = new HashMap<>();
        for (String label :setLabels) {
            map.put(label, 0);
            for (int i = 0; i < labels.size() ; i++) {
                if (label.equals(labels.get(i))) {
                    map.replace(label, map.get(label) + 1);
                }
            }
        }

        return map;
    }
    static ArrayList<List<String>> replaceMissingValues(ArrayList<List<String>> data, List<String> labels,Integer[] attributes){
        //System.out.println("replacing missing values");
        for (int i = 0; i < data.size(); i++) {
            List<String > row = data.get(i);
            for(int j=0;j<attributes.length;j++){
                    if(row.get(j).equalsIgnoreCase("?")){
                        row.set(j, findByLabel(j, labels.get(i),data,labels));
                    }
            }
        }
        return data;
    }

    static String findByLabel(int f, String label , ArrayList<List<String>> data,List<String> labels) {
        HashMap<String, Integer> sumAttr = new HashMap<String, Integer> ();
        for(int i=0;i<data.size();i++){
            if(labels.get(i).equalsIgnoreCase(label) == true && data.get(i).get(f).equalsIgnoreCase("?") == false ) {
                String key = data.get(i).get(f);
                sumAttr.putIfAbsent(key, 0);
                sumAttr.replace(key, sumAttr.get(key) + 1);
            }
        }
        String res="";
        Integer max= -10;
        for(String value:sumAttr.keySet()){
            if(sumAttr.get(value) > max ){
                max = sumAttr.get(value);
                res = value;
            }
        }
        return res;
    }

    static ArrayList<List<String>> desctretize(ArrayList<List<String>> data, double step){
        for (List<String> row:data ) {
            for (int i = 1; i < row.size(); i++) {
                double val = Math.round(Double.parseDouble(row.get(i))*10+step);
                row.set(i,Double.toString(val/10));
            }

        }
        return data;
    }

    static ArrayList<List<String>> replaceCategoricalValues(ArrayList<List<String>> data, List<String> labels,Integer[] attributes){
        Map<String, String> mapValues = new HashMap<String, String>();
        /*buying , maint, safety*/
        mapValues.put("vhigh","4");
        mapValues.put("high","3");
        mapValues.put("med","2");
        mapValues.put("low","1");
        /*doors*/
        mapValues.put("5more","5");
        /*preson*/
        mapValues.put("more","3");
        /*lug_boot: small, med, big*/
        mapValues.put("small","1");
        mapValues.put("med","2");
        mapValues.put("big","3");
        for (int i = 0; i < data.size(); i++) {
            List<String > row = data.get(i);
            for(int j=0;j<attributes.length;j++){
                if(mapValues.containsKey(row.get(j))){
                    //System.out.println("replacing "+row.get(j))+" with"+mapValues.get(row.get(j)));
                    row.set(j,mapValues.get(row.get(j)));
                }
            }
        }
        return data;
    }

    static ArrayList<List<String>> removeIDColumn(ArrayList<List<String>> data){
        //System.out.println(data.get(0).size());
        for (int i = 0; i < data.size(); i++) {
            data.set(i,data.get(i).subList(1,data.get(i).size()));
        }
        //System.out.println(data.get(0).size());
        return data;
    }
}
