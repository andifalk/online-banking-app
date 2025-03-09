# Keycloak setup

```bash
docker run -p 9090:8080 \
-e KEYCLOAK_ADMIN=admin \
-e KEYCLOAK_ADMIN_PASSWORD=admin \
quay.io/keycloak/keycloak:26.1.3 start-dev
```

Now you can go to http://localhost:9090 and login into Admin Console using the credentials admin/admin. After logging
into the Admin Console, setup realm and product-service client as follows:

In the top-left corner, there is a realm drop-down, which provides the option to create a new realm. Create a new realm
with the name `banking-demo`.

Under the `banking-demo` realm, create a new client with by providing the following details:

* Client ID: banking-client
* Client Authentication: Off
* Authentication flow: select only `Standard flow` (authorization code)
* Valid Redirect URIs: http://localhost:3000/*  (please do not use wildcards on productive systems!!)

We have registered the `banking-client` as a client and enabled `Authorization Code` flow.

Now export the `banking-demo` realm using the following commands:

```bash
$ docker ps
# copy the keycloak container id

# ssh into keycloak container
$ docker exec -it <container-id> /bin/bash

# export the realm configuration
$ /opt/keycloak/bin/kc.sh export --dir /opt/keycloak/data/import --realm banking-demo

# exit from the container
$ exit

# copy the exported realm configuration to local machine
$ docker cp <container-id>:/opt/keycloak/data/import/banking-demo-realm.json ~/Downloads/banking-demo-realm.json
```

Finally, copy the `banking-demo-realm.json` file into `src/test/resources` folder.