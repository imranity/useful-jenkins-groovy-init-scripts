import jenkins.model.*

def inst = Jenkins.getInstance()

def desc = inst.getDescriptor("hudson.plugins.git.GitSCM")

desc.setGlobalConfigName("[name to use with git commits]")
desc.setGlobalConfigEmail("[email to use with git commits]")

desc.save()
