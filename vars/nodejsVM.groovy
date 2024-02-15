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