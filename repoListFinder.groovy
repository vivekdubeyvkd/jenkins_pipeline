import groovy.json.JsonSlurperClassic 
import com.cloudbees.groovy.cps.NonCPS


@NonCPS
def jsonParse(def json) {
    new groovy.json.JsonSlurperClassic().parseText(json)
}

def get_project_url(projects){
    for(int index=0; index < projects.size() ; index++ ){
        ssh_url_to_repo = projects[index]["ssh_url_to_repo"]
        name =  projects[index]["name"]
        if(!(projects[index]["archived"])){
            if(projects[index]["namespace"]["kind"] == "group"){
              project_id = projects[index]["id"]    
              println(projects[index]["namespace"]["kind"] + " , "+ ssh_url_to_repo + "," + projects[index]["archived"])
              sh "echo ${name},${ssh_url_to_repo},${project_id} >> repolist"
            }
        }
    }
}

def get_projects_list(){
  stage("Archive Repo Name and URL"){ 
    withCredentials([string(credentialsId: '<cred token>', variable: 'TOKEN')]) {
        for(def index=0;index < 30 ; index++){    
            projectDetails=sh returnStdout: true, script: "curl -s -k --header \"PRIVATE-TOKEN: ${TOKEN}\" ${ORIGIN_GITLAB_API_URL}/projects?per_page=100\\&page=${index}"    
            projectDetailsJson=jsonParse(projectDetails)
            if(projectDetailsJson["id"]){
                get_project_url(projectDetailsJson)
            }else{
                return
            }
        }
     }    
  }     
}

pipeline {
   agent {
        label "<nodeLabel>"
    }
   environment {
      ORIGIN_GITLAB_API_URL = "<gitlabUrl>/api/v4"
    }
    stages {
        stage("Get Project List") {
           steps {
                script {
                    sh '''
                        if [ -f repolist ]
                        then
                           rm -rf repolist
                        fi   
                        '''   
                    get_projects_list()
                    archiveArtifacts 'repolist'
                }
            }
        } 
    }
}
