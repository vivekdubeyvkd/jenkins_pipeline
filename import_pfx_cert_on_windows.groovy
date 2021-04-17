// Define absolute PFX cert path 
PFX_CERT_PATH = "D:\\pfx_cert\\mypfxcertfile.pfx"

def import_pfx_cert(){
    // Define PFX-PASS credential on Jenkins to store PFX certificate password
    withCredentials([string(credentialsId: 'PFX-PASS', variable: 'PFX_PASS')]) {
        // MyCertSubjectString mentioned in command is a string pattern from certificate Subject that is getting imported here
        powershell """
            Get-ChildItem -Path Cert:\\LocalMachine\\My | where { \$_.subject -match "MyCertSubjectString" } | Remove-Item
            \$certPass = ConvertTo-SecureString -String "\$env:PFX_PASS" -Force -AsPlainText
            \$User = "windows_user_name"
            \$Cred = New-Object -TypeName "System.Management.Automation.PSCredential" -ArgumentList \$User, \$certPass
            Import-PfxCertificate -FilePath "$env:PFX_CERT_PATH" -CertStoreLocation Cert:\\LocalMachine\\My -Password \$Cred.Password 
        """
    }
}

import_pfx_cert()
