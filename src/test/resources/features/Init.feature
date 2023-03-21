Feature: jgit init command

	Background:
		Given a specific working directory
        And a new specific file named ".jgit" with content "Pranked!"
    
    Scenario: jgit init when there is no namesake
        When the user want to execute the command "init"
        Then a new jgit repository is created

    Scenario: jgit init when there is a namesake and argument
        When the user want to execute the command "init" with argument "init_tmp_files"
        Then no new jgit repository is created