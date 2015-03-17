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

