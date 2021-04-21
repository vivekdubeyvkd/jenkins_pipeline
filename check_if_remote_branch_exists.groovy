
GITHUB_ORG = "<github org name>"
GITHUB_REPO = "<github repo under above org>"

def check_remote_branch(branch_name){
    dir('repodir'){
         c = ""
         withCredentials([usernamePassword(credentialsId: 'github_token', passwordVariable: 'GITHUBPWD', usernameVariable: 'GITHUBUSER')]) {
            try{
                sh """
                   git clone https://${GITHUBUSER}:${GITHUBPWD}@github.com/${GITHUB_ORG}/${GITHUB_REPO}
                   git remote set-url origin https://${GITHUBUSER}:${GITHUBPWD}@github.com/${GITHUB_ORG}/${GITHUB_REPO}
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

check_remote_branch("vivek")
