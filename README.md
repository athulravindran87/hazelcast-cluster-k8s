# hazelcast-cluster-k8s
Embeded Hazelcast Cluster On Kubernetes

[![Build Status](http://34.68.205.106/jenkins/buildStatus/icon?job=hazelcast-cluster-master-build&subject=Master%20Build)](http://34.68.205.106/jenkins/job/hazelcast-cluster-master-build/)       [![Build Status](http://34.68.205.106/jenkins/buildStatus/icon?job=hazelcast-cluster-mutation-test&subject=Mutation%20Test)](http://34.68.205.106/jenkins/job/hazelcast-cluster-mutation-test/)    [![Codacy Badge](https://api.codacy.com/project/badge/Grade/e9e89cc98f5d4b0f9fd80d18c9935981)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=athulravindran87/hazelcast-cluster&amp;utm_campaign=Badge_Grade)     [![Quality Gate Status](http://34.67.51.46/api/project_badges/measure?project=com.athul%3Ahazelcast-cluster&metric=alert_status)](http://34.67.51.46/dashboard?id=com.athul%3Ahazelcast-cluster)       [![Bugs](http://34.67.51.46/api/project_badges/measure?project=com.athul%3Ahazelcast-cluster&metric=bugs)](http://34.67.51.46/dashboard?id=com.athul%3Ahazelcast-cluster)    [![Coverage](http://34.67.51.46/api/project_badges/measure?project=com.athul%3Ahazelcast-cluster&metric=coverage)](http://34.67.51.46/dashboard?id=com.athul%3Ahazelcast-cluster)    [![Technical Debt](http://34.67.51.46/api/project_badges/measure?project=com.athul%3Ahazelcast-cluster&metric=sqale_index)](http://34.67.51.46/dashboard?id=com.athul%3Ahazelcast-cluster)   [![Maintainability Rating](http://34.67.51.46/api/project_badges/measure?project=com.athul%3Ahazelcast-cluster&metric=sqale_rating)](http://34.67.51.46/dashboard?id=com.athul%3Ahazelcast-cluster)

## Technical Stack:                   	         
1) Hazelcast v3.12                                         
2) Spring Boot 2.x
3) Docker
4) Google Cloud - Google Kubernetes Engine

![hazelcast-cluster-k8s](https://user-images.githubusercontent.com/5833938/63640371-7947e300-c66d-11e9-83e3-725304eeacfe.jpg)

| Service Name        | port | Comments                       |  
| ------------------- | -----| -------------------------------|
| discover-server-k8    | 8761 | Eureka discovery server        |
| hazelcast-server-k8s  | 8762 | hazelcast server instance    |
| hazelcast-client-k8s  | 8764 | hazelcast client instance   |

## What's in here ??
This project is an working example of hazelcast clustering on kubernetes leveraging k8s inbuilt discovery mechanism. 
One of the micorservices deployed to the cluster is s Discovery server which is a Eureka Discovery server. The hazelcast server and client registers itself to Eureka server however will use Kubernetes Discovery API to form a cluster. Eureka will be used for other functionalities like Ribbon, Zuul etc.,

There are 3 main components as depicted in the picture above. 1) Kubernetes cluster. 2) Hazelcast servers (2) and 3) Hazelcast client. Hazelcast servers are capable of a member joining mechanism who discover each other using hazelcast group name and creates a join. They are also responsible for creation of distributed objects such as map and queues. Hazelcast client joins the hazlecast cluster when deployed to the same k8 cluster. 

## How to deploy and test ??

 1. Running on Minikube locally
   
    Pre-requisite: 
    
      1.Set up Minikube on your local machine and run the following commands
      2.Install the Google Cloud SDK, which includes the `gcloud` command-line tool. Using the gcloud command line tool,       
      install the Kubernetes command-line tool. 
    ```
    kubectl apply -f discovery-server-k8s/deploy/kube-discovery-server.yaml
    kubectl apply -f hazelcast-server-k8s/deploy/kube-rbac.yaml   
    kubectl apply -f hazelcast-server-k8s/deploy/kube-hazelcast-server.yaml
    kubectl apply -f hazelcast-client-k8s/deploy/kube-hazelcast-client.yaml
   
    
 2. Running on Google Kubernetes Engine (GKE)
    
    Pre-requisite: 
      1. Google cloud account.
      2. New Cloud project
      3. Go to Kubernetes Engine and set up a cluster.
      4.Install the Google Cloud SDK, which includes the `gcloud` command-line tool. Using the gcloud command line tool,       
      install the Kubernetes command-line tool. 
      
    Run the following commands.
    
    ```
       kubectl apply -f discovery-server-k8s/deploy/kube-discovery-server.yaml
       kubectl apply -f hazelcast-server-k8s/deploy/kube-rbac.yaml
       kubectl apply -f hazelcast-server-k8s/deploy/kube-hazelcast-server.yaml
       kubectl apply -f hazelcast-client-k8s/deploy/kube-hazelcast-client.yaml


   Go thru `commands.txt` file, it is a cheat sheet of `kube` commands.
    
To view member creattion, go to ```/actuator/health``` on hazelcast server to view custom implemented health endpoint.      
    
 ### Test
 
 #### The best way to test is "put" on one hazelcast-client instance and "get" on another hazelcast-client instance. 
 
 There are 2 sets of API endpoints to test (see the controllers)
- Map
   - /map/put?key=xx&value=yyy
   - /map/get?key=xx
- Queue
   - /queue/put?value=xx
   - /queue/get
   

