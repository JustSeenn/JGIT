Feature: Commmit Feature

	Background: 
		Given a working directory
		And a repository .jgit
#		And a temporary .txt file "dir1/dir2/file" containing "content 1"
#		And a temporary .txt file "dir1/file" containing "content"
#		And a temporary .txt file "dir1/dir2/file2" containing "content 2"
#		And a temporary .txt file "file" containing "content"'
    And a file named file with the content "Hello World\n how are you today ?\n"
    And a folder named folder1
    And a commit named commit1 which is the current commit
    And a commit named commit2
		

  Scenario: making a simple commit check for validity of created files
  	When the user want to execute the command "commit" with argument "commit msg"
  #	Then the object folder contains the right hashed files with the right content
  #	And a hashed commit file has been added to the .jgit/logs folder containing the right info
  #	And the HEAD file has been correctly updated

# get the older tests to execute with the new project struture
 # check for the validity of hash length
 # use paths to create files and folders recursively uing same old tests
 