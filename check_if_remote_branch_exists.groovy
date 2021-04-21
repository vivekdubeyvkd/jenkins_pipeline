
GITHUB_ORG = "<github org name>"
GITHUB_REPO = "<github repo under above org>"

def check_if_remote_git_branch(branch_name){
    withCredentials([usernamePassword(credentialsId: 'github_token', passwordVariable: 'GITHUBPWD', usernameVariable: 'GITHUBUSER')]) {
        GIT_REPO_URL = "https://${GITHUBUSER}:${GITHUBPWD}@github.com/${GITHUB_ORG}/${GITHUB_REPO}" 
        sh """
            git clone ${GIT_REPO_URL} repodir
        """
        dir('repodir'){
            remote_branches = ""
            try{
               sh """
                   git remote set-url origin ${GIT_REPO_URL}
                """
                remote_branches = sh(returnStdout: true, script: "git ls-remote --heads origin ${branch_name}")
            } catch(err){
                error "Remote branch ${branch_name} doesn't exists on GitHub origin"
            }
            
            echo """
                    Remote branch : ${remote_branches}
            """
        }
     }
}

check_if_remote_git_branch("<remote branch name>")
