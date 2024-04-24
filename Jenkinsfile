pipeline {
    agent {
        label 'kube-agent' // Use the label you assigned in the Docker Agent Template
    }

    environment {
        // Define environment variables
        DOCKER_IMAGE = 'axelrdiaz/server-app-1'
        REGISTRY_CREDENTIALS_ID = 'docker-hub-credentials'  // ID of your Docker credentials in Jenkins
    }

    stages {
            stage('Deploy to Kubernetes') {
                steps {
                    script {
                        try {
                        sh 'kubectl config current-context'
                            // Apply the Kubernetes configurations
                            sh 'kubectl apply -f deployment.yaml'
                            sh 'kubectl apply -f service.yaml'

                            // Check the rollout status to ensure successful deployment
                            sh 'kubectl rollout status deployment/server-app-deployment'
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
