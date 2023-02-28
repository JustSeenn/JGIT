Feature: Commmit Feature

	Background: 
		Given a project folder containing two files containing "content1" and "content2"
		

  Scenario: making a simple commit check for validity of created files
  	When the user want to execute the command "commit" with argument "commit msg"
  	Then the object folder contains the right hashed files with the right content
  	And a hashed commit file has been added to the .jgit/logs folder containing the right info
  	And the HEAD file has been correctly updated

# get the older tests to execute with the new project struture
 # check for the validity of hash length
 # use paths to create files and folders recursively uing same old tests
 