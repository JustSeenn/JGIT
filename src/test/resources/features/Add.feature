Feature: jgit add command

	Background:
		Given a working directory 
        And a new file named "test.txt" with content "Hello World"
        And an already registered file "notadded.txt" with content "You shain't add me twice."

    Scenario: Add an already registered file to the index
        When the user wants to execute the command "add" with argument "notadded.txt"
        Then the index should contain "notadded.txt" once only

    Scenario: Add test.txt to the index
        When the user wants to execute the command "add" with argument "test.txt"
        Then the index should contain "test.txt" once only
    
    Scenario: Add all files to the index
        When the user wants to execute the command "add" with argument "."
        Then all the files in the directory "." should be added to the index