
local_cloned_repo_dir = 'repodir'

def check_git_commmit(git_branch, git_commit_tag) {
    if(git_commit_tag && git_branch) {
        dir(local_cloned_repo_dir) {
            commit_in_github_branch = sh(returnStdout: true, script: "git branch -r --contains ${git_commit_tag} --points-at origin/${git_branch}").trim()
            if(commit_in_github_branch =~ "origin/${git_branch}"){
                println('commit_in_source_branch ' + git_commit_tag)
                return true
            }
            error "Commit ID or Tag ${git_commit_tag} is not in origin/${git_branch} source branch  " 
        }
    } 
}

check_git_commmit("< a valid git_branch>", "<a valid GIT commit or Tag>")
