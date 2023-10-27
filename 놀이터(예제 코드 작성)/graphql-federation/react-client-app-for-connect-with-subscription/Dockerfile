FROM node:14-alpine

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install --no-optional && npm cache clean --force

COPY . .

EXPOSE 3000

ENTRYPOINT ["npm", "start"]
