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
                            // Assuming Jenkins is running on Unix/Linux or inside Docker on any platform
                            def commitId = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                            docker.withRegistry('https://registry.hub.docker.com', REGISTRY_CREDENTIALS_ID) {
                                def app = docker.build("${DOCKER_IMAGE}:${commitId}")
                                app.push(commitId)
                                app.push("latest")
                            }
                        }
                    }
                }


        stage('Deploy to Kubernetes') {
                    steps {
                        script {
                            def commitId = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                            sh "kubectl set image deployment/server-app-deployment server-app=${DOCKER_IMAGE}:${commitId} --record"
                            sh "kubectl rollout status deployment/server-app-deployment"
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
