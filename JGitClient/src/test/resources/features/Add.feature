Feature: git add command

	Background:
		Given a jgit repository with a new file "test.txt" with content "Hello World"
        And an already registered file "notadded.txt" with content "Hello World"

    Scenario: There is nothing to add
        When I add "notadded.txt" to the index
        And "notadded.txt" is already in the index
        Then the output should contain "The file notadded.txt is already in the index." about "notadded.txt"

    Scenario: Add test.txt to the index
        When I add "test.txt" to the index
        Then the file[s] "test.txt" should be added to the index with the output "The file test.txt has been added to the index."
    
    Scenario: Add all files to the index
        When I add all the files to the index
        Then the file[s] "test.txt" should be added to the index with the output "The files in the directory . have been added to the index."
        But the file[s] "notadded.txt" should not be added a second time to the index