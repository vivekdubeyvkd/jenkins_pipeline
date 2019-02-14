def callJenkinsJobWithFileParameter(){
    withCredentials([string(credentialsId: '<token_id>', variable: 'API_TOKEN')]) {
                jsonObjectData = [ parameter : 
                                    [
                                        [name: "param", value: "value"],
                                        [name: "param", value: value],
                                        [name: "FILEDATA", file:"file0"]
                                    ]
                                ]
                parsedJson = JsonOutput.toJson(jsonObjectData).replace("\"", "\\\"")
                sh """
                    set +x 
                    curl -s -k  -f "${COPY_JOB_URL}" -F file0=@<file-path> -F json="${parsedJson}" --user "<user-name>:${API_TOKEN}"
                """
    }    
}
