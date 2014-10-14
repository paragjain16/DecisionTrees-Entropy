import java.io.*;
import java.util.*;

/**
 * Created by Parag on 06-10-2014.
 */

public class EntropyCalc_pjain11 {

    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("Usage: java EntropyCalc_pjain11 [ input file name ] [ output file name ]");
            System.exit(1);
        }
        EntropyCalc_pjain11 ec = new EntropyCalc_pjain11();
        ec.initializeAndCalculateEntropy(args);
    }

    public void initializeAndCalculateEntropy(String[] args){
        inputFile = new File(args[0]);
        outputFile = new File(args[1]);
        columns = new ArrayList<ArrayList<String>>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(inputFile));
            boolean initial = true;
            String line;
            //Read file and store data in arraylist
            while((line = br.readLine()) != null){
                String[] values = line.split(",");
                if(initial){
                    initial = false;
                    for(String value: values){
                        ArrayList<String> column = new ArrayList<String>();
                        column.add(value);
                        columns.add(column);
                    }
                }else{
                    int i = 0;
                    for(String value: values){
                        columns.get(i).add(value);
                        i++;
                    }
                }
            }
            writeOutputToFile(columns);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double calculateEntropy(ArrayList<String> column){
        double entropy = 0.0;
        int totalEntries = column.size();
        HashMap<String, Integer> distinctEntries = new HashMap<String, Integer>();
        for(String entry: column){
            if(!distinctEntries.containsKey(entry))
                distinctEntries.put(entry, 1);
            else
                distinctEntries.put(entry, distinctEntries.get(entry)+1);
        }
        Iterator<Map.Entry<String, Integer>> it = distinctEntries.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, Integer> entry = it.next();
            System.out.println(entry.getKey() + " "+ entry.getValue());
            double probability = (double)entry.getValue()/totalEntries;
            entropy += (probability*(Math.log(probability)/Math.log(2))*(-1.0));
        }
        System.out.println(entropy);
        return entropy;
    }

    public double informationGain(ArrayList<String> testColumn, ArrayList<String> labelColumn){
        double infoGain =  0.0;
        double expectedInfo = 0.0;
        int i = 0;
        double initialInfo = calculateEntropy(labelColumn);
        int totalEntries = testColumn.size();
        HashMap<String, ArrayList<String>> distinctEntries = new HashMap<String, ArrayList<String>>();
        for(String entry: testColumn){
            if(!distinctEntries.containsKey(entry)) {
                ArrayList<String> al= new ArrayList<String>();
                al.add(labelColumn.get(i));
                distinctEntries.put(entry, al);
            }else {
                distinctEntries.get(entry).add(labelColumn.get(i));
            }
            i++;
        }

        Iterator<Map.Entry<String, ArrayList<String>>> it = distinctEntries.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, ArrayList<String>> entry = it.next();
            ArrayList<String> col = entry.getValue();
            double entropy = calculateEntropy(col);
            double probability = (double)col.size()/totalEntries;
            expectedInfo += probability*entropy;
        }
        infoGain = initialInfo - expectedInfo;
        return infoGain;
    }

    public void writeOutputToFile(ArrayList<ArrayList<String>> columns){
        try {
            FileWriter fw = new FileWriter(outputFile);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            for(ArrayList<String> column: columns){
                pw.println(calculateEntropy(column));
            }
            for(int i = 0; i < columns.size()-1; i++){
                pw.println(informationGain(columns.get(i), columns.get(columns.size()-1)));
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<ArrayList<String>> columns;
    private File inputFile;
    private File outputFile;
}