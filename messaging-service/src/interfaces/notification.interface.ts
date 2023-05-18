export interface Notification {
  primaryKey: number; //primaryKey of relations from GERP database
  detail: string;
  receiver: string;
  sender: string;
  subject: string;
  moduleId: number;
  module: string;
  receiverOfficeCode: string;
  receiverSectionCode: string;
  notificationType: type;
  pushNotification: boolean;
}

export enum type {
  P = 'P',
  G = 'G',
}
export interface NotificationMQ {
  primaryKey: number; //primaryKey of relations from GERP database
  detail: string;
  receiver: [string];
  sender: string;
  subject: string;
  moduleId: number;
  module: string;
  receiverOfficeCode: string;
  receiverSectionCode: string;
  notificationType: type;
  pushNotification: boolean;
}

export interface ResultResponse {
  sub: string;
  email_verified: boolean;
}
