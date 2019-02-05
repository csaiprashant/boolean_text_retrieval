# Boolean Text Retrieval - Building a Boolean Search Engine

## Introduction
In the Boolean model for information retrieval, query terms are combined logically using Boolean operators AND, OR and NOT. Given a Boolean query, the system retrieves every document that makes the query logically true (also called an exact match). Each document is treated as a bag of words and word sequence is not considered. Our goal is to implement the methods AND, OR, NOT and perform some queries on a small corpus of Amazon.com reviews on electronics.

## Files in the Repository
- all.txt - Contains the raw reviews. Each line of the file is a review/document in our context. There are total 1248 documents.
- BooleanRetrieval.java - Main program which has the boolean querying implemented.
- DatasetFormatter.java - Helper class which provides preprocessing operations such as parsing, generating the vocabulary map and filtering stopwords based on count.
- docs.txt - Contains the reviews represented in document matrix format.
- README.md
- stopwords - Contains a list of stopwords to be filtered.
- vocab_map.txt - Contains the vocabulary map with each word mapped to a unique integer.

## Command to run the code
    Compile the program using the command 
	> javac BooleanRetrieval.java
    Run the program using the command
	> java BooleanRetrieval <queryType> <queryString 1> <queryString 2> <path to output file>
The first argument is always the queryType (PLIST, AND, OR, AND-NOT) and the last argument is always the path to the output file.

## Sample input and output
    java BooleanRetrieval AND mouse scrolling and_result.txt
    Should produce 
    mouse AND scrolling -> [80, 86, 348, 1029] 
    as the contents of and_result.txt
