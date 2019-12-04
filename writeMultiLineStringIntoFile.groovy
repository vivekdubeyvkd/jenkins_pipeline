 pipeline{
    agent { label 'node-label' }
    stages{ 
        stage('MultiLine string content into File'){
             steps{
                 script{
                   timestamps{
                        multiLineString = """<string 1>
                                              <string 2>
                        """ 
                        echo "${multiLineString}"
                        multiLineString = multiLineString + """
                           <string 3>
                           <string 4> 
                        """  
                        writeFile file: 'multiLineStringFile.txt', text: multiLineString
                        sh "cat multiLineStringFile.txt"
                    }    
                 }
             }
         }
     }   
}    
