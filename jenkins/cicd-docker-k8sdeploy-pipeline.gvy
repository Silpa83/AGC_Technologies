pipeline {
    agent {
  label 'ubuntuserver'
}

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
		
		stage('build docker image') {
           steps {
              echo "build docker image using docker file"
              sh 'docker build --file Dockerfile --tag suryalankeladevops/abc_technologies:$BUILD_NUMBER .'
           }

        }
	  
		stage('push docker image') {
	       steps {
	           withDockerRegistry(credentialsId: 'DOCKER_HUB_LOGIN', url: 'https://index.docker.io/v1/') {
               sh script: 'docker push docker.io/suryalankeladevops/abc_technologies:$BUILD_NUMBER'
                }
	           
	        }
		          
		}
		
		stage('Deploy to K8s') {
			steps {
				sh 'sed -i "s/bno/"$BUILD_NUMBER"/g" deploy/abc_app-deploy-k8s.yml'
				sh 'kubectl apply -f deploy/abc_app-deploy-k8s.yml'
			}
        }

    }

}
