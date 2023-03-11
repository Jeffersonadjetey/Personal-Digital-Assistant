import java.util.List;
import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**Author: Matthew Currie, Jefferson Adjetey
 * Date: 5/23/2021
 * CS10 Spring 2021
 * Evaluate efficiency of the Viterbi
 */




public class markovPerformanceEvaluation {
    // method that evaluates the algorithm's performance with the console
    public static void consolePerformanceEvaluation(String filename_sentences, String filename_tags){
        // build model
        Map<String,Map<String, Double>> observations = markovTraining.buildObservations(filename_sentences,filename_tags);
        Map<String, Map<String,Double>> transitions = markovTraining.buildTransitions(filename_sentences,filename_tags );

        boolean run_console = true; // boolean for running the console

        while (run_console){ // while boolean is true, run

            Scanner user_input = new Scanner(System.in); // scanner for user input
            String line = user_input.nextLine(); // create a string from the user input


            String[] observation = line.split("\\s+"); // split by whitespace into list


            if (observation[0].equals("q") &&  observation.length==1){  // if the input is only the character q, stop program
                run_console = false;
            }
            else { // if the input is anything else:

                List<String> buildBestPath = markovViterbi.constructBestPath(observations, transitions, observation);
                // create a list of the best path of the observation

                String bestPathTags = ""; // create an empty string to store the tags of the best path

                for (int i=0; i<buildBestPath.size(); i++){ // iterate through each POS of best path
                    bestPathTags += buildBestPath.get(i) + " "; // add to best path tags
                }
                System.out.println("The sentence is tagged as:\n");
                System.out.println(bestPathTags); // print best path tags
            }
        }
    }

    // method that evaluates the algorithm's performance based on files fed to it
    public static void filePerformanceEvaluation(String filename_sentencesTrain, String filename_tagsTrain,
                                                 String filename_sentencesTest, String filename_tagsTest){

        // build model
        Map<String,Map<String, Double>> observations = markovTraining.buildObservations(filename_sentencesTrain,filename_tagsTrain);
        Map<String, Map<String,Double>> transitions = markovTraining.buildTransitions(filename_sentencesTrain,filename_tagsTrain);

        int correct = 0; // initiaze counts for correct and incorrect
        int incorrect = 0;

        List<String[]> list_sentences = markovTraining.BuildSentencesList(filename_sentencesTest); // build a list of sentences
        List<String[]> solution_tags = markovTraining.BuildTagList(filename_tagsTest); // build a list of tags

        for (int i = 0; i < list_sentences.size(); i++){ // iterate through the list of sentences
            List<String> bestPathTags= markovViterbi.constructBestPath(observations, transitions, list_sentences.get(i));
            // construct a best path for the sentence

            for (int j = 0; j < bestPathTags.size() ; j++){
                // iterate through the line of tags
                String[] line = solution_tags.get(i);
                // check if the tag is correct
                if (line[j].equals(bestPathTags.get(j))){
                    correct += 1;
                }
                else{
                    incorrect += 1;
                }
            }
        } // Print the amount of words that were tagged correctly/incorrectly
        System.out.println("The algorithm has tagged approximately : "+ correct +" words correctly");

        System.out.println("The algorithm has tagged approximately : " + incorrect+ " words incorrectly");

    }

    public static void main(String[] args) {
        System.out.println("**FILE BASED**\n");
        System.out.println("File Test: Simple\n");
        filePerformanceEvaluation("simple-train-sentences.txt", "simple-train-tags.txt", "simple-test-sentences.txt", "simple-test-tags.txt");

        System.out.print("\n");
        System.out.println("File Test: Brown\n");
        filePerformanceEvaluation("brown-train-sentences.txt", "brown-train-tags.txt", "brown-test-sentences.txt", "brown-test-tags.txt");
        System.out.println("\n");
        System.out.println("**CONSOLE BASED**");
        consolePerformanceEvaluation("brown-train-sentences.txt", "brown-train-tags.txt");
    }


}
