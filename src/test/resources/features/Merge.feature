Feature: Merge two commit together

  Background:
    Given a file named file with the content "Hello World\n how are you today ?\n"
    And a folder named folder1
    And a commit named commit1
    And a commit named commit2

    Scenario: the content of "f1" and "f2" are the same
      When f1 wants to be merge with f2 and f1 and f2 have the same content
      Then the result of the merge has the content "Hello World\n how are you today ?\n"

    Scenario: "f1" modify the file and "f2" don't
      When f1 wants to be merge with f2  and f1 modified the file by "Hello the World\n how are you today ?\n"
      Then the result of the merge is a conflict with the content "Hello World\n how are you today ?\n ======= Hello the World\n how are you today ?\n <<<<<<< HEAD"

    Scenario: "f1" deleted the first line and "f2" didn't
      When f1 wants to be merge with f2 and f1 delete the first line
        Then the result of the merge has the content "Hello World\n how are you today ?\n"

  Scenario: "f1" and "f2" modify the same line
      When f1 wants to be merge with f2  and f1 modified the file by "Hello the World\n how are you today ?\n" and f2 modified the file by "Hello this World\n how are you today ?\n"
        Then the result of the merge is a conflict with the content "Hello World\n how are you today ?\n ======= Hello this World\n how are you today ?\n <<<<<<< HEAD"
