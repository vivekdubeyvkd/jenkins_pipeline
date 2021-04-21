
GIT_COMMIT_OR_TAG = "<a valid GIT commit or Tag>"
local_cloned_repo_dir = 'repodir'
GITHUB_BRANCH = "<Gi

def check_git_commmit() {
    if("${GIT_COMMIT_OR_TAG}") {
        dir(local_cloned_repo_dir) {
            commit_in_github_branch = sh(returnStdout: true, script: "git branch -r --contains ${GIT_COMMIT_OR_TAG} --points-at origin/${GITHUB_BRANCH}").trim()
            if(commit_in_github_branch =~ "origin/${GITHUB_BRANCH}"){
                println('commit_in_source_branch ' + commit_in_github_branch)
                return true
            }
            error "Commit ID or Tag ${GIT_COMMIT_OR_TAG} is not in origin/${GITHUB_BRANCH} source branch  " 
        }
    } 
}
