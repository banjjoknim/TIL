# 'Docker + Git + Minikube + Argo CD' Automatically Deploy Pipeline Sample

## 파이프라인 구성 순서

### K8s에 배포할 도커 이미지 준비

#### 1. Install Docker Engine

- [Install Docker Engine](https://docs.docker.com/engine/install/)
- [Docker Desktop](https://docs.docker.com/desktop/)

위 링크를 참조하여 `Docker Engine`을 설치하도록 한다.

#### 2. Docker Image Build

```shell
docker build -t <your-docker-username>/<your-image-tag>:<your-image-tag-name> .
```

`docker` 명령어를 이용해 이미지를 빌드한다.

명령어에 대해 자세히 알고 싶다면 아래 링크를 참조하도록 한다.

- [Build, tag, and publish an image](https://docs.docker.com/get-started/docker-concepts/building-images/build-tag-and-publish-an-image/)
- [docker build (legacy builder)](https://docs.docker.com/reference/cli/docker/build-legacy/)를 참조하도록 한다.

#### 3. Docker Image Push to DockerHub

```shell
docker push <your-dockerhub-username>/<your-image-tag>:<your-image-tag-name>
```

`docker` 명령어를 이용해 이미지를 푸시한다.

명령어에 대해 자세히 알고 싶다면 아래 링크를 참조하도록 한다.

- [Build, tag, and publish an image](https://docs.docker.com/get-started/docker-concepts/building-images/build-tag-and-publish-an-image/)
- [docker image push](https://docs.docker.com/reference/cli/docker/image/push/)를 참조하도록 한다.

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

#### 6. Install Argo CD & CLI

```shell
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
```

위 명령어를 이용해서 `Kubernetes`에 `argocd`라는 `namespace`를 생성하고, 거기에 `argocd` 관련 `resource(pod, svc, secret, configmap ...)`를
생성한다.

생성된 `Kubernetes resource`들은 아래 명령어를 이용해서 확인할 수 있다.

```shell
kubectl get pod -n argocd
kubectl get svc -n argocd
kubectl get configmap -n argocd
kubectl get secret -n argocd
```

![kubectl_get_pod_n_argocd.png](images/kubectl_get_pod_n_argocd.png)
![kubectl_get_svc_n_argocd.png](images/kubectl_get_svc_n_argocd.png)
![kubectl_get_configmap_n_argocd.png](images/kubectl_get_configmap_n_argocd.png)
![kubectl_get_secret_n_argocd.png](images/kubectl_get_secret_n_argocd.png)

추가로, 아래 명령어를 이용해서 `argocd CLI`를 설치한다.

```shell
brew install argocd
```

필자는 MacOS를 사용하기 때문에 위와 같이 설치하였으나 다른 방법을
원한다면 [여기](https://argo-cd.readthedocs.io/en/stable/getting_started/#2-download-argo-cd-cli)를 참조하도록 한다.

#### 7. Access The Argo CD API Server

기본적으로 `Argo CD API Server`는 외부로 노출된 IP를 제공하지 않는다.

따라서 Argo CD Server에 접근하기 위해서 API Server 노출 설정을 해주어야 한다.

그리고 `Argo CD API Server`는 `Kubernetes` 위에서 동작할 것이기 때문에, `kubectl`을 이용하거나 `Kubernetes`에 특정 설정을 해주어야 한다.

---

- [Service Type Load Balancer](https://argo-cd.readthedocs.io/en/stable/getting_started/#service-type-load-balancer)

```shell
kubectl patch svc argocd-server -n argocd -p '{"spec": {"type": "LoadBalancer"}}'
```

---

- [Ingress](https://argo-cd.readthedocs.io/en/stable/getting_started/#ingress)
  - [Ingress Configuration](https://argo-cd.readthedocs.io/en/stable/operator-manual/ingress/)

---

- [Port Forwarding](https://argo-cd.readthedocs.io/en/stable/getting_started/#ingress)

```shell
kubectl port-forward svc/argocd-server -n argocd 8080:443
```

---

[문서](https://argo-cd.readthedocs.io/en/stable/getting_started/#3-access-the-argo-cd-api-server)에서는 위 세 가지 방법을 제시하고 있으니,
원하는 방식을 취사 선택하여 사용하도록 한다.

참고로 필자는 `Port Forwarding`을 사용할 것이다.

#### 8. Argo CD Login

기본적으로 주어지는 `Argo CD Server`의 관리자 계정인 `admin`의 초기 비밀번호는 자동생성된다.

그리고 그 초기 비밀번호는 아래 명령어로 확인할 수 있다.

```shell
argocd admin initial-password -n argocd
```

비밀번호를 확인했으면 아래 명령어를 통해 로그인할 수 있다.

```shell
argocd login <ARGOCD_SERVER>
```

하지만 앞서 필자는 포트 포워딩을 사용할 것이라고 했다. 따라서 아래와 같은 순서로 명령어를 실행한다.

```shell
kubectl port-forward -n argocd svc/argocd-server <your-port-number>:443 # `kubectl port-forward -n argocd svc/argocd-server <your-port-number>:80` 도 가능하다.
```

![img.png](images/initial_password_port-forwarding.png)

```shell
argocd login localhost:<your-port-number>
```

![img.png](images/argocd_login.png)

#### 9. Create An Application From A Git Repository

애플리케이션 배포를 위해 `Git Repository`를 `Argo CD`에 등록(연결)해주어야 한다.

CLI 방식과 UI 방식이 존재하는데, 필자는 UI 방식을 사용하도록 하겠다.

아래와 같이 각자 상황에 알맞게 정보를 입력하고 배포 정보를 생성하도록 한다.

- `Settings`
  - `+ CONNECT REPO`

![img.png](images/settings_repositories.png)
![img.png](images/settings_connect-repo.png)
![img.png](images/settings_connect.png)

- `+ New App`

![img.png](images/argocd_new_app_general.png)
![img.png](images/argocd_new_app_source.png)
![img.png](images/argocd_new_app_destination.png)

이때, `Resource`의 내용 중에 `path`는 `Kubernetes` 설정 파일이 위치하는 경로를 입력해주어야 하는 점에 유의하도록 한다.

- `CREATE`

필요한 정보를 모두 입력하고 `CREATE` 버튼을 누르면 아래와 같이 `Application` 정보 카드를 볼 수 있다.

![argocd_applications_argocd-sample.png](images/argocd_applications_argocd-sample.png)

### 서버 배포 및 동작 확인

#### 10. Sync (Deploy) The Application

이제 마지막이다.

화면에 보이는 `argocd-sample` 애플리케이션 카드의 `SYNC` 버튼을 누르자.

필자는 `test`라는 `namespace`를 사용할 것이므로 `AUTO-CREATE NAMESPACE` 옵션에 체크하였다.

![argocd-sample-sync.png](images/argocd-sample-sync.png)

원하는 `Kubernetes Resource`를 선택하고 상단의 `SYNCHRONIZE` 버튼을 누르면...

![argocd-sample-sync-progressing.png](images/argocd-sample-sync-progressing.png)

짜잔. 아래와 같이 배포가 완료되었다.

![img.png](images/argocd-sample-deploy-complete.png)

`kubectl`을 이용해 아래와 같이 확인할 수 있다.

![img.png](images/argocd-sample-k8s-resource-check.png)

마지막으로, 아래와 같이 테스트 또한 정상적으로 동작하는 것을 볼 수 있다.

![img.png](images/argocd-sample_test.png)

### 참고 자료

- https://docs.docker.com/engine/install/
- https://docs.docker.com/docker-hub/
- https://minikube.sigs.k8s.io/docs/start/?arch=%2Fmacos%2Fx86-64%2Fstable%2Fbinary+download
- https://kubernetes.io/ko/docs/tasks/tools/
- https://kubernetes.io/docs/reference/generated/kubectl/kubectl-commands
- https://argo-cd.readthedocs.io/en/stable/getting_started/
- https://velog.io/@junsugi/Argo-CD-Image-Updater-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-with.-AWS-EKS