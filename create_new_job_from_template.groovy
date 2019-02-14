import com.cloudbees.hudson.plugins.modeling.impl.entity.EntityInstance;
import com.cloudbees.hudson.plugins.modeling.impl.entity.EntityModel;

def create_new_job_from_template(name, folderName, templateName){
    Jenkins j = Jenkins.getInstance()
    Item template = j.getItemByFullName(folderName + "/" + templateName)
    ModifiableTopLevelItemGroup folder = j.getItemByFullName(folderName)
    try{
      Job newJob = folder.createProject(template.asDescriptor(), name , true)
        if(newJob != null ){
            println newJob.getName()
            println 'Job created successfully'
        } else {
            println 'Could not create template job or it do not exists'
        }
    } catch(err) {
      println(err.message)
      if ( err.message =~ 'already exists'){
          currentBuild.result = 'SUCCESS'
      }else{
          currentBuild.result = 'ABORTED'
      }
      return
    }  
}
