# Installation Document OC-Explorer

## VPS (virtual server) requirements

- CPU: 4 cores
- Memory: 16GB
- Disk: 128GB SSD

## Software Prerequisites

- Operating system: Ubuntu Server x64 16.04 LTS or upper
- MongoDB 3.4 (will be installed below)

## Install dependencies

### Install MongoDB

Please refer to this comprehensive documentation page about how to install MongoDB on Ubuntu
https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/

quick list of commands that worked for us:

```
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 0C49F3730359A14518585931BC711F9BA15703C6
echo "deb [ arch=amd64,arm64 ] http://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/3.4 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.4.list
sudo apt update
sudo apt install mongodb-org
sudo systemctl enable mongod
sudo systemctl start mongod
```


## Install OpenJDK 8.0

`$ sudo apt install openjdk-8-jdk`

## Create user oce

`$ sudo useradd -m oce`

## Create the Derby db with users

### Create install dir

```
$ sudo mkdir /derby
$ sudo chown -R oce:oce /derby
```

### Unzip provided derby database backup

```
$ sudo apt install p7zip
$ wget http://url-to-derby-download-provided-by-dg-team
$ 7zr x -o/derby oce-derby-*.7z
```

## Download and compile the open source code from github.com

### Install Maven

`$ sudo apt install maven`

### Get the source code


```
$ su - oce
$ git clone https://github.com/devgateway/oc-explorer.git
```

### Compile the code

```
$ cd oc-explorer
$ git checkout master
$ mvn -Dmaven.javadoc.skip=true -Dmaven.test.skip=true install
```

### Copy artifact and config to startup locatinon

```
$ cd ~
$ cp forms/target/forms-*-SNAPSHOT.jar oce.jar
$ cp forms/forms.conf oce.conf
```

### Edit configuration file oce.conf

- Replace {website.url} with your website's URL

### Make symlink to enable startup as service

```
$ sudo ln -s /home/oce/oce.jar /etc/init.d/oce
$ sudo update-rc.d oce defaults
```


## Start the server

`$ sudo service oce start`
