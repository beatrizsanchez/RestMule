1) add GitHub credentials to the file github-credentials.txt (line by line with user and password separated by comma); if you add 3, then you can run the tests with one credential for one technology

2) build the project, e.g., by issuing the following command in the command line: mvn clean install -DskipTests=true

3) run individual tests as follows (I opened multiple terminals and issued one command to each terminal instance):
mvn -Dtest=GithubRepoSearchRunnerTest#testGMF test
mvn -Dtest=GithubRepoSearchRunnerTest#testSirius test
mvn -Dtest=GithubRepoSearchRunnerTest#testEugenia test

