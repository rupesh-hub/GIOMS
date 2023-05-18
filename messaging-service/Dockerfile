# custom node image with libiaries
FROM manjitmaharjan/custom-node-messaging
#ENV http_proxy=http:...
#ENV https_proxy=http:...
COPY ./kalimati-regular.otf /usr/share/fonts/
RUN fc-cache -f -v

COPY . ./app

WORKDIR /app

RUN npm install


EXPOSE 3000

ENV NODE_ENV production

CMD ["npm", "run", "start"]
