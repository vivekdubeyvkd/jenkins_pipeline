 function callJenkinsJobWithFileParameter(){
      json="{'parameter': [
         {'name': 'param', 'value': \"value\"},
         {'name': 'param', 'value': \"value\"},
         {'name': 'FILEDATA', 'file':'file0'}]}"
      export url="<jenkins-url>/job/<job-name>/build"
      curl -s -k  -f $url -F file0=@<file-path> -F json="$json" --user '<username>:<api-token>'
    fi
 }
 
 callJenkinsJobWithFileParameter
