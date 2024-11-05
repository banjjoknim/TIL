# 'Docker + Git + Minikube + Argo CD' Automatically Deploy Pipeline Sample

## 파이프라인 구성 순서

### K8s에 배포할 도커 이미지 준비

#### 1. Install Docker Engine

#### 2. Docker Image Build

#### 3. Docker Image Push to DockerHub

### K8s 환경 준비

#### 4. Install Minikube

```shell
brew install minikube
```

필자는 MacOS를 사용하기 때문에 위와 같이 설치하였으나 다른 방법을
원한다면 [여기](https://minikube.sigs.k8s.io/docs/start/?arch=%2Fmacos%2Fx86-64%2Fstable%2Fbinary+download)를 참조하도록 한다.

#### 5. Install Kubectl

```shell
brew install kubectl
```

필자는 MacOS를 사용하기 때문에 위와 같이 설치하였으나 다른 방법을 원한다면 [여기](https://kubernetes.io/ko/docs/tasks/tools/)를 참조하도록 한다.

### 배포 환경 준비

#### 6. Install Argo CD CLI

#### 7. Access The Argo CD API Server

#### 8. Argo CD Login

#### 9. Create An Application From A Git Repository

### 서버 배포 및 동작 확인

#### 10. Sync (Deploy) The Application

### 참고 자료

- https://docs.docker.com/engine/install/
- https://docs.docker.com/docker-hub/
- https://minikube.sigs.k8s.io/docs/start/?arch=%2Fmacos%2Fx86-64%2Fstable%2Fbinary+download
- https://kubernetes.io/ko/docs/tasks/tools/
- https://kubernetes.io/docs/reference/generated/kubectl/kubectl-commands
- https://argo-cd.readthedocs.io/en/stable/getting_started/
- https://velog.io/@junsugi/Argo-CD-Image-Updater-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-with.-AWS-EKS