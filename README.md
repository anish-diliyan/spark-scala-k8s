<h1 align='center'>Spark Scala-Java Kubernetes</h1>

## Prerequisites
- git and GitHub account.
- Scala plugin added in intellij.
- sbt: scala plugin comes with sbt, so no need to install sbt.
- java 17
- docker: required for K8S
- Kubectl: K8S command line tool
- MiniKube: Local K8S cluster
- Helm: To create helm chart
- Hdfs: Standalone storage
- Spark

### Install Java
```shell
   java -version
```
- If Java 17 or 11 shows → good. If not install
```shell
  sudo apt install openjdk-17-jdk -y
```
- Set Java_Home and Path in environment variable
- get value for JAVA_HOME to set : readlink -f $(which java)
- open .bashrc and add : vim ~/.bashrc
```shell
  export JAVA_HOME=/home/anish/.jdks/corretto-17.0.15
  export PATH=$PATH:$JAVA_HOME/bin:$PATH
```

### Install Docker
```shell
  docker version
```
- If not installed, then run following command one by one
```shell
  sudo apt update
  sudo apt install -y ca-certificates curl gnupg
```
- Add Docker’s official repository
```shell
  sudo install -m 0755 -d /etc/apt/keyrings
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
  echo \
    "deb [arch=$(dpkg --print-architecture) \
    signed-by=/etc/apt/keyrings/docker.gpg] \
    https://download.docker.com/linux/ubuntu \
    $(lsb_release -cs) stable" | \
    sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```
```shell
  sudo apt update
  sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```
- Enable non-root docker usage.
```shell
  sudo usermod -aG docker $USER
```
- Restart your computer and test
```shell
  docker run hello-world
```
- You should see a success message.

### Install Kubectl
- Command line tool for minikube/K8S
```shell
  kubectl version --client
```
- If not installed then install
```shell
  sudo snap install kubectl --classic
```

### Install MiniKube
- This will give you a local K8S cluster similar to OpenShift/EKS.
```shell
  minikube version
```
- Download Minikube binary, if not installed.
```shell
  curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube_latest_amd64.deb
```
- Install.
```shell
  sudo dpkg -i minikube_latest_amd64.deb
```
- Start Minikube (using Docker as driver)
```shell
  minikube start --driver=docker
```
- This will:
  - Create a Kubernetes cluster inside Docker
  - Start control plane
  - Start worker nodes
  - Setup networking
- This step may take 2–4 minutes.
- After its start, test
```shell
  kubectl get nodes
  kubectl get pods -A
```
- You should see 1 node in "Ready" state.
#### dpkg: error: dpkg frontend lock was locked by another process with pid 237128
- This error is normal — it means another process is installing or updating something (usually Ubuntu Software Center / apt auto-update).
- We must not delete the lock, we just wait or kill the process safely.
- Check which process is locking dpkg
```shell
  ps -p 237128 -o comm=
```
- If it is “apt”, “apt-get”, “unattended-upgrade”, “packagekit”
```shell
  sudo kill 237128
```
- Then clean the lock and fix dpkg
```shell
sudo rm /var/lib/dpkg/lock-frontend
sudo rm /var/lib/dpkg/lock
sudo dpkg --configure -a
sudo apt update
```
- Install Minikube again

### Install Helm (Kubernetes package manager)
```shell
  helm version
```
- If not installed then install.
```shell
  curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
```
- Verify Helm can talk to Kubernetes.
```shell
  helm list
```
- It should show an empty list but no errors.
- **Perfect — Helm is fully installed and connected to your Kubernetes cluster.**

### Install HDFS
```shell
  hadoop version
```
- Download Hadoop (latest stable), If not installed
```shell
  cd ~
  curl -O https://downloads.apache.org/hadoop/common/hadoop-3.4.1/hadoop-3.4.1.tar.gz
  tar -xvf hadoop-3.4.1.tar.gz
  mv hadoop-3.4.1 hadoop
```
- Set Environment Variables
```shell
  vim ~/.bashrc
```
```shell
  # Hadoop Env Vars
  export HADOOP_HOME=$HOME/hadoop
  export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
  export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
```
- Configure core-site.xml
```shell
  vim $HADOOP_CONF_DIR/core-site.xml
```
```xml
<configuration>
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://localhost:9000</value>
  </property>
</configuration>
```
- Configure hdfs-site.xml
```shell
  vim $HADOOP_CONF_DIR/hdfs-site.xml
```
```xml
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>

    <property>
        <name>dfs.namenode.name.dir</name>
        <value>file:///home/anish/hdfs/namenode</value>
    </property>

    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:///home/anish/hdfs/datanode</value>
    </property>
</configuration>
```
- Create directories
```shell
  mkdir -p ~/hdfs/namenode ~/hdfs/datanode
```
- Format Namenode
```shell
  hdfs namenode -format
```
- Start HDFS
```shell
  start-dfs.sh
```
- Check Java processes
```shell
  jps
```
- You should see
  - NameNode
  - DataNode
  - SecondaryNameNode
- **Error: localhost: ssh: connect to host localhost port 22: Connection refused.**
- Hadoop’s start-dfs.sh uses SSH, even in standalone/pseudo-distributed mode.
- Your machine does not have SSH server running, so Hadoop can’t SSH to localhost.
- Install and start SSH server
```shell
  sudo apt install openssh-server -y
  sudo systemctl enable ssh
  sudo systemctl start ssh
```
- Verify SSH is running.
```shell
  ssh localhost
```
- First time, it will ask, Are you sure you want to continue connecting? yes
- If it asks password → enter your Ubuntu password.
- Enable passwordless SSH (recommended)
```shell
  ssh-keygen -t rsa -b 4096
```
- This will create ~/.ssh/id_rsa and ~/.ssh/id_rsa.pub
- Add key to authorized list.
```shell
  cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
  chmod 600 ~/.ssh/authorized_keys
```
```shell
  ssh localhost
```
- It should log in without password, and you are got to start HDFS.

### Install Spark (binary only)
```shell
  spark-submit --version
```
- If not installed, install it.
```shell
 cd ~
 wget https://downloads.apache.org/spark/spark-3.5.4/spark-3.5.4-bin-hadoop3.tgz
 tar -xvf spark-3.5.4-bin-hadoop3.tgz
 mv spark-3.5.4-bin-hadoop3 spark
```
- Add to PATH
```shell
  echo 'export SPARK_HOME=$HOME/spark' >> ~/.bashrc
  echo 'export PATH=$PATH:$SPARK_HOME/bin' >> ~/.bashrc
  source ~/.bashrc
```
- Connect Spark to HDFS
```shell
  vim $SPARK_HOME/conf/spark-env.sh
```
```shell
  export HADOOP_CONF_DIR=$HOME/hadoop/etc/hadoop
```
- Quick Local Test
```shell
spark-submit \
  --class org.apache.spark.examples.SparkPi \
  $SPARK_HOME/examples/jars/spark-examples_2.12-3.5.3.jar \
  5
```
- Pi is roughly 3.14 (If this works → Spark is healthy.)

## Setup the repository
- Clone the repository (https://github.com/anish-diliyan/spark-scala-k8s)
### Build
```bash
  sbt-package.sh
```
### Executes .sh from scripts folder, this will trigger spark-submit
```bash
  ./scripts/run-spark-job.sh
```

## If You Reboot/Restart the Computer, Do the following
- Docker desktop starts Automatically, but we can check status
```shell
  docker ps
```
- Start MiniKube/K8S
```shell
  minikube start
  kubectl get nodes (check status)
  kubectl get pods -A (check if pods is running)
```
- Start Hdfs
```shell
  start-dfs.sh
  jps (check if hadoop is running)
```
- We do not need to start Spark, because it comes in picture when we do spark-submit.