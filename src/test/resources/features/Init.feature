Feature: jgit init command

	Background:
		Given a working repository
        And a new file ".jgit" with content "Pranked!"
    
    Scenario: jgit init when there is no namesake
        When the user wants to execute the command "init"
        Then a new jgit repository is created

    Scenario: jgit init when there is a namesake
        When the user wants to execute the command "init"
        Then no new jgit repository is created