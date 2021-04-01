pipeline {
    agent any

    stages {
        stage("1") {
            steps {
                script {
                    try{
                        timeout(time: 15, unit: 'SECONDS') {
                            input(message: 'Proceed with or abort Stage 1 ?')
                        }
                        echo "starting stage 1"
                    }catch (err) {
                        echo "Stopping stage 1 execution, moving with next stage"
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
