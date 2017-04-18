Clustering.java uses an input file input.txt and writes to an output file output.txt. It processes the file and groups together similar sentences (sentences where only a single word has changed) and extracts the changes, then outputs them to a file. 

I implemented this by reading in each line of the input, assuming the first two elements separated by a space would be the date and time, and adding the full string of each line to a list and an array representing each word in the sentence (exluding date and time) to another list. After reading in all the lines, I process each sentence and find groupings where the sentence differs from another only by one word. After finding all groupings, I print them to the output file. 

- What can you say about the complexity of your code?
The most work is done in comparing each string. Comparing each string is O(N^2/2 * M) where N is the number of lines and M is the maximum number of words in each sentence. Each line must be compared to all the rest of the lines which is N^2/2 comparisons and then each word must be compared against those in the other sentence. 

- How will your algorithm scale? If you had two weeks to do this task, what would you have done differently? What would be better?
My algorithm will not scale very well. Larger inputs will take much more time. If I had more time to do this task, I would add in parallelism and have the sentences checked against each other on different threads. This would vastly speed up the amount of time it takes to make the groupings. I would also refine the comparison algorithm more to speed it up. 
