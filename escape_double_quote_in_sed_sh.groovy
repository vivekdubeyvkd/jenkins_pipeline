 inputfile = "<path to file>"
 
//without escaping anything
def replace_text(){
   sh """
        sed -i "/^linetomatch/i addmebefore_linetomatch\n" ${inputfile}
   """   
}
 
//with double quotes escaping
def replace_escape_text(){
   sh """
        sed -i "/^linetomatch/i \\\"addmebefore_linetomatch\\\"\n" ${inputfile}
   """   
}
 
