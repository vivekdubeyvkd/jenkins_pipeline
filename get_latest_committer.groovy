latest_committer_details = ""

def get_latest_commiter(clonedRepoDir, fileNamesList, branchName="master'){
     dir(clonedRepoDir) {
         for(index = 0; index < fileNamesList.size(); index++){
             latest_commiter_email = sh(returnStdout: true, script: "git log -n 1 --pretty=format:%ae  origin/${branchName} -- ${fileName}").trim()
             if(latest_commiter_email =~ "@"){
                 latest_commiter_details_list = latest_commiter_email.split("@")
                 latest_committer = latest_commiter_details_list[0]
                 latest_committer_details =  "${latest_committer} ${latest_commiter_email} ${fileName}" + "\n" + latest_committer_details 
             }  
         }  
         echo """
              Latest_committer_details are as follows:
              ${latest_committer_details}
         """
     } 
}

// myRepoDir : directory underworkspace where I have cloned my repo
// fileNamesList : fileName list for which I want to find the latest committer
// here branchName will be taken as master                      
get_latest_commiter(clonedRepoDir, fileNamesList)

// myClonedRepoDir : directory underworkspace where I have cloned my repo
// MyFileNamesList : fileName list for which I want to find the latest committerr                            
// branchName is mybranch
get_latest_commiter(myClonedRepoDir, MyFileNamesList, 'mybranch')                        
                        
