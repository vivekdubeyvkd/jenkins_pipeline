
email_to = "vivek******@gmail.com"

def sendEmailNotification(){
     // filename.txt is directly under workspace, so it is being referred like this, you need to change the path as per location of your file
     // below indentaion for body is to ensure that on email, we get the uniformaly indented content from file over email
     if(fileExists('filename.txt')) {
                emailext body:'''Build URL : ${BUILD_URL}

File content is shown below :

${FILE, path="filename.txt"}''', subject: "Showing filename.txt file content on email body", to: email_to
     }
}  

// call this function as shown below, probabaly in any post build step to send notification in Jenkins pipeline
sendEmailNotification()
