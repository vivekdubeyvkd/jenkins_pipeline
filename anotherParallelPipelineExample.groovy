def stage1(){
    stage("stage1"){
        println("stage1 " + env.NAME)
    }
}

def stage2(){
   stage("stage2"){
       println("stage2 " + env.NAME)
   }
}

pipeline {  
    agent { label  "nodeLabel" }
    environment {    
        NAME = "vivek"
    }    
    stages {
        stage('start build'){
            steps {
               script{
                    parallel(
                       "stage1": { stage1() },
                       "stage2": { stage2() }
                    )
               }    
            }
         } 
    }
    post { 
        success { 
            timestamps {
                script{ 
                    println "job completed successfully"
                }  
            }   
        }
        failure {
            timestamps {
               step([$class: 'ClaimPublisher'])
            }    
        }
    }     
 }
