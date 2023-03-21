Feature: Merge two commit together

  Background:
    Given a repository .jgit
    And a working directory
    And a temporary .txt file "file.txt" containing "Hello World"
    When the user execute the command "add" with argument "."
    And the user execute the command "commit" with argument "commit1"

    Scenario: "commit1" and "commit2" are the same
      When the user execute the command "commit" with argument "commit2"
      And the user execute the command "merge" with argument "a43c7b6c9c9cf56c43fe587a5bfb35d7"
      Then The result of the merge has the content "Hello World"


    Scenario: "commit1" and "commit2" are different and commit1 didn't change
      When we modify the content of file.txt with "Hello the World"
      And the user execute the command "add" with argument "."
      And the user execute the command "commit" with argument "commit2"
      And the user execute the command "merge" with argument "a43c7b6c9c9cf56c43fe587a5bfb35d7"
      Then The result of the merge has the content "Hello the World"


    Scenario: "commit1" and "commit2" are the different and commit1 changed
      When we modify the content of file.txt with "Hello the World"
      And the user execute the command "add" with argument "."
      And the user execute the command "commit" with argument "commit2"

      When we modify the content of file.txt with "Hello this World"
      And the user execute the command "add" with argument "."
      And the user execute the command "commit" with argument "commit3"
      And the user execute the command "merge" with argument "a43c7b6c9c9cf56c43fe587a5bfb35d7"
      Then The result of the merge has the content "<<<<<<<<<Hello this World===========Hello World>>>>>>>>>"
      And we reset the working directory