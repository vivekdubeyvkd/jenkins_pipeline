/**
  ************************************************************************************************************************
  * Script Name : find_open_pr_list_across_repos
  * Purpose : Smart utility to generate reports with open PRs on GitHub repo
  *           # It computes PRs for below time ranges:
  *              1> open for more than 70 hours
  *              2> open between 54 and 70 hours
  *              3> open between 48 and 54 hours
  *              4> open and directly closed PRs(not merged) in 70 hours
  *           # It also sends personalized email notification to each developer who created the PR based on business logic for above mentioned time ranges
  * Author : Vivek Dubey
  * Copyright : Intuit Inc @ 2021  
  ************************************************************************************************************************
                ** Validate it once on some test env before actually using it for any production purpose **
  ************************************************************************************************************************
**/

import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.* 
import groovy.time.*

timeStampBackTo70hours = ""
timeStampBackTo54hours = ""
timeStampBackTo48hours = ""

UNIQUE_70_HOURS_USER_EMAIL_LIST = []
ABONDONED_70_HOURS_USER_EMAIL_LIST = []
UNIQUE_54_HOURS_USER_EMAIL_LIST = []
UNIQUE_48_HOURS_USER_EMAIL_LIST = []

GITHUB_API = " https://api.github.com"

// you can define any PR in below PR_EXCEPTION_LIST list which should not be included in PR stats computation
PR_EXCEPTION_LIST = [] 

// add all your repos in below list
REPO_LIST = [
     "GitHub_ORG/GitHub_Repo",
     "GitHub_ORG/GitHub_Repo"
    ]   
    
def get_matching_lines_in_file(filePath, pattern){
    lines = readFile(filePath).readLines()
    result = lines.findAll { it.contains(pattern) }
    if(result){
        return result
    }
    return 
}

def send_email_notification(userList, csvOutputFile, teamType){
    stage("Notification for ${teamType}"){
        if(csvOutputFile =~ "moreThan70" || csvOutputFile =~ "last70" || csvOutputFile =~ "70To54"){
              sh """#!/bin/bash
                  if [ -f user_pr.txt ]
                  then
                      rm -rf user_pr.txt
                  fi
               """
               fileLines = readFile(csvOutputFile).readLines()
               for(lineIndex = 1; lineIndex < fileLines.size(); lineIndex++){
                     lineDetails = fileLines[lineIndex]
                     sh """#!/bin/bash
                         echo "${lineDetails} <br/>" >> user_pr.txt
                     """
               }
               // update email ids as per your need
               email_dl_list = "email1@gamil.com,email2@gmail.com"
               if(fileExists("user_pr.txt")){
                   if(csvOutputFile =~ "moreThan70"){
                       emailext body: '${FILE,path="user_pr.txt"}', mimeType: 'text/html', subject: "[Abandoned]:${teamType} open PRs(more than 70 hours) status report", to: email_dl_list
                   }else if(csvOutputFile =~ "last70"){
                       emailext body: '${FILE,path="user_pr.txt"}', mimeType: 'text/html', subject: "[Abandoned]:${teamType} created and directly closed(not merged) PRs(in last 70 hours) status report", to: email_dl_list
                   }else if(csvOutputFile =~ "70To54"){
                      emailext body: '${FILE,path="user_pr.txt"}', mimeType: 'text/html', subject: "[Outliers]:${teamType} open PRs(created in between 54 and 70 hours) status report", to: email_dl_list
                   }
               }else {
                   echo """
                          File user_pr.txt does not exist, so not sending email
                   """
               }
         } 
         for(userCounter = 0; userCounter < userList.size(); userCounter++){
              userEmail = userList[userCounter]
              listOfUserPR = get_matching_lines_in_file(csvOutputFile, userEmail)
              if(listOfUserPR){
                 sh """#!/bin/bash
                     if [ -f user_pr.txt ]
                     then
                         rm -rf user_pr.txt
                     fi
                 """
                 for(prIndex = 0; prIndex < listOfUserPR.size(); prIndex++){
                      prDetails = listOfUserPR[prIndex]
                      sh """#!/bin/bash
                           echo "${prDetails} <br/>" >> user_pr.txt
                      """
                  }
                  echo """
                         List of PRs for ${userEmail}
                         ${listOfUserPR}
                  """

                  if(fileExists("user_pr.txt")){
                     if(csvOutputFile =~ "70To54"){
                         emailext body: '${FILE,path="user_pr.txt"}', mimeType: 'text/html', subject: "[Outliers]:${teamType} open PRs(created in between 54 and 70 hours) status report", to: userEmail
                     }else if(csvOutputFile =~ "54To48"){
                         emailext body: '${FILE,path="user_pr.txt"}', mimeType: 'text/html', subject: "[Warning]:${teamType} open PRs(created in between 48 and 54 hours) status report", to: userEmail
                     }else if(csvOutputFile =~ "moreThan70"){
                         emailext body: '${FILE,path="user_pr.txt"}', mimeType: 'text/html', subject: "[Abandoned]:${teamType} open PRs(more than 70 hours) status report", to: userEmail 
                     }else if(csvOutputFile =~ "last70"){
                         emailext body: '${FILE,path="user_pr.txt"}', mimeType: 'text/html', subject: "[Abandoned]:${teamType} created and directly closed(not merged) PRs(in last 70 hours) status report", to: userEmail
                     }                       
                  } else {
                       echo """
                             File user_pr.txt does not exist, so not sending email 
                       """
                  }
              }
          }
    }
}

def get_open_prs(repoName, pageNumber, timeLimitRange){
    withCredentials([usernamePassword(credentialsId: 'github_token', passwordVariable: 'GPWD', usernameVariable: 'GUSR')]) {
        if(timeLimitRange == "moreThan70"){
           sh """
              curl -s -k -u "$GUSR:$GPWD" ${GITHUB_API}/search/issues?page=${pageNumber}\\&q=repo%3A${repoName}+is%3Apr+is%3Aopen+created%3A\\<${timeStampBackTo70hours} > open_pr_${timeLimitRange}.json
           """
        }else if(timeLimitRange == "70To54"){
           sh """
              curl -s -k -u "$GUSR:$GPWD"  ${GITHUB_API}/v3/search/issues?page=${pageNumber}\\&q=repo%3A${repoName}+is%3Apr+is%3Aopen+created%3A${timeStampBackTo70hours}..${timeStampBackTo54hours} > open_pr_${timeLimitRange}.json
           """        
        }else if(timeLimitRange == "54To48"){
           sh """
              curl -s -k -u "$GUSR:$GPWD"  ${GITHUB_API}/search/issues?page=${pageNumber}\\&q=repo%3A${repoName}+is%3Apr+is%3Aopen+created%3A${timeStampBackTo54hours}..${tmpTimeStampBackTo48hours} > open_pr_${timeLimitRange}.json
           """        
        }else if(timeLimitRange == "last70"){
            sh """
              curl -s -k -u "$GUSR:$GPWD"  ${GITHUB_API}/search/issues?page=${pageNumber}\\&q=repo%3A${repoName}+is%3Apr+is%3Aclosed+closed%3A\\>${timeStampBackTo70hours} > open_pr_${timeLimitRange}.json
           """            
        }
        prJson = readJSON file: "open_pr_${timeLimitRange}.json", text: ''
        if(prJson["items"]){
            return prJson["items"]
        } 
        return 0
    }
}   

def get_user_email(userId){
    withCredentials([usernamePassword(credentialsId: 'github_token', passwordVariable: 'GPWD', usernameVariable: 'GUSR')]) {
       if(! (userId =~ "[bot]")){
            sh """
               if [ -f user.json ]
               then
                  rm -rf user.json
               fi
               curl -s -k -u "$GUSR:$GPWD" ${GITHUB_API}/users/${userId} > user.json
            """
            if(fileExists("user.json")){
                userJson = readJSON file: 'user.json', text: ''
                if(userJson["email"] && userJson["email"] != "null"){
                   return userJson["email"]
                }
            }
       }
       return userId
    }
}

def check_if_not_a_merged_pr(repoName, prNumber){
   withCredentials([usernamePassword(credentialsId: 'github_token', passwordVariable: 'GPWD', usernameVariable: 'GUSR')]) {
        sh """
           if [ -f each_pr.json ]
           then
              rm -rf each_pr.json
           fi
           curl -s -k -u "$GUSR:$GPWD"  ${GITHUB_API}/repos/${repoName}/pulls/${prNumber} > each_pr.json
        """
        if(fileExists("each_pr.json")){
             eachPrJson = readJSON file: 'each_pr.json', text: ''
             if(eachPrJson["merged_at"] && eachPrJson["merged_at"] != "null"){
                return
             }        
        }
        return prNumber
   }
}

def compute_open_pr_list(repoName, inputFile, timeLimitRange){
    for(counter = 1; counter < 20; counter++){
         pr_json = get_open_prs(repoName, counter, timeLimitRange)
         if(pr_json != 0 ){
             for(index1=0; index1 < pr_json.size(); index1++){
                 myJson = pr_json[index1]
                 pr_url = myJson["html_url"]
                 pr_title = myJson["title"].replace("\"","").replaceAll(",","")
                 pr_state = myJson["state"]
                 pr_createdby =  myJson["user"]["login"]
                 pr_createdon =  myJson["created_at"]
                 pr_createremail = get_user_email(pr_createdby)
                 println(pr_url + " " + pr_title  + " " + pr_state + " " + pr_createdby + " " + pr_createdon + " " + pr_createremail)
                 if(timeLimitRange == "moreThan70"){
                    if(! UNIQUE_70_HOURS_USER_EMAIL_LIST.contains(pr_createremail)){
                        UNIQUE_70_HOURS_USER_EMAIL_LIST.add(pr_createremail)
                    }
                 }else if(timeLimitRange == "70To54"){
                    if(! UNIQUE_54_HOURS_USER_EMAIL_LIST.contains(pr_createremail)){
                        UNIQUE_54_HOURS_USER_EMAIL_LIST.add(pr_createremail)
                    }                 
                 }else if(timeLimitRange == "54To48"){
                     if(! UNIQUE_48_HOURS_USER_EMAIL_LIST.contains(pr_createremail)){
                        UNIQUE_48_HOURS_USER_EMAIL_LIST.add(pr_createremail)
                    }                  
                 }
                 if(PR_EXCEPTION_LIST && PR_EXCEPTION_LIST.contains(pr_url)){
                     echo """
                                   This PR ${pr_url} is in input exception list, so it will not be added in email notification
                     """
                 } else {
                    if(timeLimitRange == "last70"){
                        pr_number = myJson["number"]
                        if(check_if_not_a_merged_pr(repoName, pr_number)){
                           if(! ABONDONED_70_HOURS_USER_EMAIL_LIST.contains(pr_createremail)){
                               ABONDONED_70_HOURS_USER_EMAIL_LIST.add(pr_createremail)
                           }
                           sh """
                                echo "${pr_url}, ${pr_title}, ${pr_state}, ${pr_createdby}, ${pr_createdon}, ${pr_createremail}" >> ${inputFile}_open_closed_pr_${timeLimitRange}.csv
                           """
                        }
                    }else {
                        sh """
                            echo "${pr_url}, ${pr_title}, ${pr_state}, ${pr_createdby}, ${pr_createdon}, ${pr_createremail}" >> ${inputFile}_open_pr_${timeLimitRange}.csv
                        """
                    }
                 }
             }
         }else {
             break
         }
    }
}

def compute_github_pr_details(repoName, inputFile){
    withCredentials([usernamePassword(credentialsId: 'github_token', passwordVariable: 'GPWD', usernameVariable: 'GUSR')]) {
        compute_open_pr_list(repoName, inputFile, "moreThan70") 
        compute_open_pr_list(repoName, inputFile, "70To54") 
        compute_open_pr_list(repoName, inputFile, "54To48")
        compute_open_pr_list(repoName, inputFile, "last70")
    }
}

def compute_commit_histroy(){
    stage("Repos GitHub stats") {
        sh """
            echo PR Url, Title, Status, CreatedBy, CreatedOn, UserEmail  > repos_open_pr_moreThan70.csv
            echo PR Url, Title, Status, CreatedBy, CreatedOn, UserEmail  > repos_open_pr_70To54.csv
            echo PR Url, Title, Status, CreatedBy, CreatedOn, UserEmail  > repos_open_pr_54To48.csv
            echo PR Url, Title, Status, CreatedBy, CreatedOn, UserEmail > repos_open_closed_pr_last70.csv
        """           
        for(index=0 ; index < REPO_LIST.size(); index++) {
            repoName = REPO_LIST[index]
            compute_github_pr_details(repoName, "${WORKSPACE}/repos")
        }
        echo """
              User email list for PRs older than 70 hours: ${UNIQUE_70_HOURS_USER_EMAIL_LIST}
        """
        send_email_notification(UNIQUE_70_HOURS_USER_EMAIL_LIST, "repos_open_pr_moreThan70.csv", "my repos")
        echo """
             User email list for PRs between 54 and 70 hours: ${UNIQUE_54_HOURS_USER_EMAIL_LIST}
        """
        send_email_notification(UNIQUE_54_HOURS_USER_EMAIL_LIST, "repos_open_pr_70To54.csv", "my repos")
        echo """
             User email list for PRs between 48 and 54 hours: ${UNIQUE_48_HOURS_USER_EMAIL_LIST}
        """  
        send_email_notification(UNIQUE_48_HOURS_USER_EMAIL_LIST, "repos_open_pr_54To48.csv", "my repos")
        echo """
             User email list for PRs open and directly closed(not merged) in last 70 hours : ${ABONDONED_70_HOURS_USER_EMAIL_LIST}
        """
        send_email_notification(ABONDONED_70_HOURS_USER_EMAIL_LIST, "repos_open_closed_pr_last70.csv", "my repos")
    }
}

def setDateVars(){
    stage("Set TimeStamp Vars"){
       currentDate =  java.time.LocalDateTime.now()

       tmpTimeStampBackTo70hours = currentDate.minusHours(70)
       tmpTimeStampBackTo54hours = currentDate.minusHours(54)
       tmpTimeStampBackTo48hours = currentDate.minusHours(48)

       timeStampBackTo70hours = tmpTimeStampBackTo70hours.toString().replaceAll("\\..*","Z")
       timeStampBackTo54hours = tmpTimeStampBackTo54hours.toString().replaceAll("\\..*","Z")
       timeStampBackTo48hours = tmpTimeStampBackTo48hours.toString().replaceAll("\\..*","Z")
    }
}

pipeline {
    // update Jenkins agent details as per your Jenkins setup
    agent {label "myjenkinmachinelabel||master"}
    stages {
        stage("Start") {
            steps {
                script {
                    timestamps {
                        container(container_name) {
                            setDateVars()
                            if("${EXCEPTION_PR_LIST}"){
                                PR_EXCEPTION_LIST = "${EXCEPTION_PR_LIST}".split('\n')
                            }
                            compute_commit_histroy()
                            archiveArtifacts allowEmptyArchive: true, artifacts: '*.csv', followSymlinks: false
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
