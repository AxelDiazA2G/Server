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
                        // Attempt to build and push the Docker image
                        def commitId = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                        def safeCommitId = commitId.replaceAll(/[^a-zA-Z0-9_.-]/, '_')
                        docker.withRegistry('https://registry.hub.docker.com', REGISTRY_CREDENTIALS_ID) {
                            def app = docker.build("${DOCKER_IMAGE}:${safeCommitId}")
                            app.push(safeCommitId)
                            app.push("latest")
                        }
                    } catch (Exception e) {
                        // Handle errors related to Docker operations
                        echo "Docker Image: ${env.DOCKER_IMAGE}"
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
