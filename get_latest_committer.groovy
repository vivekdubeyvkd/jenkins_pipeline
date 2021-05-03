latest_committer_details = ""

def get_latest_commiter(clonedRepoDir, fileNamesList){
     dir(clonedRepoDir) {
         for(index = 0; index < fileNamesList.size(); index++){
             latest_commiter_email = sh(returnStdout: true, script: "git log -n 1 --pretty=format:%ae  -- ${fileName}").trim()
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
// fileNamesList : fileName for which I want to find the latest committer
get_latest_commiter(clonedRepoDir, fileNamesList)
