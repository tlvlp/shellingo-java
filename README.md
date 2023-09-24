# Shellingo

A simple language-agnostic command line tool for course specific vocabulary practice.
(Without the constant harassment of the Duolingo owl :D)

## Building and setup
1. Clone the project
2. Use Maven to build the executable jar
```shell
mvn clean install
```
3. Copy the jar from the **target** folder to where you want to use it.
4. Create a subfolder named **questions** next to the jar. This is the default path to store questions in
5. Add questions (see the 'How to' section below)
6. Run the jar with:
```shell
java -jar shellingo-1.0.0.jar
```
> Note: The app also takes a single argument that overrides the default path. 
> This could be either a parent folder or an exact file.
 ```shell
java -jar shellingo-1.0.0.jar mypath/my_parent_folder
java -jar shellingo-1.0.0.jar mypath/my_parent_folder/selected_file.txt
```

## How to add vocabularies/questions
> Shellingo will read all files in the **questions** directory and all its subdirectories next to the jar file.
> This allows organizing questions into separate text files and folders and moving them in and out of the **questions** 
> folder to change the practice materials. 
> Make sure not to mix languages, or if you do indicate the language in each question :)
> All the questions will be presented in a random order. Each word will be repeated until a correct solution is given.

- Create a text file and add one word/question per row and provide the expected answer, 
separated with a pipe **|** character.
- You can also add comments with the hash **#** character. These lines will be ignored during the practice.
- Both questions and answers will be formatted to remove leading, trailing and duplicate white spaces and punctuation
- Letter casing will be ignored during the practice

```text
# These are the number 0-5 from the first Polish lesson
1|jeden
2|dwa
3|trzy
4|cztery
5|pięć
```

```text
# More complex sentences with exotic white space use that will be corrected

the elephant   likes  milk |  słoń  lubi  mleko   

# will be presented as:  
# the elephant likes milk: słoń lubi mleko 
```
