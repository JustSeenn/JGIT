Feature: git branch command
  Background:
    Given a working directory
    And a repository .jgit
    And a new file named "test.txt" with content "Content on branch first_time"

  Scenario: Create branch without initial state (initial commit)
    When the user execute the command "branch" with argument "new_branch"
    Then the branch "new_branch" does not created
    And we reset the working directory


  Scenario: Branch already exists
    Given the user execute the command "add" with argument "test.txt"
    And the user execute the command "commit" with argument "My first commit"
    When the user execute the command "branch" with argument "first_time"
    And add "A new text." at end of the file "test.txt"
    And the user execute the command "commit" with argument "My first commit"
    And the user execute the command "branch" with argument "first_time"
    And the user execute the command "checkout" with argument "first_time"
    Then the content of file "test.txt" would be "Content on branch first_time"
    And we reset the working directory

  Scenario: Create new branch
    Given the user execute the command "add" with argument "test.txt"
    And the user execute the command "commit" with argument "My first commit"
    When the user execute the command "branch" with argument "dev"
    Then the branch "dev" is create
    And we reset the working directory