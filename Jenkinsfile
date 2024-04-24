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
                            // Get the cluster server URL and user from the Kubernetes configuration
                                            def serverUrl = 'https://kubernetes.docker.internal:6443'
                                            def user = 'docker-desktop' // Use the user specified in your Kubernetes config
                                            sh "kubectl config view"
                                            // Apply the Kubernetes configurations with the specified user
                                            sh "kubectl apply -f deployment.yaml --server=${serverUrl} --user=${user}"
                                            sh "kubectl apply -f service.yaml --server=${serverUrl} --user=${user}"

                                            // Check the rollout status to ensure successful deployment
                                            sh "kubectl rollout status deployment/server-app-deployment --server=${serverUrl} --user=${user}"
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
