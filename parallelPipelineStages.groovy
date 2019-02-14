pipeline {  
    agent { label  '<node-label>' }
    stages {
        stage('stage start') {
            steps {
                timestamps {
                    script{ 
                        try{
                           // do something
                        }catch(e){
                            println e
                            currentBuild.result = 'FAILURE'
                        }
                        if(currentBuild.result == 'FAILURE'){
                            error "[ERROR] : in stage"
                        }
                    }
                }      
            }  
        }
        stage("stage -5"){
            steps {
                timestamps {
                  script{  
                    try{  
                        // do something
                    }catch(e){
                        println e
                        currentBuild.result = 'FAILURE'
                    }  
                    if(currentBuild.result == 'FAILURE'){
                        error "[ERROR] : in stage "
                    }
                  } 
                }  
            }
        }         
        stage("stage -4"){
            steps {
                timestamps {
                  script{  
                    try{  
                        // do something
                    }catch(e){
                        println e
                        currentBuild.result = 'FAILURE'
                    }  
                    if(currentBuild.result == 'FAILURE'){
                        error "[ERROR] : in stage "
                    }
                  } 
                }  
            }
        } 
        stage("stage -3"){
            steps {
                timestamps {
                  script{  
                    try{  
                        // do something
                    }catch(e){
                        println e
                        currentBuild.result = 'FAILURE'
                    }
                    if(currentBuild.result == 'FAILURE'){
                        error "[ERROR] : in stage "
                    }
                  } 
                }  
            }
        } 
        stage("stage -2"){
            steps {
                timestamps {
                  script{  
                    try{  
                        // do something
                    }catch(e){
                        println e
                        currentBuild.result = 'FAILURE'
                    }
                    if(currentBuild.result == 'FAILURE'){
                        error "[ERROR] : in stage "
                    } 
                  } 
                }  
            }
        }  
        stage("Parallel Flow : I"){
            //failFast true
            parallel{
                stage('stage -1') {
                     steps {
                         timestamps {
                            script{ 
                                try{
                                   // do soemthing
                                }catch(e){
                                    println e
                                    currentBuild.result = 'FAILURE'
                                }
                                if(currentBuild.result == 'FAILURE'){
                                    error "[ERROR] : in stage "
                                }                                
                            }
                         }      
                     }  
                }
                stage("stage 0") {
                     steps {
                         timestamps {
                            script{ 
                                try{    
                                   // do somehthing
                                }catch(e){
                                    println e
                                    currentBuild.result = 'FAILURE'
                                }
                                if(currentBuild.result == 'FAILURE'){
                                    error "[ERROR] : in stage "
                                }                                 
                            }
                         }      
                     }  
                } 
                stage("stage 1") {
                     steps {
                         timestamps {
                            script{ 
                                try{
                                    // do something
                                }catch(e){
                                    println e
                                    currentBuild.result = 'FAILURE'
                                }
                                if(currentBuild.result == 'FAILURE'){
                                     error "[ERROR] : in stage "
                                }  
                            }
                         }      
                     }  
                }                  
                stage("stage 2") {
                     steps {
                         timestamps {
                            script{ 
                                try{
                                    // do something
                                }catch(e){
                                    println e
                                    currentBuild.result = 'FAILURE'
                                }
                                if(currentBuild.result == 'FAILURE'){
                                    error "[ERROR] : in stage "
                                } 
                            }
                         }      
                     }  
                }  
            }
        } 
        stage("Parallel Flow : II"){
            parallel{
                stage("stage 3") {
                     steps {
                         timestamps {
                            script{ 
                                try{
                                    // do something
                                }catch(e){
                                    println e
                                    currentBuild.result = 'FAILURE'
                                }
                                if(currentBuild.result == 'FAILURE'){
                                    error "[ERROR] : in stage "
                                } 
                            }
                         }      
                     }  
                }     
                stage("stage 4") {
                     steps {
                         timestamps {
                            script{ 
                                try{
                                    // do something 
                                }catch(e){
                                    println e
                                    currentBuild.result = 'FAILURE'
                                }
                                if(currentBuild.result == 'FAILURE'){
                                    error "[ERROR] : in stage "
                                } 
                            }
                         }      
                     }  
                } 
            }    
        }        
        stage("stage 5") {
             steps {
                 timestamps {
                    script{ 
                        try{
                            //do something
                        }catch(e){
                            println e
                            currentBuild.result = 'FAILURE'
                        }
                        if(currentBuild.result == 'FAILURE'){
                            error "[ERROR] : in stage"
                        }                                  
                    }
                 }      
             }  
        }
    } 
    post { 
        success { 
            timestamps {
                script{ 
                    println "Build succeeded"
                }  
            }   
        }
        failure {
            step([$class: 'ClaimPublisher']) 
            timestamps {
               script{ 
                   println "build failed"
               }    
            }    
        }
    }     
 }
