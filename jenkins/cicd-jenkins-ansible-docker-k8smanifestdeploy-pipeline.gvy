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
		
		stage('ansible-dockerbuild-push') {
	        steps {
                    echo "building image and pushing to dockerhub..."
	                withDockerRegistry(credentialsId: 'DOCKER_HUB_LOGIN', url: 'https://index.docker.io/v1/') {
                    sh 'ansible-playbook -i localhost, deploy/ansible_dockerbuild_playbook.yml'

                    }
	           
	        }
	  }
		
		stage('Deploy to K8s') {
			steps {
				sh 'ansible-playbook --inventory /etc/ansible/hosts deploy/ansibleplaybook-k8smanifestdeploy.yml'
			}
        }

    }

}
