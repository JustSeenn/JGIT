Feature: Checkout a branch
  Background:
    Given a text file named textFile with the content "First content"
    And a folder named folder with textFile as new child
    And a commit named commit1 with folder as his state
    And a text file named textFile2 with the content "File 2 content"
    And a folder named folder with textFile2 as new child
    And a commit named commit2 with folder as his state

  Scenario: Move to a branch using commit hash
    When checkout a commit 1
    Then the folder contain one file
    When checkout a commit 2
    Then  the folder contain two files
    When checkout a commit 1
    Then the folder contain one file

  Scenario: Move to a branch that does not exist
    When the user want to checkout a branch named "dev"
    Then the result of the checkout
