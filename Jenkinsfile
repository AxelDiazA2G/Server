pipeline {
    agent {
        label 'docker-agent' // Use the label you assigned in the Docker Agent Template
    }

    environment {
        // Define environment variables
        DOCKER_IMAGE = 'axelrdiaz/server-app-1:latest'
        REGISTRY_CREDENTIALS_ID = 'docker-hub-credentials'  // ID of your Docker credentials in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                // Checks out the source code
                script {
                    try {
                        checkout scm
                    } catch (Exception e) {
                        echo "Error checking out code: ${e.getMessage()}"
                        error("Failed to checkout code.")
                    }
                }
            }
        }

        stage('Build and Test') {
            steps {
                script {
                    try {
                        if (isUnix()) {
                            sh 'chmod +x ./gradlew'
                            sh './gradlew clean build'
                        } else {
                            bat 'attrib +x ./gradlew.bat'
                            bat './gradlew.bat clean build'
                        }
                    } catch (Exception e) {
                        echo "Error during build/test stage: ${e.getMessage()}"
                        error("Build or test failure.")
                    }
                }
            }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    try {
                        // Assuming Jenkins is running on Unix/Linux or inside Docker on any platform
                        def commitId = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                        docker.withRegistry('https://registry.hub.docker.com', REGISTRY_CREDENTIALS_ID) {
                            def app = docker.build("${DOCKER_IMAGE}:${commitId}")
                            app.push(commitId)
                            app.push("latest")
                        }
                    } catch (Exception e) {
                        echo "Error during Docker build/push stage: ${e.getMessage()}"
                        error("Docker build or push failure.")
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    try {
                        def commitId = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                        sh "kubectl set image deployment/server-app-deployment server-app=${DOCKER_IMAGE}:${commitId} --record"
                        sh "kubectl rollout status deployment/server-app-deployment"
                    } catch (Exception e) {
                        echo "Error during Kubernetes deployment: ${e.getMessage()}"
                        error("Deployment to Kubernetes failed.")
                    }
                }
            }
        }
    }

    post {
        always {
            try {
                // Clean up workspace
                cleanWs()
            } catch (Exception e) {
                echo "Error during cleanup: ${e.getMessage()}"
            }
        }
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
