GIT_API_URL='https://api.github.com/api/v3'
GIT_ORG = "my_org"
GIT_REPO_NAME = "my_repo"

tag_name = "my_tag"

def validate_remote_tag(){
    //define a credential on Jenkins username & token named as jenkin_github_token
    withCredentials([usernamePassword(credentialsId: 'jenkin_github_token', passwordVariable: 'GPWD', usernameVariable: 'GUSER')]) {
        tag_json = sh(returnStdout: true, script: "curl -k -u \"${GUSER}:${GPWD}\" \"${GIT_API_URL}/repos/${GIT_ORG}/${GIT_REPO_NAME}/git/ref/tags/${tag_name}\"")
        tag_json_value = readJSON file: '', text: tag_json
        echo """
            tag: ${tag_json_value}
        """
        if(tag_json_value["ref"]){
            println("TAG ${tag_name} already exists")
        }else{
            println("TAG ${tag_name} does not exist")
        } 
    }
} 

validate_remote_tag(tag_name)
