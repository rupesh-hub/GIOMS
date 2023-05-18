import FileConversionService from '@services/puppeteer.service';
import { NextFunction, Response } from 'express';
import path from 'path';

class FileConverterController {
  private fileConversionService: FileConversionService;
  constructor() {
    this.fileConversionService = new FileConversionService();
  }

  public getNotification = async (req, res: Response, next: NextFunction) => {
    try {
      const { html } = req.body;
      const buffer = await this.fileConversionService.generateFile(html);
      // @ts-ignore
      res.set('Content-Type', 'application/pdf');
      res.send(buffer);
    } catch (ex) {
      next(ex);
    }
  };
}

export default FileConverterController;
