import Route from "@interfaces/routes.interface";
import {Router} from "express";
import FileConversionController from "@controllers/fileconverter.controller";

class FileConversionRoute implements Route {
  public path = '/file-convert';
  public router = Router();
  public fileConversionService = new FileConversionController();

  constructor() {
    this.initializeRoutes();
  }

  private initializeRoutes() {
    this.router.post(`${this.path}`, this.fileConversionService.getNotification);
  }
}

export default FileConversionRoute;
