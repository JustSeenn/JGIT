Feature: Commmit Feature

	Background: 
		Given a working directory
		And a repository .jgit
		And a temporary .txt file "dir1/dir2/file.txt" containing "content 1"
		And a temporary .txt file "dir1/file.txt" containing "content"
		And a temporary .txt file "dir1/dir2/file2.txt" containing "content 2"
		And a temporary .txt file "file.txt" containing "content"
		

  Scenario: making a simple commit check for validity of created files
  	When the user want to execute the command "commit" with argument "commit msg"
  #	Then the object folder contains the right hashed files with the right content
  #	And a hashed commit file has been added to the .jgit/logs folder containing the right info
  #	And the HEAD file has been correctly updated

# get the older tests to execute with the new project struture
 # check for the validity of hash length
 # use paths to create files and folders recursively uing same old tests
 