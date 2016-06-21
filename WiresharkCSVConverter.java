import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by selinf on 21.06.2016.
 */
public class WiresharkCSVConverter {

    public static void main(String [] args){
        System.out.println("Starting read/write seq");
        run();
        System.out.println("Read/write ended");

    }




    public static void run() {
        System.out.println("Enter Wireshark Comma Seperated Value File Location");
        String csvFile = new Scanner(System.in).nextLine();
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            ArrayList<String>TimeSpeed = new ArrayList<java.lang.String>(); //map for time vs speed
            ArrayList<String> ACKData = new ArrayList<String>(); // ack or data keeper
            br = new BufferedReader(new FileReader(csvFile)); // reads csv-file
            BigDecimal timeKeeperForSpeed = null; //for the calculation of time passed keeps previous time
            BigDecimal initialTime = null; //beginning time
            boolean firstParse = true;
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] csv = line.replaceAll("\"", "").split(cvsSplitBy);


                if(firstParse){
                   // System.out.print(csv[1].replace("\"",""));
                    initialTime = new BigDecimal(csv[1]);
                    timeKeeperForSpeed = initialTime;
                    firstParse = false;
                }
                BigDecimal tempTime = new BigDecimal(csv[1]); //keeps current time
                BigDecimal tempLen = new BigDecimal(csv[5]); //keeps current length
                BigDecimal sub = tempTime.subtract(timeKeeperForSpeed);
                BigDecimal currentTime = tempTime.subtract(initialTime);
                timeKeeperForSpeed = tempTime;
                //System.out.println(currentTime);
                if(sub.compareTo(BigDecimal.ZERO) == -1 || sub.compareTo(BigDecimal.ZERO) == 0 )
                    TimeSpeed.add(currentTime.toString() +" " +tempLen.divide(new BigDecimal("0.1E5"),2,RoundingMode.HALF_EVEN));
                else{
                    BigDecimal res = (tempLen.divide(sub, 2, RoundingMode.HALF_EVEN)).divide(new BigDecimal("0.1E5"),2,RoundingMode.HALF_EVEN);
                    TimeSpeed.add(currentTime.toString() +" " + res.toString());
                }
                if(csv[6].toLowerCase().contains("udt type: data")){
                    ACKData.add("1");
                    //this if is here because speed is only for the data not ack transaction.

                }else if(csv[6].toLowerCase().contains("udt type: ack") || csv[6].toLowerCase().contains("udt type: ack2"))
                    ACKData.add("0");

            }
            //writing to file via iterator.
            PrintWriter writerForACKData = new PrintWriter("files/ack-data");
            PrintWriter writerForTimeSpeed = new PrintWriter("files/speed");

            Iterator iteratorACKData = ACKData.iterator();
            Iterator iteratorTimeSpeed = TimeSpeed.iterator();


            while(iteratorACKData.hasNext()){
                String temp = (String)iteratorACKData.next();
                writerForACKData.println(temp);
            }
            while(iteratorTimeSpeed.hasNext()){
                String temp = (String)iteratorTimeSpeed.next();
                writerForTimeSpeed.println(temp);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}