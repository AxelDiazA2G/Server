pipeline {
    agent {
        label 'docker-agent' // Use the label you assigned in the Docker Agent Template
    }

    environment {
        // Define environment variables
        DOCKER_IMAGE = 'axelrdiaz/server-app-1'
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
                    def dockerContext = '.' // This sets the context to the current directory
                    try {
                        withDockerRegistry(credentialsId: "docker-hub-credentials", url: "") {
                            def commitId = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                            def safeCommitId = commitId.replaceAll(/[^a-zA-Z0-9_.-]/, '_')

                            // Build the Docker image using the specified context
                            sh "docker build -f ${dockerContext}/Dockerfile -t ${DOCKER_IMAGE}:latest ${dockerContext}"
                            sh "docker push ${DOCKER_IMAGE}:latest"
                        } // Close withDockerRegistry
                    } catch (Exception e) {
                        echo "Failed to build or push Docker image: ${e.getMessage()}"
                        error("Stopping the build due to Docker operation failure.")
                    }
                } // Close script
            } // Close steps
        } // Close stage

        stage('Deploy to Kubernetes') {
            agent {
                kubernetes {
                    label 'kube-agent'
                    yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: kubectl-container
    image: axelrdiaz/kubeagent
    env:
      - name: JENKINS_URL
        value: "http://jenkins:8080"  // Ensure this URL is accessible from within the pod
    command:
    - sh
    args:
    - -c
    - "while sleep 3600; do :; done"  // Keep the container running for Jenkins commands
    tty: true
"""
                } // Close kubernetes
            } // Close agent
            steps {
                script {
                    def dockerContext = '.' // Sets the context to the current directory
                    try {
                        // Explicitly using Jenkins URL in commands if necessary
                        env.JENKINS_URL = "http://jenkins:8080" // Override JENKINS_URL for this block
                        sh 'kubectl apply -f ${dockerContext}/deployment.yaml'
                        sh 'kubectl apply -f ${dockerContext}/service.yaml'

                        // Extract the commit ID, sanitize it, and use it for deployment
                        def commitId = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                        def safeCommitId = commitId.replaceAll(/[^a-zA-Z0-9_.-]/, '_')

                        // Set the Docker image in the Kubernetes deployment
                        sh "kubectl set image deployment/server-app-deployment server-app=${DOCKER_IMAGE}:latest --record"

                        // Check the rollout status to ensure successful deployment
                        sh "kubectl rollout status deployment/server-app-deployment"
                    } catch (Exception e) {
                        echo "Deployment failed: ${e.getMessage()}"
                        error("Stopping the build due to a failure in deploying to Kubernetes.")
                    }
                } // Close script
            } // Close steps
        } // Close stage
    } // Close stages

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
    } // Close post
} // Close pipeline
