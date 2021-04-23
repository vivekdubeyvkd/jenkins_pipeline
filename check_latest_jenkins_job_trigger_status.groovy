
def getLatestJenkinsJobStatus(jenkin_full_job_name){
    def jobData = jenkins.model.Jenkins.instance.getItemByFullName(jenkin_full_job_name)
    if(jobData){
        def statusResult = jobData.getLastBuild().getResult().toString()
        echo """
            Last build status of ${jenkin_full_job_name} : ${statusResult}
        """
        return statusResult
    } else {
         error "Jenkins job ${jenkin_full_job_name} data not found .... please check .... exiting ......"
    }  
    return
}

// call this getLatestJenkinsJobStatus as shown below
lastBuildStatus = getLatestJenkinsJobStatus("myTestJenkinsJobName")
