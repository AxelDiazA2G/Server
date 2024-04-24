pipeline {
    agent {
        kubernetes {
            // Specifics of the pod template
            label 'kube-agent-tfvrh'
        }
    }
    stages {
        stage('Example') {
            steps {
                echo 'Hello, Kubernetes!'
            }
        }
    }
}
