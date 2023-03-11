import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.StringTokenizer;
import java.lang.Math;

/**Author: Matthew Currie, Jefferson Adjetey
 * Date: 5/23/2021
 *  * CS10 Spring 2021
 * Read through files and establish annotated training data for markov model
 */

public class markovTraining {

    // method to create observations given a sentences file and a tags file
    public static Map<String, Map<String, Double>> buildObservations(String filename_sentences, String filename_tags) {

        Map<String, Map<String, Double>> POSobservations = new HashMap<String, Map<String, Double>>();
        BufferedReader sentencesInput;
        BufferedReader tagsInput;
        // initialize ADT and file readers

        try {
            tagsInput = new BufferedReader(new FileReader(filename_tags));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open one of the files.\n" + e.getMessage());
            return null;
        }

        try {
            sentencesInput = new BufferedReader(new FileReader(filename_sentences));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open one of the files.\n" + e.getMessage());
            return null;
        }
        // open files for reading while accounting for errors

        try {
            // read each line in file
            String tag_line = tagsInput.readLine();
            ArrayList tagArray = new ArrayList();

            while (tag_line != null) { // read until last line i nfile
                String[] POS = tag_line.split("\\s+");
                for (String tag : POS) { // add each tag to an indexed arrayList

                    tagArray.add(tag);
                }
                tag_line = tagsInput.readLine();

            }

            for (int i = 0; i < tagArray.size(); i++) {

                Map<String, Double> innerMap = new HashMap<>();
                POSobservations.put((String) tagArray.get(i), innerMap); // add tags to map as keys
            }


            String sentence_line = sentencesInput.readLine();
            ArrayList<String> wordArray = new ArrayList();

            while (sentence_line != null) {
                String[] words = sentence_line.split("\\s+");
                for (String word : words) { // add each word to an indexed ArrayList
                    word = word.toLowerCase();

                    wordArray.add(word);
                }
                sentence_line = sentencesInput.readLine();
            }


            // Nested for loop to fill each POS's map with word keys

            for (String POS: POSobservations.keySet()){

                for (String word: wordArray ){

                    POSobservations.get(POS).put(word, 0.0);
                }
            }

            // since the files were split the same way and indexed the same way, words will correspond with their tags
            for (int i = 0; i < tagArray.size() - 1; i++) {

                //it is already in the inner map, update the frequency
                double n = POSobservations.get(tagArray.get(i)).get(wordArray.get(i));
                POSobservations.get(tagArray.get(i)).put(wordArray.get(i), n + 1.0);
                }

            Map<String, Double> sumMap = new HashMap<>(); // create a map to store the totals of each state
            for (String POS: POSobservations.keySet()){ // iterate through each observed POS

                double sum = 0;
                for (double num: POSobservations.get(POS).values()){ // iterate through each frequency score

                    sum += num;
                }
                sumMap.put(POS, sum); // map the sum
             }

            for (String POS: POSobservations.keySet()){ // iterate through each observed POS

                for (String word: POSobservations.get(POS).keySet()){ // iterate through each word for each POS

                    double update = POSobservations.get(POS).get(word);
                    update = Math.log(update/sumMap.get(POS)); // log probability for each word
                    POSobservations.get(POS).put(word,update); // update probability

                }
            }


        } catch (IOException e) {               //catch exception in case there is an exception whiles reading from the file
            System.err.println("IO error while reading.\n" + e.getMessage());
        }

        try {
            tagsInput.close();              //close file
        } catch (IOException e) {          //catch exception in case there is an exception whiles closing file
            System.err.println("Cannot close file.\n" + e.getMessage());

        }

        try {
            sentencesInput.close();              //close file
        } catch (IOException e) {          //catch exception in case there is an exception whiles closing file
            System.err.println("Cannot close file.\n" + e.getMessage());

        }

        return POSobservations; // return observations
    }

    // method to create transitions given a file of sentences and a file of tags
    public static Map<String, Map<String, Double>> buildTransitions(String filename_sentences, String filename_tags) {
        Map<String, Map<String, Double>> POStransitions = new HashMap<String, Map<String, Double>>();
        BufferedReader sentencesInput;
        BufferedReader tagsInput;
        // initialize ADT and file readers

        try {
            tagsInput = new BufferedReader(new FileReader(filename_tags));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open one of the files.\n" + e.getMessage());
            return null;
        }

        try {
            sentencesInput = new BufferedReader(new FileReader(filename_sentences));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open one of the files.\n" + e.getMessage());
            return null;
        }
        // open files for reading while accounting for errors

        try {
            // read each line in file
            String tag_line = tagsInput.readLine();
            ArrayList<ArrayList<String>> tagArray = new ArrayList();
            ArrayList<String> keyArray = new ArrayList<>();


            while (tag_line != null) { // read until last line i nfile
                ArrayList<String> temp = null;
                String[] POS = tag_line.split("\\s+");
                ArrayList<String> innerTagArray = new ArrayList();
                for (String tag : POS) { // add each tag to an indexed arrayList

                    innerTagArray.add(tag); // inner list to retain sentences
                    keyArray.add(tag); // list to build the map's keys
                    temp = innerTagArray;
                }

                tagArray.add(temp); // list of lists ( sentences contain list of words)
                tag_line = tagsInput.readLine();


            }


            for (int i = 0; i < tagArray.size(); i++) {

                Map<String, Double> innerMap = new HashMap<>(); // initialize inner map
                POStransitions.put((String) keyArray.get(i), innerMap); // add tags to map as keys
            }

            Map<String, Double> innerMap = new HashMap<>(); // initialize inner map
            POStransitions.put("#", innerMap); // manually put the start into the transitions

            for (String POS : POStransitions.keySet()) { // manually add the POS to the POS keys with initial values of 0

                for (String addPOS : keyArray) {

                    POStransitions.get(POS).put(addPOS, 0.0);
                }
            }

            for (int i = 0; i < tagArray.size(); i++) {
                for (int j = 1; j < tagArray.get(i).size()-1; j++) {
                    // operate on edge case #
                    // it is already in the inner map, update the transitions
                    double n = POStransitions.get("#").get(tagArray.get(i).get(0));
                    POStransitions.get("#").put(tagArray.get(i).get(0), n + 1.0);


                    // if the key of the inner map exists, update transitions
                    if (POStransitions.get(tagArray.get(i).get(j))!=null){
                        double x = POStransitions.get(tagArray.get(i).get(j)).get(tagArray.get(i).get(j + 1));
                        POStransitions.get(tagArray.get(i).get(j)).put(tagArray.get(i).get(j + 1), x + 1.0);
                    }

                }

            }

            // Update log probabilities

            Map<String, Double> sumMap = new HashMap<>(); // create a map to store the totals of each state
            for (String POS: POStransitions.keySet()){ // iterate through each observed POS

                double sum = 0;
                for (double num: POStransitions.get(POS).values()){ // iterate through each frequency score
                    sum += num;
                }
                sumMap.put(POS, sum); // map the sum
            }

            for (String POS: POStransitions.keySet()){ // iterate through each transition POS

                for (String nextPOS: POStransitions.get(POS).keySet()){ // iterate through each following POS for each POS

                    double update = POStransitions.get(POS).get(nextPOS);
                    update = Math.log(update/sumMap.get(POS)); // log probability for each POS following another
                    POStransitions.get(POS).put(nextPOS,update); // update probability

                }
            }


        } catch (IOException e) {               //catch exception in case there is an exception whiles reading from the file
            System.err.println("IO error while reading.\n" + e.getMessage());
        }

        try {
            tagsInput.close();              //close file
        } catch (IOException e) {          //catch exception in case there is an exception whiles closing file
            System.err.println("Cannot close file.\n" + e.getMessage());

        }

        try {
            sentencesInput.close();              //close file
        } catch (IOException e) {          //catch exception in case there is an exception whiles closing file
            System.err.println("Cannot close file.\n" + e.getMessage());

        }

            return POStransitions;

        }

        // methods to allow for sentences and tag access in other classes

    public static List<String[]> BuildSentencesList(String filename_sentences) { // builds list of sentences
        BufferedReader sentenceInput;
        List<String[]> sentences = new ArrayList();  // holds list of sentences


        try {
            sentenceInput = new BufferedReader(new FileReader(filename_sentences));
        }
        //catch exception incase file isn't able to open. Return null
        catch (FileNotFoundException e) {
            System.err.println("Can't open file'.\n" + e.getMessage());
            return null;
        }
        try {
            String line = sentenceInput.readLine();
            while (line != null) {
                String[] sentenceList = line.split("\\s+");  //split the words by whitespace
                if (sentenceList.length == 0) continue;
                sentences.add(sentenceList);                        // add the words list to the bigger list
                line = sentenceInput.readLine();
            }

        } catch (IOException e) {
            System.err.println("IO error while reading.\n" + e.getMessage());
        }
        return sentences;
    }


    public static List<String[]> BuildTagList(String filename_tags) { // builds POS tags
        BufferedReader tagInput;
        List<String[]> tags = new ArrayList();        // list of tags


        try {
            tagInput = new BufferedReader(new FileReader(filename_tags));
        }
        // if file can't open return null
        catch (FileNotFoundException e) {
            System.err.println("Can't open file.\n" + e.getMessage());
            return null;
        }
        try {
            String line = tagInput.readLine();
            while (line != null) {
                String[] tagList = line.split("\\s+");
                if (tagList.length == 0) continue;
                tags.add(tagList);   // add tag list to overarching list
                line = tagInput.readLine();
            }

        } catch (IOException e) {
            System.err.println("IO error while reading.\n" + e.getMessage());
        }
        return tags;
    }


    public static void main(String[] args) {
        Map<String, Map<String, Double>> observations = buildObservations("example-sentences.txt", "example-tags.txt");
        Map<String, Map<String, Double>> transitions = buildTransitions("example-sentences.txt", "example-tags.txt");

        System.out.println(transitions.get("#"));


    }



}
