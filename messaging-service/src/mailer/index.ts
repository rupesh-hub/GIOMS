import nodemailer, { Transporter } from 'nodemailer';
import hbs from 'nodemailer-express-handlebars';
import { EmailMQ } from '@interfaces/email.interface';

class Mailer {
  private static transporter: Transporter;
  private port: number;
  constructor() {
    this.port = Number(process.env['email_port'] || '465');
    const hbsConfig = {
      viewEngine: {
        extName: '.handlebars',
        partialsDir: 'template/',
        layoutsDir: 'template/',
        defaultLayout: 'template',
      },
      viewPath: 'template/',
      extName: '.handlebars',
    };
    Mailer.transporter = nodemailer.createTransport({
      host: process.env.email_host || 'smtp.gmail.com',
      port: this.port,
      secure: true,
      auth: { user: process.env.email_username || 'advboard111@gmail.com', pass: process.env.email_password || '#@#@board12345' },
      tls: {
        // do not fail on invalid certs
        rejectUnauthorized: false,
      },
    });
    Mailer.transporter.use('compile', hbs(hbsConfig));
  }

  static async sendMail(data: EmailMQ): Promise<boolean> {
    try {
      console.log(data);
      const email = {
        from: 'Gerp',
        to: data.to,
        cc: data.cc,
        template: data.template ? data.template : 'template',
        subject: data.subject,
        context: data.context,
        attachments: data.attachments,
      };
      const msg = await Mailer.transporter.sendMail(email);
      console.log(msg);
      return true;
    } catch (ex) {
      console.log(ex.message);
      return false;
    }
  }
}

export default Mailer;
