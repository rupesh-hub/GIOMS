pipeline {
     agent { node { label 'master' } }
    tools {
        jdk "java"
    }   
    options{
    buildDiscarder(logRotator(daysToKeepStr: '30', numToKeepStr: '3'))
     timestamps()
   }
     stages {
         stage('Unit Tests') {
          steps {
              script{
                  echo "Running Unit Tests"
          def mvnHome = tool name: 'mvn', type: 'maven'
          sh "${mvnHome}/bin/mvn test"
              }
        }
      }
         stage('Compile Package'){
          steps{
              script{
                   def mvnHome = tool name: 'mvn', type: 'maven'
                       sh "${mvnHome}/bin/mvn clean install -DskipTests "
               }
            }
         }
      stage('Deploying Application'){
          steps{
                  sh "/var/lib/jenkins/scripts/gerp_backend.sh"
               }
        }

      stage ('Triggering Another job') {
            steps {     
                build job: 'GERP_CALENDAR', propagate: true, wait: true 
                build job: 'GERP_COMMITTEE-MANAGEMENT', propagate: true, wait: true 
                build job: 'GERP_DMS', propagate: true, wait: true   
                build job: 'GERP_MESSAGING_SERVICE', propagate: true, wait: true   
            }
        }

    
    }

}
