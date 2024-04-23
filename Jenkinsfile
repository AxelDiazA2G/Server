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
                script {
                    sh 'chmod +x ./gradlew'  // Ensure the script is executable
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
                    docker.withRegistry('https://index.docker.io/v1/', REGISTRY_CREDENTIALS_ID) {
                        // Build the Docker image with a specific tag based on the BUILD_ID
                        def app = docker.build("${DOCKER_IMAGE}-${env.BUILD_ID}")
                        // Push the specific build tag
                        app.push("${env.BUILD_ID}")
                        // Also push the 'latest' tag
                        app.push("latest")
                    }
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
