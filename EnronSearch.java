import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class Assignment3 {
    public static WeightedGraph.Graph enronEmails = new WeightedGraph.Graph();
    public static PrintWriter connectorOutput;
    public static int[] dfsNums;
    public static int[] back;
    public static int dfsnum;
    public static Set<String> connectors;
    public static int connections = 0;

    public static void createGraph(String source, String target){
        enronEmails.addEdge(source, target);
    }
    public static void scanFile(File file){
        if (file.isDirectory()) {
            //Takes in person folder and navigates to sent_items, _sent_mail, sent and inbox folders so they can be looped over
            File sent_items = new File(file, "sent_items");
            File _sent_mail = new File(file, "_sent_mail");
            File sent = new File(file, "sent");
            File inbox = new File(file, "inbox");
            
            folderLoop(sent_items);
            folderLoop(sent);
            folderLoop(_sent_mail);
            folderLoop(inbox);
        }

    }

    //loops over every mail folder... Calls loopEmail with the actual email files
    public static void folderLoop(File folder){
        if(folder.exists() && folder.isDirectory()){
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    loopEmail(file);
                }
            }
        }
    }

    public static int teamSize(String email) {
        Set<String> HashSetEmails = new HashSet<>();
        List<WeightedGraph.Edge> edges = enronEmails.adjacencyList.get(email);
        if (edges != null) {
            for (WeightedGraph.Edge edge : edges) {
                HashSetEmails.add(edge.emailTarget);
                HashSetEmails.add(edge.source);
            }
        }
        return HashSetEmails.size();
    }

    public static void loopEmail(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String sender = null;
            String recipients = null;
            String line;
            String[] recipientArr = null;

            while ((line = reader.readLine()) != null) {
                if (line.length() > 6){
                    if (line.startsWith("From:")) {
                        sender = line.substring(6).trim();
                    } else if (line.startsWith("To:")) {
                        recipients = line.substring(4).trim();
                    }
    
                    if (recipients != null && sender != null) {
                        recipientArr = recipients.split(", ");
                        for(int i = 0; i < recipientArr.length; i++){
                            if(recipientArr[i].contains("@") && recipientArr[i].contains(".")){
                                createGraph(sender, recipientArr[i]);
                                connections++;
                                if(connections%10000 == 0){
                                    System.out.println(connections);
                                }
                            }
                        }
                        recipientArr = null;
                        sender = null;
                        recipients = null;
                    }
                } else{
                    continue;
                }
                
            }
        } catch (IOException e) {

        }
    }

    public static int receivedEmails(String email) {
        Set<String> HashSetEmails = new HashSet<>();
        for (Map.Entry<String, List<WeightedGraph.Edge>> emailKey : enronEmails.adjacencyList.entrySet()) {
            List<WeightedGraph.Edge> edges = emailKey.getValue();
            for (WeightedGraph.Edge edge : edges) {
                if (edge.emailTarget.equals(email)) {
                    HashSetEmails.add(emailKey.getKey());
                }
            }
        }
        return HashSetEmails.size();
    }

    public static int sentEmails(String email) {
        List<WeightedGraph.Edge> edges = enronEmails.adjacencyList.get(email);
        if (edges != null) {
            Set<String> HashSetEmails = new HashSet<>();
            for (WeightedGraph.Edge edge : edges) {
                HashSetEmails.add(edge.emailTarget);
            }
            return HashSetEmails.size();
        }
        return 0;
    }

     /*
    * Find connectors From this point to the next multiline comment
    * are responsible for finding connectors and printing them 
    */
    
    public static void printConnectors() {
        System.out.println("Started");
        dfsNums = new int[enronEmails.adjacencyList.size()];
        back = new int[enronEmails.adjacencyList.size()];
        dfsnum = 0;
        connectors = new HashSet<>();

        try {
            connectorOutput = new PrintWriter(new FileWriter("connectors.txt"));
        } catch (IOException e) {
            System.out.println("Error creating output file");
            return;
        }

        for (String vertex : enronEmails.adjacencyList.keySet()) {
            try {
                if (dfsNums[vertexIndex(vertex)] == 0) {
                    dfs(vertex, null);
                    System.out.println("Iterate");
                } 
            } catch (Exception e) {
                // Invalid vertex encountered, continue to the next connector
                System.out.println("Invalid");
                continue;
            }
        }

        connectorOutput.close();
    }

    private static void dfs(String vertex, String parent) {
        dfsNums[vertexIndex(vertex)] = ++dfsnum;
        back[vertexIndex(vertex)] = dfsnum;
    
        for (WeightedGraph.Edge edge : enronEmails.adjacencyList.get(vertex)) {
            String neighbor = edge.emailTarget;
            if (neighbor.equals(parent)) {
                continue; // Ignore the edge to the parent vertex
            }
    
            try {
                int neighborIndex = vertexIndex(neighbor);
                if (dfsNums[neighborIndex] == 0) {
                    dfs(neighbor, vertex);
                    back[vertexIndex(vertex)] = Math.min(back[vertexIndex(vertex)], back[neighborIndex]);
    
                    if (back[neighborIndex] > dfsNums[vertexIndex(vertex)]) {
                        // Found a cut edge
                        String cutEdge = String.format("%s - Connector - %s", vertex, neighbor);
                        connectors.add(cutEdge);
                        connectorOutput.println(cutEdge);
                    }
                } else {
                    back[vertexIndex(vertex)] = Math.min(back[vertexIndex(vertex)], dfsNums[neighborIndex]);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                // Invalid vertex encountered, continue to the next connector
                continue;
            }
        }
    }

    private static int vertexIndex(String vertex) {
        return Integer.parseInt(vertex.substring(1));
    }

    /* End of find Connectors */

    // Main - Used for all user inputs and visual aspects
    public static void main(String[] args) {
        if(args[0] == null){
            System.out.println("Please enter a file path");
            System.exit(0);
        }
        //Create file
        String fPath = args[0];
        File[] fileArr = new File(fPath).listFiles();
        if(fileArr != null){
            System.out.println("--------------Reading Files--------------");
            System.out.println("-----------------Progress----------------");
            for (File person : fileArr){
                scanFile(person);
                if(connections > 100000){
                    break;
                }
            }

        } else{
            System.out.println("Path not found");
            System.exit(0);
        }
        printConnectors();
        Scanner userIn = new Scanner(System.in);

        while (true) {
            System.out.print("Email address of the individual (or EXIT to quit): ");
            String input = userIn.nextLine();
            if (input.equalsIgnoreCase("EXIT") || input.equalsIgnoreCase("quit")) {
                System.out.print("Bye!");
                userIn.close();
                System.exit(1);
            } else if(input.contains("@") && input.contains(".")){
                if(enronEmails.adjacencyList.get(input) != null){
                System.out.printf("* %s has sent messages to %d others\n", input, sentEmails(input));
                System.out.printf("* %s has received messages from %d others\n", input, receivedEmails(input));
                System.out.printf("* %s is in a team with %d individuals\n", input, teamSize(input));
                } else{
                    System.out.printf("Email address (%s) not found in the dataset.\n", input);
                }
            } else {
                System.out.println("Invalid input. Please try again(input must include '@' to be a valid email)");
            }
        }

    }
}

/*  Weighted Graph class responsible for taking in connections.
 * Uses Adjacency List to keep track of edges.
*/
class WeightedGraph {
    static class Edge {
        String source;
        String emailTarget;
        int weight;

        public Edge(String source, String emailTarget, int weight) {
            this.source = source;
            this.emailTarget = emailTarget;
            this.weight = weight;
        }
    }

    static class Graph {
        Map<String, List<Edge>> adjacencyList;

        public Graph() {
            adjacencyList = new HashMap<>();
        }

        public void addEdge(String source, String emailTarget) {
            UpdateEdge(source, emailTarget, 1);
        }

        public void UpdateEdge(String source, String emailTarget, int weight) {
            List<Edge> sourceEdges = adjacencyList.getOrDefault(source, new LinkedList<>());
            List<Edge> emailEdges = adjacencyList.getOrDefault(emailTarget, new LinkedList<>());

            for (Edge edge : sourceEdges) {
                if (edge.emailTarget.equals(emailTarget)) {
                    edge.weight += weight;
                    return;
                }
            }

            Edge newEdge = new Edge(source, emailTarget, weight);
            sourceEdges.add(newEdge);
            emailEdges.add(newEdge);

            adjacencyList.put(source, sourceEdges);
            adjacencyList.put(emailTarget, emailEdges);
        }

    }
}