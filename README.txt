
========
OVERVIEW
========

This was a very interesting project -- full of subtleties that made it challenging. In particular,

 - the "fuzzy" approach to identifying log lines as errors
 - the fact that it could operate on input files of multiple gigs
 - the 'errorCount' attribute that is at the very beginning of the file but isn't known until we are
   done processing input.
 - error handling.

I'll discuss each of these separately. but first, here are instructions on how to build and run the program.

============================
PACKAGING AND OPERATIONS
============================

The source code is delivered in the form of a zipped tarfile named logjson.tar.gz. Copy that file to the directory
where you'd like the solution to reside and untar it with:
    tar -zxvf  logjson.tar.gz
Doing so will create a 'logjson' directory. You'll need to 'cd' into that directory. To build and run the program,
you'll need version 1.8 of java and javac and you'll need maven.

To run unit tests, you should run
    mvn test
Note that one of the tests is kind of "noisy" in its output, but that is not indicative of any
test failures

To package/build the application (and also run the unit tests), run
    mvn package

Finally, after you run 'mvn package' you can run the applicaton by invoking java directly. It should be
invoked from within the 'logjson' directory you created when you un-tarred the tar.gz file.

To run the program, enter
 java -Dlog4j.configuration=config/log4j.info.properties -cp target/logjson-1.0-SNAPSHOT.jar com.jefff.exercise.Processor

The program has 3 optional arguments to set the input file, output file, and the starting lineNumber offset.
Here is the usage that prints if you pass '-h' as an argument

        Usage: |-i inputFileName| |-o outputFileName| |-n lineOffset| |-h|
          inputFileName:     default = application.log
          outputFileName:    default = errors.json
          lineOffset:        default = 0


====================
Error Identification
====================
We want to detect "error" words in the input file. Looking at the sample input file, it became
pretty clear that the code should look for more than the word "error" or common misspellings. It seemed that other
words should cause a line to be treated as an error entry. Other words such as "failed", "NullPointerException",
"incorrect" all seemed like good indicators of an error.

The "NullPointerException" word is particularly interesting since in reality there are all kinds of exceptions any of
which should be a trigger. That made me think about what should constitute a word when trying to match with our
known error keyWords. Ultimately, I decided to use not only delimited words, but also components of those words if,
as is the case with "NullPointerException" there are changes in alphabetic upper/lower cases within a word.
So something like "NullPointerException" gave me 4 words to check against known error words:
    NullPointerException, Null, Pointer, Exception
The logic to break a line into candidate words is encapsulated in the class WordGenerator and the rules it
follows are easily discerned by looking at WordGeneratorTests

Once the program breaks a line into component words, algorithms are needed to try to match the component words against
known error words. These error words are represented in the enum KeyWordEnum. The comparison itself should be and is,
insensitive to upper/lower case and should match against plural words. So, if  "error" is one of the key words,
we'd want error to match against 'errors' but probably don't want "invalids" to match against "invalid" if "invalids"
appears in a message. An attribute in KeyWordEnum conveys how that particular keyword should be matched against plurals.

I didn't have much experience with the logic involved to match 'eror' with "error" so I googled and found out
that the apache-commons library has a class called 'LevenshteinDistance' that seemed perfect for this purpose.
Of course, the code only resorts to that algorithm if less cpu-intensive matching techniques don't find a match
first. The enum KeyWordEnum has another attribute that comes into play here. For each KeyWord, it specifies
a matching tolerance for that word. Thus for a long word like "Exception" we will consider it a match if
2 substitutions can turn a word into "Exception", but for "Err", there is zero tolerance since that word is so
short. We wouldn't want "ear", "orr", etc to match against "err". Again, this is represented by the attribute
'tolerance' in KeyWordEnum.

Finally, it occurred to me that I wouldn't want to classify the message: "No errors were found" as indicative of
an error even though that sentence contains the word "error". This made me think of the concept of "negators".
If a word such as "No", "Zero", or "0" precedes an error keyword, than the error keyword is neutralized. But,
one negator will only neutralize one error indicator.

So,
  "0 db failures were found"
would not be treated as an error, but
  "0 db failures were found, but 5 IoExceptions occurred"
should be an error

Again, this logic all lives within the ErrorDetector class and its rules are illustrated in "ErrorDetectorTest"

====================
MultiGig Input/Output
====================
Basically, this meant that we couldn't hold our input file, or our generated output in memory. Lines
have to be read, processed, and reflected in output without accumulating them in large datastructures.
This wasn't too difficult, but it did complicate the json formatting a bit. The result, in JsonPrinter
is a hybrid of custom logic, and the gson toolkit. It would have been possible for gson to handle
it all out-of-the-box if all the output could have resided in memory, but we do often have to face this
kind of issue when dealiing with large amounts of data in our real projects so this is cartainly a realistic
consideration.

===============================================
Writing ErrorCount - And Other Output Features
====================---------------------------
As mentioned above, this was somewhat challenging because errorCount appears at the beginning of the
output file, but we don't know what that value will be until we've processed all of our input.
I could have written a file without it, then once known, rewritten the lines of the first file tacked
onto a brief fragment that has the now known errorCount. That seeemed very expensive though. Instead,
the code placed a placeholder in the original file, then right before program completion, the code overwrites
those few placeHolder bytes with the now known errorCount. This was far more efficient. THis is
handled in class OutputManager.

===============================================
Error Handling
===============================================

There were two kinds of errors that the program had to deal with.
 - Incorrectly formatted data or unexpected input data in the application.log
 - File/IO exceptions

My solution attempts to be very tolerant of the first kind of error. Basically it just logs the unexpected
data and moves on to the next line. Note that incorrectly formatted input lines do not throw exceptions, rather,
the parser returns null for them when it parses a bad line.

I found it interesting that one of the errors I checked for was actually present in application.log.
In that file, I found an out-of-order log entry. That is, one would expect the timestamps
in the file to be the same or always increasing. Towards the end of the file, there is one where the time went 'backward'.
I wasn't quite sure what to do in that case, disregard the offending line, or invalidate the preceding data buffered
in the LogEntryWindow. I chose the first course of action but it was a tossup.

There really isn't a good way to overcome File/IO exceptions. Basically, the program just needs to deal with them
in a thoughtful/consistent fashion. Basically, stop processing, close everything is up, and be careful not to represent
progress to date as a successful run.

Essentially, that is how I interpreted this sentence from the instructions.
  'The output JSON should always be valid, even if an exception happens while writting out errors.'
I decided that it meant that we shouldn't write a partial file, and then represent it to the user
as a finished file. In practice, the way I approached it is to have the code gradually build
a temporary file with a random 'guid' component in the file name. If this file gets completed, and
has its errorCount updated, only then, will the code rename it to the filename parameter passed in by the user.
Otherwise, it will be there for inspection with its random name. Note that file renaming is far
cheaper and less error prone than if the copied the file to its destination file name.

The general approach to File/IO exceptions is implemented in the Processor class. In ProcessorTest, I mock all the
exceptions that can be returned from the components and verify that they are handled as I intended.
