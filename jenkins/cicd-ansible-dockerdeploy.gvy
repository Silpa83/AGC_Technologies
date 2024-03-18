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
	  
		stage('Run via Ansible Playbook') {
            steps {
                script {
                    def ansiblePlaybookCmd = """
                        ansible-playbook -i localhost, deploy/ansible_dockerbuild_play.yml
                    """
                    sh ansiblePlaybookCmd
                }
			}
		}
				
		stage('docker deploy') {
	       steps {
	           echo "deploying the abc docker container into tomact server"
	           sh 'docker stop abc-container || true'
			   sh 'docker rm -f abc-container || true'
			   sh 'docker run -d -p 9090:8080 --name abc-container suryalankeladevops/abc_technologies:$BUILD_NUMBER'
	        }
		          
		}

    }
}
