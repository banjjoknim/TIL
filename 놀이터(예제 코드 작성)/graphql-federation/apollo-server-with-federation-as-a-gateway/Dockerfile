FROM node:20
WORKDIR /app
COPY . .

RUN apt install curl

# install Apollo Rover CLI
# Note the `v` prefixing the version number
# 작성 당시 Rover CLI 버전 0.17.3
RUN curl -sSL https://rover.apollo.dev/nix/v0.17.3 | sh

# To get started you need Rover's bin directory (/root/.rover/bin) in your PATH environment variable. Next time you log in this will be done automatically.
RUN ln -s /root/.rover/bin/rover /usr/local/bin/rover

# node:20-alpine 사용시 `error: This version of Rover does not support this command.` 발생에 유의
#RUN sh compose_supergraph.sh

EXPOSE 4000

CMD [ "npm", "run", "start" ]
