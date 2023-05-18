export interface EmailMQ {
  to: string;
  cc: Array<string>;
  template: string;
  subject: string;
  context: Map<string, string>;
  attachments: Array<fileInterface>;
}

export interface fileInterface {
  fileName: string;
  base64: string;
  encoding: string;
}
