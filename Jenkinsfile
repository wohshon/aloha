node {
    stage 'Git checkout'
    echo 'Checking out git repository'
    git url: 'https://github.com/redhat-helloworld-msa/aloha'

    stage 'Build project'
    echo 'Building'
    def mvnHome = tool 'M3'
    def javaHome = tool 'jdk8'
    sh "${mvnHome}/bin/mvn package"

    stage 'Deploy to Dev'
    echo 'Deploying to Dev'
    buildAloha('helloworld-msa-dev')

    stage 'Deploy to QA'
    echo 'Deploying to QA'
    deployAloha('helloworld-msa-dev', 'helloworld-msa-qa')

    stage 'Wait for approval'
    input 'Aprove to production?'

    stage 'Deploy to production'
    echo 'Deploying to production'
    deployAloha('helloworld-msa-dev', 'helloworld-msa')
}

// Creates a Build and triggers it
def buildAloha(String project){
    projectSet()
    sh "oc new-build --binary --name=aloha -l app=aloha || echo 'Build exists'"
    sh "oc start-build aloha --from-dir=. --follow"
    projectDeploy()
}

// Tag the ImageStream from an original project to force a deployment
def deployAloha(String origProject, String project){
    projectSet()
    sh "oc policy add-role-to-user system:image-puller system:serviceaccount:${project}:default -n ${origProject}"
    sh "oc tag ${origProject}/aloha:latest ${project}/aloha:latest"
    projectDeploy()
}

// Login and set the project
def projectSet(){
    sh "oc login --insecure-skip-tls-verify=true -u openshift-dev -p devel https://10.1.2.2:8443"
    sh "oc new-project ${project} || echo 'Project exists'"
    sh "oc project ${project}"
}

// Deploy the project based on a existing ImageStream
def projectDeploy(){
    sh "oc new-app aloha -l app=aloha,hystrix.enabled=true || echo 'Aplication already Exists'"
    sh "oc expose service aloha || echo 'Service already exposed'"
    sh 'oc patch dc/aloha -p \'{"spec":{"template":{"spec":{"containers":[{"name":"aloha","ports":[{"containerPort": 8778,"name":"jolokia"}]}]}}}}\''
    sh 'oc patch dc/aloha -p \'{"spec":{"template":{"spec":{"containers":[{"name":"aloha","readinessProbe":{"httpGet":{"path":"/api/health","port":8080}}}]}}}}\''
}

