pipeline {
  agent {
    docker {
      image 'maven:3-alpine'
      args '-v $HOME/.m2:/root/.m2'
    }
    
  }
  stages {
    stage('compile') {
      steps {
        sh 'mvn clean install'
      }
    }
    stage('archive') {
      steps {
        parallel(
          // "Junit": {
            // junit 'target/surefire-reports/*.xml'
            
          // },
          "Archive": {
            archiveArtifacts(artifacts: 'target/*.jar', onlyIfSuccessful: true, fingerprint: true)
            //archiveArtifacts(artifacts: 'target/Nadia*javadoc.jar', fingerprint: true)
            
          }
        )
      }
    }
  }
}