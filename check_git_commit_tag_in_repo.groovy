
GIT_COMMIT_OR_TAG = "<a valid GIT commit or Tag>"
local_cloned_repo_dir = 'repodir'

def check_git_commmit() {
    if("${GIT_COMMIT_OR_TAG}") {
        dir(local_cloned_repo_dir) {
            commit_in_source_branch = sh(returnStdout: true, script: "git branch -r --contains ${GIT_COMMIT_OR_TAG} --points-at origin/${SOURCE_BRANCH}").trim()
            if(commit_in_source_branch =~ "origin/${SOURCE_BRANCH}"){
                println('commit_in_source_branch ' + commit_in_source_branch)
            }
            error "Commit ID or Tag ${GIT_COMMIT_OR_TAG} is not in origin/${SOURCE_BRANCH} source branch  " 
        }
    } 
}
