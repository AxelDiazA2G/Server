pipeline {
    stages {
    podTemplate(inheritFrom: 'kube-agent-tfvrh') {
        stage('Example') {
            steps {
                echo 'Hello, Kubernetes!'
            }
        }
       }
    }
}
