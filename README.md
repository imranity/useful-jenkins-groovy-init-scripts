# Useful Jenkins groovy init Scripts

As you may have known, Jenkins allows adding groovy scripts initialization scripts, which means these scripts will run every time  
Jenkins starts.  

## How this works?

The jenkins server automatically looks for `groovy.d` folder under the home dir of where Jenkins run from e.g. `/var/lib/jenkins/groovy.d` 
and any groovy script in folder get executed anytime jenkins starts. 

## Why use the scripts?

Its a pretty handy way of automating Jenkins installation; lets say you want your CI setup to be automated, like everytime you 
install your jenkins environment, it should be pre-configured with certain user accounts created, some global properties set.  
Groovy is an easy way of calling Jenkins base classes of Java without getting to know much of Java itself )

## What scripts are in this repo?

I have scripts for :  
1. Creating user accounts.  
2. Creating global credentials (for ssh , username/password etc).  
3. Getting the API key of a user ( this is pretty useful because if you use (Jenkins Job Builder )[https://github.com/openstack-infra/jenkins-job-builder.git] for setting up
your CI , for the credential .ini file , you will need the API key so why not get it from a groovy script :) )  
4. Setting the global security , either LDAP , or jenkins own database etc.   
5. Setting global authorization matrix for groups like `anonymous`, `authenticated` or any specific user.  
6. Get a credential ID of a global credential, again extremely useful when you use *Jenkins Job BUilder* and in git SCM, you need 
to specify (Credential ID of ssh key for git to use in cloning some private repos)[http://docs.openstack.org/infra/jenkins-job-builder/scm.html],
this script will do it for you .  

Cheers :)
