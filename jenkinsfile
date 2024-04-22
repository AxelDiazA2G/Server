pipeline {
    agent any

    environment {
        // Define environment variables
        DOCKER_IMAGE = 'axelrdiaz/server-app-1:latest'
        REGISTRY_CREDENTIALS_ID = 'docker-hub-credentials'  // ID of your Docker credentials in Jenkins
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
                // Run Gradle build and test
                script {
                    if (isUnix()) {
                        sh './gradlew clean build'
                    } else {
                        bat './gradlew.bat clean build'
                    }
                }
            }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    // Build the Docker image
                    docker.build(env.DOCKER_IMAGE).push()
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    // Update Kubernetes deployment
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
