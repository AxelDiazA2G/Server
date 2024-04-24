pipeline {

agent none
    stages {

        stage('Example') {
        podTemplate(inheritFrom: 'kube-agent-tfvrh') {
            steps {
                echo 'Hello, Kubernetes!'
            }
        }
       }
    }
}
