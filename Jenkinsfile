pipeline {
    agent {
        kubernetes {
            // Specifics of the pod template
            label 'kube-agent'
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
