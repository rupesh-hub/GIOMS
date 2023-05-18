// import { FCMTokenService } from '@services/fcmtoken.service';
// import { NextFunction, Request, Response } from 'express';

// class FCMTokenController {
//   public fcmTokenService = new FCMTokenService();
//   constructor() {}

//   public insertToken = async (req, res: Response, next: NextFunction) => {
//     try {
//       const { token } = req.body;
//       const { id } = req.userInfo;
//       const tokenData = await this.fcmTokenService.saveToken(token, id);
//       res.status(200).json({ data: { ...tokenData }, message: 'All notification data is fetched successfully' });
//     } catch (e) {
//       next(e);
//     }
//   };
// }

// export default FCMTokenController;
