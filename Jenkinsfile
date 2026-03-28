properties([
  parameters([
    string(name: 'EMAIL_TO', defaultValue: 'mefrahnothmane@gmail.com', description: 'Emails separated by comma')
  ])
])

pipeline {
  agent any

  environment {
    JAVA_HOME  = "/usr/lib/jvm/java-21-openjdk-amd64"
    MAVEN_HOME = "/opt/maven"
    PATH       = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${PATH}"

    IMAGE_NAME = "myapp"
    IMAGE_TAG  = "${env.BUILD_NUMBER}"
    FULL_IMAGE = "${IMAGE_NAME}:${IMAGE_TAG}"
  }

  stages {

    stage('Checkout Source Code') {
      steps {
        checkout scm
        script {
          env.GIT_COMMIT_MSG = sh(script: "git log -1 --pretty=%s", returnStdout: true).trim()
        }
        sh 'java --version'
        sh 'javac --version'
        sh 'mvn --version'
      }
    }

    stage('Static Analysis') {
      steps {
        sh 'mvn -B checkstyle:check'
      }
    }

    stage('Build - Compile Code') {
      steps {
        sh 'mvn -B clean compile'
      }
    }

    stage('Container Image') {
      steps {
        sh "docker build -t ${FULL_IMAGE} ."
      }
    }

    stage('Unit Tests') {
      steps {
        sh """
          docker run --rm \
            -v "\$WORKSPACE":/app -w /app \
            ${FULL_IMAGE} sh -lc 'mvn -B test'
        """
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('sonarqube') {
          sh 'mvn -B sonar:sonar'
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
        sh """
          docker run --rm \
            -v "\$WORKSPACE":/app -w /app \
            ${FULL_IMAGE} sh -lc 'mvn -B jacoco:report'
        """
        recordCoverage tools: [[parser: 'JACOCO', pattern: 'target/site/jacoco/jacoco.xml']]
      }
    }
  }

   post {
  always {
    script {
      def junitFiles = findFiles(glob: 'target/surefire-reports/*.xml')
      if (junitFiles?.length) {
        junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
      } else {
        echo "WARNING: No JUnit reports generated!"
      }

      if (fileExists('target/site/jacoco/index.html')) {
        archiveArtifacts artifacts: 'target/site/jacoco/**',
                         fingerprint: true,
                         allowEmptyArchive: true
      } else {
        echo "WARNING: No JaCoCo coverage report generated!"
      }
    }
  }

  success {
  emailext(
    to: "${params.EMAIL_TO}",
    subject: "✅ SUCCESS – ${env.JOB_NAME} #${env.BUILD_NUMBER}",
    body: """
Hello,

The Jenkins pipeline execution completed successfully.

Build Details:
----------------------------------------
Job Name     : ${env.JOB_NAME}
Build Number : #${env.BUILD_NUMBER}
Status       : SUCCESS
Duration     : ${currentBuild.durationString}
Commit       : ${env.GIT_COMMIT ? env.GIT_COMMIT.take(7) : 'N/A'}
Commit Msg   : ${env.GIT_COMMIT_MSG ?: 'N/A'}
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
    to: "${params.EMAIL_TO}",
    subject: "❌ FAILED – ${env.JOB_NAME} #${env.BUILD_NUMBER}",
    body: """
Hello,

The Jenkins pipeline execution has failed.

Build Details:
----------------------------------------
Job Name     : ${env.JOB_NAME}
Build Number : #${env.BUILD_NUMBER}
Status       : FAILED
Duration     : ${currentBuild.durationString}
Commit       : ${env.GIT_COMMIT ? env.GIT_COMMIT.take(7) : 'N/A'}
Commit Msg   : ${env.GIT_COMMIT_MSG ?: 'N/A'}
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
    to: "${params.EMAIL_TO}",
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
Duration     : ${currentBuild.durationString}
Commit       : ${env.GIT_COMMIT ? env.GIT_COMMIT.take(7) : 'N/A'}
Commit Msg   : ${env.GIT_COMMIT_MSG ?: 'N/A'}
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
    to: "${params.EMAIL_TO}",
    subject: "⏹️ ABORTED – ${env.JOB_NAME} #${env.BUILD_NUMBER}",
    body: """
Hello,

The pipeline execution was aborted before completion.

Build Details:
----------------------------------------
Job Name     : ${env.JOB_NAME}
Build Number : #${env.BUILD_NUMBER}
Status       : ABORTED
Duration     : ${currentBuild.durationString}
Commit       : ${env.GIT_COMMIT ? env.GIT_COMMIT.take(7) : 'N/A'}
Commit Msg   : ${env.GIT_COMMIT_MSG ?: 'N/A'}
----------------------------------------

You can check the build details here:
${env.BUILD_URL}

Regards,
Jenkins Automation Server
"""
  )
}
}
}
