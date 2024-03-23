pipeline {
    agent any

    tools {
       maven 'maven3.9'
       }
    stages {
       stage('code compile') {
           steps {
             echo "perform code compiling.."
             git 'https://github.com/suryalankeladevops/AGC_Technologies.git'
             sh 'mvn compile'

           }

        }

        stage('package') {
           steps {
              echo "generate the war file"
              sh 'mvn package'
           }

           post {
             always {
               archiveArtifacts artifacts: 'target/*.war', followSymlinks: false
             }
           }
        }
		stage('docker login & ansible playbook for docker build and push') {
	       steps {
	           withDockerRegistry(credentialsId: 'DOCKER_HUB_LOGIN', url: 'https://index.docker.io/v1/') {
               sh script: 'ansible-playbook -i localhost, deploy/ansible_dockerbuild_play2.yml'
                }
	        }
		}
      stage('K8s Deploy-QA') {
       steps {
           sh 'ansible-playbook --inventory /etc/ansible/hosts deploy/ansible-pb-k8s-deploy.yml'
   }
      }
    }
}
