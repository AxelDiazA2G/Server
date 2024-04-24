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
                    try {
                        // Assuming Jenkins checks out the repository to the current directory
                        // and your project is in the root of the repository:

                        // Optionally print out files in the expected directory for debugging
                        sh 'ls -l build/libs/'

                        // Build the Docker image
                        def commitId = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                        def safeCommitId = commitId.replaceAll(/[^a-zA-Z0-9_.-]/, '_')

                        // Make sure to include the path to the Dockerfile if it's not in the root
                        def dockerfile = 'Dockerfile' // or 'path/to/Dockerfile' if it's in a subdirectory

                        // Run the Docker build command, with the build context set to the project root
                        sh "docker build -f ${dockerfile} -t ${DOCKER_IMAGE}:${safeCommitId} ."
                        sh "docker push ${DOCKER_IMAGE}:${safeCommitId}"
                        sh "docker push ${DOCKER_IMAGE}:latest"

                    } catch (Exception e) {
                        // If something goes wrong, print the error and fail the build
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
