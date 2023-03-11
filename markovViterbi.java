import java.util.*;

/**Author: Matthew Currie, Jefferson Adjetey
 * Date: 5/23/2021
 *  * CS10 Spring 2021
 * Perform a Viterbi decoding to find the best scored POS and best preceding state
 */



public class markovViterbi {
    // method to create best path given a markov model
    public static List<String> constructBestPath(Map<String, Map<String, Double>> observations, Map<String, Map<String, Double>> transitions, String[] observation) {

        Map<String, Double> currentScores = new HashMap<String, Double>(); // create a map to store the current score

        Map<String, Map<Integer, String>> backPointers = new HashMap<String, Map<Integer, String>>(); // map of maps so we can keep track of our prev

        LinkedList<String> bestPath = new LinkedList<String>(); // best path that we are trying to construct

        Set<String> currStates = new HashSet<String>(); // set of current states, doesn't need to be ordered

        currStates.add("#"); // intiialize start with a score of 0
        currentScores.put("#", 0.0);

        for (int i = 0; i < observation.length; i++) {
            // iterate through each observation
            Map<String, Double> nextScores = new HashMap<String, Double>(); // scores of next state

            Set<String> statesToVisit = new HashSet<String>(); // holds the states that need to be visited


            for (String state : currStates) { // iterate through each state in current
                if (transitions.containsKey(state)) {
                    // if we can move to another state, iterate through new states
                    for (String newStates : transitions.get(state).keySet()) {

                        statesToVisit.add(newStates); // add to states that need to be visited

                        double observationScore = 0; // initialize observation score to 0

                        if (observations.get(newStates).containsKey(observation[i].toLowerCase()) && ((observations.get(newStates
                        ).get(observation[i].toLowerCase()) == Double.POSITIVE_INFINITY) || (observations.get(newStates
                        ).get(observation[i].toLowerCase()) == Double.NEGATIVE_INFINITY) || (observations.get(newStates
                        ).get(observation[i].toLowerCase()) == Double.NaN) )){
                            observationScore = -200;

                            // sets up default score of - 10. since our non-recorded observations are assigned values of ininitiy or NaN,
                            // need to take extra steps to assign a default value

                        } else {
                            if (observations.get(newStates).get(observation[i].toLowerCase())!=null){
                                // link up observation score with the score of the next state's obsjervation
                                observationScore = observations.get(newStates).get(observation[i].toLowerCase());
                            }
                        }

                        double nextScore = currentScores.get(state) + transitions.get(state).get(newStates) + observationScore;
                        // add current + transit + obs to get next score

                        if (nextScores.containsKey(newStates)){ // check if visited

                            if (nextScore > nextScores.get(newStates)){ // take greater score
                                nextScores.put(newStates, nextScore);
                                backPointers.get(newStates).put(i,state); // update backpointers
                            }

                        } else if (backPointers.containsKey(newStates)){ // if in backpointers, update with new score and replace in backpointers
                            nextScores.put(newStates, nextScore);
                            backPointers.get(newStates).put(i, state);

                        } else { // for all other cases, update nextscores and place in backpointers
                            nextScores.put(newStates, nextScore);
                            backPointers.put(newStates, new HashMap<Integer, String>());
                            backPointers.get(newStates).put(i, state);
                        }


                    }
                }

            }
            currStates = statesToVisit;
            // current states now becomes the next states


            currentScores = nextScores;
            // current scores now become the next scores

        }

        // best state from current scores

        double largest = -500000; // initialize largest to an arbitrary number

        String bestState = ""; // initialize best state to an empty string

        for (String state : currentScores.keySet()) { // iterate through current scores
            if (currentScores.get(state) > largest) { // get largest
                largest = currentScores.get(state);
                bestState = state; // update best state

            }
        }

        //backtrace from the best to the start for every observation
        if (!backPointers.isEmpty()) { // run if there are backpointers

            String nState = bestState; // get bestState
            int x = observation.length - 1; // get length of observation

            while (!nState.equals("#")) {
                bestPath.addFirst(nState);
                if (backPointers.containsKey(nState)) nState = backPointers.get(nState).get(x);
                x=x-1;
            }

        }



        return bestPath; // return the bestPath
    }

    public static void main(String[] args) {
        Map<String, Map<String, Double>> observations = new HashMap <String, Map<String, Double>>();
        Map<String, Map<String, Double>> transitions = new HashMap<String, Map<String, Double>>();
        // initialize observations and transitions



        observations.put("CNJ", new HashMap<String, Double>());
        observations.put("N", new HashMap<String, Double>());
        observations.put("NP", new HashMap<String, Double>());
        observations.put("V", new HashMap<String, Double>());

        observations.get("CNJ").put("and", 3.0);

        observations.get("N").put("cat", 5.0);
        observations.get("N").put("dog", 5.0);
        observations.get("N").put("watch", 2.0);

        observations.get("NP").put("chase", 5.0);

        observations.get("V").put("get", 1.0);
        observations.get("V").put("chase", 2.0);
        observations.get("V").put("watch", 6.0);


        transitions.put("#", new HashMap<String, Double>());
        transitions.put("CNJ", new HashMap<String, Double>());
        transitions.put("N", new HashMap<String, Double>());
        transitions.put("NP", new HashMap<String, Double>());
        transitions.put("V", new HashMap<String, Double>());

        transitions.get("#").put("N", 5.0);
        transitions.get("#").put("NP", 2.0);

        transitions.get("CNJ").put("NP", 1.0);
        transitions.get("CNJ").put("N", 1.0);
        transitions.get("CNJ").put("V", 1.0);

        transitions.get("N").put("CNJ", 2.0);
        transitions.get("N").put("V", 6.0);

        transitions.get("NP").put("V", 2.0);

        transitions.get("V").put("NP", 2.0);
        transitions.get("V").put("N", 6.0);
        transitions.get("V").put("CNJ", 1.0);

        String[] observation = {"cat", "chase", "dog"};
        String[] observation2 = {"cat", "watch", "chase"};
        String[] observation3 = {"chase", "get", "watch"};
        String[] observation4 = {"chase", "watch", "dog", "and", "cat"};
        String[] observation5 = {"dog", "watch", "cat", "watch", "dog"};
        String[] observation6 = {"cat", "watch", "watch","and", "chase"};
        String[] observation7 = {"dog", "watch", "and", "chase", "chase"};


        System.out.println("Observations: " + observations);
        System.out.println("Transitions: "+ transitions);

        System.out.println("Observation 1 is tagged as: " + constructBestPath(observations, transitions, observation));
        System.out.println("Observation 2 is tagged as: " +constructBestPath(observations, transitions, observation2));
        System.out.println("Observation 3 is tagged as: " +constructBestPath(observations, transitions, observation3));
        System.out.println("Observation 4 is tagged as: " +constructBestPath(observations, transitions, observation4));
        System.out.println("Observation 5 is tagged as: " +constructBestPath(observations, transitions, observation5));
        System.out.println("Observation 6 is tagged as: " +constructBestPath(observations, transitions, observation6));
        System.out.println("Observation 7 is tagged as: " +constructBestPath(observations, transitions, observation7));
    }
}



