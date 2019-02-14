def checkTriggerJenkinsJob(items, jobName) {
  for (item in items) {
    if (item.class.canonicalName != 'com.cloudbees.hudson.plugins.folder.Folder') {
        if(item.fullName == jobName.substring(jobName.indexOf('/') + 1)){
            if(item.disabled != true){
                println(item.name + " is enabled and going to trigger it ")
                build job: jobName, wait: false
            } else {
                println(item.name + " is disabled and cannot be triggered ")
            }  
        }  
      
    } else {     
        checkTriggerJenkinsJob(((com.cloudbees.hudson.plugins.folder.Folder) item).getItems(), jobName)
    }
  }
} 

node{
    checkTriggerJenkinsJob(Hudson.instance.items, '<job-name>')
}
