
def getSourceCommitHead(clonedRepoDir){
    dir(clonedRepoDir){
         try{
              source_head_commit = sh(returnStdout: true, script: "git log --oneline | head -1 | awk '{print \$1}'").trim()
              if(source_head_commit){
                  return source_head_commit
              }else{
                  return
              } 
         }catch(err){
             error "error in computing source commit head"
         } 
    } 
}

// myrepodir is directory where I have cloned my github repo, you should pass your repo directory while using it
// call this function as shown below
my_commit_head = getSourceCommitHead('myrepodir')
