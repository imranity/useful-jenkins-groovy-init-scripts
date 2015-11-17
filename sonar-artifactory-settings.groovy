//Source of scripts: https://groups.google.com/forum/#!topic/jenkinsci-users/U9HLBVB5_CM
//===========================
//Sonar Script
//==========================
import jenkins.model.*
import hudson.plugins.sonar.*
import hudson.plugins.sonar.model.*

def inst = Jenkins.getInstance()

def desc = inst.getDescriptor("hudson.plugins.sonar.SonarPublisher")

def sinst = new SonarInstallation(
  "sonar4.5.1",
  false,
  "http://localhost:9000/",
  "jdbc:mysql://localhost:3306/sonar",
  "com.mysql.jdbc.Driver",
  "sonar",
  "sonar",
  "",
  "-Dsonar.sourceEncoding=\"UTF-8\"",
  new TriggersConfig(),
  "admin",
  "admin"
)
desc.setInstallations(sinst)

desc.save()


//==================================================
//Artifactory Script
//==================================================
import jenkins.model.*
import org.jfrog.*
import org.jfrog.hudson.*
import org.jfrog.hudson.util.Credentials;

def inst = Jenkins.getInstance()

def desc = inst.getDescriptor("org.jfrog.hudson.ArtifactoryBuilder")

def deployerCredentials = new Credentials("admin", "password")
def resolverCredentials = new Credentials("", "")

def sinst = [new ArtifactoryServer(
  "server-id",
  "http://localhost:8081/artifactory",
  deployerCredentials,
  resolverCredentials,
  300,
  false )
]

desc.setArtifactoryServers(sinst)

desc.save()
