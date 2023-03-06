Feature: git branch command
  Background:
    Given a working directory
    And a repository .jgit

  Scenario: Create branch without initial state (initial commit)
    When the user want to execute the command "branch" with argument "new_branch"
    Then the result is "fatal: Not a valid object in 'HEAD'"
    And the branch "new_branch" does not created


  Scenario: Branch already exists
    Given the user want to execute the command "add" with argument "."
    And the user want to execute the command "commit" with argument "My first commit"
    When the user want to execute the command "branch" with argument "first_time"
    And the user want to execute the command "branch" with argument "first_time"
    Then the result is "fatal: A branch named 'new_branch' already exists."

  Scenario: Create new branch
    Given the user want to execute the command "add" with argument "."
    And the user want to execute the command "commit" with argument "My first commit"
    When the user want to execute the command "branch" with argument "dev"
    Then the result is "Switched to a new branch 'dev'"
    And the branch "dev" is create