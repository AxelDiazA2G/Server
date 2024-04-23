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
                    if (isUnix()) {
docker.withRegistry('https://index.docker.io/v1/', REGISTRY_CREDENTIALS_ID) {
                // Build the Docker image with a specific tag based on the BUILD_ID
                def app = docker.build("${DOCKER_IMAGE}-${env.BUILD_ID}")
                // Push the specific build tag
                app.push("${env.BUILD_ID}")
                // Also push the 'latest' tag
                app.push("latest")
                    }else{
                    // Set Docker commands to be executed using batch script in Windows
                                        bat "docker withRegistry('https://index.docker.io/v1/', REGISTRY_CREDENTIALS_ID) {"
                                        // Build the Docker image with a specific tag based on the BUILD_ID
                                                    bat "docker build -t ${env.DOCKER_IMAGE}-${env.BUILD_ID} ."
                                                    // Push the specific build tag
                                                    bat "docker push ${env.DOCKER_IMAGE}-${env.BUILD_ID}"
                                                    // Also push the 'latest' tag
                                                    bat "docker push ${env.DOCKER_IMAGE}:latest"
                                                    // Log out from Docker registry
                                                    bat "docker logout index.docker.io"
                    }

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
