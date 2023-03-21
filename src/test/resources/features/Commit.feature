Feature: Commmit Feature

  Background: 
    Given a working directory
    And a repository .jgit
    And a temporary .java file "dir1/dir2/file.java" containing "content 1"
    And a temporary .txt file "dir1/file.txt" containing "content"
    And a temporary .txt file "dir1/dir2/file2.txt" containing "content 2"
    And a temporary .txt file "file.txt" containing "content"

  Scenario: making a simple commit check for validity of created files
    When the user execute the command "add" with argument "."
    When the user execute the command "commit" with argument "commit msg"
    Then the object folder contains the right hashed files with the right content
    Then a hashed commit file has been added
    And the HEAD file has been correctly updated
    And we reset the working directory
  