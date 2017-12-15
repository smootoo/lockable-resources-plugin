FROM external-sandbox.docker.dev.ahl/jenkins/jenkins:2.60.3-alpine
 
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
 
COPY security.groovy /usr/share/jenkins/ref/init.groovy.d/security.groovy
 
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
#RUN http_proxy="http://svc-ahlwebproxy:69L92j5TEsFCpz@euro-webproxy.drama.man.com:8080" /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
