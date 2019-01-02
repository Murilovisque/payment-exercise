FROM java:8

# Install maven
RUN wget http://mirror.nbtelecom.com.br/apache/maven/maven-3/3.6.0/binaries/apache-maven-3.6.0-bin.tar.gz
RUN mkdir -p /usr/share/maven
RUN tar -xzf apache-maven-3.6.0-bin.tar.gz -C /usr/share/maven --strip-components=1
RUN ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
RUN rm apache-maven-3.6.0-bin.tar.gz
ENV M2_HOME=/usr/share/maven

# Setup environment
RUN mkdir -p /var/log/payment-checkout

EXPOSE 8080

WORKDIR /payment-exercise
ENTRYPOINT /bin/bash -c "chmod +x /payment-exercise/extras/docker/setup.sh && /payment-exercise/extras/docker/setup.sh"
CMD ["bash"]