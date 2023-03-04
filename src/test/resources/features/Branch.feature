Feature: git branch command
  Background:
    Given a working directory
    And a repository .jgit

  Scenario: Create branch without initial state (initial commit)
    When the user want to execute the command "branch" with argument "new_branch"
    Then the result is "fatal: Not a valid object in 'HEAD'"


  # Does not work because of the bug in the commit command
  Scenario: Branch already exists
    Given the user want to execute the command "commit" with argument "My first commit"
    When the user want to execute the command "branch" with argument "new_branch"
    And the user want to execute the command "branch" with argument "new_branch"
    Then the result is "fatal: A branch named 'new_branch' already exists."

  # Does not work because of the bug in the commit command
  Scenario: Create new branch
    Given the user want to execute the command "commit" with argument "My first commit"
    When the user want to execute the command "branch" with argument "dev"
    Then the result is "Switched to a new branch 'dev'"