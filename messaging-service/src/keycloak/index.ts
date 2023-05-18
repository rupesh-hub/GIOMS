import { MemoryStore } from 'express-session';
import KeycloakConnect = require('keycloak-connect');
import { keyCloak } from '@interfaces/keycloak.interface';

export class KeycloakService {
  constructor() {
    this.initKeycloak();
    console.log('keycloak server');
  }
  private memoryStore: MemoryStore;
  private keycloakConnnect: KeycloakConnect.Keycloak;

  public initKeycloak(): KeycloakConnect.Keycloak {
    if (this.keycloakConnnect) {
      return this.keycloakConnnect;
    } else {
      const configJson: keyCloak = {
        realm: process.env.REALM,
        'bearer-only': process.env.BEARER_ONLY == 'true',
        'auth-server-url': process.env.AUTH_SERVER_URL,
        'ssl-required': process.env.SSL_REQUIRED,
        resource: process.env.RESOURCE,
        'confidential-port': Number(String(process.env.CONFIDENTIAL_PORT)),
      };
      this.memoryStore = new MemoryStore();
      this.keycloakConnnect = new KeycloakConnect({ store: this.memoryStore }, configJson);
      return this.keycloakConnnect;
    }
  }

  public getKeycloakInstance(): KeycloakConnect.Keycloak {
    if (this.keycloakConnnect) {
      return this.keycloakConnnect;
    }
  }

  public getMemoryStore() {
    return this.memoryStore;
  }
}
