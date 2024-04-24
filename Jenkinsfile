pipeline {
    agent none // Specifies that no global agent should be used
    stages {
        stage('Example') {
            // Define the agent within the stage
            agent {
                kubernetes {
                    // Inherit from a predefined pod template
                    inheritFrom 'kube-agent-tfvrh'
                }
            }
            steps {
                // Your build steps go here
                echo 'Hello, Kubernetes!'
            }
        }
    }
}
