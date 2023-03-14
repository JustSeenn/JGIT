Feature: Merge two commit together

  Background:
    Given a repository .jgit
    And a working directory
    And a file named file with the content "Hello World"
    And a folder named folder1
    And a commit named commit1 which is the current commit
    And a commit named commit2

    #Scenario: "commit1" and "commit2" are the same
      When the content of commit2 is "Hello World"
      And the user want to execute the command "merge" with argument "f7fab1387091b5a87aba135590d7498e"
      Then The result of the merge has the content "Hello World"

    #Scenario: "commit1" and "commit2" are the different and commit1 didn't change
      When the content of commit2 is "Hello the World"
      And the user want to execute the command "merge" with argument "f7fab1387091b5a87aba135590d7498e"
      Then The result of the merge has the content "Hello the World"

    #Scenario: "commit1" and "commit2" are the different and commit1 changed
      When the content of commit3 is "Hello the World"
      When the content of commit2 is "Hello Worldss"
      And the user want to execute the command "merge" with argument "1c77bdc8106ef925474eeb9374b5ce18"
      Then The result of the merge has the content "<<<<<<<<<Hello Worldss===========Hello the World>>>>>>>>>"
