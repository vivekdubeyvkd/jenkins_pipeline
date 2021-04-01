
pipeline {
    agent any
    stages {
        stage("1") {
            options {
                timeout(time: 1, unit: "MINUTES")
            }
            steps {
                script {
                    def move_ahead = true
                    try {
                        timeout(time: 15, unit: 'SECONDS') {
                            input(message: 'Proceed with Stage 1 ?')
                        }
                    } catch (err) {
                        move_ahead = false
                        echo "Not proceeding with Stage 1"
                    }
                    if(move_ahead) {
                        echo "Starting stage 1"
                    }
                }
            }
        }

        stage("2") {
            steps {
                echo "Starting stage 2"
            }
        }
    }
}
