
****** It is assumed JDK is already installed on your system ************

To compile the program in windows, please type -> javac -cp ".:jar1.jar:jar2.jar:jar3.jar:jar4.jar:jar5.jar:jar6.jar" *.java
To run the program please type -> java -cp ".:jar1.jar:jar2.jar:jar3.jar:jar4.jar:jar5.jar:jar6.jar" BuildingWatson <folder for wiki pages> <name of file with questions> <bit for BM25 similarity> <technique> <precision> <name of index directory>

Here are the descriptions for various attributes - 

1. <folder for wiki pages>             -        This is to specify the folder where all the 80 files containing 280000 wiki pages are stored
2. <name of file with questions>       -        This is to give the name of the file containing the set of questions
3. <bit for BM25 similarity>           -        0 for not using it
                                                1 for using it
4. <technique>                         -        naive for naive approach
                                                stem for Improved approach
                                                lemma for lemmatization on Improved approach (without stemming)
5. <precsion>                          -        P@1 for precision at one
                                                P@10 for precision at ten
6. <name of index directory>           -        Name of the directory you want to create


Recreating 'Naive' approach and 'Improved' approach - 

Here the collection of 280000 wiki pages is assumed to be contained in a folder called 'wikiDocs' and questions are assumed to be contained in a file called 'questions.txt'

To recreate 'Naive' approach use the below command line after compiling - 
java -cp ".:jar1.jar:jar2.jar:jar3.jar:jar4.jar:jar5.jar:jar6.jar" BuildingWatson wikiDocs questions.txt 0 naive P@1 Naive_Approach
This will give you the results at precision at one based on tf-idf scoring.
Please note that any Index Directory built using naive approach must have the associated 'naive' parameter passed as argument for technique

To recreate 'Improved' approach use the below command line after compiling - 
java -cp ".:jar1.jar:jar2.jar:jar3.jar:jar4.jar:jar5.jar:jar6.jar" BuildingWatson wikiDocs questions.txt 0 stem P@1 Improved_Approach
This will give you the results at precision at one based on tf-idf scoring.
Please note that any Index Directory built using stemming approach must have the associated 'stem' parameter passed as argument for technique

To recreate 'Improved' approach with lemmatization use the below command line after compiling - 
java -cp ".:jar1.jar:jar2.jar:jar3.jar:jar4.jar:jar5.jar:jar6.jar" BuildingWatson wikiDocs questions.txt 0 lemma P@1 Lemma_Improved_Approach
This willgive you the results at precision at one based on tf-idf scoring.
Please note that any Index Directory built using lemmatization approach must have the associated 'lemma' parameter passed as argument for technique

To recreate 'Improved' approach and evaluate results on precision at ten (with tf-idf scoring) use the below command line after compiling - 
java -cp ".:jar1.jar:jar2.jar:jar3.jar:jar4.jar:jar5.jar:jar6.jar" BuildingWatson wikiDocs questions.txt 0 stem P@10 Improved_Approach

To recreate 'Improved' approach and evaluate results on precision at ten (with BM25 scoring) use the below command line after compiling - 
java -cp ".:jar1.jar:jar2.jar:jar3.jar:jar4.jar:jar5.jar:jar6.jar" BuildingWatson wikiDocs questions.txt 1 stem P@10 Improved_Approach


**** PLEASE DO NOT LEAVE ANY OF THE ATTRIBUTES BLANK FOR PROPER FUNCTIONING OF CODE ****

Additional Notes - 
1. Depending on the capability of your machine the complete parsing can take 4 to 6 minutes
2. The code can be compiled and run from Windows, Linux or Mac
3. The jar files 1 to 4 correspond to Lucene functionalities and are provided with the package. Jar files 5 and 6 are for Stanford NLP (Lemmatization)
   jar5.jar is stanford-corenlp-3.5.2.jar
   jar6.jar is stanford-corenlp-3.5.2-models.jar
   You need to include the jar files manually in the package
   I am using the Stanford NLP version 3.5.2



      

   
