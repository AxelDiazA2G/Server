pipeline {
    agent {
        label 'docker-agent' // Use the label you assigned in the Docker Agent Template
    }

    environment {
        // Define environment variables
        DOCKER_IMAGE = 'server-app-1'
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
                    // Ensure the Docker context is set to the Jenkins workspace root
                    // The Jenkins workspace root should have the build/libs directory with app-1.jar in it
                    def dockerContext = '.' // This sets the context to the current directory
                    try {
                        def commitId = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                        def safeCommitId = commitId.replaceAll(/[^a-zA-Z0-9_.-]/, '_')

                        // Build the Docker image using the specified context
                        sh "docker build -f ${dockerContext}/Dockerfile -t ${DOCKER_IMAGE}:${safeCommitId} ${dockerContext}"
                        sh "docker push ${DOCKER_IMAGE}:${safeCommitId}"
                        sh "docker push ${DOCKER_IMAGE}:latest"
                    } catch (Exception e) {
                        echo "Failed to build or push Docker image: ${e.getMessage()}"
                        error("Stopping the build due to Docker operation failure.")
                    }
                }
            }
        }




        stage('Deploy to Kubernetes') {
            steps {
                script {
                    try {
                        // Extract the commit ID, sanitize it, and use it for deployment
                        def commitId = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                        def safeCommitId = commitId.replaceAll(/[^a-zA-Z0-9_.-]/, '_')

                        // Set the Docker image in the Kubernetes deployment
                        sh "kubectl set image deployment/server-app-deployment server-app=${DOCKER_IMAGE}:${safeCommitId} --record"

                        // Check the rollout status to ensure successful deployment
                        sh "kubectl rollout status deployment/server-app-deployment"
                    } catch (Exception e) {
                        // Log the error and fail the build if there is an issue with the Kubernetes commands
                        echo "Deployment failed: ${e.getMessage()}"
                        error("Stopping the build due to a failure in deploying to Kubernetes.")
                    }
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
