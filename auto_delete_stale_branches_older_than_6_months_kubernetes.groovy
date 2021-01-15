GITHUB_NAME = "github.com"
GITHUB_ORG = "vivekdubeyvkd"
GITHUB_REPO = "jenkins_pipeline"
container_name = "deletebranch-${BUILD_NUMBER}"
REMOTE_BRANCH_LIST = []
TIME_LINE_LIST = ['7 months', '8 months', '9 months', '10 months', '11 months', '1 year', '2 years', '3 years']
ACTIVE_RELEASE = ['master', 'test', 'develop', 'qa']
active_releases_list = []

def checkout_repo(){
    stage('Repo Checkout'){
        withCredentials([usernamePassword(credentialsId: 'github_token', passwordVariable: 'GPWD', usernameVariable: 'GUSER')]) {
            sh """
               git clone  https://${GUSER}:${GPWD}@${GITHUB_NAME}/${GITHUB_ORG}/${GITHUB_REPO} ${GITHUB_REPO}
            """
        }
    }
}  

def create_active_branch_list(){
    for(index = 0; index < ACTIVE_RELEASE.size(); index++){
        branch = ACTIVE_RELEASE[index]
        active_releases_list.add("origin/${branch}\\\$")
    }
    println("active_releases_list " + active_releases_list)
}

def check_if_release_branch(branch_value){
    for(idx2 =0; idx2 < active_releases_list.size(); idx2++){
        if(branch_value =~ active_releases_list[idx2]){
            return
        }
    }
    return branch_value
}

def create_remote_branch_list(){
    stage("Get Remote Branches"){
        dir("${GITHUB_REPO}"){
            tmp_branch = sh(returnStdout: true, script: "git branch -r|grep -v HEAD")
        }
        REMOTE_BRANCH_LIST = tmp_branch.split("\n")
    }
}

def branch_check_n_delete(COMMIT_SINCE, outputfile){
    create_active_branch_list()
    sh "echo> ${outputfile}"
    dir("${GITHUB_REPO}"){
        for(idx = 0; idx < REMOTE_BRANCH_LIST.size(); idx++){
            branch_value = REMOTE_BRANCH_LIST[idx].trim()
            if(check_if_release_branch(branch_value)){
                try{
                    branch_commit_details = sh(returnStdout: true, script: "git show --format=\"%ci,%cr,%cn\" ${branch_value}|head -1|grep \"${COMMIT_SINCE}\"").trim()
                    if(branch_commit_details =~ ",${COMMIT_SINCE}"){
                        branch_name_without_origin = branch_value.replace("origin/","").trim()
                        try{
                            sh """
                                git push origin ":${branch_name_without_origin}"
                                echo "${branch_commit_details},${branch_value},deleted" >> ${outputfile}
                            """
                        }catch(er){
                             sh """
                                echo "${branch_commit_details},${branch_value},not deleted" >> ${outputfile}
                            """                          
                        }
                    }   
                }catch(err){}
            }
        }
    }
}

def check_n_delete_branches(){
    create_remote_branch_list()
    stage("Check and Delete 6+ months older branches"){
        for(idx4 = 0; idx4 < TIME_LINE_LIST.size(); idx4++){
            NO_COMMIT_SINCE = TIME_LINE_LIST[idx4]
            file_name = "${NO_COMMIT_SINCE}".replace(' ','_') 	
            output_file = "${WORKSPACE}/output_${file_name}.csv"
            branch_check_n_delete(NO_COMMIT_SINCE, output_file)
        }
    }
}

pipeline {
    agent {
        kubernetes {
            defaultContainer "${container_name}"
            yaml """
                apiVersion: v1
                kind: Pod
                spec:
                    containers:
                    - name: "${container_name}"
                      image: 'docker.artifactory.a.intuit.com/maven:3.5.3-jdk-8'
                      resources:
                          requests:
                              memory: 10Gi
                      command:
                      - cat
                      tty: true
            """
        }
    }
    stages {
        stage("Start") {
            steps {
                script {
                    timestamps {
                        container(container_name) {
                            checkout_repo()
                            check_n_delete_branches()
                            archiveArtifacts allowEmptyArchive: true, artifacts: "output*.csv", followSymlinks: false
                            emailext attachmentsPattern: '*.csv', body: '', subject: 'QBDT-Core Repo Clean Up Notification for branches older than 6 months', to: 'vivek***@gmail.com'
                        }
                    }
                }
            }
        }
   
    }
    
    post {
        always {
            println("always")
        }
        failure {
            script {
                echo "Failure"
            }
        }
        success {
            script {
                echo "Success"
            }
        }
    }
}
