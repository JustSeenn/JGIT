Feature: Checkout a branch
  Background:
    Given a working directory
    And a new file named "test.txt" with content "JE SUIS ..."

  Scenario: The user wants to checkout a branch that does not exist.
    When the user want to execute the command "checkout" with argument "dxhrb_invalid_hash_kklfjd"
    Then the result is
