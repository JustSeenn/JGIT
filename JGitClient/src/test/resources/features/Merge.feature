Feature: Merge two commit together

  Background:
    Given a repository .jgit
    And a working directory
    And a temporary .txt file "file.txt" containing "content"
    And a folder named folder1
    And a commit named commit1 which is the current commit
    And a commit named commit2

    Scenario: "commit1" and "commit2" are the same
      When the content of commit2 is "Hello World"
      And the user execute the command "merge" with argument "f7fab1387091b5a87aba135590d7498e"
      Then The result of the merge has the content "Hello World"

    Scenario: "commit1" and "commit2" are the different and commit1 didn't change
      When the content of commit2 is "Hello the World"
      And the user execute the command "merge" with argument "f7fab1387091b5a87aba135590d7498e"
      Then The result of the merge has the content "Hello the World"

    Scenario: "commit1" and "commit2" are the different and commit1 changed
      When the content of commit3 is "Hello the World"
      When the content of commit2 is "Hello Worldss"
      And the user execute the command "merge" with argument "beff5b61477ae92aeee7dc415e3be37d"
      Then The result of the merge has the content "<<<<<<<<<Hello Worldss===========Hello the World>>>>>>>>>"
