pipeline {
  agent any

  environment {
    JAVA_HOME = "/usr/lib/jvm/java-21-openjdk-amd64"
    PATH = "${JAVA_HOME}/bin:${PATH}"
  }

  stages {

    stage('Check Tools') {
      steps {
        sh 'echo JAVA_HOME=$JAVA_HOME'
        sh 'java -version'
        sh 'mvn -version'
      }
    }

    stage('Build') {
      steps {
        sh 'mvn -B -DskipTests clean package'
      }
    }
  }
}
