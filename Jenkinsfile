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

  always {
    // Always publish test reports, even if the build fails
    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true

    // Always archive JaCoCo coverage report (if generated)
    archiveArtifacts artifacts: 'target/site/jacoco/**',
                     fingerprint: true,
                     allowEmptyArchive: true
  }

  success {
    emailext(
      to: "mefrahothmane@gmail.com",
      subject: "✅ SUCCESS – ${env.JOB_NAME} #${env.BUILD_NUMBER}",
      body: """
Hello,

The Jenkins pipeline execution completed successfully.

Build Details:
----------------------------------------
Job Name     : ${env.JOB_NAME}
Build Number : #${env.BUILD_NUMBER}
Status       : SUCCESS
Branch       : ${env.BRANCH_NAME ?: 'N/A'}
Duration     : ${currentBuild.durationString}
----------------------------------------

You can access the build report here:
${env.BUILD_URL}

Regards,
Jenkins Automation Server
"""
    )
  }

  failure {
    emailext(
      to: "mefrahothmane@gmail.com",
      subject: "❌ FAILED – ${env.JOB_NAME} #${env.BUILD_NUMBER}",
      body: """
Hello,

The Jenkins pipeline execution has failed.

Build Details:
----------------------------------------
Job Name     : ${env.JOB_NAME}
Build Number : #${env.BUILD_NUMBER}
Status       : FAILED
Branch       : ${env.BRANCH_NAME ?: 'N/A'}
Duration     : ${currentBuild.durationString}
----------------------------------------

Please review the console output for more details:
${env.BUILD_URL}console

Regards,
Jenkins Automation Server
"""
    )
  }

  unstable {
    emailext(
      to: "mefrahothmane@gmail.com",
      subject: "⚠️ UNSTABLE – ${env.JOB_NAME} #${env.BUILD_NUMBER}",
      body: """
Hello,

The pipeline finished with an UNSTABLE status.
This usually indicates test failures or insufficient coverage.

Build Details:
----------------------------------------
Job Name     : ${env.JOB_NAME}
Build Number : #${env.BUILD_NUMBER}
Status       : UNSTABLE
Branch       : ${env.BRANCH_NAME ?: 'N/A'}
Duration     : ${currentBuild.durationString}
----------------------------------------

More details are available here:
${env.BUILD_URL}

Regards,
Jenkins Automation Server
"""
    )
  }

  aborted {
    emailext(
      to: "mefrahothmane@gmail.com",
      subject: "⏹️ ABORTED – ${env.JOB_NAME} #${env.BUILD_NUMBER}",
      body: """
Hello,

The pipeline execution was aborted before completion.

Build Details:
----------------------------------------
Job Name     : ${env.JOB_NAME}
Build Number : #${env.BUILD_NUMBER}
Status       : ABORTED
Branch       : ${env.BRANCH_NAME ?: 'N/A'}
Duration     : ${currentBuild.durationString}
----------------------------------------

You can check the build details here:
${env.BUILD_URL}

Regards,
Jenkins Automation Server
"""
    )
  }

  cleanup {
    // Optional: clean workspace after build
    // cleanWs()
  }
}

}
