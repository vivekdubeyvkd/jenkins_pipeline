pipeline {  
    agent { label  '<node-label>&&!<node-name>' }
    environment {
       
    }
    stages {
        stage('stage I') {
            steps {
                timestamps {
                    script{ 
                         // methods
                    }  
                }  
            }
        }        
    }
    post { 
        success { 
            timestamps {
                script{ 
                    print "success"
                    if (currentBuild.previousBuild != null && currentBuild.previousBuild.result != 'SUCCESS') {
                        print "build succeeded after a failed build"
                    }           
                }  
            }   
        }
        failure {
            timestamps {
               step([$class: 'ClaimPublisher'])
               script{ 
                   print 'failed'
               }    
            }    
        }
        aborted {
            timestamps {
               step([$class: 'ClaimPublisher'])
               script{ 
                  print "build aborted"
               }    
            }    
        }         
    }     
 }
