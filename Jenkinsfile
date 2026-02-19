pipeline {
    agent any

    environment {
        JAVA_HOME  = "/usr/lib/jvm/java-21-openjdk-amd64"
        MAVEN_HOME = "/opt/maven"
        PATH       = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${PATH}"
    }

    stages {

        stage('Checkout Source Code') {
            steps {
                sh 'java --version'
                sh 'javac --version'
                sh 'mvn --version'
            }
        }

        stage('Static Analysis') {
            steps {
                sh 'mvn checkstyle:check'
            }
        }

        stage('Build - Compile Code') {
            steps {
                sh 'mvn -B clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }
        stage('SonarQube Analysis') {
    steps {
        withSonarQubeEnv('sonarqube') {
            sh 'mvn sonar:sonar'
        }
    }
}

stage('Quality Gate') {
    steps {
        timeout(time: 2, unit: 'MINUTES') {
            waitForQualityGate abortPipeline: true
        }
    }
}
        stage('Code Coverage') {
            steps {
                sh 'mvn jacoco:report'
                recordCoverage tools: [[parser: 'JACOCO', pattern: 'target/site/jacoco/jacoco.xml']]
            }
        }
        
    }

    post {
   
  success {
    emailext(
      to: "mefrahothmane@gmail.com",
      subject: "✅ Build Successful | ${env.JOB_NAME} #${env.BUILD_NUMBER}",
      body: """
Hello,

The Jenkins pipeline execution completed successfully.

Build Details:
----------------------------------------
Job Name     : ${env.JOB_NAME}
Build Number : #${env.BUILD_NUMBER}
Status       : SUCCESS
Timestamp    : ${new Date()}
----------------------------------------

You can access the build report using the link below:
${env.BUILD_URL}

Regards,
Jenkins Automation Server
"""
    )
  }

  failure {
    emailext(
      to: "mefrahothmane@gmail.com",
      subject: "❌ Build Failed | ${env.JOB_NAME} #${env.BUILD_NUMBER}",
      body: """
Hello,

The Jenkins pipeline execution has failed.

Build Details:
----------------------------------------
Job Name     : ${env.JOB_NAME}
Build Number : #${env.BUILD_NUMBER}
Status       : FAILED
Timestamp    : ${new Date()}
----------------------------------------

Please review the console output for more information:
${env.BUILD_URL}

Regards,
Jenkins Automation Server
"""
    )
  }

        always {
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/site/jacoco/**', fingerprint: true
        }
    }
}
