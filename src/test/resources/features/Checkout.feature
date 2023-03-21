Feature: Checkout a branch
  Background:
    Given a working directory
    And a repository .jgit
    And a new file named "test.txt" with content "Text at the first commit."
    And the user execute the command "add" with argument "test.txt"
    And the user execute the command "commit" with argument "the first commit"
    And add "A new text from second commit." at end of the file "test.txt"
    And the user execute the command "add" with argument "test.txt"
    And the user execute the command "commit" with argument "the second commit"


  Scenario: Checkout a branch that does not exist.
    When the user execute the command "checkout" with argument "dxhrb_invalid_hash_kklfjd"
    Then the current branch is not "dxhrb_invalid_hash_kklfjd"
    And we reset the working directory

  Scenario: Checkout a branch using branch name
    Given the user execute the command "branch" with argument "dev"
    And the user execute the command "branch" with argument "prod"
    And the user execute the command "checkout" with argument "dev"
    And add "The new content on branch dev" at end of the file "test.txt"
    And the user execute the command "add" with argument "test.txt"
    And the user execute the command "commit" with argument "the dev commit"
    And the user execute the command "checkout" with argument "prod"
    And add "The new content on branch prod" at end of the file "test.txt"
    And the user execute the command "add" with argument "test.txt"
    And the user execute the command "commit" with argument "the prod commit"
    When the user execute the command "checkout" with argument "dev"
    Then the content of file "test.txt" would be "Text at the first commit.A new text from second commit.The new content on branch dev"
    And the current branch is "dev"
    When the user execute the command "checkout" with argument "prod"
    Then the content of file "test.txt" would be "Text at the first commit.A new text from second commit.The new content on branch prod"
    And the current branch is "prod"
    And we reset the working directory
