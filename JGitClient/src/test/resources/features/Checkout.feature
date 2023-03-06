Feature: Checkout a branch
  Background:
    Given a working directory
    And a repository .jgit
    And a new file named "test.txt" with content "JE SUIS ..."
    And a new file named "test2.txt" with content "JE SUIS LE CONTENU DU FICHIER 2"
    And the user want to execute the command "add" with argument "test.txt"
    And the user want to execute the command "commit" with argument "the first commit"
    And the user want to execute the command "branch" with argument "dev"
    And add "euh... David" at end of the file "test.txt"
    And the user want to execute the command "add" with argument "test2.txt"
    And the user want to execute the command "commit" with argument "the second commit"
    And the user want to execute the command "branch" with argument "prod"


  Scenario: The user wants to checkout a branch that does not exist.
    When the user want to execute the command "checkout" with argument "dxhrb_invalid_hash_kklfjd"
    Then the result is "Branch dxhrb_invalid_hash_kklfjd does not exist"
    And the current branch is not "dxhrb_invalid_hash_kklfjd"

