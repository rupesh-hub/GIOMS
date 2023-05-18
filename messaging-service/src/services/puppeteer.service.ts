import puppeteer from "puppeteer";
import fs from 'fs';
import {v4} from 'uuid';
class PuppeteerService{
  private readonly path:string;
  constructor() {
    this.path="./files"
    try {
      // first check if directory already exists
      if (!fs.existsSync(this.path)) {
        fs.mkdirSync(this.path);
        console.log("Directory is created.");
      } else {
        console.log("Directory already exists.");
      }
    } catch (err) {
      console.log(err);
    }
  }

  async generateFile(html:string){
    const browser = await puppeteer.launch({
      headless: true,
      args: ["--no-sandbox", "--disable-setuid-sandbox"]
    });
    const page = await browser.newPage();
    await page.setContent(html)
    const id = v4();
    const buffer = await page.pdf({format: "a4",
      margin: {
        left: '10px',
        top: '30px',
        right: '10px',
        bottom: '20px'
      }})
    await browser.close();
    return buffer;
  }

}

export default  PuppeteerService;
