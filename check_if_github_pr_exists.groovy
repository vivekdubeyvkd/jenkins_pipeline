
// Please check and ensure that my_github_token is configured on your Jenkins with GitHub PAT token used with withCredentials

def check_if_github_pr_exists(githuOrgAndRepo, fromSourceBranch, toTargetBranch){
    // if my_github_token is not there on your jenkins, then use the correct GitHub credentialsId as per your jenkins setup
    withCredentials([usernamePassword(credentialsId: 'my_github_token', passwordVariable: 'GPWD', usernameVariable: 'GUSER')]) {
        sh """
             curl -u "$GUSER:$GPWD"  "https://api.github.com/api/v3/search/issues?page=1\\&q=repo%3A${githuOrgAndRepo}+is%3Apr+is%3Aopen+base%3A${toTargetBranch}+head%3A${fromSourceBranch}" > open_pr.json
        """
    }
    if(fileExists("open_pr.json")){
        prJson = readJSON file: "open_pr.json", text: ''
        if(prJson["items"]){
            //just returning the first PR from source to target branch, there might be more open PRs and to get all such PRs, just loop over prJson["items"][0] array
            return prJson["items"][0]["html_url"]
        }else{
            return 
        }  
    }
    return
}

// you can this function as shown below
IF_PR_EXISTS_FLAG = check_if_github_pr_exists("vivekdubeyvkd/jenkins_pipeline", "test", "master")
if(IF_PR_EXISTS_FLAG){
    echo """
         Existing PR is ${IF_PR_EXISTS_FLAG}
    """
} else {
    echo """
         There is no existing open PR 
    """
}  
