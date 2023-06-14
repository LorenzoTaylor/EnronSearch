# EnronSearch
# README.md

This is a Java program that processes email data from a given directory and its subdirectories, builds a weighted graph based on the connections between email senders and receivers, and prints the connectors in the graph.

The dataset can be found at [here](https://www.cs.cmu.edu/~enron/).

## Overview

The program consists of several classes and methods that perform the following tasks:

- Scan email folders and process email files.
- Build a weighted graph using email sender and receiver information.
- Calculate the team size, received emails, and sent emails for each person.
- Find and print connectors in the graph.

## Usage

1. Compile the Java program:

```
javac EmronSearch.java
```

2. Run the program with the directory containing the email folders as an argument:

```
java EnronSearch /path/to/email/dataset
```

3. The program will generate a `connectors.txt` file containing the list of connectors found in the graph.

## Classes and Methods

### Assignment3

- `enronEmails`: A global `WeightedGraph.Graph` object to store the email connections.
- `scanFile(String)`: Adds an edge between a sender and a recipient if the recipient's email address is found in the file.
- `teamSize(String email)`: Calculates the team size for a given email address.
- `receivedEmails(String email)`: Calculates the number of received emails for a given email address.
- `sentEmails(String email)`: Calculates the number of sent emails for a given email address.
- `printConnectors()`: Finds and prints the connectors in the graph.

### Other Methods

- `folderLoop(File folder)`: Recursively processes email folders and subfolders.
- `loopEmail(File folder)`: Processes email files in a given folder.
- `loopEmail(String email)`: Processes an email file and adds the sender and recipient to the graph.
- `dfs(String vertex, String parent)`: Performs a depth-first search on the graph to find connectors.

## References

- [Source 0](https://alvinalexander.com/java/jwarehouse/deeplearning4j/README.md.shtml)
- [Source 1](https://www.geeksforgeeks.org/what-is-readme-md-file/)
- [Source 2](https://www.mygreatlearning.com/blog/readme-file/)
- [Source 4](https://medium.com/analytics-vidhya/how-to-create-a-readme-md-file-8fb2e8ce24e3)
- [Source 5](https://github.com/openfin/java-example/blob/master/README.md) 
