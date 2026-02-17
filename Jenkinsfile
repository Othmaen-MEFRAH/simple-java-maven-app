pipeline {
  agent any

  environment {
    JAVA_HOME = "/usr/lib/jvm/java-21-openjdk-amd64"
    MAVEN_HOME = "/opt/maven"
    PATH = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${PATH}"
  }

  stages {
    stage('Check') {
      steps {
        sh 'java -version'
        sh 'javac -version'
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
