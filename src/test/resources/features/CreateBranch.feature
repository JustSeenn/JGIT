Feature: Create a new branch

  Scenario: Branch does not exists
    When the user want to create a new branch named dev
    Then the branch is created

  Scenario: Branch already exists
    When the user want to create a new branch named dev
    And the user want to create a branch named dev again
    Then the branch the branch named dev does not create again