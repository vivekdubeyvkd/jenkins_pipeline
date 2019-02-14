def GITLAB_API_URL = '<gitlab-url>/api/v4'
def callGitlabRestApiToGetSSHUrl(repoName){
    withCredentials([string(credentialsId: '<gitlab-token-id>', variable: 'GITLAB_API_TOKEN')]) {
       projectDetails = sh returnStdout: true, script: "curl -s -f --header \"PRIVATE-TOKEN: ${GITLAB_API_TOKEN}\" ${GITLAB_API_URL}/projects?search=${repoName}\\&per_page=100\\&page=1" 
       projectDetailsJson = readJSON text: projectDetails
       if(projectDetailsJson["id"]){
            for(int index=0; index < projectDetailsJson.size(); index++ ){
                if(projectDetailsJson[index]["name"] == repoName && projectDetailsJson[index]["namespace"]["kind"] == "group"){
                   return projectDetailsJson[index]["ssh_url_to_repo"].trim()
                }
            }
       }else{
           error "[Error] no repo named " + repoName + " found on GitLab !!!!!"
       }
     }     
}
