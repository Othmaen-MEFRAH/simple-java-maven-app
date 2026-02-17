pipeline {
	agent any
	tools {
		jdk 'JDK21'
        maven 'Maven3'
	}
	stages {
		stage('Build') {
			steps {
				sh 'mvn -B -DskipTests clean package'
			}
		}
	}
}
