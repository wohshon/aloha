node {
    stage 'Git checkout'
    echo 'Checking out git repository'
    git url: 'https://github.com/redhat-helloworld-msa/aloha'

    stage 'Build project'
    echo 'Building'
    def mvnHome = tool 'M3'
    def javaHome = tool 'jdk8'
    sh "${mvnHome}/bin/mvn package"
   
    stage 'Deploy to QA'
    echo 'Deploying to QA'
    deployAloha('helloworld-msa-qa')

    stage 'Wait for approval'
    input 'Aprove to production?'

    stage 'Deploy to production'
    echo 'Deploying to production'
    deployAloha('helloworld-msa')
}

def deployAloha(String project){
    sh "oc login --insecure-skip-tls-verify=false -u openshift-dev -p devel https://10.1.2.2:8443"
    sh "oc new-project ${project} || echo 'Project exists'"
    sh "oc project ${project}"
    sh "oc new-build --binary --name=aloha -l app=aloha || echo 'Build exists'"
    sh "oc start-build aloha --from-dir=. --follow"
    sh "oc new-app aloha -l app=aloha,hystrix.enabled=true || echo 'Aplication already Exists'"
    sh "oc expose service aloha || echo 'Service already exposed'"
    sh "oc patch dc/aloha -p '{"spec":{"template":{"spec":{"containers":[{"name":"aloha","ports":[{"containerPort": 8778,"name":"jolokia"}]}]}}}}'"
    sh "oc patch dc/aloha -p '{"spec":{"template":{"spec":{"containers":[{"name":"aloha","readinessProbe":{"httpGet":{"path":"/api/health","port":8080}}}]}}}}'"
}