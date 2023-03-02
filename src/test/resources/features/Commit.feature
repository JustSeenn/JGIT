Feature: Commmit Feature

	Background: 
		Given a project folder containing two files containing "content1" and "content2"
		

  #Scenario: making a simple commit check for validity of created files
  	When we make a commit with description "Commit1"
  	Then the object folder contains the right hashed files with the right content
  	And a hashed commit file has been added to the .jgit/logs folder containing the right info
  	And the HEAD file has been correctly updated
