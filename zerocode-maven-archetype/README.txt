To install archetype locally:
    Navigate to zerocode-maven-archetype on your machine. Run "mvn install" from this directory
    
To generate anew archetype-based project:
    Navigate to the directory that will house the project. Run 
    "mvn archetype:generate 
    -DarchetypeGroupId=zerocode.archetype 
    -DarchetypeArtifactId=zerocodeArchetype 
    -DarchetypeVersion=1.0-SNAPSHOT 
    -DgroupId=com.xbox
    -DartifactId=game-app"
    
    The generic command format is:
    "mvn archetype:generate -DarchetypeGroupId=<custom-archetype group id e.g.>
    -DarchetypeArtifactId=<custom-archetype artifactid>
    -DarchetypeVersion=<custom-archetype version>
    -DgroupId=<new project Group Id>
    -DartifactId=<new project artifact Id>"
    
Add personal GitHub token to test files:
    In both post_api_200.json and put_api_200.json, substitute your own GitHub token for the placeholder.
    In put_api_200.json, also update the name of the owner in the URL to your GitHub username
