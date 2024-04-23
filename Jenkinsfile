pipeline {
    agent any

    environment {
        // Define environment variables
        DOCKER_IMAGE = 'axelrdiaz/server-app-1:latest'
        REGISTRY_CREDENTIALS_ID = 'docker-hub-credentials'  // ID of your Docker credentials in Jenkins
        // Update the PATH environment variable to include the Docker executable path
        PATH = "C:\\Program Files\\Docker\\Docker\\resources\\bin;${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                // Checks out the source code
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                script {
                    // Ensure the gradle wrapper script is executable
                    if (isUnix()) {
                        sh 'chmod +x ./gradlew'
                        sh './gradlew clean build'
                    } else {
                        bat 'attrib +x ./gradlew.bat'
                        bat './gradlew.bat clean build'
                    }
                }
            }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    // Set Docker commands to be executed using batch script in Windows
                    bat "docker withRegistry('https://index.docker.io/v1/', REGISTRY_CREDENTIALS_ID) {"
                    bat "def app = docker.build(\"${DOCKER_IMAGE}-${env.BUILD_ID}\")"
                    bat "app.push(\"${env.BUILD_ID}\")"
                    bat "app.push(\"latest\")"
                    bat "}"
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    // Update Kubernetes deployment using shell script
                    sh "kubectl set image deployment/server-app-deployment server-app=${env.DOCKER_IMAGE} --record"
                }
            }
        }
    }

    post {
        always {
            // Clean up workspace
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
