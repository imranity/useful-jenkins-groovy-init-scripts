import jenkins.*
import hudson.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import hudson.plugins.sshslaves.*;
import hudson.model.*
import jenkins.model.*
import hudson.security.*


// SET SLAVE PORT 
def instance = Jenkins.getInstance()
instance.setNumExecutors(5)
instance.setSlaveAgentPort([55000])
// Create a global credential to work with git, this is needed to access the git related jobs.
// which means all the jobs, since all our jobs are somewhat working with git :)
// This assumes there is a ssh private key in /root/.ssh/ which i actually added in dockerfile , no need to worry
global_domain = Domain.global()
credentials_store =
Jenkins.instance.getExtensionList(
'com.cloudbees.plugins.credentials.SystemCredentialsProvider'
)[0].getStore()
credentials = new BasicSSHUserPrivateKey(
CredentialsScope.GLOBAL,
null,
"root",
new BasicSSHUserPrivateKey.UsersPrivateKeySource(),
"",
"")
credentials_store.addCredentials(global_domain, credentials)


// NOW TIME TO CONFIGURE GLOBAL SECURITY
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
//  sample LDAP setup
// Reference : Under 'Constructor Detail' section in http://javadoc.jenkins-ci.org/hudson/security/LDAPSecurityRealm.html

/*
def hudsonRealm = new LDAPSecurityRealm('tes','test','test','test','test','test','ldap',false)
*/
//  Please note createAccount is only a feature in PrivateSecurityRealm, which is type of security that allows using private jenkins
//  database for users and password
//  Passing 'false' in above instance creation actually means : allowSignup=false 
//  to allow signup , do something like this :
/*
 def testRealm = new HudsonPrivateSecurityRealm(true)
*/

//FOLLOWING IS THE ACTUAL SETUP NEEDED FOR JJB AND SWARM SLAVES
hudsonRealm.createAccount("swarm-slave","boguspassword")
hudsonRealm.createAccount("jenkins-job-builder","boguspassword")
instance.setSecurityRealm(hudsonRealm)
instance.save()
def strategy = new GlobalMatrixAuthorizationStrategy()
strategy.add(Jenkins.READ,'authenticated')
strategy.add(Item.READ,'authenticated')
strategy.add(Item.DISCOVER,'authenticated')
strategy.add(Item.CANCEL,'authenticated')
strategy.add(Item.CONFIGURE,'jenkins-job-builder')
strategy.add(Item.READ,'jenkins-job-builder')
strategy.add(Item.READ,'anonymous')
strategy.add(Item.DISCOVER,'jenkins-job-builder')
strategy.add(Item.CREATE,'jenkins-job-builder')
strategy.add(Item.DELETE,'jenkins-job-builder')
strategy.add(Jenkins.ADMINISTER, "swarm-slave")
strategy.add(Jenkins.ADMINISTER, "jenkins-job-builder")
instance.setAuthorizationStrategy(strategy)
instance.save()



//// HOW TO GET CREDENTIAL ID OF A GLOBAL CREDENTIAL - PETTY USEFUL IF YOU NEED TO WORK WITH private 
// github repos , and jenkins need a cred id so that the git scm works properly
// i really need it since i use jenkins-job-builder to define my jobs, some jobs need access to private github repos, 
// i createed creds in start of this file (see above), but jenkins-job-builder need to cred id for git scm dfinition.
// iget it using follwing code, store in a file.. 

// Normal way of creating file objects.We get the creds to be used by jobs who need access to private github repos
// Above we created global creds , we get their ID, which will then be passed to `git` section of each job
// under `credentials-id` ..so in short, jenkins git needs a cred ID, and knows the corresponding ssh key it relates to.
def file1 = new File('/tmp/creds.txt')  
def file2 = new File('/tmp/jjb.txt')  
def creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
      com.cloudbees.plugins.credentials.common.StandardUsernameCredentials.class,
      Jenkins.instance,
      null,
      null
  );
  for (c in creds) {
  //     println(c.id + ": " + c.description)
  // Writing to the files with the write method:
    file1 << "${c.id}"
  }
// here im getting API token of a user , pretty col huh, since JJB needs API token , not the normal password, to talk to jenkins and update/modify jobs

//j.jenkins.setSecurityRealm(j.createDummySecurityRealm());        
// this assumes user jenkins-job-builder has already been created...as i have done above in same file..
// to get api token of user foo , do it as " User.get('foo')
// cheers :)
User u = User.get("jenkins-job-builder")  
ApiTokenProperty t = u.getProperty(ApiTokenProperty.class)  
def token = t.getApiToken()
// token.getClass()
file2 << "${token}"
