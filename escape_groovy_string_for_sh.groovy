string_val = "Vivek Dubey"
string_wth_quote = "\"${string_val}\""
new_string_shell = string_wth_quote.replace('"', '\\"')

def string_with_quote(){
    sh """
        echo "${string_val}" # prints Vivek Dubey
        echo "${new_string_shell}". #prints "Vivek Dubey"
    """
}
