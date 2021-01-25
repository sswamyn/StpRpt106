package org.swamy;


// TODO
// Preprocessor – traverse a folder and pick all files with file name pattern of Rpt 106
//	STLMTP_E_F_0106_3340392610_14_JAN_2021.txt
//	STaaaa_E_F_0106_nnnnnnnnnn_nn_aaa_nnnn.txt [to be confirmed]

// DONE
/* Core processing – given a valid Rpt106, parse the file.
 *                   In each Record Type “1”, the ninth field should be converted
 *                   from “AA” to “AA ”.
 *                    create a new file that is identical the original Rpt106 file, expect for the
 *                     above described change.
 */

// TODO
/* Postprocessor - diff of original file and the new file to ensure the only difference
 *                   is in record type 1, and on the ninth element.
 *                 Ensure the first two characters in the ninth element of New file
 *                    is identical to the two characters in the ninth element of the original file.
 */


//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.StringTokenizer;

public class rpt106Token9 {
    //private static final Logger logger = LogManager.getLogger("rpt106Token9");
    private String fileName = "C:\\home\\STP\\LBP Testing\\LBP Testing\\Settlement-14Jan\\Settlement Report\\STLMTP_E_F_0106_3340392610_14_JAN_2021.txt";
    public static void main(String... args) {
        //logger.debug("Starting .. ");
        long time = System.nanoTime();
        // begin : real useful code
        rpt106Token9 runner = new rpt106Token9();
        runner.processFile();
        // end : real useful code! :)

        for (String s: args) {
            //logger.info("In param: ", s);
            System.out.println("Input arg:" + s);
        }
        time = System.nanoTime() - time;
        System.out.printf("time = %dms%n", (time / 1_000_000));
        //logger.debug("Done .. " + "time = %dms%n" + (time / 1_000_000));

    }


    private  void processFile() {
        try {
            File rpt106File = new File(fileName);
            //Read the file content into a String object
            String content = Files.readString(rpt106File.toPath(), Charset.defaultCharset());

            // Pass the String object to rpt106Token9.processContent() method to get the content updated
            String updatedContent = this.processContent(content);

            // Write the updated content as a new File
            this.writeUpdatedFile(updatedContent);

        } catch(IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }

    private  String processContent(String fileContent) {

        StringBuilder updatedFile = new StringBuilder();
        String[]lines = fileContent.split(System.getProperty("line.separator"));
        int lineCnt = 0;
        for (String eachLine : lines){
            lineCnt++;
            StringBuilder updatedLine = new StringBuilder();
            StringTokenizer st = new StringTokenizer(eachLine, ",");
            //System.out.println("Line # :" + lineCnt + "\t Field count: " + st.countTokens());
            int fieldCnt = 0;
            Boolean isRecordType1 = false; // reset Record type for each line
            //looping through each field
            while (st.hasMoreTokens()) {
                fieldCnt++;
                String tmpField = st.nextToken();
                //System.out.println("Filed # " + fieldCnt + "; Field value: " + tmpField);

                if (fieldCnt == 1) { //Finding the type of record/line type 0, 1 or 9
                    //System.out.println("** \t First field; \t It is:" + tmpField);
                    String stripedTmpField = tmpField.replace('\"', ' ');
                    //System.out.println("** \t \t the replaced version is:" + stripedTmpField);
                    if (isInteger(stripedTmpField)) { // This is first field in the line & is an Integer
                        //System.out.println("*** \t\t First field is an integer; \t It is:" + stripedTmpField);
                        if (Integer.parseInt(stripedTmpField.strip()) == 1) {  // Yahoo record type 1!
                            isRecordType1 = true;
                            //logger.info("found a record type #1");
                        }
                    }

                // logger.info("found a record type ");
                } //Finding the type of record/line type 0, 1 or 9

                if (isRecordType1) {
                    //System.out.println("\t ******* Filed Count:"+fieldCnt);
                    if (fieldCnt == 9) {
                        StringBuilder field9 = new StringBuilder(tmpField);
                        field9.insert(3,' ');
                        String changedField9 = field9.toString();
                        //System.out.println("\t ********** \t Field # 9: " + tmpField + "**** \t New value: " + changedField9 );
                        updatedLine.append(changedField9).append(",");
                        //System.out.println("\t ********** \t Field # 9: " + tmpField + "**** \t New value: " + changedField9 );
                    }
                    else {
                        //System.out.println("\t *******");
                        updatedLine.append(tmpField).append(",");
                    }
                }
                else {
                    updatedLine.append(tmpField).append(",");
                }
            } // end of while loop parsing each field
            int lineLength = updatedLine.length();
            updatedLine.deleteCharAt(lineLength - 1);
            updatedLine.append("\n");
            updatedFile.append(updatedLine);
           // System.out.println("\t\t **** Line # :" + lineCnt );

        }
        //System.out.println(fileContent);
        return updatedFile.toString();
    }

    private  void writeUpdatedFile(String newFile) {
        //System.out.println(newFile);
        try {
            String newFileName = fileName + ".out.txt";
            File newFileNameFileObj = new File(newFileName);
            //if (newFileNameFileObj.isFile()) {
            //    newFileNameFileObj.delete();
            //}
            //newFileNameFileObj.createNewFile();

            if (!newFileNameFileObj.isFile()) {
                newFileNameFileObj.createNewFile();
            }
            Files.writeString(newFileNameFileObj.toPath(), newFile, Charset.defaultCharset(), StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file", e);
        }
    }


    public static boolean isInteger(String str) {
        /*
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
        */
        try {
            //System.out.println("in isInteger; Input is:" + str);
            Integer.parseInt( str.strip() );
            //System.out.println("in isInteger; Retunrnig TRUE");
            return true;
        }
        catch( Exception e ) {
            //System.out.println("in isInteger; Returning FALSE");
            return false;
        }
    }
}
