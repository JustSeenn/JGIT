Feature: Merge two commit together

  Background:
    Given a repository .jgit
    And a file named file with the content "Hello World\n how are you today ?\n"
    And a folder named folder1
    And a commit named commit1 which is the current commit
    And a commit named commit2

    Scenario: "commit1" and "commit2" are the same
      When the content of commit2 is "Hello World\n how are you today ?\n"
      And  I do the command git merge "53945e5bd1cd0253cd61e084733d557a"
      Then The result of the merge has the content "Hello World\n how are you today ?\n"
