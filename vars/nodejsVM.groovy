def call(Map configMap){
    pipeline {
        agent {
            node {
                label 'AGENT-1'
            }
        }
        environment {
            packageVersion = ''
            nexusURL = '172.31.1.18:8081'
        }
        options {
            ansiColor('xterm')
            timeout(time: 1, unit: 'HOURS')
            disableConcurrentBuilds() 
        }
        parameters {
            booleanParam(name: 'Deploy', defaultValue: false, description: 'Toggle this value')
        }
        // build
        stages {
            stage('Get the Version') {
                steps {
                    script {
                        def packageJson = readJSON file: 'package.json'
                        packageVersion = packageJson.version
                        echo "application version is: ${packageVersion}"
                    }
                }
            }
            stage('Install dependencies') {
                steps {
                    script {
                        sh """
                            npm install
                        """
                    }
                }
            }
            stage('Unit Tests') {
                steps {
                    sh """
                        echo "unit tests will run here"
                        echo pwd
                    """
                }
            }
            stage ('Sonar scan') {
                steps {
                    sh """
                        sonar-scanner
                    """
                }
            }
            stage('Build') {
                steps {
                    script {
                        sh """
                            ls -la
                            zip -q -r catalogue.zip ./* -x ".git" -x "*.zip"
                            ls -ltr
                        """
                    }
                }
            }
            stage('Publish Artifact') {
                steps {
                    nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: "${nexusURL}",
                        groupId: 'com.roboshop',
                        version: "${packageVersion}",
                        repository: 'catalogue',
                        credentialsId: 'nexus-auth',
                        artifacts: [
                            [artifactId: 'catalogue',
                            classifier: '',
                            file: 'catalogue.zip',
                            type: 'zip']
                        ]
                    )
                }
            }

            stage('Deploy') {
                when {
                        expression {
                            params.Deploy
                        }
                    }
                steps {
                    script {
                        def params = [
                            string(name: "version", value: "${packageVersion}"),
                            string(name: "environment", value: "dev")
                        ]
                        build job: "catalogue-deploy", wait: true, parameters: params
                    }
                }
            }
        }
        // post build
        post {
            always {
            echo "always run till pipeline runs " 
            deleteDir()
            }
            failure {
                echo "pipeline is failed"
            }
            success {
                echo "pipeline is success"
            }
        }
    }
}